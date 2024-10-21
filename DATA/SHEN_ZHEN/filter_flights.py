input_file = '/Users/apple/Desktop/SHEN_ZHEN Code Developement/Aft 1st Revision by Pr.D/SOFT_LIWEI_3_JavaDemandScalerR1_Run!=demands/DATA/SHEN_ZHEN/ZGSZ_STD_eliminate0_150%_reduced.flights'
output_file = '/Users/apple/Desktop/SHEN_ZHEN Code Developement/Aft 1st Revision by Pr.D/SOFT_LIWEI_3_JavaDemandScalerR1_Run!=demands/DATA/SHEN_ZHEN/STD_eliminate0_150%_reduced_filteredSAREX.flights'

with open(input_file, 'r') as infile, open(output_file, 'w') as outfile:
    for line in infile:
        columns = line.split()
        if len(columns) > 1 and columns[1] == '1':
            outfile.write(line)

print(f"Filtered flights have been written to {output_file}")