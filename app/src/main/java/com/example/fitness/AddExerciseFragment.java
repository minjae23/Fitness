package com.example.fitness;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import androidx.fragment.app.FragmentTransaction;

public class AddExerciseFragment extends Fragment implements AddNewSetListener {
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private LinearLayout setListLayout;
    private TextView timerTextView;
    private FloatingActionButton fab;
    private Handler timerHandler = new Handler();
    private Map<String, Integer> exerciseSetCounts = new HashMap<>();

    private Button completeButton;
    private long startTime = 0;
    private static final String ARG_DATE = "date";

    private String date;

    private String selectedDate;


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
        completeButton.setOnClickListener(v -> {
            saveExerciseSetsToFirestore(user);
            FragmentTransaction transaction = requireActivity().getSupportFragmentManager().beginTransaction();
            Fragment weekPlanFragment = new WeekPlanFragment(); // 혹은 weekPlanFragment.newInstance() 사용
            transaction.replace(R.id.container, weekPlanFragment);
            transaction.commit();
        });

        // 타이머 자동 시작
        resetAndStartTimer();

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

        // Initialize or increment set count for the exercise
        int setCount = exerciseSetCounts.getOrDefault(exerciseName, 0) + 1;
        exerciseSetCounts.put(exerciseName, setCount);

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
                exerciseSetCounts.put(exerciseName, setCount - 1);
                updateSetNumbers(exerciseName);
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
    }

    private void addNewSetView(String exerciseName) {
        // Inflate exercise set item layout
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View exerciseSetView = inflater.inflate(R.layout.exercise_set_item, setListLayout, false);

        // Set exercise name
        TextView exerciseNameTextView = exerciseSetView.findViewById(R.id.exerciseNameTextView);
        exerciseNameTextView.setText(exerciseName);

        // Initialize or increment set count for the exercise
        int setCount = exerciseSetCounts.getOrDefault(exerciseName, 0) + 1;
        exerciseSetCounts.put(exerciseName, setCount);

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
                exerciseSetCounts.put(exerciseName, setCount - 1);
                updateSetNumbers(exerciseName);
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
    }
    public static AddExerciseFragment newInstance(String date) {
        AddExerciseFragment fragment = new AddExerciseFragment();
        Bundle args = new Bundle();
        args.putString(ARG_DATE, date); // 전달할 날짜를 Bundle에 넣음
        fragment.setArguments(args); // Fragment에 Bundle 설정
        return fragment; // 생성된 Fragment 반환
    }
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            date = getArguments().getString(ARG_DATE);
        }
    }
    private void updateSetNumbers(String exerciseName) {
        int count = 1;
        for (int i = 0; i < setListLayout.getChildCount(); i++) {
            View setView = setListLayout.getChildAt(i);
            TextView exerciseNameTextView = setView.findViewById(R.id.exerciseNameTextView);
            TextView setCountTextView = setView.findViewById(R.id.setCountTextView1);

            if (exerciseName.equals(exerciseNameTextView.getText().toString())) {
                setCountTextView.setText(String.valueOf(count++));
            }
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
            TextView setCountTextView = setView.findViewById(R.id.setCountTextView1);

            String exerciseName = exerciseNameTextView.getText().toString();
            float weight = Float.parseFloat(weightEditText.getText().toString());
            int reps = Integer.parseInt(repsEditText.getText().toString());
            int setCount = Integer.parseInt(setCountTextView.getText().toString());

            Map<String, Object> exerciseSet = new HashMap<>();
            exerciseSet.put("exerciseName", exerciseName);
            exerciseSet.put("setCount", setCount);
            exerciseSet.put("weight", weight);
            exerciseSet.put("reps", reps);

            exerciseSets.add(exerciseSet);
        }

        if (selectedDate == null) {
            selectedDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        }

        db.collection("users").document(uid).collection("exerciseSets").document(date)
                .collection("routines")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        int sessionId = task.getResult().size() + 1;
                        String sessionIdString = sessionId + "번째 운동";

                        db.collection("users").document(uid).collection("exerciseSets").document(date)
                                .collection("routines").document(sessionIdString)
                                .set(new HashMap<String, Object>() {{
                                    put("exerciseSets", exerciseSets);
                                }})
                                .addOnSuccessListener(aVoid -> Log.d("AddExerciseFragment", "Exercise sets saved successfully"))
                                .addOnFailureListener(e -> Log.w("AddExerciseFragment", "Error saving exercise sets", e));
                    } else {
                        Log.w("AddExerciseFragment", "Error getting documents.", task.getException());
                    }
                });
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
