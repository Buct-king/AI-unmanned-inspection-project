package cn.lqz.unmannedinspectionsystem.pojo.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

/**
 * 模型检测DTO
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DetectDTO {
    /**
     * 待检测图片
     */
    @NotNull
    private MultipartFile image;
    /**
     * 模型名称
     */
    @NotEmpty
    private String modelName;
}
