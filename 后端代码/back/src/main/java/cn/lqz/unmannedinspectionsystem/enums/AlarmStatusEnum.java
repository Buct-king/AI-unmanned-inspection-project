package cn.lqz.unmannedinspectionsystem.enums;

/**
 * 报警记录处理状态
 */
public enum AlarmStatusEnum {
    UNPROCESSED(0,"未处理"),
    PROCESSED(1,"已处理");

    private Integer status;
    private String description;
    AlarmStatusEnum(Integer status,String description){
        this.status = status;
        this.description = description;
    }
    public static AlarmStatusEnum getByStatus(Integer status){
        for (AlarmStatusEnum alarmStatusEnum : AlarmStatusEnum.values()) {
            if(alarmStatusEnum.getStatus().equals(status)){
                return alarmStatusEnum;
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
