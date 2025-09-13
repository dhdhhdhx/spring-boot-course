package top.zyp.boot.config.controller;

import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import top.zyp.boot.config.common.ApiResponse;
import top.zyp.boot.config.model.Mail;
import top.zyp.boot.config.model.enums.ResultStatus;
import top.zyp.boot.config.service.MailService;

/**
 * @Author: calm_sunset
 * @Date: 2025/9/12
 * @Version: 1.0
 */


@RestController
@RequestMapping("/mail")
public class MailController {
    @Resource
    private MailService mailService;

    @PostMapping("/simple")
    public ResponseEntity<ApiResponse<ResultStatus>> sendSimpleMail(@Valid @RequestBody Mail mail) {
        ResultStatus rs = mailService.sendSimpleMail(mail);
        if (rs == ResultStatus.SUCCESS) {
            return ResponseEntity.ok(ApiResponse.success("发送成功", rs));
        } else {
            return ResponseEntity.badRequest().body(ApiResponse.error("发送失败"));
        }
    }
    @PostMapping("/html")
    public ResponseEntity<ApiResponse<ResultStatus>> sendHtmlMail(@Valid @RequestBody Mail mail) {
        ResultStatus rs = mailService.sendHTMLMail(mail);
        return rs == ResultStatus.SUCCESS
                ? ResponseEntity.ok(ApiResponse.success("发送成功", rs))
                : ResponseEntity.badRequest().body(ApiResponse.error("发送失败"));
    }
    @PostMapping(value = "/attachment", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<ResultStatus>> sendAttachmentsMail(
            @Valid @RequestPart("mail") Mail mail,
            @RequestPart("files") MultipartFile[] files) {
        ResultStatus rs = mailService.sendAttachmentsMail(mail, files);
        return rs == ResultStatus.SUCCESS
                ? ResponseEntity.ok(ApiResponse.success("发送成功", rs))
                : ResponseEntity.badRequest().body(ApiResponse.error("发送失败"));
    }
}
