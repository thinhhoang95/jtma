import random

# File paths
input_file = 'DATA/SHEN_ZHEN/ZGSZ_PMS.flights'
output_file = 'DATA/SHEN_ZHEN/ZGSZ_PMS_80%_reduced.flights'

# Read all lines from the input file
with open(input_file, 'r') as f:
    lines = f.readlines()

# Shuffle the lines
random.shuffle(lines)

# Calculate the number of lines to keep (80% of original)
num_lines_to_keep = int(len(lines) * 0.8)

# Select the first 80% of the shuffled lines
reduced_lines = lines[:num_lines_to_keep]

# Write the reduced set of lines to the output file
with open(output_file, 'w') as f:
    f.writelines(reduced_lines)

print(f"Original number of flights: {len(lines)}")
print(f"Reduced number of flights: {len(reduced_lines)}")
print(f"Reduced file saved as: {output_file}")