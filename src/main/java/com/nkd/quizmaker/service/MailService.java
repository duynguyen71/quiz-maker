package com.nkd.quizmaker.service;

import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

@RequiredArgsConstructor
@Service
public class MailService {

    private final JavaMailSender javaMailSender;
    private final String FROM_ADDRESS = "testting7989@gmail.com";
    private final String SENDER_NAME = "Quiz Maker";

    public String sendVerificationCode(String receivedEmail) throws MessagingException, UnsupportedEncodingException {
        final String subject = "Account Verification Code";
        String content =
                "Your verification code is:<br>"
                        + "<h2 style=\"color: blueviolet; font-weight: bolder\">[[code]]</h2>"
                        + "Thank you,<br>"
                        + "Have a good experience in Quiz Maker.";
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage);
        mimeMessageHelper.setFrom(FROM_ADDRESS, SENDER_NAME);
        mimeMessageHelper.setTo(receivedEmail);
        mimeMessageHelper.setSubject(subject);

        String code = generateVerificationCode();
        content = content.replace("[[code]]", code);
        mimeMessageHelper.setText(content, true);
        javaMailSender.send(mimeMessage);
        return code;
    }

    public String sendAssignQuizToUser(String from, String receivedEmail, String quizTitle, String quizCode, Date startDate, Date endDate) throws MessagingException, UnsupportedEncodingException {
        final String subject = from + " from Quiz Maker just assign you a Quiz";
        String content =
                "You were just assigned a quiz name : <br>"
                        + "<h2 style=\"color: blueviolet; font-weight: bolder\">[[quizTitle]]</h2><br>"
                        + "<h2 style=\"color: blueviolet; font-weight: bolder\">Quiz Code : [[quizCode]]</h2><br>"
                        + "The quiz will start at [[startDate]] "
                        + "and end at [[endDate]] "
                        + "Thank you,<br>"
                        + "Have a good experience in Quiz Maker.";
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage);
        mimeMessageHelper.setFrom(FROM_ADDRESS, SENDER_NAME);
        mimeMessageHelper.setTo(receivedEmail);
        mimeMessageHelper.setSubject(subject);

        content = content.replace("[[quizTitle]]", quizTitle);
        content = content.replace("[[startDate]]", startDate.toString());
        content = content.replace("[[quizCode]]", quizCode);
        content = content.replace("[[endDate]]", endDate.toString());
        mimeMessageHelper.setText(content, true);
        javaMailSender.send(mimeMessage);
        return quizTitle;
    }

    public String sendAssignUpdateToUser(String from, String receivedEmail, String quizTitle, String quizCode, Date startDate, Date endDate, Date updateTime) throws MessagingException, UnsupportedEncodingException {
        final String subject = from + " from Quiz Maker just updated an assigned Quiz at "
                +new SimpleDateFormat("HH:ss dd-MM").format(updateTime).toString();
        String content =
                "Quiz assigned just update : <br>"
                        + "<h2 style=\"color: blueviolet; font-weight: bolder\">[[quizTitle]]</h2><br>"
                        + "<h2 style=\"color: blueviolet; font-weight: bolder\">Quiz Code : [[quizCode]]</h2><br>"
                        + "The quiz will start at [[startDate]] "
                        + "and end at [[endDate]] "
                        + "Thank you,<br>"
                        + "Have a good experience in Quiz Maker.";
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage);
        mimeMessageHelper.setFrom(FROM_ADDRESS, SENDER_NAME);
        mimeMessageHelper.setTo(receivedEmail);
        mimeMessageHelper.setSubject(subject);

        content = content.replace("[[quizTitle]]", quizTitle);
        content = content.replace("[[startDate]]", startDate.toString());
        content = content.replace("[[quizCode]]", quizCode);
        content = content.replace("[[endDate]]", endDate.toString());
        mimeMessageHelper.setText(content, true);
        javaMailSender.send(mimeMessage);
        return quizTitle;
    }

    public String generateVerificationCode() {
        return String.valueOf(new Random().nextInt(899999) + 100000);
    }
    public String sendPasswordChangeRequestCode(String receivedEmail,String code) throws MessagingException, UnsupportedEncodingException {
        final String subject = "Change password verification code";
        String content =
                "Your verification code is:<br>"
                        + "<h2 style=\"color: blueviolet; font-weight: bolder\">[[code]]</h2>"
                        + "Thank you,<br>"
                        + "Have a good experience in Quiz Maker.";
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage);
        mimeMessageHelper.setFrom(FROM_ADDRESS, SENDER_NAME);
        mimeMessageHelper.setTo(receivedEmail);
        mimeMessageHelper.setSubject(subject);

        content = content.replace("[[code]]", code);
        mimeMessageHelper.setText(content, true);
        javaMailSender.send(mimeMessage);
        return code;
    }

}
