package com.example.geolearn.ui.teacher;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.geolearn.R;
import com.example.geolearn.adapter.TeacherClassAdapter;
import com.example.geolearn.data.DatabaseHelper;
import com.example.geolearn.ui.main.MainActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.List;

public class TeacherClassListActivity extends AppCompatActivity {

    private RecyclerView rcvClassList;
    private DatabaseHelper dbHelper;
    private TeacherClassAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_class_list);

        dbHelper = new DatabaseHelper(this);
        rcvClassList = findViewById(R.id.rcvClassList);

        com.google.android.material.bottomnavigation.BottomNavigationView bottomNav = findViewById(R.id.bottomNavTeacher);

        // Nút Back
        findViewById(R.id.btnBack).setOnClickListener(v -> finish());

        setupBottomNavigation();

        // Đặt mặc định không chọn cái nào (vì đây là màn hình con)
        // Hoặc nếu bạn coi đây là màn Home thì để nav_home
//        bottomNav.setSelectedItemId(R.id.nav_home);

        bottomNav.setOnItemSelectedListener(item -> TeacherNav.openTab(this, item.getItemId()));

        // 1. Lấy ID Giáo viên
        SharedPreferences prefs = getSharedPreferences("UserSession", MODE_PRIVATE);
        int teacherId = prefs.getInt("KEY_USER_ID", -1);

        // 2. Lấy danh sách lớp mà giáo viên dạy
        List<String> myClasses = dbHelper.getTeachingClasses(teacherId);

        if (myClasses.isEmpty()) {
            Toast.makeText(this, "Bạn chưa quản lý lớp học nào!", Toast.LENGTH_SHORT).show();
        }

        // 3. Cài đặt RecyclerView
        rcvClassList.setLayoutManager(new LinearLayoutManager(this));

        adapter = new TeacherClassAdapter(this, myClasses, dbHelper, new TeacherClassAdapter.IOnClassClickListener() {
            @Override
            public void onClick(String className) {
                // ĐÃ XÓA DẤU // Ở ĐẦU DÒNG
                Intent intent = new Intent(TeacherClassListActivity.this, TeacherClassDetailActivity.class);
                intent.putExtra("CLASS_NAME", className);
                startActivity(intent);
            }
        });

        rcvClassList.setAdapter(adapter);
    }
    private void setupBottomNavigation() {
        com.google.android.material.bottomnavigation.BottomNavigationView bottomNav = findViewById(R.id.bottomNavTeacher);
        // Coi như đây là màn hình con, không set Selected Item để tránh hiểu nhầm
        // Hoặc có thể set là nav_home nếu muốn
        bottomNav.getMenu().setGroupCheckable(0, false, true);

        bottomNav.setOnItemSelectedListener(item -> TeacherNav.openTab(this, item.getItemId()));
    }

    @Override
    protected void onResume() {
        super.onResume();

        // ... code cũ ...

        // Cập nhật Badge tin nhắn
        DatabaseHelper db = new DatabaseHelper(this);
        SharedPreferences prefs = getSharedPreferences("UserSession", MODE_PRIVATE);
        int teacherId = prefs.getInt("KEY_USER_ID", -1);

        int unreadCount = db.getTotalUnreadCountForTeacher(teacherId);
        BottomNavigationView bottomNav = findViewById(R.id.bottomNavTeacher);

        if (bottomNav != null) {
            com.google.android.material.badge.BadgeDrawable badge = bottomNav.getOrCreateBadge(R.id.nav_chat);
            if (unreadCount > 0) {
                badge.setVisible(true);
                badge.setNumber(unreadCount);
            } else {
                badge.setVisible(false);
            }
        }
    }
}