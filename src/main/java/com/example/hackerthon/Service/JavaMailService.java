package com.example.hackerthon.Service;


import com.example.hackerthon.Dto.Request.NotificationRequest;
import com.example.hackerthon.Exception.AppException;
import com.example.hackerthon.Exception.ErrorCode;
import com.example.hackerthon.Model.User;
import com.example.hackerthon.Repo.UserRepo;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.nio.charset.StandardCharsets;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE,makeFinal = true)
@RequiredArgsConstructor
@Slf4j
public class JavaMailService{
    String MAIL_HOST="minhdaimk111@gmail.com";
    JavaMailSender javaMailSender;
    SpringTemplateEngine templateEngine;
    private final UserRepo userRepo;

    public String forgetPassword(NotificationRequest messageMail) {
        try {
            User user=userRepo.findByEmail(messageMail.email())
                    .orElseThrow(()->new AppException(ErrorCode.USER_NOT_FOUND));
            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, StandardCharsets.UTF_8.name());

            Context context = new Context();
            context.setVariable("password", user.getRawPassword());
            String html = templateEngine.process("password", context);

            helper.setTo(messageMail.email().toString());
            helper.setText(html, true);  // <--- Đảm bảo nội dung email đúng
            helper.setSubject("Mat Khau Account");
            helper.setFrom("minhdaimk111@gmail.com");

            javaMailSender.send(message);
            return new String("✅ Email sent successfully!");
        } catch (MessagingException e) {
            return new String("❌ Email sending failed: " + e.getMessage());
        }
    }




}
