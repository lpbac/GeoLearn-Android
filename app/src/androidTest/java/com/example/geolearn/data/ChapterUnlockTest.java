package com.example.geolearn.data;

import static org.junit.Assert.*;

import androidx.test.platform.app.InstrumentationRegistry;

import org.junit.Test;

public class ChapterUnlockTest {
    // DB trong bộ nhớ (name = null) nên không đụng tới dữ liệu thật của app
    private final DatabaseHelper db = new DatabaseHelper(
            InstrumentationRegistry.getInstrumentation().getTargetContext(), null);

    @Test
    public void quizResultSurvivesRetakeAndQuestionEdits() {
        int total = db.getQuestionCountByChapter(1);
        assertTrue(total > 0);

        db.saveQuizScore(1, 1, total, total);
        db.saveQuizScore(1, 1, 0, total); // làm lại điểm thấp hơn
        assertEquals(total, db.getQuizScore(1, 1));

        for (int i = 0; i < total * 2; i++) // giáo viên thêm câu hỏi
            db.addQuestion(1, "q", "a", "b", "c", "d", "A");
        assertTrue(db.isQuizPassed(1, 1));
    }

    @Test
    public void deletingMiddleChapterDoesNotUnlockNext() {
        assertFalse(db.isChapterUnlocked(1, 3));
        db.deleteChapterFull(2);
        assertFalse(db.isChapterUnlocked(1, 3)); // giờ phụ thuộc chương 1, vẫn chưa xong
        assertTrue(db.isChapterUnlocked(1, 1));
    }
}
