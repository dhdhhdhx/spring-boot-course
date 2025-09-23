package top.zyp.boot.exception.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import top.zyp.boot.exception.entity.BookAccount;
import top.zyp.boot.exception.mapper.BookAccountMapper;
import top.zyp.boot.exception.service.BookAccountService;

/**
 * @Author: calm_sunset
 * @Date: 2025/9/21
 * @Version: 1.0
 */

@Service
@Transactional
public class BookAccountServiceImpl extends ServiceImpl<BookAccountMapper, BookAccount> implements BookAccountService {

    private  void processUserBeforeSave(BookAccount bookAccount) {
        //默认未删除
        if (bookAccount.getDeleted() == null){
            bookAccount.setDeleted(0);
        }
        //默认版本号
        if(bookAccount.getVersion() == null){
            bookAccount.setVersion(0);
        }
    }
}
