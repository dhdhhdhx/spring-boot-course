package top.zyp.redis.handler;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import top.zyp.redis.exception.ServerException;
import top.zyp.redis.resylt.Result;

import java.time.LocalDateTime;

/**
 * @Author: calm_sunset
 * @Date: 2025/9/25
 * @Version: 1.0
 */


@Slf4j
@Component
@RestControllerAdvice
public class GlobalExceptionHandler implements MetaObjectHandler {

    /* ================= 自定义异常处理 ================= */
    @ExceptionHandler(ServerException.class)
    public Result<String> handleServerException(ServerException e) {
        log.warn("自定义异常：code={}，msg={}", e.getCode(), e.getMsg());
        return Result.error(e.getCode(), e.getMsg());
    }

    /* ================= 参数校验异常 ================= */
    // JSON 请求体 (@Valid + @RequestBody)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Result<String> handleMethodArgumentNotValid(MethodArgumentNotValidException ex) {
        String msg = ex.getBindingResult().getFieldErrors()
                .stream()
                .findFirst()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .orElse("请求参数不合法");
        return Result.error(400, msg);
    }

    // 表单 / 路径绑定失败 (非 @RequestBody)
    @ExceptionHandler(BindException.class)
    public Result<String> handleBindException(BindException ex) {
        FieldError fe = ex.getFieldError();
        String msg = (fe != null) ? (fe.getField() + " " + fe.getDefaultMessage())
                : "请求参数不合法";
        return Result.error(400, msg);
    }

    // 单个参数校验 (@RequestParam / @PathVariable 上的约束，如 @Min)
    @ExceptionHandler(ConstraintViolationException.class)
    public Result<String> handleConstraintViolation(ConstraintViolationException ex) {
        String msg = ex.getConstraintViolations()
                .stream()
                .findFirst()
                .map(ConstraintViolation::getMessage)
                .orElse("请求参数不合法");
        return Result.error(400, msg);
    }

    /* ================= 400 常见请求错误 ================= */
    @ExceptionHandler({HttpMessageNotReadableException.class,
            MethodArgumentTypeMismatchException.class,
            MissingServletRequestParameterException.class})
    public Result<String> handleBadRequest(Exception ex) {
        return Result.error(400, "请求格式错误");
    }

    /* ================= 兜底：未知异常 ================= */
    @ExceptionHandler(Exception.class)
    public Result<String> handleUnknown(Exception ex) {
        log.error("未知异常：", ex);
        return Result.error(500, "服务器异常，请稍后再试");
    }

    /**
     * 插入时自动填充
     */
    @Override
    public void insertFill(MetaObject metaObject) {
        // 创建时间
        this.strictInsertFill(metaObject, "createTime", LocalDateTime.class, LocalDateTime.now());
        // 更新时间
        this.strictInsertFill(metaObject, "updateTime", LocalDateTime.class, LocalDateTime.now());
    }

    /**
     * 更新时自动填充
     */
    @Override
    public void updateFill(MetaObject metaObject) {
        // 更新时间
        this.strictUpdateFill(metaObject, "updateTime", LocalDateTime.class, LocalDateTime.now());
    }
    /** 数据库唯一索引冲突 → 转业务异常 */
    @ExceptionHandler(DuplicateKeyException.class)
    public Result<Void> handleDuplicateKey(DuplicateKeyException e) {
        String cause = e.getCause() != null ? e.getCause().getMessage() : "";
        if (cause.contains("uk_phone")) {
            return Result.error("该手机号已经被注册了");
        }
        if (cause.contains("uk_account")) {
            return Result.error("账号已存在");
        }
        return Result.error("数据已存在");
    }
}
