package com.example.fitness;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.util.ArrayList;
import java.util.List;

public class ExerciseSelectionBottomSheetDialog extends BottomSheetDialogFragment {

    private AddNewSetListener addNewSetListener; // Listener 필드 추가
    private ListView exerciseListView;
    private List<String> exerciseList = new ArrayList<>();

    public ExerciseSelectionBottomSheetDialog() {
        // Sample data, replace with your own data source
        exerciseList.add("Bench Press");
        exerciseList.add("Squats");
        exerciseList.add("Deadlift");
        exerciseList.add("Overhead Press");
    }

    // Listener 설정 메서드 추가
    public void setAddNewSetListener(AddNewSetListener listener) {
        this.addNewSetListener = listener;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.bottom_sheet_exercise_selection, container, false);

        exerciseListView = view.findViewById(R.id.exerciseListView);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, exerciseList);
        exerciseListView.setAdapter(adapter);

        exerciseListView.setOnItemClickListener((parent, view1, position, id) -> {
            String selectedExercise = exerciseList.get(position);
            // Handle exercise selection
            if (addNewSetListener != null) {
                addNewSetListener.onExerciseSelected(selectedExercise); // 선택한 운동 종목 전달
            }
            dismiss();
        });

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null && dialog.getWindow() != null) {
            dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        }
    }
}
