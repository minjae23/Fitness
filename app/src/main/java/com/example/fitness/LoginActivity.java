package com.example.fitness;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInStatusCodes;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {

    protected static final int RC_SIGN_IN = 9001;
    private static final String TAG = "GoogleSignIn";

    FirebaseAuth mAuth;
    GoogleSignInClient mGoogleSignInClient;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Firebase 인증 객체 초기화
        mAuth = FirebaseAuth.getInstance();

        // Firestore 초기화
        db = FirebaseFirestore.getInstance();

        // Google 로그인 옵션 설정
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        // GoogleSignInClient 초기화
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        // 구글 로그인 상태를 확인하여 이미 로그인되어 있으면 메인 액티비티로 이동
        if (mAuth.getCurrentUser() != null) {
            startActivity(new Intent(LoginActivity.this, MainActivity.class));
            finish(); // 현재 액티비티 종료
        } else {
            // 구글 로그인 버튼 클릭 시 이벤트 처리
            setContentView(R.layout.activity_login);
            findViewById(R.id.sign_in_button).setOnClickListener(v -> signIn());
        }
    }
    // Google 로그인 실행 메소드
    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    // Google Login Results Processing Method
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Deliver credentials to Firebase if Google login is successful
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                // Google Login Failed
                Log.w(TAG, "Google sign in failed", e);
                if (e.getStatusCode() == GoogleSignInStatusCodes.SIGN_IN_CANCELLED) {
                    // If the user has canceled the login
                    Toast.makeText(LoginActivity.this, "Google sign in cancelled",
                            Toast.LENGTH_SHORT).show();
                } else if (e.getStatusCode() == GoogleSignInStatusCodes.NETWORK_ERROR) {
                    // Internet connection error
                    Toast.makeText(LoginActivity.this, "Network error. " +
                            "Please check your internet connection.", Toast.LENGTH_SHORT).show();
                } else {
                    // Other errors
                    Toast.makeText(LoginActivity.this, "Google sign in failed: " +
                            e.getStatusCode(), Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    // How to forward Google login credentials to Firebase
    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        Log.d("LoginActivity", "signInWithCredential:success");
                        FirebaseUser user = mAuth.getCurrentUser();
                        saveUserInfoToFirestore(user);
                        // 로그인 성공 시 다음 액티비티로 이동
                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish(); // 현재 액티비티 종료
                    } else {
                        Log.w("LoginActivity", "signInWithCredential:failure", task.getException());
                    }
                });
    }

    private void saveUserInfoToFirestore(FirebaseUser user) {
        if (user != null) {
            String uid = user.getUid();
            String name = user.getDisplayName();
            String email = user.getEmail();

            // Create a user map
            Map<String, Object> userMap = new HashMap<>();
            userMap.put("name", name);
            userMap.put("email", email);

            // Save user info to Firestore with merge option
            db.collection("users").document(uid)
                    .set(userMap, SetOptions.merge())
                    .addOnSuccessListener(aVoid -> Log.d("LoginActivity",
                            "User information saved successfully"))
                    .addOnFailureListener(e -> Log.w("LoginActivity",
                            "Error saving user information", e));
        }
    }
}
