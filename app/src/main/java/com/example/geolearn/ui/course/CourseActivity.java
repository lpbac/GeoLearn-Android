package com.example.geolearn.ui.course;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.geolearn.R;
import com.example.geolearn.adapter.CourseAdapter;
import com.example.geolearn.data.DatabaseHelper;
import com.example.geolearn.model.Chapter;
import com.example.geolearn.ui.main.MainActivity; // Import MainActivity
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class CourseActivity extends AppCompatActivity {

    private View btnBack;
    private RecyclerView rcv;
    private DatabaseHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course); // Layout mới đã có bottomNavigation

        rcv = findViewById(R.id.rcvCourse);
        rcv.setLayoutManager(new androidx.recyclerview.widget.LinearLayoutManager(this)); // Xếp dọc

        db = new DatabaseHelper(this);
        loadData();

        // Nút Back
        findViewById(R.id.btnBack).setOnClickListener(v -> finish());

        // 1. Ánh xạ thanh Menu
        com.google.android.material.bottomnavigation.BottomNavigationView bottomNav = findViewById(R.id.bottomNavigation);

        // Đặt mặc định chọn nút Home (vì Khóa học thuộc về Home)
        bottomNav.setSelectedItemId(R.id.nav_home);

        com.example.geolearn.ui.main.StudentNav.setup(this);
    }

    private void loadData() {
        java.util.List<Chapter> chapters = db.getAllChapters();
        CourseAdapter adapter = new CourseAdapter(this, chapters);
        rcv.setAdapter(adapter);
    }

    private void updateHeaderProgress() {
        // 1. Lấy ID user
        android.content.SharedPreferences prefs = getSharedPreferences("UserSession", MODE_PRIVATE);
        int myId = prefs.getInt("KEY_USER_ID", -1);

        // 2. Tìm chương hiện tại đang học: chương đầu tiên chưa hoàn thành.
        Chapter currentChapter = db.getCurrentLearningChapter(myId);

        // 3. Ánh xạ và gán chữ lên thẻ Tiến độ
        TextView tvCurrentChapter = findViewById(R.id.tvCurrentChapter); // ID trong XML
        if (currentChapter != null && tvCurrentChapter != null) {
            tvCurrentChapter.setText(currentChapter.getTenChuong());
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadData();
        updateHeaderProgress(); // <--- GỌI HÀM NÀY
        // ... các code load dữ liệu khác
    }
}