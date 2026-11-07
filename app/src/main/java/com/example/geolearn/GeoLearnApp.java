package com.example.geolearn;

import android.app.Application;

import androidx.appcompat.app.AppCompatDelegate;

public class GeoLearnApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        // Áp dụng dark mode đã lưu cho mọi màn (cả học sinh lẫn giáo viên) ngay khi app khởi động
        AppCompatDelegate.setDefaultNightMode(getSharedPreferences("AppSettings", MODE_PRIVATE)
                .getBoolean("KEY_DARK_MODE", false)
                ? AppCompatDelegate.MODE_NIGHT_YES
                : AppCompatDelegate.MODE_NIGHT_NO);
    }
}
