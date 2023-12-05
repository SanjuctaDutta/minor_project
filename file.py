import pandas as pd
import json

# Load the CSV file
csv_file = 'tmdb_5000_movies (1).csv'  # Replace with your CSV file path
df = pd.read_csv(csv_file)

# Define a function to extract country names from the JSON data
def extract_country_names(row):
    try:
        data = json.loads(row)
        return ', '.join(entry['name'] for entry in data)
    except json.JSONDecodeError:
        return ''
    
def extract_keyword_names(row):
    try:
        data = json.loads(row)
        return ', '.join(entry['name'] for entry in data)
    except json.JSONDecodeError:
        return ''
    
def extract_genres_names(row):
    try:
        data = json.loads(row)
        return ', '.join(entry['name'] for entry in data)
    except json.JSONDecodeError:
        return ''
    
def extract_language_names(row):
    try:
        data = json.loads(row)
        return ', '.join(entry['name'] for entry in data)
    except json.JSONDecodeError:
        return ''

# Apply the function to the 'production_countries' column
df['production_countries'] = df['production_countries'].apply(extract_country_names)

# Apply the function to the 'keywords' column
df['keywords'] = df['keywords'].apply(extract_keyword_names)

# Apply the function to the 'keywords' column
df['genres'] = df['genres'].apply(extract_genres_names)

# Apply the function to the 'keywords' column
df['spoken_languages'] = df['spoken_languages'].apply(extract_language_names)

# Save the modified data back to a new CSV file
df.to_csv('modified_file3.csv', index=False)  # Change the output file name if needed

