import pandas as pd
import json
import os

# Load your CSV file
csv_file = 'merged_file (2).csv'  # Replace with your CSV file path
df = pd.read_csv(csv_file)

def extract_names_by_role(row, role):
    if not pd.isna(row):
        crew_items = [item.strip() for item in row.split(",")]
        names = [item.split(":")[1].strip() for item in crew_items if len(item.split(":")) == 2 and item.split(":")[0].strip() == role]
        return names

# List of roles
roles = ["Sound Effects Editor", "Production Design", "Set Designer", "Best Boy Electric", "Producer",
         "Hairstylist", "Animation Director", "Modeling", "Publicist", "Original Music Composer", "Visual Effects Producer",
         "Costume Supervisor", "Music Editor", "Production Supervisor", "Transportation Coordinator", "Dialect Coach",
         "Production Manager", "Still Photographer", "Casting", "Post Production Supervisor", "Sound Re-Recording Mixer",
         "Sound Designer", "Director", "Writer", "Digital Intermediate", "Director of Photography", "Set Decoration",
         "CG Supervisor", "Set Costumer", "Construction Coordinator", "Conceptual Design", "Visual Effects Art Director",
         "Choreographer", "Stunt Coordinator", "Steadicam Operator", "Assistant Art Director", "Lighting Technician",
         "Special Effects Coordinator", "Camera Operator", "Supervising Art Director", "Executive Producer", "Screenplay",
         "Dialogue Editor", "Makeup Artist", "Visual Effects Supervisor", "Art Direction", "Editor", "Visual Effects Editor",
         "Supervising Sound Editor", "Foley", "Art Department Manager", "Makeup Department Head", "Lighting Artist",
         "Costume Design", "Motion Capture Artist", "Art Department Coordinator", "Stunts"]

# Define the output directory for JSON files
output_directory = 'role_json_files/'

# Create the output directory if it doesn't exist
os.makedirs(output_directory, exist_ok=True)

# Loop through the roles and create JSON files
for role in roles:
    role_column_name = f'names_of_{role}s'
    df[role_column_name] = df['crew'].apply(lambda row: extract_names_by_role(row, role))
    role_names = df[role_column_name]

    # Flatten the list and remove empty values
    role_names = role_names.explode().dropna()

    role_json = role_names.to_json(orient='records')

    # Define the output JSON file path
    output_json_file = f'{output_directory}{role}.json'

    # Write the JSON object to a file
    with open(output_json_file, 'w') as json_file:
        json_file.write(role_json)

    print(f"JSON file '{output_json_file}' created for role: {role}")
