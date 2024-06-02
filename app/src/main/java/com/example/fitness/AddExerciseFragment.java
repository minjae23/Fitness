package com.example.fitness;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
public class AddExerciseFragment extends Fragment implements AddNewSetListener {
    private FirebaseFirestore db;

    private FirebaseAuth mAuth;
    private LinearLayout setListLayout;
    private TextView timerTextView;
    private FloatingActionButton fab;
    private Handler timerHandler = new Handler();
    private int setCount = 1;

    private Button completeButton;
    private long startTime = 0;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_exercise, container, false);

        mAuth = FirebaseAuth.getInstance();
        setListLayout = view.findViewById(R.id.setListLayout);
        timerTextView = view.findViewById(R.id.timerTextView);
        fab = view.findViewById(R.id.fab);
        completeButton = view.findViewById(R.id.completeButton);
        db = FirebaseFirestore.getInstance();

        fab.setOnClickListener(v -> {
            ExerciseSelectionBottomSheetDialog bottomSheet = new ExerciseSelectionBottomSheetDialog();
            bottomSheet.setAddNewSetListener(this); // Listener 설정
            bottomSheet.show(getParentFragmentManager(), bottomSheet.getTag());
        });
        FirebaseUser user = mAuth.getCurrentUser();
        completeButton.setOnClickListener(v -> saveExerciseSetsToFirestore(user));

        return view;
    }

    @Override
    public void onExerciseSelected(String exerciseName) {
        addNewExerciseSet(exerciseName);
    }

    private void addNewExerciseSet(String exerciseName) {
        // Inflate exercise set item layout
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View exerciseSetView = inflater.inflate(R.layout.exercise_set_item, setListLayout, false);

        // Set exercise name
        TextView exerciseNameTextView = exerciseSetView.findViewById(R.id.exerciseNameTextView);
        exerciseNameTextView.setText(exerciseName);

        // Set up the set count
        TextView setCountTextView = exerciseSetView.findViewById(R.id.setCountTextView1);
        Button decreaseSetButton = exerciseSetView.findViewById(R.id.decreaseSetButton1);
        Button increaseSetButton = exerciseSetView.findViewById(R.id.increaseSetButton1);
        EditText weightEditText = exerciseSetView.findViewById(R.id.weightEditText1);
        EditText repsEditText = exerciseSetView.findViewById(R.id.repsEditText1);
        CheckBox completeCheckBox = exerciseSetView.findViewById(R.id.completeCheckBox1);

        setCountTextView.setText(String.valueOf(setCount));

        decreaseSetButton.setOnClickListener(v -> {
            if (setCount > 1) {
                setListLayout.removeView(exerciseSetView);
                setCount--;
                updateSetNumbers();
            }
        });

        increaseSetButton.setOnClickListener(v -> {
            addNewSetView(exerciseName);
        });

        completeCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                resetAndStartTimer();
            }
        });

        setListLayout.addView(exerciseSetView);
        setCount++;
    }

    private void addNewSetView(String exerciseName) {
        // Inflate exercise set item layout
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View exerciseSetView = inflater.inflate(R.layout.exercise_set_item, setListLayout, false);

        // Set exercise name
        TextView exerciseNameTextView = exerciseSetView.findViewById(R.id.exerciseNameTextView);
        exerciseNameTextView.setText(exerciseName);

        // Set up the set count
        TextView setCountTextView = exerciseSetView.findViewById(R.id.setCountTextView1);
        Button decreaseSetButton = exerciseSetView.findViewById(R.id.decreaseSetButton1);
        Button increaseSetButton = exerciseSetView.findViewById(R.id.increaseSetButton1);
        EditText weightEditText = exerciseSetView.findViewById(R.id.weightEditText1);
        EditText repsEditText = exerciseSetView.findViewById(R.id.repsEditText1);
        CheckBox completeCheckBox = exerciseSetView.findViewById(R.id.completeCheckBox1);

        setCountTextView.setText(String.valueOf(setCount));

        decreaseSetButton.setOnClickListener(v -> {
            if (setCount > 1) {
                setListLayout.removeView(exerciseSetView);
                setCount--;
                updateSetNumbers();
            }
        });

        increaseSetButton.setOnClickListener(v -> {
            addNewSetView(exerciseName);
        });

        completeCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                resetAndStartTimer();
            }
        });

        setListLayout.addView(exerciseSetView);
        setCount++;
    }

    private void updateSetNumbers() {
        for (int i = 0; i < setListLayout.getChildCount(); i++) {
            View setView = setListLayout.getChildAt(i);
            TextView setCountTextView = setView.findViewById(R.id.setCountTextView1);
            setCountTextView.setText(String.valueOf(i + 1));
        }
    }

    private void saveExerciseSetsToFirestore(FirebaseUser user) {

        String uid = user.getUid();

        List<Map<String, Object>> exerciseSets = new ArrayList<>();

        for (int i = 0; i < setListLayout.getChildCount(); i++) {
            View setView = setListLayout.getChildAt(i);
            TextView exerciseNameTextView = setView.findViewById(R.id.exerciseNameTextView);
            EditText weightEditText = setView.findViewById(R.id.weightEditText1);
            EditText repsEditText = setView.findViewById(R.id.repsEditText1);

            String exerciseName = exerciseNameTextView.getText().toString();
            float weight = Float.parseFloat(weightEditText.getText().toString());
            int reps = Integer.parseInt(repsEditText.getText().toString());

            Map<String, Object> exerciseSet = new HashMap<>();
            exerciseSet.put("exerciseName", exerciseName);
            exerciseSet.put("setCount", i + 1);
            exerciseSet.put("weight", weight);
            exerciseSet.put("reps", reps);

            exerciseSets.add(exerciseSet);
        }
        // Get current user
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(getContext());
        if (account != null) {
            String date = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
            String routineId = UUID.randomUUID().toString(); // 고유한 ID 생성

            db.collection("users").document(uid).collection("exerciseSets").document(date)
                    .collection("routines").document(routineId)
                    .set(new HashMap<String, Object>() {{
                        put("exerciseSets", exerciseSets);
                    }})
                    .addOnSuccessListener(aVoid -> Log.d("AddExerciseFragment", "Exercise sets saved successfully"))
                    .addOnFailureListener(e -> Log.w("AddExerciseFragment", "Error saving exercise sets", e));

        }
    }

    private void resetAndStartTimer() {
        startTime = System.currentTimeMillis();
        timerHandler.removeCallbacks(timerRunnable);
        timerHandler.post(timerRunnable);
    }

    private Runnable timerRunnable = new Runnable() {
        @Override
        public void run() {
            long millis = System.currentTimeMillis() - startTime;
            int seconds = (int) (millis / 1000);
            int minutes = seconds / 60;
            seconds = seconds % 60;

            timerTextView.setText(String.format("%02d:%02d", minutes, seconds));

            timerHandler.postDelayed(this, 500);
        }
    };
}
