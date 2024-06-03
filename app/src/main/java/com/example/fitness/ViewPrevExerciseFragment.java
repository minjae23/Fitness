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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class ViewPrevExerciseFragment extends Fragment {

    private static final String TAG = "ViewPrevExerciseFragment";
    private LinearLayout exerciseListLayout;
    private TextView noDataTextView;
    private FirebaseFirestore db;
    private String uid;
    private static final String ARG_DATE = "date";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Initialize Firestore
        db = FirebaseFirestore.getInstance();
        // Get the current user ID
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            uid = currentUser.getUid();
        }

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_view_prev_exercise, container, false);

        exerciseListLayout = rootView.findViewById(R.id.exerciseListLayout);
        noDataTextView = rootView.findViewById(R.id.noDataTextView);

        // Retrieve date from arguments
        Bundle args = getArguments();
        if (args != null && args.containsKey(ARG_DATE)) {
            String date = args.getString(ARG_DATE);
            Log.d(TAG, "Date: " + date); // Print date value to log
            // Call the fetchAndGroupExerciseSets method to fetch exercise data with the specified date
            fetchAndGroupExerciseSets(date);
        }

        return rootView;
    }

    public static ViewPrevExerciseFragment newInstance(String date) {
        ViewPrevExerciseFragment fragment = new ViewPrevExerciseFragment(); // 클래스 수정
        Bundle args = new Bundle();
        args.putString(ARG_DATE, date); // 키 수정 및 값 설정
        fragment.setArguments(args); // Fragment에 Bundle 설정
        return fragment; // 생성된 Fragment 반환
    }

    private void fetchAndGroupExerciseSets(String date) {
        db.collection("users").document(uid).collection("exerciseSets").document(date)
                .collection("routines")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Map<String, Map<String, List<Map<String, Object>>>> groupedExercises = new HashMap<>();

                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String sessionId = document.getId(); // 세션 ID 가져오기
                            Map<String, Object> exerciseSetDocument = document.getData();
                            List<Map<String, Object>> exerciseSets = (List<Map<String, Object>>) exerciseSetDocument.get("exerciseSets");

                            for (Map<String, Object> exerciseSet : exerciseSets) {
                                String exerciseName = (String) exerciseSet.get("exerciseName");

                                if (!groupedExercises.containsKey(sessionId)) {
                                    groupedExercises.put(sessionId, new HashMap<>());
                                }

                                if (!groupedExercises.get(sessionId).containsKey(exerciseName)) {
                                    groupedExercises.get(sessionId).put(exerciseName, new ArrayList<>());
                                }

                                groupedExercises.get(sessionId).get(exerciseName).add(exerciseSet);
                            }
                        }

                        if (groupedExercises.isEmpty()) {
                            noDataTextView.setVisibility(View.VISIBLE);
                        } else {
                            noDataTextView.setVisibility(View.GONE);
                            for (Map.Entry<String, Map<String, List<Map<String, Object>>>> entry : groupedExercises.entrySet()) {
                                String sessionId = entry.getKey();
                                Map<String, List<Map<String, Object>>> exercises = entry.getValue();

                                addSessionHeaderToLayout(sessionId);

                                for (Map.Entry<String, List<Map<String, Object>>> exerciseEntry : exercises.entrySet()) {
                                    String exerciseName = exerciseEntry.getKey();
                                    List<Map<String, Object>> sets = exerciseEntry.getValue();
                                    addGroupedExerciseSetToLayout(exerciseName, sets);
                                }
                            }
                        }
                    } else {
                        Log.w(TAG, "Error getting documents.", task.getException());
                    }
                });
    }

    private void addSessionHeaderToLayout(String sessionId) {
        TextView sessionHeaderTextView = new TextView(getContext());
        sessionHeaderTextView.setText(sessionId);
        sessionHeaderTextView.setTextSize(18);
        sessionHeaderTextView.setPadding(0, 20, 0, 10);
        exerciseListLayout.addView(sessionHeaderTextView);
    }

    private void addGroupedExerciseSetToLayout(String exerciseName, List<Map<String, Object>> sets) {
        View exerciseView = LayoutInflater.from(getContext()).inflate(R.layout.item_exercise, exerciseListLayout, false);

        TextView exerciseNameTextView = exerciseView.findViewById(R.id.exerciseNameTextView);
        TextView setsTextView = exerciseView.findViewById(R.id.setsTextView);

        StringBuilder setsString = new StringBuilder();
        for (Map<String, Object> set : sets) {
            setsString.append("Set ").append(set.get("setCount"))
                    .append(": ").append(set.get("weight"))
                    .append("kg x ").append(set.get("reps"))
                    .append(" reps\n");
        }

        exerciseNameTextView.setText(exerciseName);
        setsTextView.setText(setsString.toString());
        exerciseListLayout.addView(exerciseView);
    }

}
