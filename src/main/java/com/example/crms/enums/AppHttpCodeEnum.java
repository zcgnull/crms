package com.example.crms.enums;

public enum AppHttpCodeEnum {
    // 成功
    SUCCESS(200,"操作成功"),
    // 登录
    NEED_LOGIN(401,"需要登录后操作"),
    NO_OPERATOR_AUTH(403,"无权限操作"),
    SYSTEM_ERROR(500,"出现错误"),
    USERNAME_EXIST(501,"用户名已存在"),
    PHONENUMBER_EXIST(502,"手机号已存在"),
    EMAIL_EXIST(503, "邮箱已存在"),
    REQUIRE_USERNAME(504, "必须填写用户名"),
    Status_EXIST(505,"该时间段已存在其它状态！"),
    RoleName_EXIST(506,"该角色已存在"),
    UserRole_EXIST(507,"所选角色仍对应用户，无法删除"),
    DepartmentName_EXIST(508,"该部门已存在"),
    UserDepartment_EXIST(507,"所选部门仍对应用户，无法删除");
//    CONTENT_NOT_NULL(555,"无法发表空白评论"),
//    FILE_TYPE_ERROR(507,"图片类型错误，请上传jpg或者png类型图片"),
//    USERNAME_NOT_NULL(508, "用户名不能为空"),
//    NICKNAME_NOT_NULL(509, "昵称不能为空"),
//    PASSWORD_NOT_NULL(510, "密码不能为空"),
//    EMAIL_NOT_NULL(511, "邮箱不能为空"),
//    NICKNAME_EXIST(512, "昵称已存在"),
//    LOGIN_ERROR(505,"用户名或密码错误"),
//    UPDATE_MENU_NULL(513,"内容不能为空"),
//    UPDATE_MENU_CF(514,"父菜单不能为自己"),
//    ADD_MENU_FAIL(515, "当前菜单已存在"),
//    COMMENT_NO(600, "该文章不允许评论");
    int code;
    String msg;

    AppHttpCodeEnum(int code, String errorMessage){
        this.code = code;
        this.msg = errorMessage;
    }

    public int getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }
}
