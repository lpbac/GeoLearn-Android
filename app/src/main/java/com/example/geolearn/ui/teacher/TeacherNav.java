package com.example.geolearn.ui.teacher;

import android.app.Activity;
import android.content.Intent;

import com.example.geolearn.R;
import com.example.geolearn.ui.profile.ProfileActivity;

/** Chuyển tab dưới cho giáo viên: luôn đóng các màn con, để Back từ một tab quay về Trang chủ giáo viên. */
public final class TeacherNav {
    private TeacherNav() {}

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
