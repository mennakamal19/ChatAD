package com.example.chatad;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.view.MenuItem;

import com.example.chatad.fragments.chatfragment;
import com.example.chatad.fragments.homefragment;
import com.example.chatad.fragments.profilefragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class StartActivity extends AppCompatActivity
{
    BottomNavigationView navigationView;
    FragmentManager fragmentManager; // to manage your layouts
    FragmentTransaction fragmentTransaction; // transaction between fragments

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        navigationView = findViewById(R.id.bottom_navication);
        fragmentManager = getSupportFragmentManager();
        homefragment homefragment = new homefragment(); // load by default
        loadFragment(homefragment);
        navigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem)
            {
                switch (menuItem.getItemId())
                {
                    case R.id.myhome:
                        homefragment homefragment1 = new homefragment();
                        loadFragment(homefragment1);
                        return true;
                    case R.id.chats:
                        chatfragment chatfragment = new chatfragment();
                        loadFragment(chatfragment);
                        return true;
                    case R.id.myprofile:
                        profilefragment profilefragment = new profilefragment();
                        loadFragment(profilefragment);
                        return true;
                }
                return false;
            }
        });
    }

    public void loadFragment(Fragment fragment)
    {
        fragmentTransaction = getSupportFragmentManager().beginTransaction(); // start transaction
        fragmentTransaction.replace(R.id.container,fragment); // replace
        fragmentTransaction.commit();
    }
}
