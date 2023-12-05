import pandas as pd
import json

# Replace 'your_csv_file.csv' with the actual path to your CSV file
csv_file_path = 'updated_file.csv'

# Read the CSV file into a DataFrame
df = pd.read_csv(csv_file_path)

# Replace 'column_name' with the actual name of the column you want to fetch unique values from
column_name = 'Directors'

# Drop rows with NaN values in the specified column
df = df.dropna(subset=[column_name])

# Split genres in each row and create a list of all genres
all_director = [director.strip() for directors in df[column_name].str.split(',') for director in directors]

# Get unique values from the list of all genres
unique_director = set(all_director)

# Create a dictionary with 'genres' as the key and the list of unique genres as the value
director_data = {'director': list(unique_director)}

director_json_file_path = 'director_json_file.json'

# Write the dictionary to the JSON file
with open(director_json_file_path, 'w') as json_file:
    json.dump(director_data, json_file)

print("Unique director Values in {}: {}".format(column_name, list(unique_director)))
