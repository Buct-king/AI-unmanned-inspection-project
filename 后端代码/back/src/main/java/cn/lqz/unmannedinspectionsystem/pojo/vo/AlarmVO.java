package cn.lqz.unmannedinspectionsystem.pojo.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 报警记录展示
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AlarmVO {
    /**
     * 报警记录
     */
    private Long alarmId;
    /**
     * 设备id
     */
    private Integer deviceId;
    /**
     * 测点id
     */
    private Long mpId;
    /**
     * 设备名称
     */
    private String deviceName;
    /**
     * 报警类型/异常测点的状态
     */
    private Integer type;
    /**
     * 报警类型String值
     */
    private String typeString;

    /**
     * 处理状态
     */
    private Integer status;

    /**
     * 报警时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    private LocalDateTime alarmTime;

}
