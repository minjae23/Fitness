package com.example.fitness;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.QuerySnapshot;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static org.junit.Assert.*;

@RunWith(AndroidJUnit4.class)
public class FirestoreHelperTest {
    private FirestoreHelper firestoreHelper;

    @Before
    public void setUp() {
        firestoreHelper = new FirestoreHelper();

        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                .setHost("10.0.2.2:8080")  // Use emulator's IP address for Android
                .setSslEnabled(false)
                .setPersistenceEnabled(false)
                .build();
        firestore.setFirestoreSettings(settings);
    }

    @Test
    public void testInsertAndRetrieveSteps() throws ExecutionException, InterruptedException, TimeoutException {
        int steps = 1000;

        // Insert steps
        Task<DocumentReference> insertTask = firestoreHelper.insertSteps(steps);
        Tasks.await(insertTask, 5, TimeUnit.SECONDS);

        // Retrieve steps
        CollectionReference stepsCollection = firestoreHelper.getStepsCollection();
        Task<QuerySnapshot> getTask = stepsCollection.get();
        QuerySnapshot querySnapshot = Tasks.await(getTask, 5, TimeUnit.SECONDS);

        boolean found = false;
        for (DocumentSnapshot document : querySnapshot.getDocuments()) {
            if (document.contains("steps") && document.getLong("steps") == steps) {
                found = true;
                break;
            }
        }

        assertTrue(found);
    }
}
