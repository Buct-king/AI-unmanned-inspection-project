package cn.lqz.unmannedinspectionsystem.pojo.entity;

import lombok.Data;
import java.util.Date;

@Data
public class CameraModelBind {
    private Long id;
    private String cameraId;
    private String cameraName;
    private String cameraSource;
    private String modelName;
    private Date createTime;
    private Date updateTime;
} 