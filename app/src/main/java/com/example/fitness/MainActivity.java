package com.example.fitness;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    String ID;
    WeekPlanFragment weekPlanFragment;
    GraphFragment graphFragment;
    FriendFragment friendFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        weekPlanFragment =new WeekPlanFragment();
        graphFragment =new GraphFragment();
        friendFragment =new FriendFragment();

        UserProfile userProfileFragment = new UserProfile();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.container, weekPlanFragment);
        transaction.commit();





        BottomNavigationView bottom_menu = findViewById(R.id.bottom_menu);
        bottom_menu.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                if (id == R.id.third_tab ) {
                    getSupportFragmentManager().beginTransaction().replace(R.id.container,friendFragment ).commit();
                    return true;
                }
                if(id == R.id.second_tab){
                    getSupportFragmentManager().beginTransaction().replace(R.id.container,graphFragment ).commit();
                    return true;
                }
                if(id == R.id.first_tab){
                    getSupportFragmentManager().beginTransaction().replace(R.id.container,weekPlanFragment ).commit();
                    return true;
                }
                if(id == R.id.fourth_tab){
                    getSupportFragmentManager().beginTransaction().replace(R.id.container, userProfileFragment).commit();
                    return true;
                }
                return false;
            }
        });
    }
}
