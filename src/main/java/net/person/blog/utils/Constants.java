package net.person.blog.utils;

public interface Constants {

    interface User{
        String ROLE_ADMIN = "role_admin";
        String ROLE_NORMAL = "role_normal";
        String DEFAULT_AVATAR = "https://cdn.sunofbeaches.com/images/default_avatar.png";
        String DEFAULT_STATE = "1";

        //Redis中图灵验证码的key
        String KEY_CAPTCHA_CONTENT = "key_captcha_content_";

        //Redis中邮箱验证码的key
        String KEY_EMAIL_CODE_CONTENT = "key_email_code_content_";
        String KEY_EMAIL_SEND_IP = "key_email_send_ip_";
        String KEY_EMAIL_SEND_ADDRESS = "key_email_send_address_";

        //Redis中token的key
        String KEY_TOKEN_MD5 = "key_token_md5_";

        //Cookie中tokenMD5的key
        String KEY_COOKIE_TOKEN = "blog_system";
    }

    interface Settings{
        String MANAGER_ACCOUNT_INIT_STATE = "manager_account_init_state";
        String WEB_SIZE_TITLE = "web_size_title";
        String WEB_SIZE_DESCRIPTION = "web_size_description";
        String WEB_SIZE_KEYWORDS = "web_size_keywords";
        String WEB_SIZE_VIEW_COUNT = "web_size_view_count";
    }

    interface Page{
        int DEFAULT_PAGE = 1;
        int DEFAULT_SIZE = 5;
    }

    /**
     * 单位秒
     */
    interface TimeValue{
        int MIN = 60;
        int HOUR = 60 * MIN;
        int DAY = 24 * HOUR;
        int MONTH = 30 * DAY;
        int YEAR = 12 * MONTH;
    }

    interface ImageType{
        String PREFIX = "image/";
        String TYPE_JPG = "jpg";
        String TYPE_PNG = "png";
        String TYPE_GIF = "gif";
        String TYPE_JPG_WITH_PREFIX = PREFIX + "jpeg";
        String TYPE_PNG_WITH_PREFIX = PREFIX + "png";
        String TYPE_GIF_WITH_PREFIX = PREFIX + "gif";
    }

    interface Article{
        int TITLE_MAX_LENGTH = 128;
        int SUMMARY_MAX_LENGTH = 256;
        //文章的状态：0表示删除，1表示已经发布，2表示草稿，3表示待审核
        String STATE_DELETE = "0";
        String STATE_PUBLISH = "1";
        String STATE_DRAFT = "2";
        String STATE_CHECK = "3";

        //文章是否置顶，0表示不置顶，1表示置顶
        String TOP_FALSE = "0";
        String TOP_TRUE = "1";
    }

    interface Comment{
        String STATE_DELETE = "0";
        String STATE_PUBLISH = "1";

        //文章是否置顶，0表示不置顶，1表示置顶
        String TOP_FALSE = "0";
        String TOP_TRUE = "1";
    }

}
