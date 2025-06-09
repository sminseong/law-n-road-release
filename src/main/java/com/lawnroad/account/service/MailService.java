package com.lawnroad.account.service;


import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;


@Service
public class MailService {


    private final JavaMailSender mailSender;

    public MailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    private String generateCode() {
        return String.valueOf((int)(Math.random() * 900000) + 100000); // 6자리
    }

    public String sendAuthCode(String to) {
        String code = generateCode();

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, false, "UTF-8");

            helper.setTo(to);
            helper.setSubject("이메일 인증번호");
            helper.setText("인증번호는 " + code + " 입니다.", false);
            helper.setFrom(new InternetAddress("dlwjdtn2014@gmail.com", "로앤로드"));

            mailSender.send(message);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return code;
    }


}
