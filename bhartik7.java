import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
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

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.BufferedReader;
import java.nio.charset.StandardCharsets;

public class bhartik7 {
    public static void main(String[] args) {
        try {
            BufferedReader br = new BufferedReader(new FileReader("C:\\Users\\Sweety\\OneDrive\\Desktop\\minor5\\your_output_file.csv"));
            String movieName = getInput("Enter a movie name: ");
            List<String> genres = take(movieName, br);
            Map<String, Integer> matched = out(genres, br);
            br.close();

            List<Map.Entry<String, Integer>> sortedMovies = new ArrayList<>(matched.entrySet());
            sortedMovies.sort((a, b) -> b.getValue() - a.getValue());

            int count = 0;
            for (Map.Entry<String, Integer> entry : sortedMovies) {
                if (!entry.getKey().equals(movieName)) {
                    System.out.println("Movie: " + entry.getKey() + ", Matching Tags: " + entry.getValue());
                    count++;
                    if (count == 15) {
                        break;
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static String getInput(String prompt) {
        System.out.print(prompt);
        BufferedReader br = new BufferedReader(new java.io.InputStreamReader(System.in));
        try {
            return br.readLine();
        } catch (IOException e) {
            return "";
        }
    }

    private static List<String> take(String movieName, BufferedReader br) throws IOException {
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