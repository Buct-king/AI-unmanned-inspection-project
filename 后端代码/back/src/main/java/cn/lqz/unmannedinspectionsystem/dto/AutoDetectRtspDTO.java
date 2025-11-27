package cn.lqz.unmannedinspectionsystem.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class AutoDetectRtspDTO {
    @NotBlank(message = "ip不能为空")
    private String ip;
    @NotBlank(message = "port不能为空")
    private String port;
    @NotBlank(message = "user不能为空")
    private String user;
    @NotBlank(message = "pwd不能为空")
    private String pwd;
} 