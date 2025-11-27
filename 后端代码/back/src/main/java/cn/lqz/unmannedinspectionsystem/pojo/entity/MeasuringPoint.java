package cn.lqz.unmannedinspectionsystem.pojo.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 测点
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MeasuringPoint extends BaseEntity{
    /**
     * 测点id
     */
    private Long mpId;
    /**
     * 设备id
     */
    private Integer deviceId;
    /**
     * 测点名称
     */
    private String name;
    /**
     * 测点类型
     */
    private Integer type;
    /**
     * 测点状态
     */
    private Integer status;
}
