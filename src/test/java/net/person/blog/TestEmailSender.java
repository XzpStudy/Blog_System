package net.person.blog;


import net.person.blog.utils.EmailSend;

import javax.mail.MessagingException;


public class TestEmailSender {
    public static void main(String[] args) throws MessagingException {
        EmailSend.subject("测试邮件发送")
                .from("个人博客系统")
                .text("这是发送的内容：ab12rf")
                .to("1360786842@qq.com")
                .send();
    }
}
