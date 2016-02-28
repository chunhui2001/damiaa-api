package net.snnmo.assist;

/**
 * Created by cc on 16/2/28.
 */
public enum  UserRole {
    ROLE_USER,          // 所有注册用户都具有该角色
    ROLE_ADMIN,         // 系统管理员
    ROLE_SUPERADMIN,    // 超级系统管理员
    ROLE_VIP,           // 具有该角色的用户可以享受特殊价格
    ROLE_SUPER_VIP      // 具有该角色的用户可以享受全网最低价
}
