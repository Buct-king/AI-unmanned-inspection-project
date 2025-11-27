package cn.lqz.unmannedinspectionsystem.pojo.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DeviceStatusCountVO {
    /**
     * 运行中设备数
     */
    private Integer runningCount;
    /**
     * 报警设备数
     */
    private Integer alarmCount;
    /**
     * 运行中设备百分比
     */
    private Integer runningPercent;
    /**
     * 报警设备百分比
     */
    private Integer alarmPercent;
}
