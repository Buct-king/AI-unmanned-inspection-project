package cn.lqz.unmannedinspectionsystem.enums;

/**
 * 设备运行状态枚举
 */
public enum DeviceRunStatusEnum {
    STOP(0,"停机"),
    RUNNING(1,"运行"),
    DISCONNECT(2,"断网");

    private Integer status;
    private String description;

    DeviceRunStatusEnum(Integer status,String description){
        this.status = status;
        this.description = description;
    }

    public static DeviceRunStatusEnum getByStatus(Integer status){
        for (DeviceRunStatusEnum deviceRunStatusEnum : DeviceRunStatusEnum.values()) {
            if(deviceRunStatusEnum.status.equals(status)){
                return deviceRunStatusEnum;
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
