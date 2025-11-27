package cn.lqz.unmannedinspectionsystem.pojo.entity;

import lombok.Data;
import java.util.Date;

@Data
public class ModelPt {
    private Long id;
    private String fileName;
    private String title;
    private String description;
    private Integer status;
    private Date createTime;
    private Date updateTime;
} 