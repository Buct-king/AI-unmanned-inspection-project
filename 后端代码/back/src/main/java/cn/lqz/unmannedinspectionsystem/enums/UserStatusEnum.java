package cn.lqz.unmannedinspectionsystem.enums;

public enum UserStatusEnum {
    DISABLE(0,"禁用"),
    ENABLE(1,"启用");

    private Integer status;
    private String description;
    UserStatusEnum(Integer status,String description){
        this.status = status;
        this.description = description;
    }
    public static UserStatusEnum getByStatus(Integer status){
        for (UserStatusEnum userStatusEnum : UserStatusEnum.values()) {
            if(userStatusEnum.status.equals(status)){
                return userStatusEnum;
            }
        }
        return null;
    }
    public Integer getStatus() {
        return status;
    }

    public String getDescription() {
        return description;
    }
}
