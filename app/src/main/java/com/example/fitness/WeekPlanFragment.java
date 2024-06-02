package com.example.fitness;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentTransaction;
import androidx.fragment.app.ListFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentTransaction;
import androidx.fragment.app.ListFragment;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class WeekPlanFragment extends ListFragment {

    private TextView weekLabel;
    private Calendar currentWeek;

    private LinearLayout sList, mList, tList, wList, thList, fList, satList;
    private Button sundayDate, mondayDate, tuesdayDate, wednesdayDate, thursdayDate, fridayDate, saturdayDate;
    private List<Button> dateButtons;

    private List<Day> dayList;

    public static WeekPlanFragment newInstance(String param1, String param2) {
        WeekPlanFragment fragment = new WeekPlanFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_week_plan, container, false);

        weekLabel = rootView.findViewById(R.id.weekLabel);
        Button prevWeekButton = rootView.findViewById(R.id.prevWeekButton);
        Button nextWeekButton = rootView.findViewById(R.id.nextWeekButton);

        sList = rootView.findViewById(R.id.sundayList);
        mList = rootView.findViewById(R.id.mondayList);
        tList = rootView.findViewById(R.id.tuesdayList);
        wList = rootView.findViewById(R.id.wednesdayList);
        thList = rootView.findViewById(R.id.thursdayList);
        fList = rootView.findViewById(R.id.fridayList);
        satList = rootView.findViewById(R.id.saturdayList);

        sundayDate = rootView.findViewById(R.id.sundayDate);
        mondayDate = rootView.findViewById(R.id.mondayDate);
        tuesdayDate = rootView.findViewById(R.id.tuesdayDate);
        wednesdayDate = rootView.findViewById(R.id.wednesdayDate);
        thursdayDate = rootView.findViewById(R.id.thursdayDate);
        fridayDate = rootView.findViewById(R.id.fridayDate);
        saturdayDate = rootView.findViewById(R.id.saturdayDate);

        dateButtons = new ArrayList<>();
        dateButtons.add(sundayDate);
        dateButtons.add(mondayDate);
        dateButtons.add(tuesdayDate);
        dateButtons.add(wednesdayDate);
        dateButtons.add(thursdayDate);
        dateButtons.add(fridayDate);
        dateButtons.add(saturdayDate);

        currentWeek = Calendar.getInstance();

        updateWeekLabel();
        loadWeekDays();

        prevWeekButton.setOnClickListener(v -> {
            currentWeek.add(Calendar.WEEK_OF_YEAR, -1);
            updateWeekLabel();
            loadWeekDays();
        });

        nextWeekButton.setOnClickListener(v -> {
            currentWeek.add(Calendar.WEEK_OF_YEAR, 1);
            updateWeekLabel();
            loadWeekDays();
        });

        return rootView;
    }

    private void updateWeekLabel() {
        SimpleDateFormat sdf = new SimpleDateFormat("MMMM dd, yyyy");
        Calendar startOfWeek = (Calendar) currentWeek.clone();
        startOfWeek.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
        String weekText = "Week of " + sdf.format(startOfWeek.getTime());
        weekLabel.setText(weekText);
    }

    private void loadWeekDays() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd");
        SimpleDateFormat dayFormat = new SimpleDateFormat("EEE");
        Calendar week = (Calendar) currentWeek.clone();
        week.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);

        for (int i = 0; i < 7; i++) {
            String dayOfWeek = dayFormat.format(week.getTime());
            String date = dateFormat.format(week.getTime());
            boolean isToday = isToday(week);

            Button dateButton = dateButtons.get(i);
            dateButton.setText(date);
            dateButton.setTag(dayOfWeek);

            if (isToday) {
                dateButton.setTextColor(Color.RED);
            } else {
                dateButton.setTextColor(Color.BLACK);
            }

            dateButton.setOnClickListener(view -> showAddEventFragment(dayOfWeek, date));
            week.add(Calendar.DAY_OF_MONTH, 1);
        }
    }

    private boolean isToday(Calendar date) {
        Calendar today = Calendar.getInstance();
        return date.get(Calendar.YEAR) == today.get(Calendar.YEAR) && date.get(Calendar.DAY_OF_YEAR) == today.get(Calendar.DAY_OF_YEAR);
    }

    private void showAddEventFragment(String day, String date) {
        AddEventFragment addEventFragment = AddEventFragment.newInstance(day, date);
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        transaction.replace(R.id.frameLayout, addEventFragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }
}
