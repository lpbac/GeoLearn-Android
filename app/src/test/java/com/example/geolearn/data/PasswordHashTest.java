package com.example.geolearn.data;

import static org.junit.Assert.*;

import org.junit.Test;

public class PasswordHashTest {
    @Test
    public void hashThenVerify() {
        String h = DatabaseHelper.hashPassword("123");
        assertNotEquals(h, DatabaseHelper.hashPassword("123")); // salt khác nhau
        assertTrue(DatabaseHelper.verifyPassword("123", h));
        assertFalse(DatabaseHelper.verifyPassword("124", h));
        assertFalse(DatabaseHelper.verifyPassword("123", "123"));
    }
}
