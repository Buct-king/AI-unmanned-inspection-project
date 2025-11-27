package cn.lqz.unmannedinspectionsystem.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Data
public class YoloConfig {
    // 入参图片文件夹
    @Value("${service.yolo.input-image-file-folder-path}")
    private String inputImageFolder;
    @Value("${service.yolo.output-image-file-folder-path}")
    // 出参图片文件夹
    private String outputImageFolder;
    // model文件文件夹
    @Value("${service.yolo.model-file-folder-path}")
    private String modelFolder;
    // 模型执行器
    @Value("${service.yolo.executor-path}")
    private String executorPath;

}
