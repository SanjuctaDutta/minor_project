import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.exceptions.CsvValidationException;

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;
import java.io.*;
import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.BufferedReader;
import java.nio.charset.StandardCharsets;

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

public class knowledge_based3  {
      static class DataHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if ("POST".equals(exchange.getRequestMethod())) {
                try (InputStreamReader isr = new InputStreamReader(exchange.getRequestBody());
                     BufferedReader br = new BufferedReader(isr)) {
                    Map<String, String> data = new HashMap<>();

                    String line;
                    while ((line = br.readLine()) != null) {
                        // Split the data into key-value pairs based on a separator (e.g., comma)
                        String[] parts = line.split(",");
                        if (parts.length == 2) {
                            String key = parts[0].trim();
                            String value = parts[1].trim();
                            data.put(key, value);
                        }
                    }

                    // Access individual pieces of data
                    String genre = data.get("genre");
                    String language = data.get("language");
                    String country = data.get("country");
                    String keywords = data.get("keywords");
                    String actor = data.get("actor");
                    String director = data.get("director");
                    String choreographer = data.get("choreographer");

                    // Process and use the individual variables as needed
                    String response = "Received data: Genre=" + genre + ", Language=" + language + ", Country=" + country;

                    // Send a response if necessary
                    exchange.sendResponseHeaders(200, response.length());
                    exchange.getResponseBody().write(response.getBytes());
                } catch (IOException e) {
                    e.printStackTrace();
                    exchange.sendResponseHeaders(500, 0); // Internal Server Error
                }
            } else {
                exchange.sendResponseHeaders(405, 0); // Method Not Allowed
            }

            exchange.close();
        }
    }
    public static void main(String[] args) throws IOException {
        int port = 8080;
        HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);
        // Define a context for the '/receive_data' endpoint
        server.createContext("/receive_data", new DataHandler());

        // Start the server
        server.start();
        List<Movie> movieDatabase = new ArrayList<>();

        try (CSVReader csvReader = new CSVReaderBuilder(new FileReader("merged_file (2).csv")).withSkipLines(1).build()) {
            String[] row;
            while ((row = csvReader.readNext()) != null) {
                String name = row[1]; // Assumes the second column is "title_x"
                String genre = row[4]; // Assumes the fifth column is "genres"
                String Directors = row[2]; // Assumes the third column is "Directors"
                String cast = row[3]; // Assumes the fourth column is "cast_names"
                String country = row[7]; // Assumes the ninth column is "production_countries"
                String language = row[8]; // Assumes the seventh column is "original_language"

                movieDatabase.add(new Movie(name, genre, Directors, cast, country, language));
            }
        } catch (IOException | CsvValidationException e) {
            e.printStackTrace();
        }
        System.out.println("Movie Database (First 3 Movies):");
for (int i = 0; i < Math.min(3, movieDatabase.size()); i++) {
    System.out.println(movieDatabase.get(i).name);

}
// Add this code within your main method, after loading the movieDatabase.

if (movieDatabase.size() >= 2) {
    Movie firstMovie = movieDatabase.get(0);
    

    System.out.println("Checking the first movie:");
    if (firstMovie.matchesCriteria(firstMovie.genre, firstMovie.getDirectors(), firstMovie.getCast(), firstMovie.getCountry(), firstMovie.getLanguage())) {
        System.out.println("First movie matches the criteria.");
    } else {
        System.out.println("First movie does not match the criteria.");
    }

    
} else {
    System.out.println("There are less than two movies in the database.");
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
                
            }

            
        }



        // relax the criteria progressively
        if (!movieFound) {
            for (int relaxedCount = 1; relaxedCount <= 5; relaxedCount++) {
                for (int combination = 0; combination < 5; combination++) {
                    if (Integer.bitCount(combination) == relaxedCount) {
                        selectedGenre = (combination & 1) == 1 ? "" : selectedGenre;
                        selectedDirectors = (combination & 2) == 2 ? "" : selectedDirectors;
                        selectedCast = (combination & 4) == 4 ? "" : selectedCast;
                        selectedCountry = (combination & 8) == 8 ? "" : selectedCountry;
                        selectedLanguage = (combination & 16) == 16 ? "" : selectedLanguage;
                        relaxedCriteria = true;

                        for (Movie movie : movieDatabase) {
                            if (movie.matchesCriteria(selectedGenre, selectedDirectors, selectedCast, selectedCountry, selectedLanguage)) {
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
}
