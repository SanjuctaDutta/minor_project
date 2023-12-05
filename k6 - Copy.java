import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.exceptions.CsvValidationException;

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

class Movie {
    public String name;
    public String genre;
    private String Directors;
    private String cast;
    private String country;
    private String language;

    public Movie(String name, String genre, String Directors, String cast, String country, String language) {
        this.name = name;
        this.genre = genre;
        this.Directors = Directors;
        this.cast = cast;
        this.country = country;
        this.language = language;
    }

    public String getName() {
        return name;
    }

    public String getGenre() {
        return genre;
    }

    public String getDirectors() {
        return Directors;
    }

    public String getCast() {
        return cast;
    }

    public String getCountry() {
        return country;
    }

    public String getLanguage() {
        return language;
    }

    public boolean matchesCriteria(String selectedGenre, String selectedDirectors, String selectedCast, String selectedCountry, String selectedLanguage) {
        return (selectedGenre.isEmpty() || genre.contains(selectedGenre)) &&
                (selectedDirectors.isEmpty() || Directors.contains(selectedDirectors)) &&
                (selectedCast.isEmpty() || cast.contains(selectedCast)) &&
                (selectedCountry.isEmpty() || selectedCountry.equals(country)) &&
                (selectedLanguage.isEmpty() || selectedLanguage.equals(language));
    }
}

public class k6 {
    public static void main(String[] args) {
        // Load movie database from CSV file
        List<Movie> movieDatabase = loadMovieDatabaseFromCSV("updated_file.csv");

        // Display the first 3 movies in the database
        System.out.println("Movie Database (First 3 Movies):");
        for (int i = 0; i < Math.min(3, movieDatabase.size()); i++) {
            System.out.println(movieDatabase.get(i).name);
        }

        Scanner scanner = new Scanner(System.in);

        System.out.println("Welcome to the Movie Recommendation System!");
        System.out.print("Choose a genre (or leave empty for any): ");
        String selectedGenre = scanner.nextLine();
        System.out.print("Choose a Directors member (e.g., Director) name (or leave empty for any): ");
        String selectedDirectors = scanner.nextLine();
        System.out.print("Choose a casting (or leave empty for any): ");
        String selectedCast = scanner.nextLine();
        System.out.print("Choose a country (or leave empty for any): ");
        String selectedCountry = scanner.nextLine();
        System.out.print("Choose a language (or leave empty for any): ");
        String selectedLanguage = scanner.nextLine();

        boolean movieFound = false;
        boolean relaxedCriteria = false;

        // Find movies that match the criteria
        for (Movie movie : movieDatabase) {
            if (movie.matchesCriteria(selectedGenre, selectedDirectors, selectedCast, selectedCountry, selectedLanguage)) {
                System.out.println("Recommended Movie: " + movie.name);
                movieFound = true;
            }
        }

        // Relax the criteria progressively
        if (!movieFound) {
            for (int relaxedCount = 1; relaxedCount <= 5; relaxedCount++) {
                for (int combination = 0; combination < (1 << 5); combination++) {
                    if (Integer.bitCount(combination) == relaxedCount) {
                        String[] relaxedCriteriaArray = new String[5];
                        for (int i = 0; i < 5; i++) {
                            if ((combination & (1 << i)) != 0) {
                                relaxedCriteriaArray[i] = "";  // Relax the i-th criteria
                            }
                        }
                        relaxedCriteria = true;

                        for (Movie movie : movieDatabase) {
                            if (movie.matchesCriteria(
                                    relaxedCriteriaArray[0],
                                    relaxedCriteriaArray[1],
                                    relaxedCriteriaArray[2],
                                    relaxedCriteriaArray[3],
                                    relaxedCriteriaArray[4])) {
                                System.out.println("Recommended Movie (Relaxed Criteria): " + movie.getName());
                                movieFound = true;
                            }
                        }
                    }

                    if (movieFound) {
                        break;
                    }
                }

                if (movieFound) {
                    break;
                }
            }
        }

        if (!movieFound) {
            System.out.println("Sorry, no movie found based on the given criteria.");
        }

        if (!movieFound && relaxedCriteria) {
            System.out.println("No more recommendations based on relaxed criteria.");
        }

        scanner.close();
    }

    private static List<Movie> loadMovieDatabaseFromCSV(String csvFilePath) {
        List<Movie> movieDatabase = new ArrayList<>();

        try (CSVReader csvReader = new CSVReaderBuilder(new FileReader(csvFilePath)).withSkipLines(1).build()) {
            String[] row;
            while ((row = csvReader.readNext()) != null) {
                String name = row[1];
                String genre = row[4];
                String Directors = row[2];
                String cast = row[3];
                String country = row[7];
                String language = row[8];

                movieDatabase.add(new Movie(name, genre, Directors, cast, country, language));
            }
        } catch (IOException | CsvValidationException e) {
            e.printStackTrace();
        }

        return movieDatabase;
    }
}

