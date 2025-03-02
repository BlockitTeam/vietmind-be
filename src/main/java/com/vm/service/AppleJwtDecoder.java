package com.vm.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.DecodedJWT;

import java.util.Date;

public class AppleJwtDecoder {
    public static void main(String[] args) {
        // Token từ phía FE gửi lên
        String token = "eyJraWQiOiJkTWxFUkJhRmRLIiwiYWxnIjoiUlMyNTYifQ.eyJpc3MiOiJodHRwczovL2FwcGxlaWQuYXBwbGUuY29tIiwiYXVkIjoib3JnLnJlYWN0anMubmF0aXZlLmV4YW1wbGUudmlldG1pbmQtbW9iaWxlIiwiZXhwIjoxNzQwOTg4MTMzLCJpYXQiOjE3NDA5MDE3MzMsInN1YiI6IjAwMTM3MC43MzUxZGY3YTY1NzE0MmQyYjM3ZWZiMzg3ODgyMGFjMS4xNTE4Iiwibm9uY2UiOiJjYjViZTNlNmIzMzliYTA3NzQyOWQzZDZiMTE0ODI3ODBlOWQ0NDUwMmQxOWJkMDE5YWE0MmVjNjgzMTdjZDIzIiwiY19oYXNoIjoiY3hrZDRoV1N2cWg5ODVkU3VJZVhPQSIsImVtYWlsIjoibWc2NGI4eHA1dkBwcml2YXRlcmVsYXkuYXBwbGVpZC5jb20iLCJlbWFpbF92ZXJpZmllZCI6dHJ1ZSwiaXNfcHJpdmF0ZV9lbWFpbCI6dHJ1ZSwiYXV0aF90aW1lIjoxNzQwOTAxNzMzLCJub25jZV9zdXBwb3J0ZWQiOnRydWV9.IQCz-ef2_MdUPwBT2q5Ukg0NOfNz5bXE4PO6xK3QCkXVFPI86cvu3M9K6TKGQj7aBIUkbAvOkjsJMA9f17A6Y_BVFe2q0Zoe7q9IzWy8UUkAQE7_FneIj7bWT5bCJXXuJ9KRlWtjL5Vkchd08NZaDUtQKH_tjGbBC1TRgzOAOeqr0XCAXRgPIEmjEnwzkUcmagh__3Bd2CrnnLw52xDk9viI2eONjsXArmIR_PqBFlQykj7dUJhSN7bCjN--K_YHpplD26EANLxr_TjfieZ_jEp0W8blq1AS6cSOmHuw9Ds95ty2Z4t3FgwGu4yWU3zJ3dCQOumte4C8KmFeSwz_GA"; // Thay bằng token thực tế

        // Giải mã token
        DecodedJWT jwt = JWT.decode(token);

        // Lấy các thông tin cần thiết
        String userId = jwt.getSubject(); // ID của user (Apple ID)
        String email = jwt.getClaim("email").asString(); // Email của user
        boolean emailVerified = jwt.getClaim("email_verified").asBoolean(); // Email đã xác thực chưa
        Date expiresAt = jwt.getExpiresAt(); // Thời gian hết hạn

        // In ra kết quả
        System.out.println("User ID: " + userId);
        System.out.println("Email: " + email);
        System.out.println("Email Verified: " + emailVerified);
        System.out.println("Token Expiry: " + expiresAt);
    }
}
