package org.example.boardback.service.auth;

public interface EmailService {
    void sendTextEmail(String to, String subject, String text);
    void sendHtmlEmail(String to, String subject, String html);
    void sendVerifyCode(String to, String verifyCode);
    void sendPasswordReset(String to, String url);
}