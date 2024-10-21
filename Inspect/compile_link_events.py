import pandas as pd
import re

def parse_log_file(file_path):
    # Initialize a list to hold all parsed entries
    entries = []
    
    # Read the entire file content
    with open(file_path, 'r') as file:
        content = file.read()
    
    # Split the content into individual entries using '***' as the delimiter
    # The split may include empty strings, so we filter them out
    raw_entries = [entry.strip() for entry in content.split('***') if entry.strip()]
    
    # Define regex patterns for key-value extraction
    patterns = {
        'flightNumber': re.compile(r'Flight number:\s*(\d+)'),
        'callSign': re.compile(r'Callsign:\s*(\S+)'),
        'entryTime': re.compile(r'Entry time:\s*([0-9.]+)'),
        'speed_in': re.compile(r'Speed in:\s*([0-9.]+)'),
        'wtCat': re.compile(r'WTCat:\s*(\d+)'),
        'route': re.compile(r'route=\s*([0-9\s]+)'),
        'tOri': re.compile(r'tOri=([0-9.]+)'),
        'tDest': re.compile(r'tDest=([0-9.]+)'),
        'travelTime': re.compile(r'travelTime=([0-9.]+)'),
        'linkType': re.compile(r'linkType=(\w+)'),
        'nodeOri': re.compile(r'nodeOri=(\d+)'),
        'nodeDest': re.compile(r'nodeDest=(\d+)'),
        'timeOnPMSRing': re.compile(r'timeOnPMSRing=([0-9.]+)'),
    }
    
    # Iterate over each raw entry and extract the required fields
    for raw_entry in raw_entries:
        entry = {}
        
        # Extract simple key-value pairs
        for key, pattern in patterns.items():
            match = pattern.search(raw_entry)
            if match:
                entry[key] = match.group(1)
            else:
                entry[key] = None  # Assign None if the pattern is not found
        
        # Convert numeric fields to appropriate types
        numeric_fields = ['flightNumber', 'entryTime', 'speed_in', 'wtCat',
                          'tOri', 'tDest', 'travelTime', 'nodeOri', 'nodeDest', 'timeOnPMSRing']
        for field in numeric_fields:
            if entry[field] is not None:
                try:
                    # Convert to integer if the field represents an integer
                    if field in ['flightNumber', 'wtCat', 'nodeOri', 'nodeDest']:
                        entry[field] = int(entry[field])
                    else:
                        entry[field] = float(entry[field])
                except ValueError:
                    # If conversion fails, keep as string
                    pass
        
        # Convert the route to a single string
        if entry['route']:
            entry['route'] = ' '.join(entry['route'].split())
        
        # Append the parsed entry to the list
        entries.append(entry)
    
    # Create a DataFrame from the list of entries
    df = pd.DataFrame(entries, columns=[
        'flightNumber', 'callSign', 'entryTime', 'speed_in', 'wtCat',
        'route', 'nodeOri', 'tOri', 'nodeDest', 'tDest',
        'travelTime', 'linkType', 'timeOnPMSRing'
    ])
    
    return df

# Example usage
if __name__ == "__main__":
    # Path to your log file
    log_file_path = 'PMSeliminate0150reducedfilteredSAREX_link_travel_time.log'
    
    # Parse the log file and get the DataFrame
    df = parse_log_file(log_file_path)
    
    # Remove duplicates
    df = df.drop_duplicates()

    # Save to CSV
    df.to_csv('PMSeliminate0150reducedfilteredSAREX_link_travel_time.csv', index=False)
