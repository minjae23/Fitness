package com.example.fitness;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import android.widget.ImageView;
import android.widget.Toast;

public class UserProfileActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        ImageView imageView=findViewById(R.id.imageView);
        imageView.setImageResource(R.drawable.profile);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        loadUserInfo();
        Button btnLogoutGoogle = findViewById(R.id.btn_logout_google);
        btnLogoutGoogle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // GoogleSignInClient를 사용하여 로그아웃 실행
                GoogleSignIn.getClient(UserProfileActivity.this, GoogleSignInOptions.DEFAULT_SIGN_IN)
                        .signOut()
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    // 로그아웃 성공
                                    FirebaseAuth.getInstance().signOut();
                                    Toast.makeText(UserProfileActivity.this, "구글 로그아웃 성공", Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(getApplicationContext(),LoginActivity.class);
                                    startActivity(intent);
                                    // 여기에 추가적인 작업을 수행할 수 있습니다.
                                } else {
                                    // 로그아웃 실패
                                    Toast.makeText(UserProfileActivity.this, "구글 로그아웃 실패", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });
    }

    private void loadUserInfo() {
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
                            Double skeletalMuscleMass = document.getDouble("sket");
                            Double bodyFatPercentage = document.getDouble("fat");

                            Double bench = document.getDouble("weight");
                            Double squat = document.getDouble("squat");
                            Double dead = document.getDouble("dead");



                            // Update UI with user information
                            TextView nameTextView = findViewById(R.id.nameTextView);
                            TextView emailTextView = findViewById(R.id.emailTextView);
                            TextView Weight = findViewById(R.id.weight);
                            TextView Sket = findViewById(R.id.skeletal_muscle_mass);
                            TextView fat = findViewById(R.id.body_fat_per);
                            TextView Bench = findViewById(R.id.bench);
                            TextView Squat = findViewById(R.id.squat);
                            TextView Dead = findViewById(R.id.dead);

                            Weight.setText(weight != null ?"몸무게\n\n" + weight + "kg"  : "몸무게\n\n-kg\n입력전");
                            Sket.setText(skeletalMuscleMass != null ? "몸무게\n\n" + skeletalMuscleMass + "kg"  : "골격근량\n\n-kg\n입력전");
                            fat.setText(bodyFatPercentage != null ? "몸무게\n\n" + bodyFatPercentage + "%"  : "체지방량\n\n-%\n입력전");

                            Bench.setText(bench != null ? "벤치프레스 : " + bench + "kg"  : "벤치프레스 : 0kg");
                            Squat.setText(squat != null ? "스쿼트 : " + squat + "kg"  : "스쿼트 : 0kg");
                            Dead.setText(dead != null ? "데드리프트 : " + dead + "kg"  : "데드리프트 : 0kg");

                            nameTextView.setText(name);
                            emailTextView.setText(email);
                        } else {
                            Log.d("UserProfileActivity", "No such document");
                        }
                    } else {
                        Log.d("UserProfileActivity", "get failed with ", task.getException());
                    }
                }
            });
        }
    }

}
