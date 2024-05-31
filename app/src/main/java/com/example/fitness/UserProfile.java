package com.example.fitness;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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

public class UserProfile extends Fragment {
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user_profile, container, false);

        ImageView imageView = view.findViewById(R.id.imageView);
        imageView.setImageResource(R.drawable.profile);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        loadUserInfo(view);

        Button btnLogoutGoogle = view.findViewById(R.id.btn_logout_google);
        btnLogoutGoogle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Execute logout using GoogleSignInClient
                GoogleSignIn.getClient(getActivity(), GoogleSignInOptions.DEFAULT_SIGN_IN)
                        .signOut()
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    // Logout successful
                                    FirebaseAuth.getInstance().signOut();
                                    Toast.makeText(getActivity(), "Google logout successful", Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(getActivity(), LoginActivity.class);
                                    startActivity(intent);
                                    // Perform additional actions if needed
                                } else {
                                    // Logout failed
                                    Toast.makeText(getActivity(), "Google logout failed", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });

        return view;
    }



    private void loadUserInfo(View view) {
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

                            Double bench = document.getDouble("bench");
                            Double squat = document.getDouble("squat");
                            Double dead = document.getDouble("dead");

                            // Update UI with user information
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
