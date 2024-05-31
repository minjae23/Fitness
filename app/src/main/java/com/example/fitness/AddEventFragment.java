package com.example.fitness;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class AddEventFragment extends Fragment {

    private static final String ARG_DAY = "day";
    private static final String ARG_DATE = "date";

    private String userId;
    private String day;
    private String date;

    public static AddEventFragment newInstance(String day, String date) {
        AddEventFragment fragment = new AddEventFragment();
        Bundle args = new Bundle();
        args.putString(ARG_DAY, day);
        args.putString(ARG_DATE, date);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            day = getArguments().getString(ARG_DAY);
            date = getArguments().getString(ARG_DATE);
        }

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            userId = currentUser.getUid();
            // 현재 날짜 가져오기
            date = getCurrentDate();
        } else {
            Log.w(TAG, "User is not signed in.");
        }
    }

    private String getCurrentDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd", Locale.getDefault());
        return sdf.format(new Date());
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_event, container, false);
        TextView selectedDateText = view.findViewById(R.id.selectedDate);
        selectedDateText.setText(day + " " + date);

        Button prevButton = view.findViewById(R.id.prevButton);
        Button saveEventButton = view.findViewById(R.id.saveEventButton);

        prevButton.setOnClickListener(v -> {
            // Navigate to ViewExerciseFragment
            getActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.container, new ViewPrevExerciseFragment())
                    .addToBackStack(null)
                    .commit();
        });

        saveEventButton.setOnClickListener(v -> {
            // Navigate to AddExerciseFragment
            getActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.container, new AddExerciseFragment())
                    .addToBackStack(null)
                    .commit();
        });

        return view;
    }
}
