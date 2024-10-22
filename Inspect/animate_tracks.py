import pandas as pd
import matplotlib.pyplot as plt
import matplotlib.animation as animation
import numpy as np


def read_node_coordinates(filename):
    """
    Reads a data file and returns a list of coordinate tuples (x, y, z).

    Parameters:
    - filename (str): Path to the data file.

    Returns:
    - list of tuples: Each tuple contains (x, y, z) as floats.

    Raises:
    - ValueError: If any line does not contain valid float values.
    - FileNotFoundError: If the specified file does not exist.
    """
    coordinates = []
    try:
        with open(filename, 'r') as file:
            for line_number, line in enumerate(file):
                stripped_line = line.strip()
                # Skip empty lines
                if not stripped_line:
                    continue
                parts = stripped_line.split()
                if len(parts) < 3:
                    raise ValueError(f"Line {line_number} does not contain enough values.")
                try:
                    x = float(parts[0])
                    y = float(parts[1])
                    coordinates.append((line_number, (x, y)))
                except ValueError:
                    raise ValueError(f"Line {line_number} contains non-numeric values.")
                
        # Create a dictionary for easy lookup
        node_coord_dict = {node_id: (x, y) for node_id, (x, y) in coordinates}
        return node_coord_dict
    except FileNotFoundError:
        raise FileNotFoundError(f"The file '{filename}' does not exist.")
    
def get_xy_for_pms(time_on_pms_ring, total_time, node_coord_dict, node_ori, node_dest, t):
    x1, y1 = node_coord_dict[node_ori]
    x2, y2 = node_coord_dict[node_dest]
    alpha_zero = np.arctan2(y1 - y2, x1 - x2)
    r = np.sqrt((x2-x1)**2 + (y2-y1)**2)
    time_on_radial_line = total_time - time_on_pms_ring
    speed = r/time_on_radial_line
    alpha = speed * time_on_pms_ring / r
    if t <= time_on_pms_ring:
        # The aircraft is on PMS ring
        percentage = t/time_on_pms_ring
        x = x2 + np.cos(alpha_zero + alpha * percentage) * r
        y = y2 + np.sin(alpha_zero + alpha * percentage) * r
    else:
        # The aircraft is on radial line
        percentage = (t - time_on_pms_ring) / (total_time - time_on_pms_ring)
        xPMS = x2 + np.cos(alpha_zero + alpha) * r
        yPMS = y2 + np.sin(alpha_zero + alpha) * r
        x = xPMS - percentage * (xPMS - x2)
        y = yPMS - percentage * (yPMS - y2)
    return x, y

def get_filename_without_extension(filepath):
    """
    Extracts the filename without extension from a given filepath.
    
    Parameters:
    - filepath (str): The full path or just the filename.
    
    Returns:
    - str: The filename without the extension.
    
    Example:
    >>> get_filename_without_extension('path/to/my.complex.file.name.txt')
    'my.complex.file.name'
    """
    # Split the filepath into directory path and filename
    *_, filename = filepath.replace('\\', '/').split('/')
    
    # Split the filename by '.' and join all parts except the last one
    return '.'.join(filename.split('.')[:-1])

if __name__ == "__main__":

    # link_events_file_name = 'PMSeliminate0150reducedfilteredSAREX_link_travel_time.csv'
    link_events_file_name = 'STDeliminate0150reducedfilteredSAREX_link_travel_time.csv'
    # node_coord_file_name = 'DATA/SHEN_ZHEN/ZGSZ_PMS.nodes'
    node_coord_file_name = 'DATA/SHEN_ZHEN/ZGSZ_STD.nodes'
    file_name_without_extension = get_filename_without_extension(link_events_file_name)
    animation_file_name = file_name_without_extension + '.mp4'


    df = pd.read_csv(link_events_file_name)
    node_coord_dict = read_node_coordinates(node_coord_file_name)
        
    # Convert time columns to float if not already
    df['tOri'] = df['tOri'].astype(float)
    df['tDest'] = df['tDest'].astype(float)

    # Group the data by flightNumber
    flights = {}
    for flight_num, flight_data in df.groupby('flightNumber'):
        # Sort the segments by tOri
        sorted_segments = flight_data.sort_values('tOri')
        flights[flight_num] = sorted_segments.to_dict('records')


    # Define the time range for the animation
    # You might want to set this based on your data
    min_time = min(df['tOri'])
    max_time = max(df['tDest'])

    # Define time steps (e.g., every second)
    time_step = 10  # Adjust as needed
    # times = np.arange(min_time, max_time, time_step)
    times = np.arange(12 * 3600, 16 * 3600, time_step)

    # Initialize the plot
    fig, ax = plt.subplots(figsize=(10, 8))

    # Plot the nodes
    for node_id, (x, y) in node_coord_dict.items():
        ax.plot(x, y, 'ko')  # black circles
        ax.text(x + 1, y + 1, str(node_id), fontsize=9)

    # Set plot limits based on node coordinates
    all_x = [coord[0] for coord in node_coord_dict.values()]
    all_y = [coord[1] for coord in node_coord_dict.values()]
    ax.set_xlim(min(all_x) - 10, max(all_x) + 10)
    ax.set_ylim(min(all_y) - 10, max(all_y) + 10)
    ax.set_xlabel('X Coordinate')
    ax.set_ylabel('Y Coordinate')
    ax.set_title('Aircraft Positions Animation')

    # Initialize aircraft scatter plot
    scat = ax.scatter([], [], facecolors='none', edgecolors='red', s=5**2 * 4, linewidths=1, marker='o', label='Aircraft')

    # Optional: Add legend
    ax.legend()

    def interpolate_position(segment, current_time):
        """
        Given a segment dict and current_time, interpolate the position.
        """
        t_ori = segment['tOri']
        t_dest = segment['tDest']
        node_ori = segment['nodeOri']
        node_dest = segment['nodeDest']
        time_on_pms_ring = segment['timeOnPMSRing']
        travel_time = segment['travelTime']
        link_type = segment['linkType']

        if t_dest == t_ori:
            return node_coord_dict[node_ori]
        
        if link_type == 'pms':
            return get_xy_for_pms(time_on_pms_ring, travel_time, node_coord_dict, node_ori, node_dest, current_time - t_ori)
        else:
            fraction = (current_time - t_ori) / (t_dest - t_ori)
            fraction = np.clip(fraction, 0, 1)  # Ensure fraction is between 0 and 1
            
            x1, y1 = node_coord_dict[node_ori]
            x2, y2 = node_coord_dict[node_dest]
            x = x1 + (x2 - x1) * fraction
            y = y1 + (y2 - y1) * fraction
            return (x, y)

    def update(frame_time):
        """
        Update function for each frame.
        """
        positions = []
        for flight_num, segments in flights.items():
            # Find the current segment for this flight
            current_segment = None
            for segment in segments:
                if segment['tOri'] <= frame_time < segment['tDest']:
                    current_segment = segment
                    break
            if current_segment:
                pos = interpolate_position(current_segment, frame_time)
                positions.append(pos)
            else:
                # Optionally, handle flights that have ended or not yet started
                pass

        
        
        if positions:
            xs, ys = zip(*positions)
            scat.set_offsets(np.c_[xs, ys])
        else:
            scat.set_offsets(np.empty((0, 2)))
        
        # Count conflicts
        n_conflicts = 0
        for i in range(len(positions)):
            for j in range(i+1, len(positions)):
                x1, y1 = positions[i]
                x2, y2 = positions[j]
                distance = np.sqrt((x2-x1)**2 + (y2-y1)**2)
                if distance < 3:
                    n_conflicts += 1

        # Total aircraft    
        n_aircraft = len(positions)
        
        ax.set_title(f'Time: {frame_time:.0f} | Conflicts: {n_conflicts} | Total Aircraft: {n_aircraft}')
        return scat,

    # Create the animation
    ani = animation.FuncAnimation(
        fig, update, frames=times, interval=100, blit=True, repeat=False
    )

    # To save the animation, uncomment the following line
    ani.save(animation_file_name, writer='ffmpeg', fps=10)

    plt.show()
