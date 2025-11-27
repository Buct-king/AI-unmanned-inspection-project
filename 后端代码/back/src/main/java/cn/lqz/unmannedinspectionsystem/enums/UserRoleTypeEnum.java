package cn.lqz.unmannedinspectionsystem.enums;

/**
 * 用户角色类型枚举
 */
public enum UserRoleTypeEnum {
    USER(0,"用户"),
    ADMIN(1,"管理员");
    private Integer type;
    private String description;
    UserRoleTypeEnum(Integer type, String description){
        this.type = type;
        this.description = description;
    }
    public static UserRoleTypeEnum getByType(Integer type){
        for (UserRoleTypeEnum userRoleTypeEnum : UserRoleTypeEnum.values()) {
            if (userRoleTypeEnum.getType().equals(type)){
                return userRoleTypeEnum;
            }
        }
        return null;
    }
    public Integer getType(){
        return type;
    }
    public String getDescription(){
        return description;
    }
}
