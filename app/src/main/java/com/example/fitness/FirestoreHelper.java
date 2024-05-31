package com.example.fitness;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class FirestoreHelper {
    private static final String COLLECTION_NAME = "steps";
    private FirebaseFirestore db;

    public FirestoreHelper() {
        db = FirebaseFirestore.getInstance();
    }

    public Task<DocumentReference> insertSteps(int steps) {
        Map<String, Object> data = new HashMap<>();
        data.put("steps", steps);
        return db.collection(COLLECTION_NAME).add(data);
    }

    public CollectionReference getStepsCollection() {
        return db.collection(COLLECTION_NAME);
    }
}
