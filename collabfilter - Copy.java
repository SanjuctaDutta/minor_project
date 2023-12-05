import java.util.*;

public class collabfilter{

        private static Map<String, Map<String, Double>> dataset;

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
        List<String> rankingsList = new ArrayList<>();

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

    public static void main(String[] args) {
        dataset = new HashMap<>();

        dataset.put("Anisha Gupta", Map.of(
                "Lady in the Water", 2.5,
                "Snakes on a Plane", 3.5,
                "Just My Luck", 3.0,
                "Superman Returns", 3.5,
                "You, Me and Dupree", 2.5,
                "The Night Listener", 3.0,
                "Avatar",4.0
        ));

        dataset.put("Sanjucta Dutta", Map.of(
                "Lady in the Water", 3.0,
                "Snakes on a Plane", 3.5,
                "Just My Luck", 1.5,
                "Superman Returns", 5.0,
                "The Night Listener", 3.0,
                "You, Me and Dupree", 3.5
        ));

        dataset.put("Bharti Kohli", Map.of(
            "Lady in the Water", 2.5,
                "Snakes on a Plane", 3.5,
                "Just My Luck", 3.0,
                "Superman Returns", 3.5,
                "You, Me and Dupree", 2.5,
                "The Night Listener", 3.0
        ));


        List<String> recommendations = userRecommendations("Sanjucta Dutta");
        System.out.println(recommendations);
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
}
