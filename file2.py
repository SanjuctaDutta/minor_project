import pandas as pd
import json

# Load the CSV file
csv_file = 'tmdb_5000_credits.csv'  # Replace with your CSV file path
df = pd.read_csv(csv_file)

# Define a function to extract cast names from the JSON data
def extract_names(cast_data):
    cast_list = json.loads(cast_data)
    names = [actor['name'] for actor in cast_list]
    return ', '.join(names)  # Convert the list of names to a comma-separated string

# Apply the function to the 'cast' column to create a new 'cast_names' column
df['cast_names'] = df['cast'].apply(extract_names)

# Define a function to extract crew job and name from the JSON data
def extract_crew_info(row):
    try:
        data = json.loads(row)
        return ', '.join(f"{entry['job']}: {entry['name']}" for entry in data)
    except json.JSONDecodeError:
        return ''

# Apply the function to the 'crew' column
df['crew'] = df['crew'].apply(extract_crew_info)





# Save the modified data back to a new CSV file
df.to_csv('modified_file.csv', index=False)  # Change the output file name if needed
