package cn.lqz.unmannedinspectionsystem.pojo.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 报警记录
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Alarm extends BaseEntity{
    /**
     * 报警id
     */
    private Long alarmId;
    /**
     * 设备id
     */
    private Integer deviceId;
    /**
     * 所属测点id
     */
    private Long mpId;
    /**
     * 报警类型/异常测点的状态
     */
    private Integer type;
    /**
     * 报警描述
     */
    private String description;
    /**
     * 处置状态
     */
    private Integer status;
    /**
     * 报警时间
     */
    private LocalDateTime alarmTime;
    /**
     * 处理时间点
     */
    private LocalDateTime processedTime;
}
