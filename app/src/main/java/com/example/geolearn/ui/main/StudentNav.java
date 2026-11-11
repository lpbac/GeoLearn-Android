package com.example.geolearn.ui.main;

import android.app.Activity;
import android.content.Intent;

import com.example.geolearn.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

/** Thanh điều hướng dưới của các màn con học sinh: bấm tab nào thì về MainActivity ở tab đó. */
public final class StudentNav {
    private StudentNav() {}

    public static void setup(Activity from) {
        BottomNavigationView nav = from.findViewById(R.id.bottomNavigation);
        if (nav == null) return;
        nav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            String tab = id == R.id.nav_home ? "" : id == R.id.nav_chat ? "CHAT" : id == R.id.nav_profile ? "PROFILE" : null;
            if (tab == null) return false;
            Intent intent = new Intent(from, MainActivity.class)
                    .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            if (!tab.isEmpty()) intent.putExtra("NAVIGATE_TO", tab);
            from.startActivity(intent);
            from.finish();
            return true;
        });
    }
}
