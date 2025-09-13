package top.zyp.boot.config.impl;

import jakarta.annotation.Resource;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import top.zyp.boot.config.model.Mail;
import top.zyp.boot.config.model.enums.ResultStatus;
import top.zyp.boot.config.service.MailService;

import java.nio.charset.StandardCharsets;
import java.util.Objects;

/**
 * @Author: calm_sunset
 * @Date: 2025/9/12
 * @Version: 1.0
 */


@Service
public class MailServiceImpl implements MailService {

    @Value("${spring.mail.username}")
    private String from;

    @Resource
    private JavaMailSender javaMailSender;
    /* ---------------- ① 简单文本 ---------------- */
    @Override
    public ResultStatus sendSimpleMail(Mail mail) {
        try {
            SimpleMailMessage msg = new SimpleMailMessage();
            msg.setFrom(from);
            msg.setTo(mail.getTo());
            msg.setSubject(mail.getSubject());
            msg.setText(mail.getContent());
            javaMailSender.send(msg);
            return ResultStatus.SUCCESS;
        } catch (Exception e) {
            return ResultStatus.FAIL;
        }
    }

    /* ---------------- ② HTML 富文本 ---------------- */
    @Override
    public ResultStatus sendHTMLMail(Mail mail) {
        try {
            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, false, StandardCharsets.UTF_8.name());
            helper.setFrom(from);
            helper.setTo(mail.getTo());
            helper.setSubject(mail.getSubject());
            helper.setText(mail.getContent(), true);   // true = HTML
            javaMailSender.send(message);
            return ResultStatus.SUCCESS;
        } catch (Exception e) {
            return ResultStatus.FAIL;
        }
    }

    /* ---------------- ③ 带附件 ---------------- */
    @Override
    public ResultStatus sendAttachmentsMail(Mail mail, MultipartFile[] files) {
        try {
            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, StandardCharsets.UTF_8.name());
            helper.setFrom(from);
            helper.setTo(mail.getTo());
            helper.setSubject(mail.getSubject());
            helper.setText(mail.getContent(), true);
            if (files != null) {
                for (MultipartFile f : files) {
                    if (f != null && !f.isEmpty()) {
                        helper.addAttachment(
                                Objects.requireNonNull(f.getOriginalFilename()),
                                new ByteArrayResource(f.getBytes()));
                    }
                }
            }
            javaMailSender.send(message);
            return ResultStatus.SUCCESS;
        } catch (Exception e) {
            return ResultStatus.FAIL;
        }
    }


}