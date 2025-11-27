package cn.lqz.unmannedinspectionsystem.enums;

/**
 * 设备健康状态枚举
 */
public enum DeviceHealthStatusEnum {
    NORMAL(1,"正常"),
    ALARM(2,"报警"),
    LEAKAGE(3,"跑冒漏滴"),
    INSTRUMENT(4,"仪表异常");

    private final Integer status;
    private final String description;

    DeviceHealthStatusEnum(Integer status,String description){
        this.status = status;
        this.description = description;
    }

    public static DeviceHealthStatusEnum getByStatus(Integer status){
        for (DeviceHealthStatusEnum deviceHealthStatusEnum : DeviceHealthStatusEnum.values()) {
            if(deviceHealthStatusEnum.status.equals(status)){
                return deviceHealthStatusEnum;
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
