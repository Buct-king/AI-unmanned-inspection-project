package cn.lqz.unmannedinspectionsystem.pojo.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 车间
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Workshop extends BaseEntity{
    /**
     * 车间id
     */
    private Integer workshopId;
    /**
     * 车间名称
     */
    private String name;
    /**
     * 车间状态
     */
    private Integer status;
    /**
     * 拥有的设备数目
     */
    private Integer deviceCount;
}
