package com.example.fitness;

import android.util.Log;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;

public class ExerciseRoutineUtils {
    private static final String TAG = "ExerciseRoutineUtils";
    private static FirebaseFirestore db = FirebaseFirestore.getInstance();
    public interface ExerciseRoutineCallback {
        void onCallback(List<Map<String, Object>> exerciseList);
    }

    public static void saveExerciseRoutine(String userId, String date, String exerciseName, double weight, int reps, int sets) {
        Map<String, Object> exerciseData = new HashMap<>();
        exerciseData.put("name", exerciseName);
        exerciseData.put("weight", weight);
        exerciseData.put("reps", reps);
        exerciseData.put("sets", sets);

        db.collection("users").document(userId)
                .collection("routines").document(date)
                .collection("exercises").add(exerciseData)
                .addOnSuccessListener(documentReference -> Log.d(TAG, "Exercise routine saved successfully"))
                .addOnFailureListener(e -> Log.w(TAG, "Error saving exercise routine", e));
    }

    public static void getExerciseRoutine(String userId, String date, final ExerciseRoutineCallback callback) {
        db.collection("users").document(userId)
                .collection("routines").document(date)
                .collection("exercises").get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        List<DocumentSnapshot> documents = task.getResult().getDocuments();
                        List<Map<String, Object>> exerciseList = new ArrayList<>();
                        for (DocumentSnapshot document : documents) {
                            Map<String, Object> exercise = new HashMap<>();
                            exercise.put("name", document.getString("name"));
                            exercise.put("weight", document.getDouble("weight"));
                            exercise.put("reps", document.getLong("reps"));
                            exercise.put("sets", document.getLong("sets"));
                            exerciseList.add(exercise);
                        }
                        callback.onCallback(exerciseList);
                    } else {
                        Log.w(TAG, "Error getting exercise routine", task.getException());
                    }
                });
    }
}
