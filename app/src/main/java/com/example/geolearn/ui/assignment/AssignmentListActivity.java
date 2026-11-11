package com.example.geolearn.ui.assignment;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.geolearn.R;
import com.example.geolearn.adapter.AssignmentAdapter; // Import Adapter mới tạo
import com.example.geolearn.data.DatabaseHelper;
import com.example.geolearn.model.Chapter;

import java.util.List;

public class AssignmentListActivity extends AppCompatActivity {

    private RecyclerView rcvAssignment;
    private DatabaseHelper dbHelper;
    private int userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_assignment_list);

        dbHelper = new DatabaseHelper(this);

        // Lấy ID học sinh
        SharedPreferences prefs = getSharedPreferences("UserSession", MODE_PRIVATE);
        userId = prefs.getInt("KEY_USER_ID", -1);

        findViewById(R.id.btnBack).setOnClickListener(v -> finish());

        rcvAssignment = findViewById(R.id.rcvAssignment);
        rcvAssignment.setLayoutManager(new GridLayoutManager(this, 2));

        loadData();
        com.example.geolearn.ui.main.StudentNav.setup(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadData();
        updateHeaderProgress();
    }

    private void updateHeaderProgress() {
        TextView tvCurrentChapter = findViewById(R.id.tvCurrentChapter);
        if (tvCurrentChapter != null) {
            Chapter current = dbHelper.getCurrentChapter(userId);
            if (current != null) {
                tvCurrentChapter.setText(current.getTenChuong());
            }
        }
    }

    private void loadData() {
        List<Chapter> chapters = dbHelper.getAllChapters();

        // Gọi Adapter từ file riêng
        AssignmentAdapter adapter = new AssignmentAdapter(this, chapters, dbHelper, userId);
        rcvAssignment.setAdapter(adapter);
    }
}