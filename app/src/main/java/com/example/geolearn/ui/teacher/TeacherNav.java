package com.example.geolearn.ui.teacher;

import android.app.Activity;
import android.content.Intent;

import com.example.geolearn.R;
import com.example.geolearn.ui.profile.ProfileActivity;

/** Chuyển tab dưới cho giáo viên: luôn đóng các màn con, để Back từ một tab quay về Trang chủ giáo viên. */
public final class TeacherNav {
    private TeacherNav() {}

    /** Gắn chuyển tab và số tin chưa đọc cho thanh dưới của màn giáo viên. */
    public static void setup(Activity from, com.google.android.material.bottomnavigation.BottomNavigationView nav) {
        if (nav == null) return;
        nav.setOnItemSelectedListener(item -> openTab(from, item.getItemId()));
        int myId = from.getSharedPreferences("UserSession", Activity.MODE_PRIVATE).getInt("KEY_USER_ID", -1);
        com.example.geolearn.ui.main.StudentNav.showChatBadge(nav,
                new com.example.geolearn.data.DatabaseHelper(from).getTotalUnreadCountForTeacher(myId));
    }

    public static boolean openTab(Activity from, int itemId) {
        Class<?> target = itemId == R.id.nav_home ? TeacherMainActivity.class
                : itemId == R.id.nav_chat ? TeacherChatActivity.class
                : itemId == R.id.nav_profile ? ProfileActivity.class
                : null;
        if (target == null) return false;
        if (target.isInstance(from)) return true;

        Intent home = new Intent(from, TeacherMainActivity.class)
                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        if (target == TeacherMainActivity.class) {
            from.startActivity(home);
            return true;
        }
        // CLEAR_TOP đóng mọi màn phía trên Trang chủ giáo viên, rồi mở tab đích lên trên
        from.startActivities(new Intent[]{home, new Intent(from, target)});
        return false; // không đổi mục đang chọn ở màn nguồn (Trang chủ vẫn nằm dưới, Back về đó)
    }
}
