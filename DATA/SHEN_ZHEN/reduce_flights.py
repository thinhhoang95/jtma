import random

# Read all lines from the file
with open('DATA/SHEN_ZHEN/ZGSZ_PMS_scaled_1.5.flights', 'r') as file:
    lines = file.readlines()

# Separate the header from the data
header = lines[0]
data = lines[1:]

# Shuffle the data lines
random.shuffle(data)

# Select the first 972 shuffled lines (973 total including header)
selected_lines = data[:973]

# Write the header and selected lines to the new file
with open('DATA/SHEN_ZHEN/Reduced_ZGSZ_PMS_scaled_1.5.flights', 'w') as file:
    file.write(header)
    file.writelines(selected_lines)

print(f"New file 'Reduced_ZGSZ_PMS_scaled_1.5.flights' created with 973 total lines (1 header + 973 data lines).")