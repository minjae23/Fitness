package com.example.fitness;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

public class GraphFragment extends Fragment {

    private TextView textViewDate;
    private TextView textViewWeight;

    // 날짜 및 총무게 배열 (예시)
    private String[] dates = {"2024-06-01", "2024-06-02", "2024-06-03", "2024-06-04", "2024-06-05", "2024-06-06", "2024-06-07"};
    private int[] weights = {50, 60, 55, 70, 65, 75, 80}; // 총무게 (예시)

    // 현재 인덱스 (1주일에 해당하는 날짜를 순환하도록 설정)
    private int currentIndex = 0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_graph, container, false);

        // XML 레이아웃에서 텍스트뷰 및 버튼 참조
        textViewDate = view.findViewById(R.id.textViewDate);
        textViewWeight = view.findViewById(R.id.textViewWeight);
        Button btnPrevWeek = view.findViewById(R.id.btnPrevWeek);
        Button btnNextWeek = view.findViewById(R.id.btnNextWeek);

        // 이전 주 버튼 클릭 리스너 설정
        btnPrevWeek.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentIndex = (currentIndex - 1 + dates.length) % dates.length; // 이전 날짜 인덱스 계산
                updateTextViews(); // 텍스트뷰 업데이트
            }
        });

        // 다음 주 버튼 클릭 리스너 설정
        btnNextWeek.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentIndex = (currentIndex + 1) % dates.length; // 다음 날짜 인덱스 계산
                updateTextViews(); // 텍스트뷰 업데이트
            }
        });

        // 초기 상태 설정
        updateTextViews();

        return view;
    }

    // 텍스트뷰 업데이트 메소드
    private void updateTextViews() {
        textViewDate.setText(dates[currentIndex]); // 현재 날짜 설정
        textViewWeight.setText(String.valueOf(weights[currentIndex])); // 해당 날짜의 총무게 설정
    }
}
