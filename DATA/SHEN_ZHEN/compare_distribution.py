import matplotlib.pyplot as plt
import numpy as np
from collections import defaultdict

def load_flight_data(filename):
    flight_data = defaultdict(lambda: defaultdict(int))
    with open(filename, 'r') as file:
        next(file)  # Skip the header line
        for line in file:
            parts = line.strip().split()
            if len(parts) >= 3:
                entry_point = int(parts[1])
                entry_time = int(parts[2])
                hour = entry_time // 3600
                flight_data[entry_point][hour] += 1
    return flight_data

# Load data from all three files
baseline_data = load_flight_data('/Users/apple/Desktop/SHEN_ZHEN Code Developement/Aft 1st Revision by Pr.D/SOFT_LIWEI_3_JavaDemandScalerR1_Run!=demands/DATA/SHEN_ZHEN/ZGSZ_PMS_eliminate0.flights')
percent150_data = load_flight_data('/Users/apple/Desktop/SHEN_ZHEN Code Developement/Aft 1st Revision by Pr.D/SOFT_LIWEI_3_JavaDemandScalerR1_Run!=demands/DATA/SHEN_ZHEN/ZGSZ_PMS_eliminate0_150%_reduced.flights')
doubled_data = load_flight_data('/Users/apple/Desktop/SHEN_ZHEN Code Developement/Aft 1st Revision by Pr.D/SOFT_LIWEI_3_JavaDemandScalerR1_Run!=demands/DATA/SHEN_ZHEN/ZGSZ_PMS_eliminate0_200%_reduced.flights')

# Get all unique entry points
entry_points = sorted(set(list(baseline_data.keys()) + list(percent150_data.keys()) + list(doubled_data.keys())))

# Create a grid of subplots (one for each entry point)
fig, axes = plt.subplots(len(entry_points), 1, figsize=(12, 2*len(entry_points)), sharex=True)
fig.suptitle('Distribution Comparison: baseline vs 150% vs doubled Flight Sets')

for i, entry_point in enumerate(entry_points):
    ax = axes[i]
    hours = range(24)
    
    baseline_counts = [baseline_data[entry_point][h] for h in hours]
    percent150_counts = [percent150_data[entry_point][h] for h in hours]
    doubled_counts = [doubled_data[entry_point][h] for h in hours]
    
    # Bar plots
    ax.bar(hours, baseline_counts, alpha=0.3, label='baseline', color='blue')
    ax.bar(hours, percent150_counts, alpha=0.3, label='150percent', color='red')
    ax.bar(hours, doubled_counts, alpha=0.3, label='Doubled', color='green')
    
    # Line plots
    ax.plot(hours, baseline_counts, color='blue', linewidth=2)
    ax.plot(hours, percent150_counts, color='red', linewidth=2)
    ax.plot(hours, doubled_counts, color='green', linewidth=2)
    
    ax.set_ylabel(f'Entry Point {entry_point}')
    ax.legend()

axes[-1].set_xlabel('Hour of Day')
plt.tight_layout()
plt.show()

# Print some basic statistics
print(f"baseline dataset: {sum(sum(counts.values()) for counts in baseline_data.values())} flights")
print(f"150% dataset: {sum(sum(counts.values()) for counts in percent150_data.values())} flights")
print(f"Doubled dataset: {sum(sum(counts.values()) for counts in doubled_data.values())} flights")