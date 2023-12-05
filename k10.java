import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.net.URL;
import java.net.URLDecoder;
public class k10 {

    public static final String FILE_PATH = "C:\\Users\\Sweety\\OneDrive\\Desktop\\minor5\\ratings.json";
    private static Map<String, Map<String, Double>> dataset;

    // Load dataset from a JSON file
    private static void loadDatasetFromJson(String filePath) {
        JSONParser parser = new JSONParser();
        System.out.println(filePath);
        

        try (FileReader reader = new FileReader(filePath)) {
            JSONObject jsonData = (JSONObject) parser.parse(reader);
            dataset = new HashMap<>();

            for (Object userObj : jsonData.keySet()) {
                String user = (String) userObj;
                JSONObject movieRatings = (JSONObject) jsonData.get(user);

                Map<String, Double> ratings = new HashMap<>();
                for (Object movieObj : movieRatings.keySet()) {
                    String movie = (String) movieObj;
                    double rating = (double) movieRatings.get(movie);
                    ratings.put(movie, rating);
                }

                dataset.put(user, ratings);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    

    public static double similarityScore(String person1, String person2) {
        Map<String, Integer> bothViewed = new HashMap<>();

        for (String item : dataset.get(person1).keySet()) {
            if (dataset.get(person2).containsKey(item)) {
                bothViewed.put(item, 1);
            }
        }

        if (bothViewed.size() == 0) {
            return 0;
        }

        double sumOfEuclideanDistance = 0;

        for (String item : bothViewed.keySet()) {
            sumOfEuclideanDistance += Math.pow(dataset.get(person1).get(item) - dataset.get(person2).get(item), 2);
        }

        return 1 / (1 + Math.sqrt(sumOfEuclideanDistance));
    }

    public static double pearsonCorrelation(String person1, String person2) {
        Map<String, Integer> bothRated = new HashMap<>();

        for (String item : dataset.get(person1).keySet()) {
            if (dataset.get(person2).containsKey(item)) {
                bothRated.put(item, 1);
            }
        }

        int numberOfRatings = bothRated.size();

        if (numberOfRatings == 0) {
            return 0;
        }

        double person1PreferencesSum = bothRated.keySet().stream().mapToDouble(item -> dataset.get(person1).get(item)).sum();
        double person2PreferencesSum = bothRated.keySet().stream().mapToDouble(item -> dataset.get(person2).get(item)).sum();

        double person1SquarePreferencesSum = bothRated.keySet().stream().mapToDouble(item -> Math.pow(dataset.get(person1).get(item), 2)).sum();
        double person2SquarePreferencesSum = bothRated.keySet().stream().mapToDouble(item -> Math.pow(dataset.get(person2).get(item), 2)).sum();

        double productSumOfBothUsers = bothRated.keySet().stream().mapToDouble(item -> dataset.get(person1).get(item) * dataset.get(person2).get(item)).sum();

        double numeratorValue = productSumOfBothUsers - (person1PreferencesSum * person2PreferencesSum / numberOfRatings);
        double denominatorValue = Math.sqrt(
                (person1SquarePreferencesSum - Math.pow(person1PreferencesSum, 2) / numberOfRatings) *
                        (person2SquarePreferencesSum - Math.pow(person2PreferencesSum, 2) / numberOfRatings)
        );

        if (denominatorValue == 0) {
            return 0;
        } else {
            return numeratorValue / denominatorValue;
        }
    }

    public static List<String> mostSimilarUsers(String person, int numberOfUsers) {
        List<Pair<Double, String>> scores = new ArrayList<>();

        for (String otherPerson : dataset.keySet()) {
            if (!otherPerson.equals(person)) {
                double similarity = pearsonCorrelation(person, otherPerson);
                scores.add(new Pair<>(similarity, otherPerson));
            }
        }

        scores.sort(Comparator.comparing(Pair::getKey));
        Collections.reverse(scores);

        List<String> similarPersons = new ArrayList<>();
        for (int i = 0; i < numberOfUsers && i < scores.size(); i++) {
            similarPersons.add(scores.get(i).getValue());
        }

        return similarPersons;
    }

    public static List<String> userRecommendations(String person) {
        Map<String, Double> totals = new HashMap<>();
        Map<String, Double> simSums = new HashMap<>();

        for (String other : dataset.keySet()) {
            if (other.equals(person)) {
                continue;
            }

            double similarity = pearsonCorrelation(person, other);

            if (similarity <= 0) {
                continue;
            }

            for (String item : dataset.get(other).keySet()) {
                if (!dataset.get(person).containsKey(item) || dataset.get(person).get(item) == 0) {
                    totals.putIfAbsent(item, 0.0);
                    totals.put(item, totals.get(item) + dataset.get(other).get(item) * similarity);

                    simSums.putIfAbsent(item, 0.0);
                    simSums.put(item, simSums.get(item) + similarity);
                }
            }
        }

        List<Pair<Double, String>> rankings = new ArrayList<>();

        for (String item : totals.keySet()) {
            rankings.add(new Pair<>(totals.get(item) / simSums.get(item), item));
        }

        rankings.sort(Comparator.comparing(Pair::getKey));
        Collections.reverse(rankings);

        List<String> recommendationsList = new ArrayList<>();
        for (Pair<Double, String> ranking : rankings) {
            recommendationsList.add(ranking.getValue());
        }

        return recommendationsList;
    }

    // Helper class for Pair
    static class Pair<K, V> {
        private final K key;
        private final V value;

        public Pair(K key, V value) {
            this.key = key;
            this.value = value;
        }

        public K getKey() {
            return key;
        }

        public V getValue() {
            return value;
        }
    }

    static class DataHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            try {
                // Extract the search term from the request
                // String search = exchange.getRequestURI().getQuery();
                try (BufferedReader br = new BufferedReader(new FileReader(FILE_PATH))) {
                    // Get the search term from the request body
                    String search = new BufferedReader(new InputStreamReader(exchange.getRequestBody()))
                            .lines().reduce("", (accumulator, actual) -> accumulator + actual);
                    search = URLDecoder.decode(search, "UTF-8");
                    String[] parts = search.split("=");
if (parts.length == 2) {
    search = parts[1].trim();
}
                    System.out.println(search);

                // Your recommendation logic here based on the search term
                List<String> recommendations = userRecommendations(search);
                System.out.println(recommendations);
                

                String jsonRecommendations = convertRecommendationsToJson(recommendations);
                System.out.println(jsonRecommendations);

                // Send recommendations to Flask
                sendDataToFlask(new StringBuilder(jsonRecommendations));

                // Send the recommendations as the response
                sendResponse(exchange, jsonRecommendations, 200);}
            } catch (IOException e) {
                e.printStackTrace();
                sendResponse(exchange, "Error processing search term: " + e.getMessage(), 500);
            }catch (Exception e) {
                e.printStackTrace();
                exchange.sendResponseHeaders(500, 0); // Internal Server Error
            }
        }
        private String convertRecommendationsToJson(List<String> recommendations) {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("recommendations", recommendations);
            return jsonObject.toJSONString();
        }

        private void sendDataToFlask(StringBuilder responseBuilder) {
            try {
                String flaskUrl = "http://127.0.0.1:5000/receive_processed_data3"; // Replace with your Flask service URL
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
     

        private void sendResponse(HttpExchange exchange, String response, int statusCode) throws IOException {
            // Send the response back to the client
            exchange.sendResponseHeaders(statusCode, response.length());
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(response.getBytes());
            }
        }
    }
    

    public static void main(String[] args) throws IOException {
        int port = 8083;
        HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);

        // Set up a context for handling requests
        server.createContext("/collaborative_filtering", new DataHandler());

        // Start the server
        server.start();
         System.out.println(" server is running on port 8083");

        // Load dataset from JSON file
        loadDatasetFromJson(FILE_PATH);
        
    }
}
