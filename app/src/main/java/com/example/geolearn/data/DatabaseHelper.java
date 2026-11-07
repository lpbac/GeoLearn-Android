package com.example.geolearn.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.geolearn.model.Chapter; // Import Model Chapter
import com.example.geolearn.model.Question; // Import Model Question
import com.example.geolearn.model.User; // Import Model User

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

    public static class TeacherAnalytics {
        public String className;
        public int totalStudents;
        public int totalChapters;
        public int completedItems;
        public int totalItems;
        public int excellentCount;
        public int goodCount;
        public int averageCount;
        public int weakCount;
        public final List<String> warnings = new ArrayList<>();
    }

    public static class AbilityItem {
        public String label;
        public int scorePercent;
        public String status;

        public AbilityItem(String label, int scorePercent, String status) {
            this.label = label;
            this.scorePercent = scorePercent;
            this.status = status;
        }
    }

    private static final String DATABASE_NAME = "GeoLearn.db";
    private static final int DATABASE_VERSION = 3;

    // --- 1. ĐỊNH NGHĨA CÁC HẰNG SỐ (CONSTANTS) ---

    // Bảng Người dùng
    public static final String TABLE_USER = "NguoiDung";
    public static final String COL_USER_ID = "MaNguoiDung";
    public static final String COL_USERNAME = "TenDangNhap";
    public static final String COL_PASSWORD = "MatKhau";
    public static final String COL_FULLNAME = "HoTen";
    public static final String COL_ROLE = "QuyenHan";
    public static final String COL_CLASS = "MaLop";

    // Bảng Chương học
    public static final String TABLE_CHAPTER = "ChuongHoc";
    public static final String COL_CHAPTER_ID = "MaChuong";
    public static final String COL_CHAPTER_NAME = "TenChuong";
    public static final String COL_CHAPTER_DESC = "MoTa";
    public static final String COL_CHAPTER_ORDER = "ThuTuBaiHoc";

    // Bảng Tài liệu
    public static final String TABLE_DOCUMENT = "TaiLieu";
    public static final String COL_DOC_ID = "MaTaiLieu";
    public static final String COL_DOC_CHAPTER_ID = "MaChuong";
    public static final String COL_DOC_TYPE = "Loai"; // 'PDF' hoặc 'Video'
    public static final String COL_DOC_FILENAME = "TenFile"; // Tên file trong Assets

    // Bảng Câu hỏi (Mới)
    public static final String TABLE_QUESTION = "CauHoi";
    public static final String COL_Q_ID = "MaCH";
    public static final String COL_Q_CHAPTER_ID = "MaChuong";
    public static final String COL_Q_CONTENT = "NoiDung";
    public static final String COL_Q_A = "DapAnA";
    public static final String COL_Q_B = "DapAnB";
    public static final String COL_Q_C = "DapAnC";
    public static final String COL_Q_D = "DapAnD";
    public static final String COL_Q_CORRECT = "DapAnDung";

    // Bảng Điểm số (Mới)
    public static final String TABLE_SCORE = "DiemSo";
    public static final String COL_SCORE_ID = "ID";
    public static final String COL_SCORE_USER_ID = "MaNguoiDung";
    public static final String COL_SCORE_CHAPTER_ID = "MaChuong";
    public static final String COL_SCORE_VALUE = "Diem";

    // --- BẢNG MỚI: BÀI TẬP (ASSIGNMENT) ---
    public static final String TABLE_ASSIGNMENT = "BaiTap";
    public static final String COL_ASS_ID = "MaBT";
    public static final String COL_ASS_CHAPTER_ID = "MaChuong";
    public static final String COL_ASS_CONTENT = "DeBai";

    // --- BẢNG MỚI: NỘP BÀI (SUBMISSION) ---
    public static final String TABLE_SUBMISSION = "NopBai";
    public static final String COL_SUB_ID = "MaNop";
    public static final String COL_SUB_USER_ID = "MaNguoiDung";
    public static final String COL_SUB_ASS_ID = "MaBT";
    public static final String COL_SUB_TEXT = "TraLoiVanBan";
    public static final String COL_SUB_FILE = "DuongDanFile"; // Lưu URI của file PDF/Word
    public static final String COL_SUB_GRADE = "DiemGV"; // Mới
    public static final String COL_SUB_FEEDBACK = "NhanXetGV"; // Mới

    // --- BẢNG MỚI: TIN NHẮN (CHAT) ---
    public static final String TABLE_MESSAGE = "TinNhan";
    public static final String COL_MSG_ID = "MaTN";
    public static final String COL_MSG_SENDER_ID = "NguoiGui"; // ID người gửi
    public static final String COL_MSG_CONTENT = "NoiDung";
    public static final String COL_MSG_TIME = "ThoiGian";
    public static final String COL_MSG_ROOM = "PhongChat";
    private static final String COL_MSG_IS_READ = "IsRead";

    private final Context context;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Tạo bảng User
        String createTableUser = "CREATE TABLE " + TABLE_USER + " (" +
                COL_USER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_USERNAME + " TEXT, " +
                COL_PASSWORD + " TEXT, " +
                COL_FULLNAME + " TEXT, " +
                COL_ROLE + " INTEGER, " +
                COL_CLASS + " TEXT)";
        db.execSQL(createTableUser);

        // Tạo bảng Chapter
        String createTableChapter = "CREATE TABLE " + TABLE_CHAPTER + " (" +
                COL_CHAPTER_ID + " INTEGER PRIMARY KEY, " +
                COL_CHAPTER_NAME + " TEXT, " +
                COL_CHAPTER_DESC + " TEXT, " +
                COL_CHAPTER_ORDER + " INTEGER)";
        db.execSQL(createTableChapter);

        // Tạo bảng Document
        String createTableDoc = "CREATE TABLE " + TABLE_DOCUMENT + " (" +
                COL_DOC_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_DOC_CHAPTER_ID + " INTEGER, " +
                COL_DOC_TYPE + " TEXT, " +
                COL_DOC_FILENAME + " TEXT)";
        db.execSQL(createTableDoc);

        // Tạo bảng Question
        String createTableQuestion = "CREATE TABLE " + TABLE_QUESTION + " (" +
                COL_Q_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_Q_CHAPTER_ID + " INTEGER, " +
                COL_Q_CONTENT + " TEXT, " +
                COL_Q_A + " TEXT, " + COL_Q_B + " TEXT, " + COL_Q_C + " TEXT, " + COL_Q_D + " TEXT, " +
                COL_Q_CORRECT + " TEXT)";
        db.execSQL(createTableQuestion);

        // Tạo bảng Score
        String createTableScore = "CREATE TABLE " + TABLE_SCORE + " (" +
                COL_SCORE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_SCORE_USER_ID + " INTEGER, " +
                COL_SCORE_CHAPTER_ID + " INTEGER, " +
                COL_SCORE_VALUE + " INTEGER)";
        db.execSQL(createTableScore);

        // Tạo bảng Bài tập
        db.execSQL("CREATE TABLE " + TABLE_ASSIGNMENT + " (" +
                COL_ASS_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_ASS_CHAPTER_ID + " INTEGER, " +
                COL_ASS_CONTENT + " TEXT)");

        db.execSQL("CREATE TABLE " + TABLE_SUBMISSION + " (" +
                COL_SUB_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_SUB_USER_ID + " INTEGER, " +
                COL_SUB_ASS_ID + " INTEGER, " +
                COL_SUB_TEXT + " TEXT, " +
                COL_SUB_FILE + " TEXT, " +
                COL_SUB_GRADE + " REAL DEFAULT -1, " + // Thêm cột điểm (-1 là chưa chấm)
                COL_SUB_FEEDBACK + " TEXT)"); // Thêm cột nhận xét

        String createMessageTable = "CREATE TABLE " + TABLE_MESSAGE + " (" +
                COL_MSG_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_MSG_SENDER_ID + " INTEGER, " +
                COL_MSG_CONTENT + " TEXT, " +
                COL_MSG_ROOM + " TEXT, " +
                COL_MSG_TIME + " TEXT, " + // Giả sử bạn đã có cột thời gian này
                COL_MSG_IS_READ + " INTEGER DEFAULT 0)"; // Mặc định là 0 (Chưa xem)
        db.execSQL(createMessageTable);

        // Nạp dữ liệu mẫu
        insertSampleData(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USER);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CHAPTER);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_DOCUMENT);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_QUESTION);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SCORE);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ASSIGNMENT);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SUBMISSION);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_MESSAGE);
        onCreate(db);
    }

    private void insertSampleData(SQLiteDatabase db) {
        // Dữ liệu User
        // User chính (ID 1)
        db.execSQL("INSERT INTO " + TABLE_USER + " VALUES(null, 'sv1', '" + hashPassword("123") + "', 'Nguyễn Văn Minh', 1, '12A1')");

        // --- THÊM BẠN BÈ CÙNG LỚP 12A1 (Để test tính năng Chat) ---
        db.execSQL("INSERT INTO " + TABLE_USER + " VALUES(null, 'sv2', '" + hashPassword("123") + "', 'Phạm Thùy Trang', 1, '12A1')");
        db.execSQL("INSERT INTO " + TABLE_USER + " VALUES(null, 'sv3', '" + hashPassword("123") + "', 'Trương Hồng Tân', 1, '12A1')");
        db.execSQL("INSERT INTO " + TABLE_USER + " VALUES(null, 'sv4', '" + hashPassword("123") + "', 'Hoàng Minh Khải', 1, '12A1')");

        // Một bạn lớp khác (Sẽ KHÔNG hiện trong danh sách chat)
        db.execSQL("INSERT INTO " + TABLE_USER + " VALUES(null, 'sv5', '" + hashPassword("123") + "', 'Người Lớp Khác', 1, '12A2')");

        // SỬA DÒNG NÀY: Thầy Hậu dạy 2 lớp là 12A1 và 12A2 (ngăn cách bởi dấu phẩy)
        db.execSQL("INSERT INTO " + TABLE_USER + " VALUES(null, 'gv1', '" + hashPassword("123") + "', 'Đặng Hồng Phúc', 2, '12A2,12A1')");
        db.execSQL("INSERT INTO " + TABLE_USER + " VALUES(null, 'gv2', '" + hashPassword("123") + "', 'Nguyễn Kiều Hưng', 2, '12A2,12A1')");

        // Dữ liệu Chương học
        db.execSQL("INSERT INTO " + TABLE_CHAPTER
                + " VALUES(1, 'Chương 1: Địa lý tự nhiên Việt Nam', 'Tìm hiểu vị trí địa lý, hình dạng lãnh thổ, địa hình, khí hậu, sông ngòi và biển đảo của Việt Nam.', 1)");
        db.execSQL("INSERT INTO " + TABLE_CHAPTER
                + " VALUES(2, 'Chương 2: Địa lý dân cư và các ngành kinh tế', 'Tìm hiểu đặc điểm dân cư, nguồn lao động, đô thị hóa và sự phát triển các ngành nông nghiệp, công nghiệp, dịch vụ của Việt Nam.', 2)");
        db.execSQL("INSERT INTO " + TABLE_CHAPTER
                + " VALUES(3, 'Chương 3: Bản đồ và Địa lý thế giới', 'Rèn kỹ năng đọc bản đồ, Atlat, tính tỉ lệ và tìm hiểu các châu lục, đại dương, đới khí hậu trên thế giới.', 3)");

        // Dữ liệu Tài liệu
        db.execSQL("INSERT INTO " + TABLE_DOCUMENT + " VALUES(null, 1, 'PDF', 'Chuong1.pdf')");
        db.execSQL("INSERT INTO " + TABLE_DOCUMENT + " VALUES(null, 1, 'Video', 'video1.mp4')");
        db.execSQL("INSERT INTO " + TABLE_DOCUMENT + " VALUES(null, 2, 'PDF', 'Chuong2.pdf')");
        db.execSQL("INSERT INTO " + TABLE_DOCUMENT + " VALUES(null, 2, 'Video', 'video2.mp4')");
        db.execSQL("INSERT INTO " + TABLE_DOCUMENT + " VALUES(null, 3, 'PDF', 'Chuong3.pdf')");
        db.execSQL("INSERT INTO " + TABLE_DOCUMENT + " VALUES(null, 3, 'Video', 'video3.mp4')");

        // // Dữ liệu Câu hỏi Trắc nghiệm
        // db.execSQL("INSERT INTO " + TABLE_QUESTION + " VALUES(null, 1, 'Máy tính
        // ENIAC ra đời năm nào?', '1945', '1946', '1950', '1955', 'B')");
        // db.execSQL("INSERT INTO " + TABLE_QUESTION + " VALUES(null, 1, 'Thế hệ máy
        // tính thứ nhất dùng linh kiện gì?', 'Bóng bán dẫn', 'Vi mạch', 'Đèn điện tử
        // chân không', 'Chip AI', 'C')");
        // db.execSQL("INSERT INTO " + TABLE_QUESTION + " VALUES(null, 2, 'ALU là viết
        // tắt của gì?', 'Arithmetic Logic Unit', 'All Logic Unit', 'Array Logic Unit',
        // 'Area Logic Unit', 'A')");

        importQuestionsFromAssets(db);

        // Thêm đề bài mẫu cho Chương 1 và 2
        // Thêm đề bài mẫu cho Chương 1, 2 và 3
        db.execSQL("INSERT INTO " + TABLE_ASSIGNMENT
                + " VALUES(null, 1, 'Trình bày đặc điểm vị trí địa lý của Việt Nam và phân tích ý nghĩa của vị trí đó đối với tự nhiên và phát triển kinh tế.')");
        db.execSQL("INSERT INTO " + TABLE_ASSIGNMENT
                + " VALUES(null, 2, 'Phân tích đặc điểm khí hậu nhiệt đới ẩm gió mùa ở nước ta và nêu ảnh hưởng của nó đến sản xuất nông nghiệp.')");
        db.execSQL("INSERT INTO " + TABLE_ASSIGNMENT
                + " VALUES(null, 3, 'Một bản đồ có tỉ lệ 1:200 000, khoảng cách giữa hai điểm trên bản đồ là 6 cm. Tính khoảng cách thực tế (ra km) và trình bày các bước tính.')");

        // Thêm vài tin nhắn mẫu cho xôm tụ
        // Tin nhắn nhóm lớp
        // Sửa lại insertSampleData trong DatabaseHelper.java

        // Tin nhắn nhóm lớp (QUAN TRỌNG: Phải để số 1 ở cuối để coi là ĐÃ ĐỌC)
        db.execSQL("INSERT INTO " + TABLE_MESSAGE
                + " VALUES(null, 6, 'Chào lớp 12A1, hôm nay các em đọc trước tài liệu Chương 2 nhé.', 'Lớp 12A1', '08:05', 1)");
        db.execSQL("INSERT INTO " + TABLE_MESSAGE
                + " VALUES(null, 1, 'Dạ thầy, em đã tải tài liệu về rồi ạ.', 'Lớp 12A1', '08:08', 1)");
        db.execSQL("INSERT INTO " + TABLE_MESSAGE
                + " VALUES(null, 2, 'Thầy ơi phần bài tập Chương 1 nộp đến khi nào ạ?', 'Lớp 12A1', '08:12', 1)");
        db.execSQL("INSERT INTO " + TABLE_MESSAGE
                + " VALUES(null, 6, 'Hạn nộp là 22h tối nay, các em chú ý hoàn thành đúng hạn.', 'Lớp 12A1', '08:15', 1)");

        // Tin riêng mẫu giữa học sinh và bạn cùng lớp / giáo viên
        db.execSQL("INSERT INTO " + TABLE_MESSAGE
                + " VALUES(null, 2, 'Minh ơi, cậu làm xong câu hỏi ôn tập chưa?', '1_2', '09:20', 1)");
        db.execSQL("INSERT INTO " + TABLE_MESSAGE
                + " VALUES(null, 1, 'Mình làm gần xong rồi, lát gửi cậu phần gợi ý nhé.', '1_2', '09:24', 1)");
        db.execSQL("INSERT INTO " + TABLE_MESSAGE
                + " VALUES(null, 6, 'Minh nhớ bổ sung bản đồ minh họa vào bài nộp nhé.', '1_6', '14:10', 1)");
        db.execSQL("INSERT INTO " + TABLE_MESSAGE
                + " VALUES(null, 1, 'Dạ vâng thầy, em sẽ cập nhật ngay ạ.', '1_6', '14:18', 1)");
        db.execSQL("INSERT INTO " + TABLE_MESSAGE
                + " VALUES(null, 3, 'Tối nay nhóm mình ôn quiz Chương 2 không?', '1_3', '19:30', 1)");
    }

    // ==========================================================
    // CÁC HÀM TRUY VẤN DỮ LIỆU (QUAN TRỌNG)
    // ==========================================================

    // 1. Lấy tên file PDF
    public String getPdfFileName(int maChuong) {
        SQLiteDatabase db = this.getReadableDatabase();
        String fileName = "";
        Cursor cursor = db.rawQuery(
                "SELECT " + COL_DOC_FILENAME + " FROM " + TABLE_DOCUMENT +
                        " WHERE " + COL_DOC_CHAPTER_ID + "=? AND " + COL_DOC_TYPE + "='PDF'",
                new String[] { String.valueOf(maChuong) });
        if (cursor.moveToFirst()) {
            fileName = cursor.getString(0);
        }
        cursor.close();
        return fileName;
    }

    // 2. Lấy tên file Video
    public String getVideoFileName(int maChuong) {
        SQLiteDatabase db = this.getReadableDatabase();
        String fileName = "";
        Cursor cursor = db.rawQuery(
                "SELECT " + COL_DOC_FILENAME + " FROM " + TABLE_DOCUMENT +
                        " WHERE " + COL_DOC_CHAPTER_ID + "=? AND " + COL_DOC_TYPE + "='Video'",
                new String[] { String.valueOf(maChuong) });
        if (cursor.moveToFirst()) {
            fileName = cursor.getString(0);
        }
        cursor.close();
        return fileName;
    }

    // 3. Lấy Danh sách Tất cả Chương (ĐÃ BỔ SUNG LẠI)
    public List<Chapter> getAllChapters() {
        List<Chapter> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        // Sắp xếp theo thứ tự
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_CHAPTER + " ORDER BY " + COL_CHAPTER_ORDER + " ASC", null);

        if (cursor.moveToFirst()) {
            do {
                // Lấy dữ liệu theo cột index (0=ID, 1=Tên, 2=Mô tả, 3=Thứ tự)
                int id = cursor.getInt(0);
                String name = cursor.getString(1);
                String desc = cursor.getString(2);
                int order = cursor.getInt(3);

                // Logic khóa: Ví dụ mở chương 1 & 2, khóa chương 3 trở đi
                boolean isLocked = (id > 2);

                list.add(new Chapter(id, name, desc, order, isLocked));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return list;
    }

    // 4. Kiểm tra Đăng nhập (ĐÃ BỔ SUNG LẠI)
    public User checkLogin(String username, String password) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT * FROM " + TABLE_USER + " WHERE " + COL_USERNAME + "=?",
                new String[] { username });

        while (cursor.moveToNext()) {
            String stored = cursor.getString(2);
            boolean legacy = stored != null && !stored.contains("$");
            boolean ok = legacy ? stored.equals(password) : verifyPassword(password, stored);
            if (!ok) continue;
            if (legacy) { // DB cũ còn mật khẩu thô: băm lại ngay khi đăng nhập đúng
                ContentValues v = new ContentValues();
                v.put(COL_PASSWORD, hashPassword(password));
                db.update(TABLE_USER, v, COL_USER_ID + "=?", new String[] { String.valueOf(cursor.getInt(0)) });
            }
            User user = new User();
            user.setMaNguoiDung(cursor.getInt(0)); // Cột 0: ID
            user.setTenDangNhap(cursor.getString(1)); // Cột 1: Username
            // Cột 2: Password
            user.setHoTen(cursor.getString(3)); // Cột 3: Họ tên
            user.setQuyenHan(cursor.getInt(4)); // Cột 4: Quyền hạn
            user.setMaLop(cursor.getString(5)); // Cột 5: Mã lớp

            cursor.close();
            return user;
        }
        cursor.close();
        return null; // Đăng nhập thất bại
    }

    // Định dạng lưu: "<salt hex>$<sha256(salt + password) hex>"
    // shortcut: SHA-256 một vòng dễ brute-force hơn PBKDF2/bcrypt, nâng cấp nếu có server/dữ liệu thật.
    static String hashPassword(String password) {
        byte[] salt = new byte[16];
        new java.security.SecureRandom().nextBytes(salt);
        return toHex(salt) + "$" + sha256(salt, password);
    }

    static boolean verifyPassword(String password, String stored) {
        int i = stored.indexOf('$');
        if (i <= 0) return false;
        byte[] salt = fromHex(stored.substring(0, i));
        return java.security.MessageDigest.isEqual(
                sha256(salt, password).getBytes(java.nio.charset.StandardCharsets.UTF_8),
                stored.substring(i + 1).getBytes(java.nio.charset.StandardCharsets.UTF_8));
    }

    private static String sha256(byte[] salt, String password) {
        try {
            java.security.MessageDigest md = java.security.MessageDigest.getInstance("SHA-256");
            md.update(salt);
            return toHex(md.digest(password.getBytes(java.nio.charset.StandardCharsets.UTF_8)));
        } catch (java.security.NoSuchAlgorithmException e) {
            throw new IllegalStateException(e); // SHA-256 luôn có trên Android
        }
    }

    private static String toHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) sb.append(String.format("%02x", b));
        return sb.toString();
    }

    private static byte[] fromHex(String hex) {
        byte[] out = new byte[hex.length() / 2];
        for (int i = 0; i < out.length; i++)
            out[i] = (byte) Integer.parseInt(hex.substring(2 * i, 2 * i + 2), 16);
        return out;
    }

    public boolean isUsernameExists(String username) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT " + COL_USER_ID + " FROM " + TABLE_USER + " WHERE " + COL_USERNAME + "=? LIMIT 1",
                new String[] { username });
        boolean exists = cursor.moveToFirst();
        cursor.close();
        return exists;
    }

    public long registerUser(String username, String password, String fullName, int role, String classCode) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_USERNAME, username);
        values.put(COL_PASSWORD, hashPassword(password));
        values.put(COL_FULLNAME, fullName);
        values.put(COL_ROLE, role);
        values.put(COL_CLASS, classCode);
        return db.insert(TABLE_USER, null, values);
    }

    // 5. Lấy danh sách Câu hỏi theo Chương (Mới cho Quiz)
    // Trong DatabaseHelper.java

    public List<Question> getQuestionsByChapter(int chapterId) {
        List<Question> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        String query = "SELECT * FROM " + TABLE_QUESTION + " WHERE " + COL_Q_CHAPTER_ID + " = ?";
        Cursor cursor = db.rawQuery(query, new String[] { String.valueOf(chapterId) });

        if (cursor.moveToFirst()) {
            do {
                // 1. LẤY ID (QUAN TRỌNG)
                // Giả sử cột ID là cột đầu tiên (index 0). Hoặc dùng
                // cursor.getColumnIndex(COL_Q_ID)
                int id = cursor.getInt(0);

                String content = cursor.getString(cursor.getColumnIndexOrThrow(COL_Q_CONTENT));
                String a = cursor.getString(cursor.getColumnIndexOrThrow(COL_Q_A));
                String b = cursor.getString(cursor.getColumnIndexOrThrow(COL_Q_B));
                String c = cursor.getString(cursor.getColumnIndexOrThrow(COL_Q_C));
                String d = cursor.getString(cursor.getColumnIndexOrThrow(COL_Q_D));
                String correct = cursor.getString(cursor.getColumnIndexOrThrow(COL_Q_CORRECT));

                // 2. DÙNG CONSTRUCTOR MỚI (CÓ ID)
                list.add(new Question(id, content, a, b, c, d, correct));

            } while (cursor.moveToNext());
        }
        cursor.close();
        return list;
    }

    // 1. Lưu điểm thi (Nếu chưa thi thì thêm mới, thi rồi thì cập nhật điểm mới)
    public void saveQuizScore(int userId, int chapterId, int correctAnswers) {
        SQLiteDatabase db = this.getWritableDatabase();
        android.content.ContentValues values = new android.content.ContentValues();
        values.put(COL_SCORE_USER_ID, userId);
        values.put(COL_SCORE_CHAPTER_ID, chapterId);
        values.put(COL_SCORE_VALUE, correctAnswers); // Lưu số câu đúng

        // Thử cập nhật điểm cũ trước
        int rows = db.update(TABLE_SCORE, values,
                COL_SCORE_USER_ID + "=? AND " + COL_SCORE_CHAPTER_ID + "=?",
                new String[] { String.valueOf(userId), String.valueOf(chapterId) });

        // Nếu không tìm thấy dòng nào để cập nhật -> Thêm mới
        if (rows == 0) {
            db.insert(TABLE_SCORE, null, values);
        }
    }

    // 2. Lấy điểm thi của một chương
    // Trả về: Số câu đúng (hoặc -1 nếu chưa làm bài)
    public int getQuizScore(int userId, int chapterId) {
        SQLiteDatabase db = this.getReadableDatabase();
        int score = -1; // Mặc định là -1 (Chưa làm)

        Cursor cursor = db.rawQuery(
                "SELECT " + COL_SCORE_VALUE + " FROM " + TABLE_SCORE +
                        " WHERE " + COL_SCORE_USER_ID + "=? AND " + COL_SCORE_CHAPTER_ID + "=?",
                new String[] { String.valueOf(userId), String.valueOf(chapterId) });

        if (cursor.moveToFirst()) {
            score = cursor.getInt(0);
        }
        cursor.close();
        return score;
    }

    // --- HÀM LẤY ĐỀ BÀI (Thêm vào cuối file) ---
    public String getAssignmentQuestion(int chapterId) {
        String content = getAssignmentContent(chapterId);
        return content.isEmpty() ? "Không có bài tập cho chương này." : content;
    }

    // Nội dung bài tập thật, "" nếu chương không có bài tập (dùng cho form sửa chương)
    public String getAssignmentContent(int chapterId) {
        SQLiteDatabase db = this.getReadableDatabase();
        String content = "";
        Cursor cursor = db.rawQuery(
                "SELECT " + COL_ASS_CONTENT + " FROM " + TABLE_ASSIGNMENT + " WHERE " + COL_ASS_CHAPTER_ID + "=?",
                new String[] { String.valueOf(chapterId) });
        if (cursor.moveToFirst() && cursor.getString(0) != null) {
            content = cursor.getString(0);
        }
        cursor.close();
        return content;
    }


    // --- HÀM LƯU BÀI LÀM (Thêm vào cuối file) ---
    public void saveSubmission(int userId, int chapterId, String text, String filePath) {
        SQLiteDatabase db = this.getWritableDatabase();
        // Lấy ID bài tập dựa trên chương (Giả sử mỗi chương 1 bài)
        // Trong thực tế cần logic phức tạp hơn, nhưng BTL làm vậy là ổn
        int assId = 0;
        Cursor c = db.rawQuery(
                "SELECT " + COL_ASS_ID + " FROM " + TABLE_ASSIGNMENT + " WHERE " + COL_ASS_CHAPTER_ID + "=?",
                new String[] { String.valueOf(chapterId) });
        if (c.moveToFirst())
            assId = c.getInt(0);
        c.close();

        if (assId > 0) {
            android.content.ContentValues values = new android.content.ContentValues();
            values.put(COL_SUB_USER_ID, userId);
            values.put(COL_SUB_ASS_ID, assId);
            values.put(COL_SUB_TEXT, text);
            values.put(COL_SUB_FILE, filePath);
            values.put(COL_SUB_GRADE, -1);
            values.put(COL_SUB_FEEDBACK, "");

            int updatedRows = db.update(TABLE_SUBMISSION, values,
                    COL_SUB_USER_ID + "=? AND " + COL_SUB_ASS_ID + "=?",
                    new String[] { String.valueOf(userId), String.valueOf(assId) });
            if (updatedRows == 0) {
                db.insert(TABLE_SUBMISSION, null, values);
            }
        }
    }

    // Hàm lấy bài làm của học sinh (để xem lại hoặc kiểm tra đã nộp chưa)
    // Trả về: String[] {Nội dung, Đường dẫn file} hoặc NULL nếu chưa làm
    public String[] getSubmissionDetail(int userId, int chapterId) {
        SQLiteDatabase db = this.getReadableDatabase();
        String[] result = null;

        // Lấy ID bài tập của chương này
        int assId = 0;
        Cursor c1 = db.rawQuery(
                "SELECT " + COL_ASS_ID + " FROM " + TABLE_ASSIGNMENT + " WHERE " + COL_ASS_CHAPTER_ID + "=?",
                new String[] { String.valueOf(chapterId) });
        if (c1.moveToFirst())
            assId = c1.getInt(0);
        c1.close();

        if (assId > 0) {
            // Tìm trong bảng NopBai
            Cursor c2 = db.rawQuery("SELECT " + COL_SUB_TEXT + ", " + COL_SUB_FILE + " FROM " + TABLE_SUBMISSION +
                    " WHERE " + COL_SUB_USER_ID + "=? AND " + COL_SUB_ASS_ID + "=?" +
                    " ORDER BY CASE WHEN " + COL_SUB_FILE + " IS NOT NULL AND " + COL_SUB_FILE + " != '' THEN 0 ELSE 1 END, " + COL_SUB_ID + " DESC LIMIT 1",
                    new String[] { String.valueOf(userId), String.valueOf(assId) });

            if (c2.moveToFirst()) {
                result = new String[2];
                result[0] = c2.getString(0); // Nội dung văn bản
                result[1] = c2.getString(1); // Đường dẫn file
            }
            c2.close();
        }
        return result;
    }

    // --- SỬA HÀM GỬI TIN NHẮN (Thêm tham số roomName) ---
    public void sendGroupMessage(int senderId, String content, String roomName) {
        SQLiteDatabase db = this.getWritableDatabase();
        android.content.ContentValues values = new android.content.ContentValues();
        values.put(COL_MSG_SENDER_ID, senderId);
        values.put(COL_MSG_CONTENT, content);

        // Lấy giờ hiện tại
        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("HH:mm", java.util.Locale.getDefault());
        values.put(COL_MSG_TIME, sdf.format(new java.util.Date()));

        values.put(COL_MSG_ROOM, roomName); // <-- LƯU TÊN PHÒNG

        db.insert(TABLE_MESSAGE, null, values);
    }

    // --- SỬA HÀM LẤY TIN NHẮN (Thêm điều kiện WHERE PhongChat) ---
    public Cursor getMessagesByRoom(String roomName) {
        SQLiteDatabase db = this.getReadableDatabase();
        // Chỉ lấy tin nhắn của phòng chat này thôi
        String query = "SELECT m.*, u.HoTen FROM " + TABLE_MESSAGE + " m " +
                "LEFT JOIN " + TABLE_USER + " u ON m." + COL_MSG_SENDER_ID + " = u." + COL_USER_ID +
                " WHERE m." + COL_MSG_ROOM + " = ?"; // <-- ĐIỀU KIỆN LỌC

        return db.rawQuery(query, new String[] { roomName });
    }

    // Sửa lại hàm getLastMessage (Thêm tham số myId)
    public String getLastMessage(String roomId, int myId) {
        SQLiteDatabase db = this.getReadableDatabase();
        String finalResult = "";

        // Query giữ nguyên
        String query = "SELECT m." + COL_MSG_CONTENT + ", m." + COL_MSG_SENDER_ID + ", u." + COL_FULLNAME +
                " FROM " + TABLE_MESSAGE + " m " +
                " LEFT JOIN " + TABLE_USER + " u ON m." + COL_MSG_SENDER_ID + " = u." + COL_USER_ID +
                " WHERE m." + COL_MSG_ROOM + "=? " +
                " ORDER BY m." + COL_MSG_ID + " DESC LIMIT 1";

        Cursor cursor = db.rawQuery(query, new String[] { roomId });

        if (cursor.moveToFirst()) {
            String content = cursor.getString(0);
            int senderId = cursor.getInt(1);
            String senderName = cursor.getString(2);

            // --- LOGIC MỚI Ở ĐÂY ---
            if (senderId == myId) {
                // Nếu ID người gửi TRÙNG VỚI ID CỦA MÌNH -> Hiện "Bạn: ..."
                finalResult = "Bạn: " + content;
            } else {
                // Nếu là người khác -> Hiện "Tên: ..."
                if (senderName == null)
                    senderName = "Ẩn danh";

                // Cắt lấy tên cuối cùng (Ví dụ: Trần Thị Hoa -> Hoa)
                String[] nameParts = senderName.split(" ");
                String shortName = nameParts.length > 0 ? nameParts[nameParts.length - 1] : senderName;

                finalResult = shortName + ": " + content;
            }
        }
        cursor.close();
        return finalResult;
    }

    // Hàm lấy thông tin User theo ID (để hiện lên Profile)
    public User getUserById(int userId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_USER + " WHERE " + COL_USER_ID + "=?",
                new String[] { String.valueOf(userId) });

        if (cursor.moveToFirst()) {
            User user = new User();
            user.setMaNguoiDung(cursor.getInt(0));
            user.setTenDangNhap(cursor.getString(1));
            // user.setMatKhau(cursor.getString(2)); // Không cần lấy mật khẩu
            user.setHoTen(cursor.getString(3));
            user.setQuyenHan(cursor.getInt(4));
            user.setMaLop(cursor.getString(5));

            cursor.close();
            return user;
        }
        cursor.close();
        return null;
    }

    public boolean updateUserFullName(int userId, String fullName) {
        SQLiteDatabase db = this.getWritableDatabase();
        android.content.ContentValues values = new android.content.ContentValues();
        values.put(COL_FULLNAME, fullName);
        int rows = db.update(TABLE_USER, values, COL_USER_ID + "=?", new String[] { String.valueOf(userId) });
        return rows > 0;
    }

    // 1. Lấy Mã Lớp của User hiện tại
    public String getUserClass(int userId) {
        SQLiteDatabase db = this.getReadableDatabase();
        String maLop = "";
        Cursor cursor = db.rawQuery("SELECT " + COL_CLASS + " FROM " + TABLE_USER + " WHERE " + COL_USER_ID + "=?",
                new String[] { String.valueOf(userId) });
        if (cursor.moveToFirst()) {
            maLop = cursor.getString(0);
        }
        cursor.close();
        return maLop;
    }

    // 2. Lấy danh sách tên các bạn cùng lớp (Trừ chính mình ra)
    public List<android.util.Pair<Integer, String>> getClassmates(String maLop, int myId) {
        List<android.util.Pair<Integer, String>> names = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        // Lấy Họ Tên của những người cùng MaLop nhưng khác ID của mình
        Cursor cursor = db.rawQuery("SELECT " + COL_USER_ID + ", " + COL_FULLNAME + " FROM " + TABLE_USER +
                " WHERE " + COL_CLASS + "=? AND " + COL_USER_ID + "!=?",
                new String[] { maLop, String.valueOf(myId) });

        if (cursor.moveToFirst()) {
            do {
                names.add(new android.util.Pair<>(cursor.getInt(0), cursor.getString(1)));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return names;
    }

    public List<android.util.Pair<Integer, String>> getTeachersByClass(String maLop) {
        List<android.util.Pair<Integer, String>> names = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery("SELECT " + COL_USER_ID + ", " + COL_FULLNAME + " FROM " + TABLE_USER +
                " WHERE " + COL_ROLE + "=2 AND (" + COL_CLASS + "=? OR " + COL_CLASS + " LIKE ? OR " + COL_CLASS
                + " LIKE ? OR " + COL_CLASS + " LIKE ?)",
                new String[] { maLop, maLop + ",%", "%," + maLop + ",%", "%," + maLop });

        if (cursor.moveToFirst()) {
            do {
                names.add(new android.util.Pair<>(cursor.getInt(0), cursor.getString(1)));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return names;
    }

    // Hàm lấy ID của người dùng dựa vào Tên (Để tạo phòng chat riêng)
    public int getUserIdByName(String fullname) {
        SQLiteDatabase db = this.getReadableDatabase();
        int id = -1; // Mặc định là không tìm thấy
        Cursor cursor = db.rawQuery("SELECT " + COL_USER_ID + " FROM " + TABLE_USER + " WHERE " + COL_FULLNAME + "=?",
                new String[] { fullname });
        if (cursor.moveToFirst()) {
            id = cursor.getInt(0);
        }
        cursor.close();
        return id;
    }

    // --- THÊM VÀO DatabaseHelper.java ---

    // Kiểm tra xem User đã nộp bài tập của Chương này chưa?
    public boolean isAssignmentSubmitted(int userId, int chapterId) {
        SQLiteDatabase db = this.getReadableDatabase();

        // Kỹ thuật JOIN 2 bảng:
        // Tìm trong bảng SUBMISSION (s) nối với ASSIGNMENT (a)
        // Điều kiện: UserID trùng khớp VÀ ChapterID trong bảng Assignment trùng khớp
        String query = "SELECT COUNT(*) FROM " + TABLE_SUBMISSION + " s " +
                " JOIN " + TABLE_ASSIGNMENT + " a ON s." + COL_SUB_ASS_ID + " = a." + COL_ASS_ID +
                " WHERE s." + COL_SUB_USER_ID + "=? AND a." + COL_ASS_CHAPTER_ID + "=?";

        Cursor cursor = db.rawQuery(query, new String[] { String.valueOf(userId), String.valueOf(chapterId) });

        boolean isSubmitted = false;
        if (cursor.moveToFirst()) {
            // Nếu đếm được > 0 bản ghi -> Đã nộp
            isSubmitted = cursor.getInt(0) > 0;
        }
        cursor.close();
        return isSubmitted;
    }

    public boolean isChapterUnlocked(int userId, int chapterId) {
        // Chương 1 luôn mở cho học viên mới vào
        if (chapterId == 1)
            return true;

        // Muốn học chương hiện tại, phải hoàn thành chương trước đó
        return isChapterCompleted(userId, chapterId - 1);
    }

    // --- LƯU TRẠNG THÁI XEM TÀI LIỆU ---
    public void saveDocProgress(int userId, int chapterId, String docType, int status) {
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            db.execSQL("CREATE TABLE IF NOT EXISTS LichSuXem (MaNguoiDung INTEGER, MaChuong INTEGER, LoaiTaiLieu TEXT, TrangThai INTEGER, PRIMARY KEY (MaNguoiDung, MaChuong, LoaiTaiLieu))");
            
            android.content.ContentValues values = new android.content.ContentValues();
            values.put("MaNguoiDung", userId);
            values.put("MaChuong", chapterId);
            values.put("LoaiTaiLieu", docType);
            values.put("TrangThai", status);
            db.replace("LichSuXem", null, values);
        } catch (Exception ignored) {}
    }

    // --- KIỂM TRA ĐÃ XEM TÀI LIỆU CHƯA ---
    public boolean isDocViewed(int userId, int chapterId, String docType) {
        try {
            SQLiteDatabase db = this.getReadableDatabase();
            db.execSQL("CREATE TABLE IF NOT EXISTS LichSuXem (MaNguoiDung INTEGER, MaChuong INTEGER, LoaiTaiLieu TEXT, TrangThai INTEGER, PRIMARY KEY (MaNguoiDung, MaChuong, LoaiTaiLieu))");
            
            // Nếu không có tài liệu loại này thì mặc định coi như đã xem
            boolean hasDoc = false;
            Cursor cDoc = db.rawQuery("SELECT COUNT(*) FROM TaiLieu WHERE MaChuong=? AND Loai=?", new String[]{String.valueOf(chapterId), docType});
            if (cDoc.moveToFirst()) {
                hasDoc = cDoc.getInt(0) > 0;
            }
            cDoc.close();
            if (!hasDoc) return true;

            // Kiểm tra xem đã lưu trạng thái xem chưa
            boolean viewed = false;
            Cursor cView = db.rawQuery("SELECT TrangThai FROM LichSuXem WHERE MaNguoiDung=? AND MaChuong=? AND LoaiTaiLieu=?", 
                    new String[]{String.valueOf(userId), String.valueOf(chapterId), docType});
            if (cView.moveToFirst()) {
                viewed = cView.getInt(0) == 1;
            }
            cView.close();
            return viewed;
        } catch (Exception e) {
            return true; // Nếu lỗi mặc định cho qua
        }
    }

    public boolean hasDocumentInChapter(int chapterId, String docType) {
        try {
            SQLiteDatabase db = this.getReadableDatabase();
            Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM " + TABLE_DOCUMENT +
                            " WHERE " + COL_DOC_CHAPTER_ID + "=? AND " + COL_DOC_TYPE + "=?",
                    new String[]{String.valueOf(chapterId), docType});
            boolean hasData = false;
            if (cursor.moveToFirst()) {
                hasData = cursor.getInt(0) > 0;
            }
            cursor.close();
            return hasData;
        } catch (Exception e) {
            return false;
        }
    }

    // Hàm phụ: Kiểm tra xem chương này có bài tập nào không?
    // (Để tránh trường hợp thầy quên giao bài mà trò bị khoá oan)
    public boolean hasAssignmentInChapter(int chapterId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM " + TABLE_ASSIGNMENT +
                " WHERE " + COL_ASS_CHAPTER_ID + "=?",
                new String[] { String.valueOf(chapterId) });
        boolean hasData = false;
        if (cursor.moveToFirst()) {
            hasData = cursor.getInt(0) > 0;
        }
        cursor.close();
        return hasData;
    }

    public int getQuestionCountByChapter(int chapterId) {
        SQLiteDatabase db = this.getReadableDatabase();
        int count = 0;
        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM " + TABLE_QUESTION + " WHERE " + COL_Q_CHAPTER_ID + "=?",
                new String[] { String.valueOf(chapterId) });
        if (cursor.moveToFirst())
            count = cursor.getInt(0);
        cursor.close();
        return count;
    }

    public TeacherAnalytics getTeacherAnalytics(String className) {
        TeacherAnalytics analytics = new TeacherAnalytics();
        analytics.className = className;
        analytics.totalStudents = getStudentCountByClass(className);
        analytics.totalChapters = getChapterCount();
        analytics.totalItems = analytics.totalStudents * analytics.totalChapters;

        List<User> students = getStudentsByClass(className);
        List<Chapter> chapters = getAllChapters();
        int totalPercent = 0;
        int scoreCount = 0;
        int lowScoreSignals = 0;
        int unsubmittedSignals = 0;

        for (User student : students) {
            for (Chapter chapter : chapters) {
                int totalQuestions = getQuestionCountByChapter(chapter.getMaChuong());
                int score = getQuizScore(student.getMaNguoiDung(), chapter.getMaChuong());
                boolean assignmentDone = !hasAssignmentInChapter(chapter.getMaChuong())
                        || isAssignmentSubmitted(student.getMaNguoiDung(), chapter.getMaChuong());
                if (isChapterCompleted(student.getMaNguoiDung(), chapter.getMaChuong()))
                    analytics.completedItems++;
                if (!assignmentDone)
                    unsubmittedSignals++;

                if (totalQuestions > 0 && score >= 0) {
                    int percent = Math.round((score * 100f) / totalQuestions);
                    totalPercent += percent;
                    scoreCount++;
                    if (percent >= 85)
                        analytics.excellentCount++;
                    else if (percent >= 70)
                        analytics.goodCount++;
                    else if (percent >= 50)
                        analytics.averageCount++;
                    else {
                        analytics.weakCount++;
                        lowScoreSignals++;
                    }
                }
            }
        }

        for (Chapter chapter : chapters) {
            int totalQuestions = getQuestionCountByChapter(chapter.getMaChuong());
            if (totalQuestions == 0 || students.isEmpty())
                continue;
            int attempts = 0;
            int wrong = 0;
            for (User student : students) {
                int score = getQuizScore(student.getMaNguoiDung(), chapter.getMaChuong());
                if (score >= 0) {
                    attempts += totalQuestions;
                    wrong += Math.max(0, totalQuestions - score);
                }
            }
            if (attempts > 0) {
                int wrongRate = Math.round((wrong * 100f) / attempts);
                if (wrongRate >= 40) {
                    analytics.warnings.add(chapter.getTenChuong() + ": " + wrongRate
                            + "% lượt trả lời sai, nên ôn lại nội dung trọng tâm.");
                }
            }
        }

        if (analytics.warnings.isEmpty()) {
            if (lowScoreSignals > 0)
                analytics.warnings
                        .add("Có " + lowScoreSignals + " lượt điểm dưới 50%, cần theo dõi nhóm học sinh yếu.");
            if (unsubmittedSignals > 0)
                analytics.warnings
                        .add("Có " + unsubmittedSignals + " lượt chưa nộp bài tập, cần nhắc học sinh hoàn thành.");
        }
        if (analytics.warnings.isEmpty())
            analytics.warnings.add("Chưa có cảnh báo nghiêm trọng. Lớp học đang theo tiến độ tốt.");
        return analytics;
    }

    public List<AbilityItem> getStudentAbilityAnalysis(int userId) {
        List<AbilityItem> result = new ArrayList<>();
        for (Chapter chapter : getAllChapters()) {
            int totalQuestions = getQuestionCountByChapter(chapter.getMaChuong());
            int score = getQuizScore(userId, chapter.getMaChuong());
            int percent = 0;
            String status = "Chưa làm quiz";
            if (totalQuestions > 0 && score >= 0) {
                percent = Math.round((score * 100f) / totalQuestions);
                if (percent >= 80)
                    status = "Nắm vững";
                else if (percent >= 50)
                    status = "Cần luyện thêm";
                else
                    status = "Cần cải thiện";
            } else if (totalQuestions == 0) {
                status = "Chưa có câu hỏi";
            }
            result.add(new AbilityItem(getKnowledgeAreaName(chapter), percent, status));
        }
        return result;
    }

    private String getKnowledgeAreaName(Chapter chapter) {
        String name = chapter.getTenChuong();
        String lower = name.toLowerCase();
        if (lower.contains("tự nhiên") || lower.contains("tu nhien"))
            return "Tự nhiên";
        if (lower.contains("dân cư") || lower.contains("kinh tế") || lower.contains("dan cu"))
            return "Dân cư-Kinh tế";
        if (lower.contains("bản đồ") || lower.contains("thế giới") || lower.contains("ban do"))
            return "Bản đồ-Thế giới";
        return name.replace("Chương " + chapter.getMaChuong() + ":", "").trim();
    }

    // Thông báo cho học sinh biết chương trước còn thiếu điều kiện nào
    public String getUnlockHint(int userId, int chapterId) {
        int prev = chapterId - 1;
        List<String> missing = new ArrayList<>();
        int total = getQuestionCountByChapter(prev);
        int score = getQuizScore(userId, prev);
        if (total > 0 && !(score >= 0 && ((double) score / total) * 10 >= 5.0))
            missing.add("đạt từ 5/10 điểm Quiz");
        if (hasAssignmentInChapter(prev) && !isAssignmentSubmitted(userId, prev))
            missing.add("nộp bài tập");
        if (hasDocumentInChapter(prev, "PDF") && !isDocViewed(userId, prev, "PDF"))
            missing.add("xem tài liệu PDF");
        if (hasDocumentInChapter(prev, "Video") && !isDocViewed(userId, prev, "Video"))
            missing.add("xem hết video");
        if (missing.isEmpty())
            return "Hãy hoàn thành các chương trước để mở chương này!";
        return "Ở chương trước, bạn còn cần:\n• " + android.text.TextUtils.join("\n• ", missing);
    }

    public boolean isChapterCompleted(int userId, int chapterId) {
        int totalQuestions = getQuestionCountByChapter(chapterId);
        int score = getQuizScore(userId, chapterId);
        boolean isQuizPassed = totalQuestions == 0 || (score >= 0 && ((double) score / totalQuestions) * 10 >= 5.0);

        boolean isAssignmentDone = !hasAssignmentInChapter(chapterId) || isAssignmentSubmitted(userId, chapterId);
        // Chỉ đòi xem tài liệu khi chương thật sự có tài liệu đó
        boolean isPdfViewed = !hasDocumentInChapter(chapterId, "PDF") || isDocViewed(userId, chapterId, "PDF");
        boolean isVideoViewed = !hasDocumentInChapter(chapterId, "Video") || isDocViewed(userId, chapterId, "Video");

        return isQuizPassed && isAssignmentDone && isPdfViewed && isVideoViewed;
    }

    // Hàm tìm chương đang học: chương đầu tiên đã mở nhưng chưa hoàn thành.
    public Chapter getCurrentLearningChapter(int userId) {
        List<Chapter> chapters = getAllChapters();
        Chapter lastUnlocked = null;

        for (Chapter c : chapters) {
            if (!isChapterUnlocked(userId, c.getMaChuong())) {
                break;
            }

            lastUnlocked = c;
            if (!isChapterCompleted(userId, c.getMaChuong())) {
                return c;
            }
        }

        if (lastUnlocked != null) return lastUnlocked;
        if (!chapters.isEmpty()) return chapters.get(0);
        return null;
    }

    // Hàm tìm Chương cao nhất đang được mở
    public Chapter getCurrentChapter(int userId) {
        SQLiteDatabase db = this.getReadableDatabase();
        List<Chapter> chapters = getAllChapters(); // Lấy tất cả chương
        Chapter current = null;

        for (Chapter c : chapters) {
            // Kiểm tra xem chương này có mở không
            if (isChapterUnlocked(userId, c.getMaChuong())) {
                current = c; // Nếu mở thì tạm ghi nhận là chương hiện tại
            } else {
                // Nếu gặp chương bị khoá -> Dừng lại ngay, chương trước đó chính là chương hiện
                // tại
                break;
            }
        }

        // Nếu chưa mở chương nào (hiếm gặp) thì trả về chương 1
        if (current == null && !chapters.isEmpty())
            return chapters.get(0);

        return current;
    }

    // --- CÁC HÀM MỚI CHO TÍNH NĂNG CHAT XỊN ---

    // 1. Lấy thời gian (ID) tin nhắn cuối cùng để sắp xếp
    public long getLastMessageTime(String roomId) {
        SQLiteDatabase db = this.getReadableDatabase();
        long lastId = 0;
        // Lấy ID lớn nhất (tin mới nhất) của phòng đó
        Cursor cursor = db.rawQuery(
                "SELECT MAX(" + COL_MSG_ID + ") FROM " + TABLE_MESSAGE + " WHERE " + COL_MSG_ROOM + "=?",
                new String[] { roomId });
        if (cursor.moveToFirst()) {
            lastId = cursor.getLong(0);
        }
        cursor.close();
        return lastId; // Trả về 0 nếu chưa có tin nhắn nào
    }

    // Trạng thái đã đọc lưu RIÊNG cho từng người: ID tin nhắn cuối cùng người đó đã xem trong phòng.
    // (Cột IsRead dùng chung cho cả phòng nên 1 người mở nhóm lớp là cả lớp mất chấm chưa đọc.)
    private static final String READ_TABLE_SQL = "CREATE TABLE IF NOT EXISTS DaDoc (MaNguoiDung INTEGER, Phong TEXT, "
            + "MaTinCuoi INTEGER, PRIMARY KEY (MaNguoiDung, Phong))";
    private static final String UNREAD_COND = " AND " + COL_MSG_SENDER_ID + "!=? AND " + COL_MSG_ID
            + " > COALESCE((SELECT MaTinCuoi FROM DaDoc WHERE MaNguoiDung=? AND Phong=" + TABLE_MESSAGE + "."
            + COL_MSG_ROOM + "), 0)";

    // 2. Kiểm tra xem phòng này có tin nhắn mới (của người khác) mà mình chưa đọc không?
    public boolean hasUnreadMessage(String roomId, int myId) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL(READ_TABLE_SQL);
        String me = String.valueOf(myId);
        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM " + TABLE_MESSAGE + " WHERE " + COL_MSG_ROOM + "=?" + UNREAD_COND,
                new String[] { roomId, me, me });
        boolean hasUnread = cursor.moveToFirst() && cursor.getInt(0) > 0;
        cursor.close();
        return hasUnread;
    }

    // 3. Đánh dấu phòng là ĐÃ ĐỌC đối với một người
    public void markMessagesAsRead(String roomId, int userId) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL(READ_TABLE_SQL);
        db.execSQL("INSERT OR REPLACE INTO DaDoc (MaNguoiDung, Phong, MaTinCuoi) VALUES (?, ?, "
                + "(SELECT COALESCE(MAX(" + COL_MSG_ID + "), 0) FROM " + TABLE_MESSAGE + " WHERE " + COL_MSG_ROOM + "=?))",
                new Object[] { userId, roomId, roomId });
    }

    // Số tin chưa đọc của học sinh: chỉ tính phòng lớp của mình và phòng riêng có ID của mình
    public int getUnreadCount(int myUserId) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL(READ_TABLE_SQL);
        String me = String.valueOf(myUserId);
        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM " + TABLE_MESSAGE + " WHERE (" + COL_MSG_ROOM + "=? OR "
                + COL_MSG_ROOM + " LIKE ? ESCAPE '\\' OR " + COL_MSG_ROOM + " LIKE ? ESCAPE '\\')" + UNREAD_COND,
                new String[] { "Lớp " + getUserClass(myUserId), me + "\\_%", "%\\_" + me, me, me });
        int count = cursor.moveToFirst() ? cursor.getInt(0) : 0;
        cursor.close();
        return count;
    }

    // --- HÀM NHẬP CÂU HỎI TỪ FILE ASSETS (CẤP ĐỘ 2) ---
    private void importQuestionsFromAssets(SQLiteDatabase db) {
        try {
            // Mở file questions.txt trong thư mục assets
            java.io.InputStream is = context.getAssets().open("questions.txt");
            java.io.BufferedReader reader = new java.io.BufferedReader(new java.io.InputStreamReader(is));

            String line;
            db.beginTransaction(); // Bắt đầu giao dịch (giúp Insert nhanh hơn)

            while ((line = reader.readLine()) != null) {
                // Cắt chuỗi dựa trên dấu gạch đứng |
                // Cấu trúc: MaChuong|NoiDung|A|B|C|D|DapAn
                String[] parts = line.split("\\|");

                if (parts.length == 7) { // Đảm bảo đủ dữ liệu
                    String chapterId = parts[0].trim();
                    String content = parts[1].trim();
                    String a = parts[2].trim();
                    String b = parts[3].trim();
                    String c = parts[4].trim();
                    String d = parts[5].trim();
                    String correct = parts[6].trim();

                    // Câu lệnh Insert
                    String sql = "INSERT INTO " + TABLE_QUESTION + " VALUES(null, ?, ?, ?, ?, ?, ?, ?)";
                    db.execSQL(sql, new Object[] { chapterId, content, a, b, c, d, correct });
                }
            }
            db.setTransactionSuccessful(); // Xác nhận thành công
            db.endTransaction(); // Kết thúc
            reader.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // (Ở đây demo đơn giản là lấy tất cả các Mã lớp có trong bảng User)
    public List<String> getAllClasses() {
        List<String> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT DISTINCT " + COL_CLASS + " FROM " + TABLE_USER + " WHERE " + COL_CLASS + " IS NOT NULL", null);
        if (cursor.moveToFirst()) {
            do {
                list.add(cursor.getString(0));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return list;
    }

    // 2. Lấy danh sách Học sinh trong một lớp
    public List<User> getStudentsByClass(String maLop) {
        List<User> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        // Lấy những user có quyền = 1 (Học sinh) và thuộc lớp này
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_USER +
                " WHERE " + COL_CLASS + "=? AND " + COL_ROLE + "=1",
                new String[] { maLop });
        if (cursor.moveToFirst()) {
            do {
                User u = new User();
                u.setMaNguoiDung(cursor.getInt(0));
                u.setTenDangNhap(cursor.getString(1));
                u.setHoTen(cursor.getString(3));
                // ... các trường khác nếu cần
                list.add(u);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return list;
    }

    // --- HÀM MỚI: Lấy danh sách các lớp mà GV này dạy ---
    public List<String> getTeachingClasses(int teacherId) {
        List<String> classes = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        // Chỉ lấy MaLop của đúng ông giáo viên đang đăng nhập
        Cursor cursor = db.rawQuery("SELECT " + COL_CLASS + " FROM " + TABLE_USER + " WHERE " + COL_USER_ID + "=?",
                new String[] { String.valueOf(teacherId) });

        if (cursor.moveToFirst()) {
            String classString = cursor.getString(0);
            if (classString != null && !classString.isEmpty()) {
                // Tách chuỗi dựa trên dấu phẩy
                String[] arr = classString.split(",");
                for (String s : arr) {
                    classes.add(s.trim()); // trim() để xóa khoảng trắng thừa
                }
            }
        }
        cursor.close();
        return classes;
    }

    // 1. Đếm tổng số chương học hiện có
    public int getChapterCount() {
        SQLiteDatabase db = this.getReadableDatabase();
        int count = 0;
        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM " + TABLE_CHAPTER, null);
        if (cursor.moveToFirst()) {
            count = cursor.getInt(0);
        }
        cursor.close();
        return count;
    }

    // 2. Đếm số lớp mà Giáo viên đang dạy
    // (Tận dụng lại logic tách chuỗi "12A1,12A2" đã làm)
    public int getClassCount(int teacherId) {
        List<String> classes = getTeachingClasses(teacherId); // Hàm này bạn đã có ở bước trước
        return classes.size();
    }

    // Đếm số lượng học sinh trong một lớp cụ thể
    public int getStudentCountByClass(String className) {
        SQLiteDatabase db = this.getReadableDatabase();
        int count = 0;
        // Đếm User có Role=1 (Học sinh) và thuộc lớp className
        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM " + TABLE_USER +
                " WHERE " + COL_CLASS + "=? AND " + COL_ROLE + "=1",
                new String[] { className });

        if (cursor.moveToFirst()) {
            count = cursor.getInt(0);
        }
        cursor.close();
        return count;
    }

    // --- HÀM CHẤM ĐIỂM BÀI TẬP (Dành cho GV) ---
    // --- HÀM CHẤM ĐIỂM BÀI TẬP (ĐÃ FIX LỖI) ---
    public void gradeSubmission(int userId, int chapterId, double score, String feedback) {
        SQLiteDatabase db = this.getWritableDatabase();

        // 1. Tìm ID bài tập
        int assId = 0;
        Cursor c = db.rawQuery(
                "SELECT " + COL_ASS_ID + " FROM " + TABLE_ASSIGNMENT + " WHERE " + COL_ASS_CHAPTER_ID + "=?",
                new String[] { String.valueOf(chapterId) });
        if (c.moveToFirst())
            assId = c.getInt(0);
        c.close();

        // 2. Cập nhật điểm
        if (assId > 0) {
            android.content.ContentValues values = new android.content.ContentValues();
            values.put(COL_SUB_GRADE, score);
            values.put(COL_SUB_FEEDBACK, feedback);

            // SỬA LẠI DÒNG NÀY CHO ĐÚNG CÚ PHÁP SQL:
            db.update(TABLE_SUBMISSION, values,
                    COL_SUB_USER_ID + "=? AND " + COL_SUB_ASS_ID + "=?",
                    new String[] { String.valueOf(userId), String.valueOf(assId) });
        }
    }

    // Lấy thông tin bài làm kèm điểm số (Trả về mảng 4 phần tử: Text, File, Điểm,
    // Nhận xét)
    public String[] getSubmissionFullDetail(int userId, int chapterId) {
        SQLiteDatabase db = this.getReadableDatabase();
        String[] result = null;
        int assId = 0;
        Cursor c1 = db.rawQuery(
                "SELECT " + COL_ASS_ID + " FROM " + TABLE_ASSIGNMENT + " WHERE " + COL_ASS_CHAPTER_ID + "=?",
                new String[] { String.valueOf(chapterId) });
        if (c1.moveToFirst())
            assId = c1.getInt(0);
        c1.close();

        if (assId > 0) {
            Cursor c2 = db.rawQuery(
                    "SELECT " + COL_SUB_TEXT + ", " + COL_SUB_FILE + ", " + COL_SUB_GRADE + ", " + COL_SUB_FEEDBACK +
                            " FROM " + TABLE_SUBMISSION +
                            " WHERE " + COL_SUB_USER_ID + "=? AND " + COL_SUB_ASS_ID + "=?" +
                            " ORDER BY CASE WHEN " + COL_SUB_FILE + " IS NOT NULL AND " + COL_SUB_FILE + " != '' THEN 0 ELSE 1 END, " + COL_SUB_ID + " DESC LIMIT 1",
                    new String[] { String.valueOf(userId), String.valueOf(assId) });
            if (c2.moveToFirst()) {
                result = new String[4];
                result[0] = c2.getString(0); // Bài làm
                result[1] = c2.getString(1); // Link file
                result[2] = c2.getString(2); // Điểm (-1 nếu chưa chấm)
                result[3] = c2.getString(3); // Nhận xét
            }
            c2.close();
        }
        return result;
    }

    // 1. Lấy danh sách tất cả cuộc trò chuyện của Giáo Viên (Gồm Lớp học + Chat
    // riêng)
    // Trả về danh sách tên phòng (Room ID)
    // --- [MỚI] HÀM LẤY DANH SÁCH CHAT CHO GIÁO VIÊN ---
    public List<String> getTeacherConversations(int teacherId) {
        List<String> rooms = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        // 1. Lấy danh sách Lớp (QUAN TRỌNG: Thêm chữ "Lớp " vào đầu)
        List<String> classes = getTeachingClasses(teacherId);
        for (String className : classes) {
            rooms.add("Lớp " + className); // Ví dụ: "Lớp 12A1"
        }

        // 2. Lấy danh sách Học sinh nhắn tin riêng (Private Chat)
        // Tìm các phòng có chứa ID giáo viên (VD: "1_5" hoặc "5_2")
        Cursor cursor = db.rawQuery("SELECT DISTINCT " + COL_MSG_ROOM + " FROM " + TABLE_MESSAGE +
                " WHERE " + COL_MSG_ROOM + " LIKE ? ESCAPE '\\' OR " + COL_MSG_ROOM + " LIKE ? ESCAPE '\\'",
                new String[] { "%\\_" + teacherId, teacherId + "\\_%" });

        if (cursor.moveToFirst()) {
            do {
                String room = cursor.getString(0);
                if (!rooms.contains(room)) {
                    rooms.add(room);
                }
            } while (cursor.moveToNext());
        }
        cursor.close();

        // 3. Sắp xếp: Phòng nào có tin mới nhất lên đầu
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            rooms.sort((room1, room2) -> {
                long time1 = getLastMessageTime(room1);
                long time2 = getLastMessageTime(room2);
                return Long.compare(time2, time1); // Giảm dần
            });
        }

        return rooms;
    }

    // --- [MỚI] LẤY TÊN HIỂN THỊ CHO PHÒNG CHAT ---
    public String getRoomDisplayName(String roomId, int myId) {
        if (roomId.startsWith("Lớp ")) {
            return roomId; // Là nhóm lớp, trả về nguyên văn
        }

        // Là chat riêng (VD: "1_5")
        String[] parts = roomId.split("_");
        if (parts.length == 2) {
            try {
                int u1 = Integer.parseInt(parts[0]);
                int u2 = Integer.parseInt(parts[1]);
                // Tìm ID người kia
                int otherId = (u1 == myId) ? u2 : u1;
                User otherUser = getUserById(otherId);
                if (otherUser != null) {
                    return otherUser.getHoTen();
                }
            } catch (Exception e) {
                return roomId;
            }
        }
        return roomId;
    }

    // --- [MỚI] ĐẾM TIN NHẮN CHƯA ĐỌC CỦA GV (Để hiện chấm đỏ) ---
    public int getTotalUnreadCountForTeacher(int teacherId) {
        int total = 0;
        List<String> rooms = getTeacherConversations(teacherId);
        for (String room : rooms) {
            if (hasUnreadMessage(room, teacherId)) {
                total++;
            }
        }
        return total;
    }

    // --- HÀM LẤY DANH SÁCH CHAT CỦA HỌC SINH ---
    public List<String> getStudentConversations(int studentId) {
        List<String> rooms = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        // 1. LẤY CHAT NHÓM LỚP (Quan trọng: Phải khớp logic với bên Giáo viên)
        String myClass = getUserClass(studentId); // Ví dụ trả về "12A1"
        if (myClass != null && !myClass.isEmpty()) {
            // QUY ƯỚC CHUNG: Tên phòng phải là "Lớp " + Mã lớp
            // Ví dụ: "Lớp 12A1" (Phải có chữ Lớp và dấu cách)
            rooms.add("Lớp " + myClass);
        }

        // 2. LẤY CHAT RIÊNG VỚI GIÁO VIÊN
        // Tìm các phòng có format "STUDENTID_TEACHERID"
        // Ví dụ: Học sinh ID=1. Tìm các phòng "1_%"
        Cursor cursor = db.rawQuery("SELECT DISTINCT " + COL_MSG_ROOM + " FROM " + TABLE_MESSAGE +
                " WHERE " + COL_MSG_ROOM + " LIKE ? ESCAPE '\\' OR " + COL_MSG_ROOM + " LIKE ? ESCAPE '\\'",
                new String[] { studentId + "\\_%", "%\\_" + studentId });

        if (cursor.moveToFirst()) {
            do {
                String room = cursor.getString(0);
                if (!rooms.contains(room)) {
                    rooms.add(room);
                }
            } while (cursor.moveToNext());
        }
        cursor.close();

        return rooms;
    }

    // --- CÁC HÀM QUẢN LÝ BÀI HỌC (CHO GIÁO VIÊN) ---

    // 1. Lấy chi tiết 1 chương theo ID
    public Chapter getChapterById(int chapterId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Chapter chapter = null;
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_CHAPTER + " WHERE " + COL_CHAPTER_ID + "=?",
                new String[] { String.valueOf(chapterId) });
        if (cursor.moveToFirst()) {
            chapter = new Chapter(
                    cursor.getInt(0),
                    cursor.getString(1),
                    cursor.getString(2),
                    cursor.getInt(3),
                    false // isLocked không quan trọng ở đây
            );
        }
        cursor.close();
        return chapter;
    }

    // 2. Thêm chương mới (Trả về ID chương vừa tạo)
    public int addChapterFull(String name, String desc, String pdfFile, String videoFile, String assignment) {
        SQLiteDatabase db = this.getWritableDatabase();

        // A. Thêm vào bảng Chapter
        int newOrder = getChapterCount() + 1;
        android.content.ContentValues cvChapter = new android.content.ContentValues();
        cvChapter.put(COL_CHAPTER_NAME, name);
        cvChapter.put(COL_CHAPTER_DESC, desc);
        cvChapter.put(COL_CHAPTER_ORDER, newOrder);
        long chapterId = db.insert(TABLE_CHAPTER, null, cvChapter);

        if (chapterId != -1) {
            // B. Thêm Tài liệu PDF
            if (!pdfFile.isEmpty()) {
                addDocument((int) chapterId, "PDF", pdfFile);
            }
            // C. Thêm Tài liệu Video
            if (!videoFile.isEmpty()) {
                addDocument((int) chapterId, "Video", videoFile);
            }
            // D. Thêm Bài tập tự luận
            if (!assignment.isEmpty()) {
                android.content.ContentValues cvAss = new android.content.ContentValues();
                cvAss.put(COL_ASS_CHAPTER_ID, chapterId);
                cvAss.put(COL_ASS_CONTENT, assignment);
                db.insert(TABLE_ASSIGNMENT, null, cvAss);
            }
        }
        return (int) chapterId;
    }

    // 3. Cập nhật chương đã có
    public void updateChapterFull(int chapterId, String name, String desc, String pdfFile, String videoFile, String assignment) {
        SQLiteDatabase db = this.getWritableDatabase();

        // A. Cập nhật tên và mô tả chương
        android.content.ContentValues cvChapter = new android.content.ContentValues();
        cvChapter.put(COL_CHAPTER_NAME, name);
        cvChapter.put(COL_CHAPTER_DESC, desc);
        db.update(TABLE_CHAPTER, cvChapter, COL_CHAPTER_ID + "=?", new String[] { String.valueOf(chapterId) });

        // B. Cập nhật tài liệu (Xóa cũ thêm mới cho nhanh)
        db.delete(TABLE_DOCUMENT, COL_DOC_CHAPTER_ID + "=?", new String[] { String.valueOf(chapterId) });
        if (!pdfFile.isEmpty())
            addDocument(chapterId, "PDF", pdfFile);
        if (!videoFile.isEmpty())
            addDocument(chapterId, "Video", videoFile);

        // C. Cập nhật bài tập
        // Sửa tại chỗ để giữ nguyên MaBT, bài đã nộp và điểm đã chấm không bị mất
        String[] chapArg = { String.valueOf(chapterId) };
        if (assignment.isEmpty()) {
            // Giáo viên xoá bài tập: xoá luôn bài nộp của nó để không còn dữ liệu mồ côi
            db.execSQL("DELETE FROM " + TABLE_SUBMISSION + " WHERE " + COL_SUB_ASS_ID + " IN (SELECT " + COL_ASS_ID
                    + " FROM " + TABLE_ASSIGNMENT + " WHERE " + COL_ASS_CHAPTER_ID + "=?)", chapArg);
            db.delete(TABLE_ASSIGNMENT, COL_ASS_CHAPTER_ID + "=?", chapArg);
        } else {
            android.content.ContentValues cvAss = new android.content.ContentValues();
            cvAss.put(COL_ASS_CONTENT, assignment);
            if (db.update(TABLE_ASSIGNMENT, cvAss, COL_ASS_CHAPTER_ID + "=?", chapArg) == 0) {
                cvAss.put(COL_ASS_CHAPTER_ID, chapterId);
                db.insert(TABLE_ASSIGNMENT, null, cvAss);
            }
        }
    }

    public boolean deleteChapterFull(int chapterId) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_DOCUMENT, COL_DOC_CHAPTER_ID + "=?", new String[] { String.valueOf(chapterId) });
        db.delete(TABLE_QUESTION, COL_Q_CHAPTER_ID + "=?", new String[] { String.valueOf(chapterId) });
        db.delete(TABLE_SCORE, COL_SCORE_CHAPTER_ID + "=?", new String[] { String.valueOf(chapterId) });
        // Xoá lịch sử xem, tránh chương mới dùng lại ID cũ bị tính là đã xem
        db.execSQL("CREATE TABLE IF NOT EXISTS LichSuXem (MaNguoiDung INTEGER, MaChuong INTEGER, LoaiTaiLieu TEXT, TrangThai INTEGER, PRIMARY KEY (MaNguoiDung, MaChuong, LoaiTaiLieu))");
        db.delete("LichSuXem", "MaChuong=?", new String[] { String.valueOf(chapterId) });

        Cursor cursor = db.rawQuery(
                "SELECT " + COL_ASS_ID + " FROM " + TABLE_ASSIGNMENT + " WHERE " + COL_ASS_CHAPTER_ID + "=?",
                new String[] { String.valueOf(chapterId) });
        while (cursor.moveToNext()) {
            db.delete(TABLE_SUBMISSION, COL_SUB_ASS_ID + "=?", new String[] { String.valueOf(cursor.getInt(0)) });
        }
        cursor.close();
        db.delete(TABLE_ASSIGNMENT, COL_ASS_CHAPTER_ID + "=?", new String[] { String.valueOf(chapterId) });
        int rows = db.delete(TABLE_CHAPTER, COL_CHAPTER_ID + "=?", new String[] { String.valueOf(chapterId) });
        return rows > 0;
    }

    // Hàm phụ: Thêm tài liệu
    private void addDocument(int chapterId, String type, String fileName) {
        SQLiteDatabase db = this.getWritableDatabase();
        android.content.ContentValues values = new android.content.ContentValues();
        values.put(COL_DOC_CHAPTER_ID, chapterId);
        values.put(COL_DOC_TYPE, type);
        values.put(COL_DOC_FILENAME, fileName);
        db.insert(TABLE_DOCUMENT, null, values);
    }

    // --- BỔ SUNG: QUẢN LÝ CÂU HỎI TRẮC NGHIỆM ---

    // 1. Thêm câu hỏi mới (Lưu đáp án dạng "A", "B", "C", "D")
    public void addQuestion(int chapterId, String content, String opA, String opB, String opC, String opD,
            String correctAns) {
        SQLiteDatabase db = this.getWritableDatabase();
        android.content.ContentValues values = new android.content.ContentValues();
        values.put(COL_Q_CHAPTER_ID, chapterId);
        values.put(COL_Q_CONTENT, content);
        values.put(COL_Q_A, opA);
        values.put(COL_Q_B, opB);
        values.put(COL_Q_C, opC);
        values.put(COL_Q_D, opD);
        values.put(COL_Q_CORRECT, correctAns); // Lưu "A", "B"...
        db.insert(TABLE_QUESTION, null, values);
    }

    // 2. Xóa câu hỏi
    public void deleteQuestion(int questionId) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_QUESTION, COL_Q_ID + "=?", new String[] { String.valueOf(questionId) });
    }

    // 3. Cập nhật câu hỏi (Sửa)
    public void updateQuestion(int questionId, String content, String opA, String opB, String opC, String opD,
            String correctAns) {
        SQLiteDatabase db = this.getWritableDatabase();
        android.content.ContentValues values = new android.content.ContentValues();
        values.put(COL_Q_CONTENT, content);
        values.put(COL_Q_A, opA);
        values.put(COL_Q_B, opB);
        values.put(COL_Q_C, opC);
        values.put(COL_Q_D, opD);
        values.put(COL_Q_CORRECT, correctAns);

        // Cập nhật dòng có ID tương ứng
        db.update(TABLE_QUESTION, values, COL_Q_ID + "=?", new String[] { String.valueOf(questionId) });
    }

    // ══════════════════════════════════════════════════════
    // TÍNH NĂNG: QUÉT TIẾN ĐỘ HỌC (SCAN STUDY PROGRESS)
    // ══════════════════════════════════════════════════════

    /** Model tiến độ từng chương */
    public static class ChapterProgress {
        public int    chapterId;
        public String chapterName;
        public boolean quizDone;        // Đã đạt quiz chưa (>= 5/10)
        public int    quizScore;        // Số câu đúng (-1 nếu chưa làm)
        public int    totalQuestions;   // Tổng câu hỏi
        public boolean assignmentDone;  // Đã nộp bài tập chưa
        public boolean hasAssignment;   // Có bài tập không
        public boolean pdfDone;         // Đã xem PDF chưa
        public boolean hasPdf;          // Có file PDF không
        public boolean videoDone;       // Đã xem Video chưa
        public boolean hasVideo;        // Có file Video không

        /** DONE = tất cả xong | PARTIAL = làm một phần | TODO = chưa làm gì */
        public String getStatus() {
            boolean allDone = (totalQuestions == 0 || quizDone)
                    && (!hasAssignment || assignmentDone)
                    && (!hasPdf || pdfDone)
                    && (!hasVideo || videoDone);
            boolean anyDone = quizDone || quizScore >= 0 || assignmentDone || pdfDone || videoDone;
            if (allDone) return "DONE";
            if (anyDone) return "PARTIAL";
            return "TODO";
        }

        public int getProgressPercent() {
            int parts = 0, done = 0;
            if (totalQuestions > 0) { parts++; if (quizDone) done++; }
            if (hasAssignment)       { parts++; if (assignmentDone) done++; }
            if (hasPdf)              { parts++; if (pdfDone) done++; }
            if (hasVideo)            { parts++; if (videoDone) done++; }
            if (parts == 0) return 100;
            return (done * 100) / parts;
        }
    }

    /**
     * Quét toàn bộ chương và trả về tiến độ học của user.
     * Bao gồm: trạng thái quiz, điểm, trạng thái bài tập, pdf, video.
     */
    public List<ChapterProgress> getStudyProgress(int userId) {
        List<ChapterProgress> result = new ArrayList<>();
        List<Chapter> chapters = getAllChapters();

        for (Chapter ch : chapters) {
            ChapterProgress p = new ChapterProgress();
            p.chapterId   = ch.getMaChuong();
            p.chapterName = ch.getTenChuong();

            // Quiz
            p.totalQuestions = getQuestionCountByChapter(ch.getMaChuong());
            p.quizScore      = getQuizScore(userId, ch.getMaChuong()); // -1 nếu chưa làm
            // "Xong" quiz = đạt >= 5/10, cùng chuẩn với isChapterCompleted (mở khoá chương sau)
            p.quizDone       = (p.totalQuestions == 0)
                    || (p.quizScore >= 0 && p.quizScore * 10.0 / p.totalQuestions >= 5.0);

            // Bài tập
            p.hasAssignment   = hasAssignmentInChapter(ch.getMaChuong());
            p.assignmentDone  = !p.hasAssignment || isAssignmentSubmitted(userId, ch.getMaChuong());

            // PDF & Video
            p.hasPdf     = hasDocumentInChapter(ch.getMaChuong(), "PDF");
            p.pdfDone    = isDocViewed(userId, ch.getMaChuong(), "PDF");
            p.hasVideo   = hasDocumentInChapter(ch.getMaChuong(), "Video");
            p.videoDone  = isDocViewed(userId, ch.getMaChuong(), "Video");

            result.add(p);
        }
        return result;
    }

    /** Lấy danh sách chương CHƯA hoàn thành (TODO hoặc PARTIAL) */
    public List<ChapterProgress> getUnfinishedChapters(int userId) {
        List<ChapterProgress> all = getStudyProgress(userId);
        List<ChapterProgress> unfinished = new ArrayList<>();
        for (ChapterProgress p : all) {
            if (!"DONE".equals(p.getStatus())) {
                unfinished.add(p);
            }
        }
        return unfinished;
    }

    // ══════════════════════════════════════════════════════
    // TÍNH NĂNG: AI KIỂM TRA BÀI TẬP ĐÃ NỘP
    // ══════════════════════════════════════════════════════

    /** Model chứa đề bài + bài làm của học sinh để đưa cho AI chấm */
    public static class SubmissionForReview {
        public int    chapterId;
        public String chapterName;
        public String assignmentQuestion; // Đề bài
        public String userAnswer;         // Bài làm của học sinh
    }

    /**
     * Lấy tất cả bài tập đã nộp của user (có đề bài + bài làm).
     * Dùng để hiện danh sách cho AI review.
     */
    public List<SubmissionForReview> getSubmittedAssignments(int userId) {
        List<SubmissionForReview> result = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        // JOIN: NopBai → BaiTap → ChuongHoc
        String query =
            "SELECT ch." + COL_CHAPTER_ID + ", ch." + COL_CHAPTER_NAME +
            ", bt." + COL_ASS_CONTENT +
            ", nb." + COL_SUB_TEXT +
            ", nb." + COL_SUB_FILE +
            " FROM " + TABLE_SUBMISSION + " nb" +
            " JOIN " + TABLE_ASSIGNMENT + " bt ON nb." + COL_SUB_ASS_ID + " = bt." + COL_ASS_ID +
            " JOIN " + TABLE_CHAPTER + " ch ON bt." + COL_ASS_CHAPTER_ID + " = ch." + COL_CHAPTER_ID +
            " WHERE nb." + COL_SUB_USER_ID + " = ?" +
            " ORDER BY ch." + COL_CHAPTER_ORDER + " ASC";

        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(userId)});
        if (cursor.moveToFirst()) {
            do {
                SubmissionForReview s = new SubmissionForReview();
                s.chapterId           = cursor.getInt(0);
                s.chapterName         = cursor.getString(1);
                s.assignmentQuestion  = cursor.getString(2);
                s.userAnswer          = cursor.getString(3);
                String filePath       = cursor.getString(4);

                boolean hasText = (s.userAnswer != null && !s.userAnswer.trim().isEmpty());
                boolean hasFile = (filePath != null && !filePath.trim().isEmpty());

                if (hasText || hasFile) {
                    if (!hasText) {
                        s.userAnswer = "[Học sinh đã nộp bài bằng file đính kèm. Định dạng file hiện tại AI chưa quét trực tiếp được nội dung. Hãy hướng dẫn học sinh bổ sung bài làm bằng văn bản để chấm bài]";
                    }
                    result.add(s);
                }
            } while (cursor.moveToNext());
        }
        cursor.close();
        return result;
    }

}