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

public class WeekPlanFragment extends ListFragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    private TextView weekLabel;
    private Calendar currentWeek;

    ListView listView;
    private ListAdapter listViewAdapter;
    ArrayList<Plan> planArray;
    WeekPlanSystem weekPlanSystem;
    SQLiteDatabase db;
    WeekPlanFragment weekPlanFragment;

    Button delbtn;
    Button savebtn;

    private LinearLayout sList, mList, tList, wList, thList, fList, satList;

    private Button sundayDate, mondayDate, tuesdayDate, wednesdayDate, thursdayDate, fridayDate, saturdayDate;

    String ID;

    //선택된 날짜
    private String selectedDay;

    public static WeekPlanFragment newInstance(String param1, String param2) {
        WeekPlanFragment fragment = new WeekPlanFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_week_plan, container, false);

        // 로그인된 데이터를 넘겨줌
        SharedPreferences info = getActivity().getSharedPreferences("Info", Context.MODE_PRIVATE);
        ID = info.getString("userID", null);
        System.out.println(ID);

        planArray = new ArrayList<>();
        weekPlanSystem = new WeekPlanSystem(rootView.getContext());
        db = weekPlanSystem.getWritableDatabase();
        weekPlanSystem.onCreate(db);

        delbtn = rootView.findViewById(R.id.del);
        savebtn = rootView.findViewById(R.id.save);

        sList = rootView.findViewById(R.id.sundayList);
        mList = rootView.findViewById(R.id.mondayList);
        tList = rootView.findViewById(R.id.tuesdayList);
        wList = rootView.findViewById(R.id.wednesdayList);
        thList = rootView.findViewById(R.id.thursdayList);
        fList = rootView.findViewById(R.id.fridayList);
        satList = rootView.findViewById(R.id.saturdayList);

        // 날짜 버튼 초기화
        sundayDate = rootView.findViewById(R.id.sundayDate);
        mondayDate = rootView.findViewById(R.id.mondayDate);
        tuesdayDate = rootView.findViewById(R.id.tuesdayDate);
        wednesdayDate = rootView.findViewById(R.id.wednesdayDate);
        thursdayDate = rootView.findViewById(R.id.thursdayDate);
        fridayDate = rootView.findViewById(R.id.fridayDate);
        saturdayDate = rootView.findViewById(R.id.saturdayDate);

        // 주간 달력 관련 초기화
        weekLabel = rootView.findViewById(R.id.weekLabel);
        Button prevWeekButton = rootView.findViewById(R.id.prevWeekButton);
        Button nextWeekButton = rootView.findViewById(R.id.nextWeekButton);

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

        planArray.clear();
        planArray = weekPlanSystem.callPlan(db, ID);

        for (int i = 0; i < planArray.size(); i++) {
            TextView newTextView = new TextView(getContext());

            // 텍스트뷰에 들어갈 내용 설정
            newTextView.setText(planArray.get(i).getExerPartArray());

            newTextView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            newTextView.setBackgroundColor(Color.parseColor("#FF8B3E"));
            newTextView.setTextColor(Color.parseColor("#FFFFFF"));

            switch (planArray.get(i).getWeekly()) {
                case "일":
                    sList.addView(newTextView);
                    break;
                case "월":
                    mList.addView(newTextView);
                    break;
                case "화":
                    tList.addView(newTextView);
                    break;
                case "수":
                    wList.addView(newTextView);
                    break;
                case "목":
                    thList.addView(newTextView);
                    break;
                case "금":
                    fList.addView(newTextView);
                    break;
                case "토":
                    satList.addView(newTextView);
                    break;
                default:
                    Toast.makeText(getActivity(), "운동 추가 에러", Toast.LENGTH_SHORT).show();
            }
        }

        // 리스트뷰 초기화
        String[] inivalues = new String[]{};
        ArrayAdapter<String> adapter2 = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, inivalues);
        setListAdapter(adapter2);

        String[] values = new String[]{"팔", "어깨", "하체", "가슴", "등"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, values);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        final String[] items = {"확인", "취소"};
        builder.setTitle("확인을 누르시면 스케쥴 내용이 전체삭제됩니다.");
        builder.setItems(items, (dialog, which) -> {
            if (which == 0) {
                weekPlanSystem.initTable(db, ID);
                refreshFg();
            }
        });

        // 날짜 클릭 이벤트 설정
        sundayDate.setOnClickListener(view -> showAddEventFragment("일", sundayDate.getText().toString()));
        mondayDate.setOnClickListener(view -> showAddEventFragment("월", mondayDate.getText().toString()));
        tuesdayDate.setOnClickListener(view -> showAddEventFragment("화", tuesdayDate.getText().toString()));
        wednesdayDate.setOnClickListener(view -> showAddEventFragment("수", wednesdayDate.getText().toString()));
        thursdayDate.setOnClickListener(view -> showAddEventFragment("목", thursdayDate.getText().toString()));
        fridayDate.setOnClickListener(view -> showAddEventFragment("금", fridayDate.getText().toString()));
        saturdayDate.setOnClickListener(view -> showAddEventFragment("토", saturdayDate.getText().toString()));

        savebtn.setOnClickListener(v -> {
            weekPlanSystem.initTable(db, ID);
            for (int i = 0; i < planArray.size(); i++) {
                weekPlanSystem.addPlan(db, ID, planArray.get(i));
            }
            String[] values1 = new String[]{};
            ArrayAdapter<String> adapter1 = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, values1);
            setListAdapter(adapter1);
        });

        delbtn.setOnClickListener(v -> builder.show());

        return rootView;
    }

    // 주간 달력 업데이트 함수
    private void updateWeekLabel() {
        SimpleDateFormat sdf = new SimpleDateFormat("MMMM dd, yyyy");
        Calendar startOfWeek = (Calendar) currentWeek.clone();
        startOfWeek.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
        String weekText = "Week of " + sdf.format(startOfWeek.getTime());
        weekLabel.setText(weekText);
    }

    private void loadWeekDays() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd");
        Calendar week = (Calendar) currentWeek.clone();
        week.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);

        highlightToday(sundayDate, week);
        sundayDate.setText(dateFormat.format(week.getTime()));
        week.add(Calendar.DAY_OF_MONTH, 1);

        highlightToday(mondayDate, week);
        mondayDate.setText(dateFormat.format(week.getTime()));
        week.add(Calendar.DAY_OF_MONTH, 1);

        highlightToday(tuesdayDate, week);
        tuesdayDate.setText(dateFormat.format(week.getTime()));
        week.add(Calendar.DAY_OF_MONTH, 1);

        highlightToday(wednesdayDate, week);
        wednesdayDate.setText(dateFormat.format(week.getTime()));
        week.add(Calendar.DAY_OF_MONTH, 1);

        highlightToday(thursdayDate, week);
        thursdayDate.setText(dateFormat.format(week.getTime()));
        week.add(Calendar.DAY_OF_MONTH, 1);

        highlightToday(fridayDate, week);
        fridayDate.setText(dateFormat.format(week.getTime()));
        week.add(Calendar.DAY_OF_MONTH, 1);

        highlightToday(saturdayDate, week);
        saturdayDate.setText(dateFormat.format(week.getTime()));
    }

    // 오늘 날짜 강조 함수
    private void highlightToday(Button button, Calendar date) {
        Calendar today = Calendar.getInstance();
        if (date.get(Calendar.YEAR) == today.get(Calendar.YEAR) &&
                date.get(Calendar.DAY_OF_YEAR) == today.get(Calendar.DAY_OF_YEAR)) {
            button.setTextColor(Color.RED);
        } else {
            button.setTextColor(Color.BLACK);
        }
    }

    // 새로운 프래그먼트 표시 함수
    private void showAddEventFragment(String day, String date) {
        AddEventFragment addEventFragment = AddEventFragment.newInstance(day, date);
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        transaction.replace(R.id.frameLayout, addEventFragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    public void refreshFg() {
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.detach(this).attach(this).commit();
    }

    // 아이템 클릭 이벤트
    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        String strText = (String) l.getItemAtPosition(position); //이게 운동 목록

        // 텍스트뷰 객체 생성
        TextView newTextView = new TextView(getContext());

        // 텍스트뷰에 들어갈 내용 설정
        newTextView.setText(strText);

        // 텍스트 중앙정렬
        newTextView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        newTextView.setBackgroundColor(Color.parseColor("#FF8B3E"));
        newTextView.setTextColor(Color.parseColor("#FFFFFF"));

        // 생성 및 설정된 텍스트뷰를 레이아웃에 적용
        switch (selectedDay) {
            case "일":
                sList.addView(newTextView);
                Plan sunPlan = new Plan("일", strText);
                planArray.add(sunPlan);
                break;
            case "월":
                mList.addView(newTextView);
                Plan monPlan = new Plan("월", strText);
                planArray.add(monPlan);
                break;
            case "화":
                tList.addView(newTextView);
                Plan tuePlan = new Plan("화", strText);
                planArray.add(tuePlan);
                break;
            case "수":
                wList.addView(newTextView);
                Plan wedPlan = new Plan("수", strText);
                planArray.add(wedPlan);
                break;
            case "목":
                thList.addView(newTextView);
                Plan thuPlan = new Plan("목", strText);
                planArray.add(thuPlan);
                break;
            case "금":
                fList.addView(newTextView);
                Plan friPlan = new Plan("금", strText);
                planArray.add(friPlan);
                break;
            case "토":
                satList.addView(newTextView);
                Plan satPlan = new Plan("토", strText);
                planArray.add(satPlan);
                break;
            default:
                Toast.makeText(getActivity(), "운동 추가 에러", Toast.LENGTH_SHORT).show();
        }
    }
}