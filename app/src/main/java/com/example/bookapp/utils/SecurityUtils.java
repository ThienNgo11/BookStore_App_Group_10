package com.example.bookapp.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class SecurityUtils {

    /**
     * Băm một chuỗi mật khẩu bằng thuật toán SHA-256.
     * @param password Mật khẩu dạng văn bản thuần (plain text).
     * @return Một chuỗi Hexa 64 ký tự đã được băm, hoặc null nếu có lỗi.
     */
    public static String hashPassword(String password) {
        if (password == null || password.isEmpty()) {
            return null;
        }

        try {
            // 1. Khởi tạo đối tượng MessageDigest với thuật toán SHA-256
            MessageDigest md = MessageDigest.getInstance("SHA-256");

            // 2. "Nghiền" (digest) mật khẩu (chuyển sang byte)
            byte[] hashedBytes = md.digest(password.getBytes());

            // 3. Chuyển đổi mảng byte kết quả thành chuỗi Hexa (16)
            // Vì 1 byte = 8 bit, cần 2 ký tự Hexa để biểu diễn. 32 byte * 2 = 64 ký tự.
            StringBuilder sb = new StringBuilder();
            for (byte b : hashedBytes) {
                // "%02x" đảm bảo mỗi byte được biểu diễn bằng 2 ký tự, kể cả số 0
                sb.append(String.format("%02x", b));
            }

            return sb.toString();

        } catch (NoSuchAlgorithmException e) {
            // Lỗi này gần như không bao giờ xảy ra nếu tên "SHA-256" là đúng.
            e.printStackTrace();
            return null;
        }
    }


}