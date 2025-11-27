package cn.lqz.unmannedinspectionsystem.enums;

import cn.lqz.unmannedinspectionsystem.pojo.entity.MeasuringPoint;

/**
 * 测点状态枚举
 */
public enum MeasuringPointStatusEnum {
    NORMAL(0,"正常运行"),
    PIPELINE_LEAKAGE(1,"管道泄漏"),
    LIGHT_ABNORMAL(2,"指示灯异常"),
    LIQUID_LEAKAGE(3,"液体泄露"),
    POINTER_ABNORMAL(4,"指针异常"),
    READING_ABNORMAL(5,"读数异常");
    private Integer status;
    private String description;
    MeasuringPointStatusEnum(Integer status,String description){
        this.status = status;
        this.description = description;
    }

    public static MeasuringPointStatusEnum getByStatus(Integer status){
        for (MeasuringPointStatusEnum measuringPointStatusEnum : MeasuringPointStatusEnum.values()) {
            if(measuringPointStatusEnum.status.equals(status)){
                return measuringPointStatusEnum;
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
