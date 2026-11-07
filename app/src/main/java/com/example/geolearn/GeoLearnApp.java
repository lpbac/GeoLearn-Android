package com.example.geolearn;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class GeoLearnApp extends Application {
    // Các thanh điều hướng dưới đang dùng trong app
    private static final int[] BOTTOM_NAV_IDS = {
            R.id.bottomNavigation, R.id.bottomNavTeacher, R.id.bottomNavProfile
    };

    @Override
    public void onCreate() {
        super.onCreate();
        // Áp dụng dark mode đã lưu cho mọi màn (cả học sinh lẫn giáo viên) ngay khi app khởi động
        AppCompatDelegate.setDefaultNightMode(getSharedPreferences("AppSettings", MODE_PRIVATE)
                .getBoolean("KEY_DARK_MODE", false)
                ? AppCompatDelegate.MODE_NIGHT_YES
                : AppCompatDelegate.MODE_NIGHT_NO);

        // Ẩn thanh điều hướng dưới khi bàn phím mở, tránh nó bị đẩy lên che ô đang nhập
        registerActivityLifecycleCallbacks(new ActivityLifecycleCallbacks() {
            @Override
            public void onActivityStarted(@NonNull Activity activity) {
                for (int id : BOTTOM_NAV_IDS) {
                    View nav = activity.findViewById(id);
                    if (nav != null && nav.getTag(R.id.bottomNavigation) == null) {
                        nav.setTag(R.id.bottomNavigation, Boolean.TRUE);
                        hideWhenKeyboardOpen(nav);
                    }
                }
            }

            @Override public void onActivityCreated(@NonNull Activity a, @Nullable Bundle b) { }
            @Override public void onActivityResumed(@NonNull Activity a) { }
            @Override public void onActivityPaused(@NonNull Activity a) { }
            @Override public void onActivityStopped(@NonNull Activity a) { }
            @Override public void onActivitySaveInstanceState(@NonNull Activity a, @NonNull Bundle b) { }
            @Override public void onActivityDestroyed(@NonNull Activity a) { }
        });
    }

    private static void hideWhenKeyboardOpen(View nav) {
        nav.getViewTreeObserver().addOnGlobalLayoutListener(() -> {
            WindowInsetsCompat insets = ViewCompat.getRootWindowInsets(nav);
            boolean imeVisible = insets != null && insets.isVisible(WindowInsetsCompat.Type.ime());
            int visibility = imeVisible ? View.GONE : View.VISIBLE;
            if (nav.getVisibility() != visibility)
                nav.setVisibility(visibility);
        });
    }
}
