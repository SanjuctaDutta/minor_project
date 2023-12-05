import pandas as pd
import json

# Replace 'your_csv_file.csv' with the actual path to your CSV file
csv_file_path = 'updated_file.csv'

# Read the CSV file into a DataFrame
df = pd.read_csv(csv_file_path)

# Replace 'column_name' with the actual name of the column you want to fetch unique values from
column_name = 'cast_names'

# Drop rows with NaN values in the specified column
df = df.dropna(subset=[column_name])

# Split genres in each row and create a list of all genres
all_cast = [cast.strip() for casts in df[column_name].str.split(',') for cast in casts]

# Get unique values from the list of all genres
unique_cast = set(all_cast)

# Create a dictionary with 'genres' as the key and the list of unique genres as the value
cast_data = {'cast': list(unique_cast)}

cast_json_file_path = 'cast_json_file.json'

# Write the dictionary to the JSON file
with open(cast_json_file_path, 'w') as json_file:
    json.dump(cast_data, json_file)

print("Unique cast Values in {}: {}".format(column_name, list(unique_cast)))
