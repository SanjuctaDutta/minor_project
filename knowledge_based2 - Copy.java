import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.exceptions.CsvValidationException;

import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;

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

public class MovieRecommendationServer {
    static class DataHandler implements HttpHandler {
        private final List<Movie> movieDatabase;

        public DataHandler(List<Movie> movieDatabase) {
            this.movieDatabase = movieDatabase;
        }

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
                    String selectedGenre = data.get("genre");
                    String selectedLanguage = data.get("language");
                    String selectedCountry = data.get("country");
                    // Add more data variables as needed

                    boolean movieFound = false;
                    boolean relaxedCriteria = false;

                    // Find movies that match the criteria
                    for (Movie movie : movieDatabase) {
                        if (movie.matchesCriteria(selectedGenre, "", "", selectedCountry, selectedLanguage)) {
                            System.out.println("Recommended Movie: " + movie.getName());
                            movieFound = true;
                        }
                    }

                    // Relax the criteria progressively
                    if (!movieFound) {
                        for (int relaxedCount = 1; relaxedCount <= 5; relaxedCount++) {
                            for (int combination = 0; combination < 5; combination++) {
                                if (Integer.bitCount(combination) == relaxedCount) {
                                    selectedGenre = (combination & 1) == 1 ? "" : selectedGenre;
                                    // Add more logic for other variables as needed
                                    relaxedCriteria = true;

                                    for (Movie movie : movieDatabase) {
                                        if (movie.matchesCriteria(selectedGenre, "", "", selectedCountry, selectedLanguage)) {
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

        // Load your movie database
        List<Movie> movieDatabase = loadMovieDatabase();

        // Define a context for the '/receive_data' endpoint
        server.createContext("/receive_data", new DataHandler(movieDatabase));

        // Start the server
        server.start();
    }

    private static List<Movie> loadMovieDatabase() {
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

        return movieDatabase;
    }
}
