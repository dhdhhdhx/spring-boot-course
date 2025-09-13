package top.zyp.boot.config.model;

/**
 * @Author: calm_sunset
 * @Date: 2025/9/12
 * @Version: 1.0
 */


import cn.hutool.core.io.FileUtil;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;
import top.zyp.boot.config.model.enums.ResultStatus;
import top.zyp.boot.config.service.MailService;

import java.io.File;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
class MailServiceTest {

    @Resource
    private MailService mailService;

    @Test
    void sendSimpleMail() {
        Mail mail = new Mail();
        mail.setTo("1514921150@qq.com");
        mail.setSubject("测试简单邮件");
        mail.setContent("简单邮件内容");

        ResultStatus resultStatus = mailService.sendSimpleMail(mail);
        assertEquals(ResultStatus.SUCCESS, resultStatus);
    }


    /* ---------------- 2. HTML 邮件 ---------------- */
    @Test
    void sendHTMLMail() {
        Mail mail = new Mail();
        mail.setTo("1514921150@qq.com");
        mail.setSubject("测试HTML邮件");

        String html = """
                            <!DOCTYPE html>
                            <html>
                            <head>
                              <meta charset="UTF-8">
                              <style>
                                body{font-family:Microsoft YaHei;background:#f6f8fa;margin:0;}
                                .container{max-width:600px;margin:30px auto;background:#fff;border-radius:8px;box-shadow:0 2px 8px rgba(0,0,0,.1);}
                                .header{background:#2d8cf0;color:#fff;text-align:center;padding:20px;font-size:24px;font-weight:bold;}
                                .content{padding:30px;color:#333;line-height:1.6;}
                                .button{display:inline-block;margin:20px 0;padding:12px 28px;background:#2d8cf0;color:#fff;text-decoration:none;border-radius:4px;}
                                .footer{background:#f1f1f1;text-align:center;font-size:12px;color:#666;padding:15px;}
                              </style>
                            </head>
                            <body>
                              <div class="container">
                                <div class="header">账号注册成功确认</div>
                                <div class="content">
                                  <h2>亲爱的用户，欢迎加入！</h2>
                                  <p>您的账号已经注册成功，请点击下方按钮完成邮箱确认：</p>
                                  <a href="https://example.com/verify?token=xxxxxx" class="button">确认我的邮箱</a>
                                  <p>为了保障您的账号安全，本链接将在 <b>24 小时内失效</b>。</p>
                                </div>
                                <div class="footer">©2025 示例平台 · 本邮件由系统自动发送，请勿直接回复</div>
                              </div>
                            </body>
                            </html>
                            """
                ;
        mail
                .setContent(html);

        ResultStatus rs = mailService.sendHTMLMail(mail);
        assertEquals(ResultStatus.SUCCESS, rs);
    }
    /* ---------------- 3. 带附件邮件 ---------------- */
    @Test
    void sendAttachmentsMail() throws IOException {
        Mail mail = new Mail();
        mail.setTo("1514921150@qq.com");
        mail.setSubject("测试带附件的邮件");
        mail.setContent("带附件的邮件内容");

        // 准备两个本地文件（换成你电脑里真实存在的文件）
        File file1 = new File("C://Users//calm_sunset//Desktop//资料//web框架//0-后端环境准备.pdf");
        File file2 = new File("C://Users//calm_sunset//Desktop//资料//web框架//1-基础入门.pdf");

        MultipartFile[] files = new MultipartFile[] {
                new MockMultipartFile(
                        file1.getName(),
                        file1.getName(),
                        MediaType.IMAGE_JPEG_VALUE,
                        FileUtil.readBytes(file1)),
                new MockMultipartFile(
                        file2.getName(),
                        file2.getName(),
                        MediaType.IMAGE_JPEG_VALUE,
                        FileUtil.readBytes(file2))
        };

        ResultStatus rs = mailService.sendAttachmentsMail(mail, files);
        assertEquals(ResultStatus.SUCCESS, rs);
    }
}
