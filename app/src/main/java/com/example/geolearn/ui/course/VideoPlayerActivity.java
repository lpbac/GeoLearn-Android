package com.example.geolearn.ui.course;

import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.Toast;
import android.widget.VideoView;
import androidx.appcompat.app.AppCompatActivity;
import com.example.geolearn.R;
import com.example.geolearn.data.DatabaseHelper;
import java.io.File;

public class VideoPlayerActivity extends AppCompatActivity {

    private VideoView videoView;
    private DatabaseHelper dbHelper;
    private ImageView btnPlayOverlay;
    private int currentChapterId = 1;

    // Cộng dồn thời gian xem thật: mỗi giây chỉ cộng phần tiến lên do phát bình thường, bỏ qua các lần tua
    private final android.os.Handler ticker = new android.os.Handler(android.os.Looper.getMainLooper());
    private long watchedMs = 0;
    private int lastPos = 0;
    private final Runnable trackWatchTime = new Runnable() {
        @Override
        public void run() {
            int pos = videoView.getCurrentPosition();
            int delta = pos - lastPos;
            if (videoView.isPlaying() && delta > 0 && delta <= 2000)
                watchedMs += delta;
            lastPos = pos;
            ticker.postDelayed(this, 1000);
        }
    };

    @Override
    protected void onDestroy() {
        ticker.removeCallbacks(trackWatchTime);
        super.onDestroy();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_player);

        videoView = findViewById(R.id.videoView);
        btnPlayOverlay = findViewById(R.id.btnPlayOverlay);
        dbHelper = new DatabaseHelper(this);

        // Nút Back
        findViewById(R.id.btnBack).setOnClickListener(v -> finish());

        // Tối ưu hóa ngăn chặn log "Empty SMPTE 2094-40 data" trên các máy Xiaomi/HDR
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            getWindow().setColorMode(android.content.pm.ActivityInfo.COLOR_MODE_DEFAULT);
        }
        videoView.getHolder().setFormat(android.graphics.PixelFormat.RGBA_8888);

        // 1. Nhận ID chương
        if (getIntent().hasExtra("CHAPTER_ID")) {
            currentChapterId = getIntent().getIntExtra("CHAPTER_ID", 1);
        }

        // 2. Lấy tên file từ Database
        String fileName = dbHelper.getVideoFileName(currentChapterId);

        if (fileName != null && !fileName.isEmpty()) {
            playVideoSmart(fileName);
        } else {
            Toast.makeText(this, "Không tìm thấy video cho chương này!", Toast.LENGTH_SHORT).show();
        }
    }

    // --- HÀM THÔNG MINH: TỰ ĐỘNG CHỌN NGUỒN VIDEO ---
    private void playVideoSmart(String fileName) {
        try {
            // CÁCH 1: Tìm trong bộ nhớ máy (Dành cho video Giáo viên mới upload)
            File fileInStorage = new File(getFilesDir(), fileName);
            if (fileInStorage.exists()) {
                // Nếu tìm thấy trong máy -> Chạy luôn
                videoView.setVideoPath(fileInStorage.getAbsolutePath());
                setupVideoController();
                return; // Xong việc, thoát hàm
            }

            // CÁCH 2: Tìm trong thư mục RAW (Dành cho video có sẵn trong code)
            String rawName = fileName.replace(".mp4", "").trim(); // Bỏ đuôi .mp4
            int resId = getResources().getIdentifier(rawName, "raw", getPackageName());

            if (resId != 0) {
                String path = "android.resource://" + getPackageName() + "/" + resId;
                videoView.setVideoURI(Uri.parse(path));
                setupVideoController();
            } else {
                // CÁCH 3: (Dự phòng) Nếu không thấy đâu cả
                Toast.makeText(this, "Lỗi: Không tìm thấy file video " + fileName, Toast.LENGTH_LONG).show();
            }

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Lỗi phát video: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void setupVideoController() {
        MediaController mediaController = new MediaController(this);
        videoView.setMediaController(mediaController);
        mediaController.setAnchorView(videoView);

        videoView.setOnPreparedListener(mp -> {
            videoView.start();
            if (btnPlayOverlay != null) {
                btnPlayOverlay.setVisibility(android.view.View.GONE);
            }
        });

        // Chỉ tính là đã xem khi thời gian xem thực tế (không tính phần tua) đạt từ 80% thời lượng
        ticker.post(trackWatchTime);
        videoView.setOnCompletionListener(mp -> {
            int duration = videoView.getDuration();
            if (duration <= 0 || watchedMs >= duration * 0.8) {
                dbHelper.saveDocProgress(
                        getSharedPreferences("UserSession", MODE_PRIVATE).getInt("KEY_USER_ID", -1),
                        currentChapterId, "Video", 1);
                Toast.makeText(this, "Đã hoàn thành video bài giảng!", Toast.LENGTH_SHORT).show();
            } else {
                int percent = (int) (watchedMs * 100L / duration);
                Toast.makeText(this, "Bạn mới xem " + percent + "% video. Hãy xem hết (không tua) để hoàn thành!",
                        Toast.LENGTH_LONG).show();
            }
        });

        if (btnPlayOverlay != null) {
            btnPlayOverlay.setOnClickListener(v -> {
                videoView.start();
                btnPlayOverlay.setVisibility(android.view.View.GONE);
            });
        }
    }
}