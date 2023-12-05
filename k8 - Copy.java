import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.*;
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
import java.net.URL;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.BufferedReader;
import java.nio.charset.StandardCharsets;


public class k8 {
    private static final String FILE_PATH = "C:\\Users\\Sweety\\OneDrive\\Desktop\\minor5\\your_output_file.csv";

    public static void main(String[] args) throws IOException {
        int port = 8082;
        HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);

        // Set up a context for handling requests
        server.createContext("/content_filtering", new DataHandler());

        // Start the server
        server.start();
    }

    static class DataHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            try (BufferedReader br = new BufferedReader(new FileReader(FILE_PATH))) {
                // Get the search term from the request body
                String search = new BufferedReader(new InputStreamReader(exchange.getRequestBody()))
                        .lines().reduce("", (accumulator, actual) -> accumulator + actual);

                // Process the search term
                // String movieName = getInput("Enter a movie name: ");
                List<String> genres = take(search, br);
                Map<String, Integer> matched = out(genres, br);
                
                // Your existing code for processing and printing matched movies
                printMatchedMovies(matched, search);

                // Send a response back to the client
                sendResponse(exchange, "Search term processed successfully!");
            } catch (IOException e) {
                e.printStackTrace();
                sendResponse(exchange, "Error processing search term: " + e.getMessage(), 500);
            }
        }

        // private static String getInput(String prompt) {
        //     System.out.print(prompt);
        //     BufferedReader br = new BufferedReader(new java.io.InputStreamReader(System.in));
        //     try {
        //         return br.readLine();
        //     } catch (IOException e) {
        //         return "";
        //     }
        // }

        private static List<String> take(String search, BufferedReader br) throws IOException {
            // Your existing code for reading and processing genres
            String movieName = search.replace("search=", "");
            List<String> genres = new ArrayList<>();
            String line;
            
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length >= 2 && parts[1].equals(movieName)) {
                    String genre = parts[2].substring(1, parts[2].length() - 1);
                    String[] genreTokens = genre.split("\\.");
    
                    for (String token : genreTokens) {
                        token = token.trim();
                        genres.add(token);
                    }
                    break;
                }
            }
    
            if (genres.isEmpty()) {
                System.out.println("Movie not found in the dataset.");
            } else {
                System.out.println("Tags for the movie " + movieName + ":");
                for (String genre : genres) {
                    System.out.print(genre+", ");
                }
            }
    
            return genres;
        }

        private static Map<String, Integer> out(List<String> genres, BufferedReader br) throws IOException {
            // Your existing code for processing and counting matching genres
            Map<String, Integer> matchingMovies = new HashMap<>();
            String line;
    
            while ((line = br.readLine()) != null) {
                String[] tokens = line.split(",");
                if (tokens.length >= 2) {
                    String genreData = tokens[2].replaceAll("[{} ]", "");
                    String[] movieGenres = genreData.split("\\.");
    
                    int countMatchingGenres = 0;
    
                    for (String genre : movieGenres) {
                        if (genres.contains(genre)) {
                            countMatchingGenres++;
                        }
                    }
    
                    if (countMatchingGenres > 0) {
                        matchingMovies.put(tokens[1], countMatchingGenres);
                    }
                }
            }
    
            return matchingMovies;
        }
        }

        private static void printMatchedMovies(Map<String, Integer> matched, String movieName) {
            StringBuilder responseBuilder = new StringBuilder();
            System.out.println("Matched movies for " + movieName + ":");
            for (Map.Entry<String, Integer> entry : matched.entrySet()) {
                responseBuilder.append(entry.getKey()).append(", ");
                System.out.println("Movie: " + entry.getKey() + ", Matching Tags: " + entry.getValue());
            }
            sendDataToFlask(responseBuilder);
        }
        private static void sendDataToFlask(StringBuilder responseBuilder) {
            try {
                String flaskUrl = "http://127.0.0.1:5000/receive_processed_data2"; // Replace with your Flask service URL
                URL url = new URL(flaskUrl);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        
                connection.setRequestMethod("POST");
                connection.setDoOutput(true);
                connection.setRequestProperty("Content-Type", "application/json");
        
                // Get the output stream for the connection
                try (OutputStream os = connection.getOutputStream()) {
                    os.write(responseBuilder.toString().getBytes(StandardCharsets.UTF_8));
                    os.flush();
                }
        
                int responseCode = connection.getResponseCode();
                System.out.println("HTTP Response Code from Flask: " + responseCode);
        
                connection.disconnect();
                System.out.println("Response sent back to Flask.");
            } catch (IOException e) {
                e.printStackTrace();
                System.out.println("Exception caught: " + e.getMessage());
            }
        }
        private static void sendResponse(HttpExchange exchange, String response) throws IOException {
            sendResponse(exchange, response, 200);
        }

        private static void sendResponse(HttpExchange exchange, String response, int statusCode) throws IOException {
            // Send the response back to the client
            exchange.sendResponseHeaders(statusCode, response.length());
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(response.getBytes());
            }
        }
    }

