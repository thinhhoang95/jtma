import itertools
import pandas as pd
import numpy as np
import random
from collections import defaultdict

horizontal_separation = 6

def scale_flights(df, scaling_factor):
    # Add an 'hour' column to group flights per hour
    df['hour'] = df['entry_time'] // 3600

    # Calculate the number of flights per entry point per hour
    distribution = df.groupby(['entry_point', 'hour']).size().reset_index(name='flight_count')

    # Initialize a list to hold new flights
    new_flights = []

    n_flights_added = 0

    # Scaling up the flights
    flip_flop = False
    for index, row in distribution.iterrows():
        entry_point = row['entry_point']
        hour = row['hour']
        flight_count = int(row['flight_count'])
        total_flights_needed = int(flight_count * scaling_factor)

        # Original flights for this entry point and hour
        original_flights = df[(df['entry_point'] == entry_point) & (df['hour'] == hour)]

        # Generate additional flights
        n_new = total_flights_needed - flight_count


        if flip_flop:
            n_new += 1
            
        flip_flop = not flip_flop

        n_flights_added += n_new
        for i in range(n_new):
            # Randomly select an original flight to copy
            flight_to_copy = original_flights.sample(n=1).iloc[0].copy()
            # Generate a new callsign
            flight_to_copy['callsign'] = f"{flight_to_copy['callsign']}_COPY_{i}"
            new_flights.append(flight_to_copy)

    print('There are {} new flights to be added'.format(n_flights_added))

    # Append new flights to the original dataframe
    if new_flights:
        df = pd.concat([df, pd.DataFrame(new_flights)], ignore_index=True)

    # Recalculate the distribution after scaling
    df['hour'] = df['entry_time'] // 3600
    distribution = df.groupby(['entry_point', 'hour']).size().reset_index(name='flight_count')

    # Randomize entry times while maintaining separation
    # Set conflict_tolerance to allow a small number of overlaps (e.g., 5)
    conflict_tolerance = 5
    df = randomize_entry_times(df, distribution, conflict_tolerance)

    # Drop the 'hour' column as it's no longer needed
    df.drop(columns=['hour'], inplace=True)

    return df

def randomize_entry_times(df, distribution, conflict_tolerance=0):
    """
    Randomize entry times for flights while maintaining minimum separation.

    Parameters:
    - df: DataFrame containing flights.
    - distribution: DataFrame with 'entry_point', 'hour', and 'flight_count'.
    - conflict_tolerance: Number of allowed conflicts per entry point.

    Returns:
    - DataFrame with updated 'entry_time'.
    """
    # Dictionary to hold scheduled entry times for each entry point
    scheduled_times = defaultdict(list)
    
    # Initialize a dictionary to keep track of conflicts per entry point
    conflicts = defaultdict(int)

    # Iterate over each entry point
    for entry_point in df['entry_point'].unique():
        # Get all flights for this entry point
        flights = df[df['entry_point'] == entry_point].copy()
        
        # Get the distribution for this entry point
        entry_distribution = distribution[distribution['entry_point'] == entry_point]
        
        # Iterate over each hour for this entry point
        for _, row in entry_distribution.iterrows():
            hour = row['hour']
            flight_count = row['flight_count']
            
            # Get all flights for this entry point and hour
            flights_in_hour = flights[flights['hour'] == hour]
            n_flights = len(flights_in_hour)
            
            if n_flights == 0:
                continue  # No flights to schedule in this hour

            minimum_speed = flights_in_hour['speed'].min()
            
            # Calculate the median speed for this entry point and hour
            median_speed = flights_in_hour['speed'].median()
            if np.isnan(median_speed) or median_speed == 0:
                # Handle cases with undefined or zero median speed
                median_speed = flights_in_hour['speed'].mean()
                if np.isnan(median_speed) or median_speed == 0:
                    median_speed = 450  # Default speed in knots
            
            # Calculate minimum time separation in seconds
            min_sep_time = (horizontal_separation * 3600) / minimum_speed + 3
            
            # Total available time slots in the hour
            total_slots = int(3600 // min_sep_time)
            if total_slots == 0:
                total_slots = 1  # At least one slot per hour
            
            # Generate possible entry times within the hour
            possible_times = [hour * 3600 + i * min_sep_time for i in range(total_slots)]

            # print(f'Hour {hour} has {total_slots} slots')
            # print(f'Possible times: {possible_times}')

            # Drop the last time slot if it is too close to the end of the hour
            if possible_times[-1] > hour * 3600 + 3570:
                possible_times = possible_times[:-1]

            # Drop the first time slot if it is too close to the start of the hour
            if possible_times[0] < hour * 3600 + 30:
                possible_times = possible_times[1:]
            
            # Shuffle the possible times to randomize
            random.shuffle(possible_times)

            # print(f'Hour {hour} has {total_slots} slots')
            # print(f'Possible times: {possible_times}')
            
            # If there are more flights than available slots, allow some overlaps based on conflict_tolerance
            extra_flights = max(0, n_flights - len(possible_times))
            if extra_flights > conflict_tolerance:
                print(f"Warning: Entry point {entry_point}, hour {hour} has {n_flights} flights but only {len(possible_times)} slots with conflict tolerance {conflict_tolerance}. Some conflicts may occur.")
                extra_flights = conflict_tolerance  # Limit extra flights to conflict_tolerance
            
            # Assign times to flights
            for i, (_, flight) in enumerate(flights_in_hour.iterrows()):
                # entry_time = hour * 3600 + random.uniform(0, 3600) # randomize the entry time

                if i < len(possible_times):
                    entry_time = possible_times[i]
                elif i - len(possible_times) < conflict_tolerance:
                    # Assign overlapping time
                    entry_time = possible_times[-1] + (i - len(possible_times) + 1) * min_sep_time * 0.5  # Half separation
                    conflicts[entry_point] += 1
                    entry_time = np.nan # Set to NaN to avoid conflicts
                else:
                    # Assign random time outside the hour to avoid excessive conflicts
                    # entry_time = hour * 3600 + random.uniform(0, 3600)
                    entry_time = np.nan # Set to NaN to avoid conflicts
                
                # Update the entry_time in the DataFrame
                df.loc[flight.name, 'entry_time'] = entry_time
                
                # Add to scheduled times
                scheduled_times[entry_point].append(entry_time)

    if any(conflicts.values()):
        print(f"Total conflicts introduced: {sum(conflicts.values())}")

    return df

# Example usage:
# Assuming your dataset is in a CSV file named 'flights.csv'
# with columns: 'callsign', 'entry_point', 'speed', 'entry_time'

def detect_conflicts(df):
    """
    Detects conflicts at entry points based on entry times and speeds.
    
    Parameters:
    df (pd.DataFrame): DataFrame with columns [id, entry_time, callsign, aircraft_type, entry_point, speed, altitude]
                       - entry_time: Time since midnight in hours.
                       - speed: Speed in knots (nautical miles per hour).
    
    Returns:
    pd.DataFrame: DataFrame with columns [entry_point, conflict_count]
    """
    
    # Initialize a dictionary to store conflict counts per entry point
    conflict_dict = {}
    
    # Group the dataframe by entry_point
    grouped = df.groupby('entry_point')
    
    for entry_point, group in grouped:
        conflict_count = 0
        
        # Generate all unique pairs of aircraft within the group
        # Using combinations to avoid duplicate pairs and self-pairing
        for (idx1, row1), (idx2, row2) in itertools.combinations(group.iterrows(), 2):
            t1 = row1['entry_time']
            t2 = row2['entry_time']
            v1 = row1['speed'] / 3600.
            v2 = row2['speed'] / 3600.

            v_lead = v1 if t1 < t2 else v2 
            
            v_follow = v2 if t1 < t2 else v1
            t_lead = t1 if t1 < t2 else t2 
            t_follow = t2 if t1 < t2 else t1

            time_sep_required = horizontal_separation / v_lead 
            if t_lead + time_sep_required > t_follow:
                conflict_count += 1
                print('-'*10)
                print(f"Conflict detected between {row1['callsign']} and {row2['callsign']} at entry point {entry_point}")
                print(f"Entry time of lead: {t_lead} (hour {t_lead // 3600}), entry time of follow: {t_follow} (hour {t_follow // 3600}), time separation required: {time_sep_required}, speed of lead: {v_lead}, speed of follow: {v_follow}")
        
        # Store the conflict count for the current entry point
        conflict_dict[entry_point] = conflict_count
    
    # Convert the conflict dictionary to a DataFrame
    conflict_df = pd.DataFrame(list(conflict_dict.items()), columns=['entry_point', 'conflict_count'])
    
    return conflict_df

if __name__ == "__main__":
    # Read the dataset
    df = pd.read_csv('DATA/SHEN_ZHEN/ZGSZ_PMS_eliminate0.flights', delimiter=' ')
    print('There are {} flights in total'.format(len(df)))
    df.columns = ['callsign', 'entry_point', 'entry_time', 'speed', 'wake_turbulance']

    # Set all the speed to 300
    # df['speed'] = 300

    # Set the scaling factor (e.g., 2 for doubling the flights)
    scaling_factor = 1.5

    # Scale up the flights
    df_scaled = scale_flights(df, scaling_factor)

    # Optionally, sort the DataFrame by entry_point and entry_time for readability
    df_scaled.sort_values(by=['entry_point', 'entry_time'], inplace=True)

    # Drop the rows with NaN entry_time
    df_scaled = df_scaled[df_scaled['entry_time'].notna()]

    # Convert entry_time to integer
    df_scaled['entry_time'] = df_scaled['entry_time'].astype(int)

    # Make sure the entry_time is positive
    entry_time_delta = df_scaled['entry_time'].min()
    df_scaled['entry_time'] = df_scaled['entry_time'] - entry_time_delta


    # Check for conflicts again
    print('Checking for conflicts at entry points...')
    # df_conflicts = detect_conflicts(df_scaled)

    # print(df_conflicts.head(20))
    
    # Save the scaled dataset to a new CSV file
    df_scaled.to_csv(f'DATA/SHEN_ZHEN/ZGSZ_PMS_scaled_{scaling_factor}.flights', index=False, sep=' ')
