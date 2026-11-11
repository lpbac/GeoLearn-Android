package com.example.geolearn.ui.auth;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.geolearn.R;
import com.example.geolearn.data.DatabaseHelper;
import com.google.android.material.textfield.TextInputEditText;

public class RegisterActivity extends AppCompatActivity {

    private TextInputEditText edtFullName;
    private TextInputEditText edtUsername;
    private TextInputEditText edtPassword;
    private TextInputEditText edtConfirmPassword;
    private TextInputEditText edtClassCode;
    private Spinner spinnerRole;
    private Button btnRegister;
    private TextView tvBackToLogin;

    private DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        databaseHelper = new DatabaseHelper(this);

        edtFullName = findViewById(R.id.edtFullName);
        edtUsername = findViewById(R.id.edtRegisterUsername);
        edtPassword = findViewById(R.id.edtRegisterPassword);
        edtConfirmPassword = findViewById(R.id.edtConfirmPassword);
        edtClassCode = findViewById(R.id.edtClassCode);
        spinnerRole = findViewById(R.id.spinnerRole);
        btnRegister = findViewById(R.id.btnRegister);
        tvBackToLogin = findViewById(R.id.tvBackToLogin);

        setupRoleSpinner();

        btnRegister.setOnClickListener(v -> handleRegister());
        tvBackToLogin.setOnClickListener(v -> finish());
    }

    private void setupRoleSpinner() {
        String[] roles = {"Học sinh", "Giáo viên"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, roles);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerRole.setAdapter(adapter);
    }

    // Hiện lỗi dưới ô nhập (TextInputLayout) thay vì icon lỗi đè lên nút con mắt
    private void showError(TextInputEditText edt, String msg) {
        android.view.ViewParent p = edt.getParent();
        while (p != null && !(p instanceof com.google.android.material.textfield.TextInputLayout))
            p = p.getParent();
        if (p == null) {
            edt.setError(msg);
            return;
        }
        com.google.android.material.textfield.TextInputLayout til = (com.google.android.material.textfield.TextInputLayout) p;
        til.setErrorIconDrawable(null); // giữ nút con mắt, lỗi chỉ hiện bằng chữ đỏ bên dưới
        til.setError(msg);
        if (msg != null)
            edt.requestFocus();
    }

    private void handleRegister() {
        String fullName = getInputText(edtFullName);
        String username = getInputText(edtUsername);
        String password = getInputText(edtPassword);
        String confirmPassword = getInputText(edtConfirmPassword);
        // Bỏ dấu cách quanh dấu phẩy ("12A1, 12A2"): truy vấn tìm giáo viên theo lớp so khớp chính xác
        String classCode = getInputText(edtClassCode).toUpperCase().replaceAll("\\s*,\\s*", ",");
        int role = spinnerRole.getSelectedItemPosition() == 0 ? 1 : 2;
        for (TextInputEditText e : new TextInputEditText[] { edtFullName, edtUsername, edtPassword, edtConfirmPassword, edtClassCode })
            showError(e, null);

        if (fullName.isEmpty()) {
            showError(edtFullName, "Vui lòng nhập họ tên");
            return;
        }
        if (username.isEmpty()) {
            showError(edtUsername, "Vui lòng nhập tên đăng nhập");
            return;
        }
        if (username.length() < 3) {
            showError(edtUsername, "Tên đăng nhập tối thiểu 3 ký tự");
            return;
        }
        if (password.isEmpty()) {
            showError(edtPassword, "Vui lòng nhập mật khẩu");
            return;
        }
        if (password.length() < 3) {
            showError(edtPassword, "Mật khẩu tối thiểu 3 ký tự");
            return;
        }
        if (!password.equals(confirmPassword)) {
            showError(edtConfirmPassword, "Mật khẩu xác nhận không khớp");
            return;
        }
        if (classCode.isEmpty()) {
            showError(edtClassCode, role == 1 ? "Vui lòng nhập mã lớp học" : "Vui lòng nhập lớp học phụ trách");
            return;
        }

        setRegisterEnabled(false);

        new Thread(() -> {
            if (databaseHelper.isUsernameExists(username)) {
                runOnUiThread(() -> {
                    setRegisterEnabled(true);
                    showError(edtUsername, "Tên đăng nhập đã tồn tại");
                    Toast.makeText(this, "Tên đăng nhập đã tồn tại", Toast.LENGTH_SHORT).show();
                });
                return;
            }

            long newUserId = databaseHelper.registerUser(username, password, fullName, role, classCode);
            runOnUiThread(() -> {
                setRegisterEnabled(true);
                if (newUserId > 0) {
                    Toast.makeText(this, "Đăng ký thành công, hãy đăng nhập", Toast.LENGTH_LONG).show();
                    finish();
                } else {
                    Toast.makeText(this, "Đăng ký thất bại, vui lòng thử lại", Toast.LENGTH_SHORT).show();
                }
            });
        }).start();
    }

    private String getInputText(TextInputEditText editText) {
        return editText.getText() == null ? "" : editText.getText().toString().trim();
    }

    private void setRegisterEnabled(boolean enabled) {
        btnRegister.setEnabled(enabled);
        btnRegister.setText(enabled ? "ĐĂNG KÝ" : "ĐANG ĐĂNG KÝ...");
    }
}
