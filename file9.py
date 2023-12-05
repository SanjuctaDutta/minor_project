import pandas as pd
import json

# Load your CSV file
csv_file = 'merged_file (2).csv'  # Replace with your CSV file path
df = pd.read_csv(csv_file)

# Extract unique names of crew members with a specific role
def extract_unique_names_by_role(row, role, unique_names_set):
    if not pd.isna(row):
        crew_items = [item.strip() for item in row.split(",")]
        for item in crew_items:
            parts = item.split(":")
            if len(parts) == 2:
                job, name = parts
                if job.strip() == role:
                    unique_names_set.add(name.strip())

# Define the role you're interested in (e.g., "Director")
role_of_interest = "Director"

# Create a set to store unique director names
unique_director_names = set()

# Apply the function to extract unique names and update the set
df['crew'].apply(lambda row: extract_unique_names_by_role(row, role_of_interest, unique_director_names))

# Convert the set to a list
unique_director_names_list = list(unique_director_names)

# Create a JSON object from the unique director names
unique_director_names_json = {"unique_director_names": unique_director_names_list}

# Define the output JSON file path
output_json_file = 'unique_directors.json'

# Write the JSON object to a file
with open(output_json_file, 'w') as json_file:
    json.dump(unique_director_names_json, json_file)

# Print the unique names of Directors
print("Unique Director Names:", unique_director_names_list)

# Confirm that the JSON file has been created
print(f"JSON file '{output_json_file}' created.")
