package cn.lqz.unmannedinspectionsystem.enums;

/**
 * 设备类型枚举
 */
public enum DeviceTypeEnum {
    PUMP(0,"水泵"),
    EDI(1,"EDI装置"),
    PIPELINE(2,"管道");
    private Integer type;
    private String description;

    DeviceTypeEnum(Integer type,String description){
        this.type = type;
        this.description = description;
    }

    public static DeviceTypeEnum getByType(Integer type){
        for (DeviceTypeEnum deviceTypeEnum : DeviceTypeEnum.values()) {
            if(deviceTypeEnum.getType().equals(type)){
                return deviceTypeEnum;
            }
        }
        return null;
    }

    public Integer getType() {
        return type;
    }

    public String getDescription() {
        return description;
    }
}
