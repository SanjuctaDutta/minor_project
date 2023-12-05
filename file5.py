import pandas as pd

# Load your CSV file
csv_file = 'merged_file (2).csv'  # Replace with your CSV file path
df = pd.read_csv(csv_file)

# Extract names of crew members with a specific role
def extract_names_by_role(row, role):
    if not pd.isna(row):
        crew_items = [item.strip() for item in row.split(",")]
        names = []
        for item in crew_items:
            parts = item.split(":")
            if len(parts) == 2:
                job, name = parts
                if job.strip() == role:
                    names.append(name.strip())
        return ", ".join(names)
    return ""

# Define the role you're interested in (e.g., "Director")
role_of_interest = "Director"



# Apply the function to create a new column with names of Directors
df[f'names_of_{role_of_interest}s'] = df['crew'].apply(lambda row: extract_names_by_role(row, role_of_interest))

# Access the names of Directors for all rows
director_names = df[f'names_of_{role_of_interest}s']

# Create a JSON object from the director names
director_names_json = director_names.to_json(orient='records')

# Define the output JSON file path
output_json_file = 'directors.json'

# Write the JSON object to a file
with open(output_json_file, 'w') as json_file:
    json_file.write(director_names_json)

# Print the names of Directors for all rows
print(director_names)

# Confirm that the JSON file has been created
print(f"JSON file '{output_json_file}' created.")

