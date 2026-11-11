package com.example.geolearn.ui.quiz;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.geolearn.R;
import com.example.geolearn.adapter.QuizAdapter;
import com.example.geolearn.data.DatabaseHelper;
import com.example.geolearn.model.Chapter;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.List;

public class QuizActivity extends AppCompatActivity {

    private RecyclerView rcvQuiz;
    private DatabaseHelper dbHelper;
    private QuizAdapter adapter;
    private List<Chapter> listChapters;
    private BottomNavigationView bottomNavigation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);

        dbHelper = new DatabaseHelper(this);

        // Nút Back
        findViewById(R.id.btnBack).setOnClickListener(v -> finish());

        // Cài đặt RecyclerView
        rcvQuiz = findViewById(R.id.rcvQuiz);

        // Chia làm 2 cột
        rcvQuiz.setLayoutManager(new GridLayoutManager(this, 2));
        com.example.geolearn.ui.main.StudentNav.setup(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadData(); // Load lại dữ liệu mỗi khi màn hình hiện lên (để cập nhật màu sắc)
        updateHeaderProgress();
    }

    private void loadData() {
        // 1. Lấy toàn bộ chương từ Database
        listChapters = dbHelper.getAllChapters();

        // 2. Đổ vào Adapter
        adapter = new QuizAdapter(this, listChapters);
        rcvQuiz.setAdapter(adapter);
    }

    private void updateHeaderProgress() {
        // 1. Lấy ID user
        android.content.SharedPreferences prefs = getSharedPreferences("UserSession", MODE_PRIVATE);
        int myId = prefs.getInt("KEY_USER_ID", -1);

        // 2. Tìm chương hiện tại
        DatabaseHelper db = new DatabaseHelper(this);
        Chapter currentChapter = db.getCurrentChapter(myId);

        // 3. Ánh xạ và gán chữ lên thẻ Tiến độ
        TextView tvCurrentChapter = findViewById(R.id.tvCurrentChapter); // ID trong XML
        if (currentChapter != null && tvCurrentChapter != null) {
            tvCurrentChapter.setText(currentChapter.getTenChuong());
        }
    }

}