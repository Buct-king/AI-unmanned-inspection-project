package cn.lqz.unmannedinspectionsystem.pojo.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Device extends BaseEntity{
    /**
     * 设备id
     */
    private Integer deviceId;
    /**
     * 设备类型
     */
    private Integer deviceType;
    /**
     * 设备名称
     */
    private String name;
    /**
     * 运行状态
     */
    private Integer runStatus;
    /**
     * 健康状态
     */
    private Integer healthStatus;
    /**
     * 车间Id
     */
    private Integer workshopId;
}
