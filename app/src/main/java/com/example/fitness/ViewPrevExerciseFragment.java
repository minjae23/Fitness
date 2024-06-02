package com.example.fitness;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class ViewPrevExerciseFragment extends Fragment {

    private static final String TAG = "ViewPrevExerciseFragment";
    private String userId;
    private String date;
    private LinearLayout exerciseListLayout;
    private TextView noDataTextView;
    private FirebaseFirestore db;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            userId = currentUser.getUid();
            db = FirebaseFirestore.getInstance();
            date = getCurrentDate();
            loadExerciseRoutine();
        } else {
            Log.w(TAG, "User is not signed in.");
        }
    }

    private void loadExerciseRoutine() {
        db.collection("users").document(userId).collection("exerciseSets").document(date)
                .collection("routines")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        QuerySnapshot querySnapshot = task.getResult();
                        if (querySnapshot.isEmpty()) {
                            noDataTextView.setVisibility(View.VISIBLE);
                        } else {
                            noDataTextView.setVisibility(View.GONE);
                            for (QueryDocumentSnapshot document : querySnapshot) {
                                Map<String, Object> exerciseSet = document.getData();
                                addExerciseSetToLayout(exerciseSet);
                            }
                        }
                    } else {
                        Log.w(TAG, "Error getting documents.", task.getException());
                    }
                });
    }

    private void addExerciseSetToLayout(Map<String, Object> exerciseSet) {
        View exerciseView = LayoutInflater.from(getContext()).inflate(R.layout.item_exercise, exerciseListLayout, false);

        TextView exerciseNameTextView = exerciseView.findViewById(R.id.exerciseNameTextView);
        TextView weightTextView = exerciseView.findViewById(R.id.weightTextView);
        TextView repsTextView = exerciseView.findViewById(R.id.repsTextView);
        TextView setsTextView = exerciseView.findViewById(R.id.setsTextView);

        String exerciseName = (String) exerciseSet.get("exerciseName");
        List<Map<String, Object>> sets = (List<Map<String, Object>>) exerciseSet.get("exerciseSets");

        StringBuilder setsString = new StringBuilder();
        for (Map<String, Object> set : sets) {
            setsString.append("Set ").append(set.get("setCount"))
                    .append(": ").append(set.get("weight"))
                    .append("kg x ").append(set.get("reps"))
                    .append(" reps\n");
        }

        exerciseNameTextView.setText(exerciseName);
        weightTextView.setText("Weight: See below");
        repsTextView.setText("Reps: See below");
        setsTextView.setText(setsString.toString());

        exerciseListLayout.addView(exerciseView);
    }

    private String getCurrentDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        return sdf.format(new Date());
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_view_prev_exercise, container, false);
        exerciseListLayout = view.findViewById(R.id.exerciseListLayout);
        noDataTextView = view.findViewById(R.id.noDataTextView);
        return view;
    }
}
