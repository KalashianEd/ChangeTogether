package com.example.changetogether;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.MenuItem;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;
    private FrameLayout frameLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        bottomNavigationView = findViewById(R.id.bottomNavView);
        frameLayout = findViewById(R.id.frameLayout);
        // IDK THIS COMMENT
        // IDK THIS COMMENT SECOND AZIZ

        // Устанавливаем слушатель для BottomNavigationView
        bottomNavigationView.setOnItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment selectedFragment = null;

                int itemID = item.getItemId();
                if (itemID == R.id.navHome) {
                    selectedFragment = new HomeFragment();
                } else if (itemID == R.id.navSearch) {
                    selectedFragment = new SearchFragment();
                } else if (itemID == R.id.navSettings) {
                    selectedFragment = new SettingsFragment();
                } else if (itemID == R.id.navProfile) {
                    selectedFragment = new ProfileFragment();
                }

                if (selectedFragment != null) {
                    loadFragment(selectedFragment);
                    return true; // Важно вернуть true, чтобы система знала, что нажатие обработано
                }

                return false;
            }
        });

        // Загружаем начальный фрагмент
        loadFragment(new HomeFragment());
    }

    private void loadFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frameLayout, fragment); // Заменяем, а не добавляем
        fragmentTransaction.commit();
    }
}
