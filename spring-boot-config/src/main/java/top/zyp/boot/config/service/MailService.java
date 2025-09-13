package top.zyp.boot.config.service;

import org.springframework.web.multipart.MultipartFile;
import top.zyp.boot.config.model.Mail;
import top.zyp.boot.config.model.enums.ResultStatus;

public interface MailService {
    ResultStatus sendSimpleMail(Mail mail);
    ResultStatus sendHTMLMail(Mail mail);
    ResultStatus sendAttachmentsMail(Mail mail, MultipartFile[] files);
}