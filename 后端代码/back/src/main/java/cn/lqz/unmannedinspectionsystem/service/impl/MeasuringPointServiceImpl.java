package cn.lqz.unmannedinspectionsystem.service.impl;

import cn.lqz.unmannedinspectionsystem.enums.ResponseCodeEnum;
import cn.lqz.unmannedinspectionsystem.exceptions.BaseException;
import cn.lqz.unmannedinspectionsystem.mapper.AlarmMapper;
import cn.lqz.unmannedinspectionsystem.mapper.MeasuringPointMapper;
import cn.lqz.unmannedinspectionsystem.pojo.dto.AlarmPageQueryDTO;
import cn.lqz.unmannedinspectionsystem.pojo.dto.MeasuringPointPageQueryDTO;
import cn.lqz.unmannedinspectionsystem.pojo.vo.*;
import cn.lqz.unmannedinspectionsystem.service.AlarmService;
import cn.lqz.unmannedinspectionsystem.service.MeasuringPointService;
import cn.lqz.unmannedinspectionsystem.utils.FilePathUtils;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.util.Base64;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class MeasuringPointServiceImpl implements MeasuringPointService {
    @Value("${service.mp.image-folder-path}")
    private String mpImageFolderPath;
    private final MeasuringPointMapper measuringPointMapper;
    private final AlarmService alarmService;
    /**
     * 测点分页查询
     * @param measuringPointPageQueryDTO
     * @return
     */
    @Override
    public PageResultVO loadMeasuringPointList(MeasuringPointPageQueryDTO measuringPointPageQueryDTO) {
        log.info("分页查询测点记录：{}",measuringPointPageQueryDTO);
        PageHelper.startPage(measuringPointPageQueryDTO.getPageNo(),measuringPointPageQueryDTO.getPageSize());
        Page<MeasuringPointVO> measuringPointVOPage = measuringPointMapper.findByStatus(measuringPointPageQueryDTO);
        // 记载测点图片
        // 测点图片文件夹
        File folder = new File(mpImageFolderPath);
        if(!folder.exists()){
            folder.mkdirs();
        }
        try{
            for (MeasuringPointVO measuringPointVO : measuringPointVOPage) {
                // 测点图片文件
                measuringPointVO.setBase64Image(loadImage(measuringPointVO.getMpId()));
            }
        }catch (Exception e){
            log.error("加载测点图片失败");
            throw new BaseException(ResponseCodeEnum.CODE_500);
        }
        return new PageResultVO(measuringPointPageQueryDTO.getPageNo(),measuringPointPageQueryDTO.getPageSize(),measuringPointVOPage.getTotal(),measuringPointVOPage.getResult());
    }

    /**
     * 测点图片
     * @param mpId
     */
    @Override
    public String loadImage(Long mpId) {
        log.info("获取测点图片：{}",mpId);
        // 测点图片文件夹
        File folder = new File(mpImageFolderPath);
        if(!folder.exists()){
            folder.mkdirs();
        }
        // 测点图片文件
        File file = new File(FilePathUtils.generateMpImageFilePath(mpImageFolderPath,mpId));
        // 转Base64返回
        try{
            BufferedImage bufferedImage = ImageIO.read(file);

            // 如果图片是 ARGB 格式（TYPE_4BYTE_ABGR），转换为 RGB 格式
            if (bufferedImage.getType() == BufferedImage.TYPE_4BYTE_ABGR) {
                BufferedImage rgbImage = new BufferedImage(
                        bufferedImage.getWidth(),
                        bufferedImage.getHeight(),
                        BufferedImage.TYPE_3BYTE_BGR
                );
                Graphics2D g = rgbImage.createGraphics();
                g.drawImage(bufferedImage, 0, 0, null);
                g.dispose();
                bufferedImage = rgbImage;
            }

            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            ImageIO.write(bufferedImage,"jpg",byteArrayOutputStream);
            byte[] imageBytes = byteArrayOutputStream.toByteArray();
            return Base64.getEncoder().encodeToString(imageBytes);
        }catch (Exception e){
            log.error("加载测点图片失败");
            throw new BaseException(ResponseCodeEnum.CODE_500);
        }
    }

    /**
     * 获取测点详情
     * @param mpId
     * @return
     */
    @Override
    public MeasuringPointDetailVO getMeasuringPointDetail(Long mpId) {
        log.info("获取测点详情：{}",mpId);
        // 获取测点详情
        MeasuringPointDetailVO measuringPointDetailVO = measuringPointMapper.findMpDetailByMpId(mpId);
        // 获取测点图片编码
        measuringPointDetailVO.setBase64Image(loadImage(mpId));
        // 获取该测点的所有报警记录
        AlarmPageQueryDTO alarmPageQueryDTO = new AlarmPageQueryDTO();
        alarmPageQueryDTO.setMpId(mpId);
        alarmPageQueryDTO.setPageNo(1);
        alarmPageQueryDTO.setPageSize(10);
        PageResultVO pageResultVO = alarmService.loadAlarmList(alarmPageQueryDTO);
        List<AlarmVO> list = pageResultVO.getList();
        measuringPointDetailVO.setMpAlarmList(list);
        return measuringPointDetailVO;
    }
}
