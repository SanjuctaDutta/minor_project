import pandas as pd
import os

# Load your CSV file
csv_file = 'merged_file (2).csv'  # Replace with your CSV file path
df = pd.read_csv(csv_file)

# Function to extract names based on the role
def extract_names_by_role(crew_info, role):
    if not pd.isna(crew_info):
        crew_items = [item.strip() for item in crew_info.split(",")]
        names = [item.split(":")[1].strip() for item in crew_items if item.split(":")[0].strip() == role]
        return ", ".join(names)
    else:
        return ''

# List of roles
roles = ["Director", "Choreographer"]  # Add the roles you want

# Loop through the roles and create columns for each
for role in roles:
    column_name = f'{role}s'  # Example: "Directors", "Choreographers"
    df[column_name] = df['crew'].apply(lambda x: extract_names_by_role(x, role))

# Print the DataFrame to see the result
print(df[['title_x', 'Directors', 'Choreographers']])

# Save the DataFrame with the new columns to a new CSV file
df.to_csv('updated_file.csv', index=False)

