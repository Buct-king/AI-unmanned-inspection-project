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
import java.util.List;

/**
 * 测点详情VO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MeasuringPointDetailVO {
    /**
     * 测点id
     */
    private Long mpId;
    /**
     * 车间名称
     */
    private String workshopName;
    /**
     * 设备名称
     */
    private String deviceName;
    /**
     * 测点名称
     */
    private String mpName;
    /**
     * 最后更新时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    private LocalDateTime lastUpdateTime;
    /**
     * 测点图片
     */
    private String base64Image;
    /**
     * 该测点的历史报警记录
     * 报警时间、报警类型、处理状态
     */
    private List<AlarmVO> mpAlarmList;
}
