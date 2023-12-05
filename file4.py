import pandas as pd

# Load your CSV file
csv_file = 'merged_file (2).csv'  # Replace with your CSV file path
df = pd.read_csv(csv_file)

# Extract job names from the 'crew' column
def extract_job_names(row):
    if not pd.isna(row):
        job_names = [item.split(":")[0].strip() for item in row.split(",")]
        return ", ".join(job_names)
    return ""

df['job_names'] = df['crew'].apply(extract_job_names)
jobs=  df['crew'].apply(extract_job_names)
def extract_and_remove_duplicates(row):
    if not pd.isna(row):
        job_names = [item.split(":")[0].strip() for item in row.split(",")]
        unique_job_names = list(set(job_names))  # Remove duplicates
        return ", ".join(unique_job_names)
    return ""

df['unique_job_names'] = df['crew'].apply(extract_and_remove_duplicates)

unique_job_names_for_first_row = df.loc[0, 'unique_job_names']
print(unique_job_names_for_first_row)


# Display the DataFrame with job names
