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

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            userId = currentUser.getUid();
            // 현재 날짜 가져오기
            date = getCurrentDate();
            // 운동 루틴 불러오기
            loadExerciseRoutine();
        } else {
            Log.w(TAG, "User is not signed in.");
        }
    }

    private void loadExerciseRoutine() {
        ExerciseRoutineUtils.getExerciseRoutine(userId, date, new ExerciseRoutineUtils.ExerciseRoutineCallback() {
            @Override
            public void onCallback(List<Map<String, Object>> exerciseList) {
                if (exerciseList.isEmpty()) {
                    noDataTextView.setVisibility(View.VISIBLE);
                } else {
                    noDataTextView.setVisibility(View.GONE);
                    for (Map<String, Object> exercise : exerciseList) {
                        String name = (String) exercise.get("name");
                        Double weight = (Double) exercise.get("weight");
                        Long reps = (Long) exercise.get("reps");
                        Long sets = (Long) exercise.get("sets");

                        Log.d(TAG, "Exercise: " + name + ", Weight: " + weight + ", Reps: " + reps + ", Sets: " + sets);

                        // 불러온 데이터를 UI에 표시하는 코드
                        addExerciseToLayout(name, weight, reps, sets);
                    }
                }
            }
        });
    }

    private void addExerciseToLayout(String name, Double weight, Long reps, Long sets) {
        View exerciseView = LayoutInflater.from(getContext()).inflate(R.layout.item_exercise, exerciseListLayout, false);

        TextView exerciseNameTextView = exerciseView.findViewById(R.id.exerciseNameTextView);
        TextView weightTextView = exerciseView.findViewById(R.id.weightTextView);
        TextView repsTextView = exerciseView.findViewById(R.id.repsTextView);
        TextView setsTextView = exerciseView.findViewById(R.id.setsTextView);

        exerciseNameTextView.setText(name);
        weightTextView.setText("weight: " + weight + " kg");
        repsTextView.setText("reps: " + reps);
        setsTextView.setText("sets: " + sets);

        exerciseListLayout.addView(exerciseView);
    }

    private String getCurrentDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd", Locale.getDefault());
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
