package cn.lqz.unmannedinspectionsystem.pojo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MeasuringPointPageQueryDTO extends PageDTO{
    /**
     * 车间名称
     */
    private String workshopName;
    /**
     * 设备类型
     */
    private Integer deviceType;
    /**
     * 测点状态
     */
    private Integer status;
    /**
     * 设备名称
     */
    private String deviceName;
}
