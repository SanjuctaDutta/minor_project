import pandas as pd

# Load the first CSV file into a DataFrame
df1 = pd.read_csv('modified_file.csv')

# Load the second CSV file into another DataFrame
df2 = pd.read_csv('modified_file3.csv')

# Clean the 'id' column in both DataFrames to ensure it only contains valid integers
df1['id'] = pd.to_numeric(df1['id'], errors='coerce')
df2['id'] = pd.to_numeric(df2['id'], errors='coerce')

# Drop rows where 'id' is NaN (non-numeric values)
df1.dropna(subset=['id'], inplace=True)
df2.dropna(subset=['id'], inplace=True)

# Convert the 'id' column to 'int64' data type
df1['id'] = df1['id'].astype('int64')
df2['id'] = df2['id'].astype('int64')

# Merge the two DataFrames based on the 'id' column
merged_df = pd.merge(df1, df2, on='id', how='inner')
# 'how' parameter specifies how to merge: 'inner' keeps only common 'id' values

# Save the merged DataFrame to a new CSV file
merged_df.to_csv('merged_file.csv', index=False)

