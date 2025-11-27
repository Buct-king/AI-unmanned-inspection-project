package cn.lqz.unmannedinspectionsystem.utils;

import cn.lqz.unmannedinspectionsystem.enums.ResponseCodeEnum;
import cn.lqz.unmannedinspectionsystem.exceptions.BaseException;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

/**
 * yolo调用
 */
public class YoloUtils {
    /**
     * 调用yolo模型
     * @param outputFileFolderPath
     * @param uuid
     * @param inputFilePath
     * @return
     */
    public static String execute(String outputFileFolderPath,String uuid,String inputFilePath) {
        File outputFileFolder = new File(outputFileFolderPath);
        if(!outputFileFolder.exists()){
            outputFileFolder.mkdirs();
        }
        // 入参图片文件
        File inputFile = new File(inputFilePath);
        // 出参图片文件
        String outputImageFilePath = FilePathUtils.generateYoloOutputImageFilePath(outputFileFolderPath, uuid);
        File outputFile = new File(outputImageFilePath);

        // demo，把入参图片 复制到 出参图片
        try(
                FileInputStream fileInputStream = new FileInputStream(inputFile);
                FileOutputStream fileOutputStream = new FileOutputStream(outputFile)
        ){
            byte[] byteData = new byte[1024];
            int length;
            while((length=fileInputStream.read(byteData))!=-1){
                fileOutputStream.write(byteData,0,length);
            }
            fileOutputStream.flush();
        }catch (Exception e){
            throw new BaseException(ResponseCodeEnum.CODE_500);
        }

        return outputImageFilePath;
    }
}
