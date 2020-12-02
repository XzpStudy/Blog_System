package net.person.blog.response;

/**
 * @author 16272
 */
public enum  ResponseState {

    //枚举响应常用状态
    SUCCESS(true,10000,  "操作成功"),
    FAILED(false,20000,  "操作失败"),
    JOIN_IN_SUCCESS(true,20002,"注册成功"),
    LOGIN_SUCCESS(true,60000, "登录成功"),
    PERMISSION_DENIED(false,40001,  "权限不够"),
    ACCOUNT_NOT_LOGIN(false,40002,  "账号未登录"),
    ACCOUNT_DENIED(false,40003,  "账号已经被禁用"),
    ERROR_403(true,4003, "权限不足"),
    ERROR_404(true,4004, "页面丢失"),
    ERROR_504(true,5004, "系统繁忙，请稍后重试"),
    ERROR_505(true,5005, "请求失败，请检查数据");

    private boolean success;
    private int code;
    private String message;

    ResponseState(boolean success, int code, String message) {
        this.success = success;
        this.code = code;
        this.message = message;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
