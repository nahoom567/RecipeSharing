package com.example.recipeproject;

import android.util.Log;
import org.simmetrics.StringMetric;
import org.simmetrics.metrics.StringMetrics;
import java.util.ArrayList;
import java.util.List;

public class SearchCorrector {
    private final List<String> database = new ArrayList<>();
    // this is the SIMILARITY_THRESHOLD which is the amount of similarity needed for
    // the names of recipes to be considered similar enough
    // I chose the number based on my experience to what is similar enough, but it is possible to
    // change it to be what fits your personal preferences
    private static final double SIMILARITY_THRESHOLD = 0.2;

    public SearchCorrector() {

    }

    // correcting the search query
    public String correctSearchQuery(String query) {
        String closestWord = null;
        double maxSimilarity = 0.0;

        StringMetric metric = StringMetrics.levenshtein(); // Changed to Levenshtein distance
        Log.d("SearchCorrector", "Starting search for query: " + query);

        for (String word : database) {
            double similarity = metric.compare(query, word);
            Log.d("SearchCorrector", "Comparing with word: " + word + ", Similarity: " + similarity);

            if (similarity > maxSimilarity && similarity >= SIMILARITY_THRESHOLD) {
                maxSimilarity = similarity;
                closestWord = word;
            }
        }

        if (closestWord != null) {
            // Return the closest word found in the database
            Log.d("SearchCorrector", "Closest word found: " + closestWord);
            return closestWord;
        } else {
            // Handle the case where no similar word is found
            Log.d("SearchCorrector", "No similar word found for query: " + query);
            return "No similar word found";
        }
    }

    // adding a word to the database
    public void addWordToDatabase(String word) {
        database.add(word);
        Log.d("SearchCorrector", "Added word to database: " + word);
    }
}
