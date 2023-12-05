import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.exceptions.CsvValidationException;
import java.net.URL;


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
import java.io.*;
import java.net.HttpURLConnection;
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
    public String Directors;
    public String cast;
    public String country;
    public String language;
    public String choreographer;

    public Movie(String name, String genre, String Directors, String cast, String country, String language,String choreographer) {
        this.name = name;
        this.genre = genre;
        this.Directors = Directors;
        this.cast = cast;
        this.country = country;
        this.language = language;
        this.choreographer= choreographer;
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


     public String getChoreographer() {
        return choreographer;
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

    public boolean matchesCriteria(String selectedGenre, String selectedDirectors, String selectedCast, String selectedCountry, String selectedLanguage,String selectedChoreographer) {
    boolean genreMatch = selectedGenre.isEmpty() || genre.toLowerCase().contains(selectedGenre.toLowerCase());
    boolean directorsMatch = selectedDirectors.isEmpty() || Directors.toLowerCase().contains(selectedDirectors.toLowerCase());
    boolean castMatch = selectedCast.isEmpty() || cast.toLowerCase().contains(selectedCast.toLowerCase());
    boolean countryMatch = selectedCountry.isEmpty() || selectedCountry.equalsIgnoreCase(country);
    boolean choreographerMatch = selectedChoreographer.isEmpty() || choreographer.toLowerCase().contains(selectedChoreographer.toLowerCase());
    boolean languageMatch = selectedLanguage.isEmpty() || selectedLanguage.equalsIgnoreCase(language);
        System.out.println("Genre Match: " + genreMatch);
    System.out.println("Directors Match: " + directorsMatch);
    System.out.println("Cast Match: " + castMatch);
    System.out.println("Country Match: " + countryMatch);
    System.out.println("Choreographer Match: " + choreographerMatch);
    System.out.println("Language Match: " + languageMatch);
    boolean overallMatch = genreMatch && directorsMatch && castMatch && countryMatch && choreographerMatch && languageMatch;

    System.out.println("Overall Match: " + overallMatch);
        return overallMatch;
    }
}

public class k5 {
    static class DataHandler implements HttpHandler {
        private final List<Movie> movieDatabase;

        public DataHandler(List<Movie> movieDatabase) {
            this.movieDatabase = movieDatabase;
        }

        @Override
        public void handle(HttpExchange exchange) throws IOException {
            System.out.println("hey sanjucta");
            if ("POST".equals(exchange.getRequestMethod())) {
                try (InputStreamReader isr = new InputStreamReader(exchange.getRequestBody());
                     BufferedReader br = new BufferedReader(isr)) {
                    Map<String, String> data = new HashMap<>();
                    StringBuilder responseBuilder = new StringBuilder();

                    StringBuilder requestBody = new StringBuilder();
                    String line;
                    while ((line = br.readLine()) != null) {
                        System.out.println("Received data line: " + line);
                        requestBody.append(line);
                    }
        
                    // Split the query string from the body (everything after the '?')
                    String queryString = requestBody.toString();
                    String[] queryParams = queryString.split("&");
        
                    for (String param : queryParams) {
                        String[] keyValue = param.split("=");
                        if (keyValue.length == 2) {
                            String key = keyValue[0];
                            String value = keyValue[1];
                            data.put(key, value);
                        }
                    }
                    for (Map.Entry<String, String> entry : data.entrySet()) {
                        String key = entry.getKey();
                        String value = entry.getValue();
                        System.out.println("Key: " + key + ", Value: " + value);
                    }
                    
                 
   
                    // Access individual pieces of data
                    String selectedGenre = data.get("genre");
                    String selectedLanguage = data.get("language");
                    String selectedCountry = data.get("country");
                    String selectedChoreographer=data.get("choreographer");
                    String selectedDirectors=data.get("director");
                    String selectedCast =data.get("actor");
                    System.out.println("hellobuddies");
                    System.out.println("Selected Criteria:");
                    System.out.println("Genre: " + selectedGenre);
                    System.out.println("Directors: " + selectedDirectors);
                    System.out.println("Cast: " + selectedCast);
                    System.out.println("Country: " + selectedCountry);
                    System.out.println("Language: " + selectedLanguage);
                    System.out.println("Choreographer: " + selectedChoreographer);
                    // Add more data variables as needed

                    boolean movieFound = false;
                    boolean relaxedCriteria = false;

                    // Find movies that match the criteria
                    for (Movie movie : movieDatabase) {
                        if (movie.matchesCriteria(selectedGenre,selectedDirectors,selectedCast,selectedCountry,selectedLanguage,selectedChoreographer)) {
                            String recommendedMovie = "Recommended Movie (Relaxed Criteria): " + movie.getName();
                            responseBuilder.append(recommendedMovie).append("\n"); // Append the recommendation to the response
                            System.out.println("Recommended Movie: " + movie.getName());
                            
                        }
                    }

                    // Relax the criteria progressively
                    if (!movieFound) {
                       
                        for (int relaxedCount = 1; relaxedCount <= 6; relaxedCount++) {
                            for (int combination = 0; combination <= 63; combination++) {
                                System.out.println("Relaxed Count: " + relaxedCount + ", Combination: " + combination);
                                
                                String selected_genre = selectedGenre;
                                String selected_directors = selectedDirectors;
                                String selected_cast = selectedCast;
                                String selected_country = selectedCountry;
                                String selected_language = selectedLanguage;
                                String selected_choreographer = selectedChoreographer;
                                if ((combination & 1) == 1) {
                                    selected_genre= ""; // Relax the genre criteria
                                }
                        
                                if ((combination & 2) == 2) {
                                    selected_directors = ""; // Relax the directors criteria
                                }
                        
                                if ((combination & 4) == 4) {
                                    selected_cast = ""; // Relax the cast criteria
                                }
                        
                                if ((combination & 8) == 8) {
                                    selected_country = ""; // Relax the country criteria
                                }
                        
                                if ((combination & 16) == 16) {
                                    selected_language = ""; // Relax the language criteria
                                }
                        
                                if ((combination & 32) == 32) {
                                    selected_choreographer= ""; // Relax the choreographer criteria
                                }
                                System.out.println("Criteria: Genre=" + selected_genre + ", Directors=" + selected_directors + ", Cast=" + selected_cast
                                + ", Country=" + selected_country + ", Language=" + selected_language + ", Choreographer=" + selected_choreographer);
                                    for (Movie movie : movieDatabase) {
                                    //   System.out.println("Checking movie: ");
                                        if (movie.matchesCriteria(selected_genre, selected_directors, selected_cast, selected_country, selected_language, selected_choreographer)) {
                                            System.out.println("Recommended Movie"+ (relaxedCount)+"combination"+combination+": " + movie.getName());
                                            String recommendedMovie = "Recommended Movie (Relaxed Criteria): " + movie.getName();
                                            responseBuilder.append(recommendedMovie).append("\n"); // Append the recommendation to the response
                                            //  movieFound = true;
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
                    System.out.println("process completed");
                    String responseMessage = responseBuilder.toString();
                    try {
                        String flaskUrl = "http://127.0.0.1:5000/receive_processed_data"; // Replace with your Flask service URL
                        URL url = new URL(flaskUrl);
                        System.out.println("Before making the HTTP request to Flask.");
                        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    
                        connection.setRequestMethod("POST");
                        connection.setDoOutput(true);
                        connection.setRequestProperty("Content-Type", "application/json");

                        System.out.println("Before making the HTTP request to Flask. 2");
                        // Get the output stream for the connection
                        // Get the response code from Flask (if needed)
                        
                        OutputStream os = connection.getOutputStream();
                        os.write(responseMessage.getBytes(StandardCharsets.UTF_8));
                        os.flush();
                        os.close();
                    
                        int responseCode = connection.getResponseCode();
                        System.out.println("HTTP Response Code from Flask: " + responseCode);
                    
                        connection.disconnect();
                        System.out.println("Response sent back to Flask.");
                    } catch (IOException e) {
                        e.printStackTrace();
                        System.out.println("Exception caught: " + e.getMessage());
                        exchange.sendResponseHeaders(500, 0); // Internal Server Error
                    }
                    
                    System.out.println("After the try-catch block.");
                    

            exchange.sendResponseHeaders(200, responseMessage.getBytes(StandardCharsets.UTF_8).length);
            OutputStream os = exchange.getResponseBody();
            os.write(responseMessage.getBytes(StandardCharsets.UTF_8));
            os.close();
                    

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
        int port = 8081;
        HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);

        // Load your movie database
        List<Movie> movieDatabase = loadMovieDatabase();

        // Define a context for the '/receive_data' endpoint
        server.createContext("/receive_data", new DataHandler(movieDatabase));

        // Start the server
        server.start();
        System.out.println("Server is running on port " + port);
    }

    private static List<Movie> loadMovieDatabase() {
        List<Movie> movieDatabase = new ArrayList<>();

        try (CSVReader csvReader = new CSVReaderBuilder(new FileReader("updated_file.csv")).withSkipLines(1).build()) {
            String[] row;
            while ((row = csvReader.readNext()) != null) {
                String name = row[1]; // Assumes the second column is "title_x"
                String genre = row[4]; // Assumes the fifth column is "genres"
                String Directors = row[9]; // Assumes the third column is "Directors"
                String cast = row[3]; // Assumes the fourth column is "cast_names"
                String country = row[7]; // Assumes the ninth column is "production_countries"
                String language = row[8]; // Assumes the seventh column is "original_language"
                String choreographer=row[10];
                movieDatabase.add(new Movie(name, genre, Directors, cast, country, language,choreographer));
                
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
