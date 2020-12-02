package net.person.blog.services.impl;

import net.person.blog.utils.EmailSend;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;

/**
 * 进行异步操作类
 */
@Service
public class TaskService {


    /**
     * 异步发送邮件验证码
     * @param verifyCode
     * @param emailAddress
     * @throws MessagingException
     */
    @Async
    public void sendEmailVerifyCode(String verifyCode,String emailAddress) throws MessagingException {
        EmailSend.sendRegisterVerifyCode(verifyCode,emailAddress);
    }
}
