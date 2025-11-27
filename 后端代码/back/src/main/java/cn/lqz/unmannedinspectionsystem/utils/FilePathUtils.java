package cn.lqz.unmannedinspectionsystem.utils;

import cn.lqz.unmannedinspectionsystem.config.YoloConfig;
import cn.lqz.unmannedinspectionsystem.constants.FileConstants;
import jakarta.annotation.Resource;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * 文件路径工具类
 */
public class FilePathUtils {

    /**
     * 生成yolo入参图片路径
     * @param folderPath
     * @param id
     * @return
     */
    public static String generateYoloInputImageFilePath(String folderPath,String id){
        return folderPath + FileConstants.PATH_SEPARATOR + id+FileConstants.IMAGE_SUFFIX;
    }

    /**
     * 生成yolo出参图片路径
     * @param folderPath
     * @param id
     * @return
     */
    public static String generateYoloOutputImageFilePath(String folderPath,String id){
        return folderPath + FileConstants.PATH_SEPARATOR + id+FileConstants.IMAGE_SUFFIX;
    }

    /**
     * 生成模型文件路径
     * @param folderPath
     * @param modelName
     * @return
     */
    public static String generateYoloModelFilePath(String folderPath,String modelName){
        return folderPath + FileConstants.PATH_SEPARATOR + modelName + FileConstants.MODEL_SUFFIX;
    }

    public static String generateMpImageFilePath(String folderPath,Long mpId){
        return folderPath + FileConstants.PATH_SEPARATOR + mpId + FileConstants.IMAGE_SUFFIX;
    }
}
