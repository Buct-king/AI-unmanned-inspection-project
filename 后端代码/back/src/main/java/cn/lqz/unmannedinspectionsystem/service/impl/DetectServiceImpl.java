package cn.lqz.unmannedinspectionsystem.service.impl;

import cn.lqz.unmannedinspectionsystem.config.YoloConfig;
import cn.lqz.unmannedinspectionsystem.enums.ResponseCodeEnum;
import cn.lqz.unmannedinspectionsystem.exceptions.BaseException;
import cn.lqz.unmannedinspectionsystem.pojo.dto.DetectDTO;
import cn.lqz.unmannedinspectionsystem.service.DetectService;
import cn.lqz.unmannedinspectionsystem.utils.FilePathUtils;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.util.Base64;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class DetectServiceImpl implements DetectService {
    private final YoloConfig yoloConfig;
    /**
     * 图片检测
     * @param detectDTO
     */
    @Override
    public String detect(DetectDTO detectDTO) {
        log.info("图片检测：{}",detectDTO.getModelName());
        MultipartFile image = detectDTO.getImage();
        // 图片目录生成
        File yoloInputImageFileFolder = new File(yoloConfig.getInputImageFolder());
        if(!yoloInputImageFileFolder.exists()){
            yoloInputImageFileFolder.mkdirs();
        }
        // 将图片存到本地
        UUID uuid = UUID.randomUUID();
        File inputFile = new File(FilePathUtils.generateYoloInputImageFilePath(yoloConfig.getInputImageFolder(),uuid.toString()));
        try{
            image.transferTo(inputFile);
        }catch (Exception e){
            log.error("图片上传异常");
            throw new BaseException(ResponseCodeEnum.CODE_500);
        }

        // 模型不存在
        String modelFilePath = FilePathUtils.generateYoloModelFilePath(yoloConfig.getModelFolder(), detectDTO.getModelName());
        if(!new File(modelFilePath).exists()){
            throw new BaseException(ResponseCodeEnum.CODE_400.getCode(),"模型不存在");
        }

        // 检测
        executeYolo(uuid.toString(),detectDTO.getModelName());

        // 读取图片
        File outputFile = new File(FilePathUtils.generateYoloOutputImageFilePath(yoloConfig.getOutputImageFolder(),uuid.toString()));
        // 转Base64返回
        try{
            BufferedImage bufferedImage = ImageIO.read(outputFile);
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            ImageIO.write(bufferedImage,"jpg",byteArrayOutputStream);
            byte[] imageBytes = byteArrayOutputStream.toByteArray();
            return Base64.getEncoder().encodeToString(imageBytes);

        }catch (Exception e){
            log.error("加载测点图片失败");
            throw new BaseException(ResponseCodeEnum.CODE_500);
        }
    }


    private void executeYolo(String fileUUID,String modelName){
        String inputImagePath = FilePathUtils.generateYoloInputImageFilePath(yoloConfig.getInputImageFolder(),fileUUID);
        String outputFilePath = FilePathUtils.generateYoloOutputImageFilePath(yoloConfig.getOutputImageFolder(),fileUUID);
        String modelPath = FilePathUtils.generateYoloModelFilePath(yoloConfig.getModelFolder(),modelName);
        String executorPath = yoloConfig.getExecutorPath();;
        String[] command = {
                executorPath,
                "--model",modelPath,
                "--input",inputImagePath,
                "--output",outputFilePath
        };
        ProcessBuilder processBuilder = new ProcessBuilder(command);
        try{
            Process process = processBuilder.start();
            // 同步等待执行结果
            int exitCode = process.waitFor();
            if(exitCode!=0){
                log.error("yolo命令脚本调用失败，退出码：{}",exitCode);
                throw new BaseException(ResponseCodeEnum.CODE_500);
            }
        }catch (Exception e){
            log.error("yolo模型检测失败");
            throw new BaseException(ResponseCodeEnum.CODE_500);
        }
    }
}
