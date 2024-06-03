package com.example.fitness;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import androidx.appcompat.app.AlertDialog;

import java.util.HashMap;
import java.util.Map;

public class UserProfile extends Fragment {
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private TextView weightTextView, skeletalMuscleMassTextView, bodyFatPercentageTextView;
    private TextView benchTextView, squatTextView, deadTextView;
    private Button weightButton, skeletalMuscleMassButton, bodyFatPercentageButton;
    private Button benchButton, squatButton, deadButton;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user_profile, container, false);

        ImageView imageView = view.findViewById(R.id.imageView);
        imageView.setImageResource(R.drawable.profile);

        weightTextView = view.findViewById(R.id.weight);
        skeletalMuscleMassTextView = view.findViewById(R.id.skeletal_muscle_mass);
        bodyFatPercentageTextView = view.findViewById(R.id.body_fat_per);
        benchTextView = view.findViewById(R.id.bench);
        squatTextView = view.findViewById(R.id.squat);
        deadTextView = view.findViewById(R.id.dead);

        weightButton = view.findViewById(R.id.button);
        skeletalMuscleMassButton = view.findViewById(R.id.button2);
        bodyFatPercentageButton = view.findViewById(R.id.button3);
        benchButton = view.findViewById(R.id.input_bench);
        squatButton = view.findViewById(R.id.input_squat);
        deadButton = view.findViewById(R.id.input_dead);

        weightButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showInputDialog(weightTextView, "체중 입력", "체중을 입력하세요:", "weight");
            }
        });

        skeletalMuscleMassButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showInputDialog(skeletalMuscleMassTextView, "골격근량 입력", "골격근량을 입력하세요:", "skeletalMuscleMass");
            }
        });

        bodyFatPercentageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showInputDialog(bodyFatPercentageTextView, "체지방률 입력", "체지방률을 입력하세요:", "bodyFatPercentage");
            }
        });

        benchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showInputDialog(benchTextView, "벤치프레스 입력", "벤치프레스를 입력하세요:", "bench");
            }
        });

        squatButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showInputDialog(squatTextView, "스쿼트 입력", "스쿼트를 입력하세요:", "squat");
            }
        });

        deadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showInputDialog(deadTextView, "데드리프트 입력", "데드리프트를 입력하세요:", "dead");
            }
        });

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        loadUserInfo(view);

        Button btnLogoutGoogle = view.findViewById(R.id.btn_logout_google);
        btnLogoutGoogle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // AlertDialog.Builder를 사용하여 확인 다이얼로그를 생성합니다.
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setMessage("정말 로그아웃 하시겠습니까?")
                        .setPositiveButton("예", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // "예"를 선택할 경우 로그아웃을 실행합니다.
                                GoogleSignIn.getClient(getActivity(), GoogleSignInOptions.DEFAULT_SIGN_IN)
                                        .signOut()
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    FirebaseAuth.getInstance().signOut();
                                                    Toast.makeText(getActivity(), "Google logout successful", Toast.LENGTH_SHORT).show();
                                                    Intent intent = new Intent(getActivity(), LoginActivity.class);
                                                    startActivity(intent);
                                                } else {
                                                    Toast.makeText(getActivity(), "Google logout failed", Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        });
                            }
                        })
                        .setNegativeButton("아니오", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // "아니오"를 선택할 경우 아무 작업도 수행하지 않습니다.
                            }
                        });
                // 다이얼로그를 표시합니다.
                builder.show();
            }
        });


        return view;
    }

    private void showInputDialog(final TextView textView, String title, String message, final String field) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle(title);
        builder.setMessage(message);

        final EditText input = new EditText(getContext());
        builder.setView(input);

        builder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String userInput = input.getText().toString();
                textView.setText(userInput);

                // Update Firestore with the new value
                FirebaseUser user = mAuth.getCurrentUser();
                if (user != null) {
                    String uid = user.getUid();
                    Map<String, Object> data = new HashMap<>();
                    data.put(field, Double.parseDouble(userInput));

                    db.collection("users").document(uid).update(data)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(getActivity(), "Data updated", Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(getActivity(), "Data update failed", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }
            }
        });

        builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }

    private void loadUserInfo(final View view) {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            String uid = user.getUid();

            DocumentReference docRef = db.collection("users").document(uid);
            docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            String name = document.getString("name");
                            String email = document.getString("email");

                            Double weight = document.getDouble("weight");
                            Double skeletalMuscleMass = document.getDouble("skeletalMuscleMass");
                            Double bodyFatPercentage = document.getDouble("bodyFatPercentage");

                            Double bench = document.getDouble("bench");
                            Double squat = document.getDouble("squat");
                            Double dead = document.getDouble("dead");

                            TextView nameTextView = view.findViewById(R.id.nameTextView);
                            TextView emailTextView = view.findViewById(R.id.emailTextView);
                            TextView weightTextView = view.findViewById(R.id.weight);
                            TextView skeletalMuscleMassTextView = view.findViewById(R.id.skeletal_muscle_mass);
                            TextView bodyFatPercentageTextView = view.findViewById(R.id.body_fat_per);
                            TextView benchTextView = view.findViewById(R.id.bench);
                            TextView squatTextView = view.findViewById(R.id.squat);
                            TextView deadTextView = view.findViewById(R.id.dead);

                            weightTextView.setText(weight != null ? "몸무게\n\n" + weight + "kg" : "몸무게\n\n-kg\n입력전");
                            skeletalMuscleMassTextView.setText(skeletalMuscleMass != null ? "골격근량\n\n" + skeletalMuscleMass + "kg" : "골격근량\n\n-kg\n입력전");
                            bodyFatPercentageTextView.setText(bodyFatPercentage != null ? "체지방량\n\n" + bodyFatPercentage + "%" : "체지방량\n\n-%\n입력전");

                            benchTextView.setText(bench != null ? "벤치프레스 : " + bench + "kg" : "벤치프레스 : 0kg");
                            squatTextView.setText(squat != null ? "스쿼트 : " + squat + "kg" : "스쿼트 : 0kg");
                            deadTextView.setText(dead != null ? "데드리프트 : " + dead + "kg" : "데드리프트 : 0kg");

                            nameTextView.setText(name);
                            emailTextView.setText(email);
                        } else {
                            Log.d("UserProfileFragment", "No such document");
                        }
                    } else {
                        Log.d("UserProfileFragment", "get failed with ", task.getException());
                    }
                }
            });
        }
    }
}
