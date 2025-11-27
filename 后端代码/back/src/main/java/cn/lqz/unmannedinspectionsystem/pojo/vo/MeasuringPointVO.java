package cn.lqz.unmannedinspectionsystem.pojo.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MeasuringPointVO {
    /**
     * 测点id
     */
    private Long mpId;
    /**
     * 设备名称
     */
    private String deviceName;
    /**
     * 测点名称
     */
    private String measuringPointName;
    /**
     * 测点状态
     */
    private Integer measuringPointStatus;
    /**
     * 车间名称
     */
    private String workshopName;
    /**
     * 测点图片Base64编码
     */
    private String base64Image;


}
