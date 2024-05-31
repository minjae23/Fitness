package com.example.fitness;

import android.content.Intent;

import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.IdlingRegistry;
import androidx.test.espresso.IdlingResource;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.ActivityTestRule;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.AuthResult;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@RunWith(AndroidJUnit4.class)
public class LoginActivityTest {

    @Rule
    public ActivityTestRule<LoginActivity> activityRule = new ActivityTestRule<>(LoginActivity.class);

    private MyIdlingResource idlingResource;

    @Mock
    FirebaseAuth mockFirebaseAuth;
    @Mock
    FirebaseUser mockFirebaseUser;
    @Mock
    GoogleSignInClient mockGoogleSignInClient;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        idlingResource = new MyIdlingResource();
        IdlingRegistry.getInstance().register(idlingResource);
    }

    @Test
    public void testGoogleSignInButton() {
        when(mockFirebaseAuth.getCurrentUser()).thenReturn(mockFirebaseUser);
        when(mockFirebaseUser.getDisplayName()).thenReturn("Test User");
        when(mockFirebaseUser.getEmail()).thenReturn("testuser@example.com");

        // Mock the GoogleSignInClient and FirebaseAuth instances
        LoginActivity loginActivity = activityRule.getActivity();
        loginActivity.mAuth = mockFirebaseAuth;
        loginActivity.mGoogleSignInClient = mockGoogleSignInClient;

        // Simulate clicking the sign-in button
        onView(withId(R.id.sign_in_button)).perform(click());

        // Mock the GoogleSignInAccount and AuthCredential
        GoogleSignInAccount mockGoogleSignInAccount = Mockito.mock(GoogleSignInAccount.class);
        when(mockGoogleSignInAccount.getIdToken()).thenReturn("mockIdToken");
        AuthCredential mockAuthCredential = GoogleAuthProvider.getCredential("mockIdToken", null);

        // Mock the sign-in result
        AuthResult mockAuthResult = Mockito.mock(AuthResult.class);
        Task<AuthResult> mockAuthTask = Mockito.mock(Task.class);
        when(mockFirebaseAuth.signInWithCredential(mockAuthCredential)).thenReturn(mockAuthTask);
        when(mockAuthTask.getResult()).thenReturn(mockAuthResult);

        // Trigger the onActivityResult
        Intent data = new Intent();
        data.putExtra("authAccount", mockGoogleSignInAccount);
        loginActivity.onActivityResult(LoginActivity.RC_SIGN_IN, LoginActivity.RESULT_OK, data);

        // Verify that the user info was saved
        onView(withId(R.id.emailTextView))
                .check(matches(withText("Test User\n(testuser@example.com)")));

        // Set idling resource to idle
        idlingResource.setIdle(true);
    }

    @After
    public void tearDown() {
        IdlingRegistry.getInstance().unregister(idlingResource);
    }
}
