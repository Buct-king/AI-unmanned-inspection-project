package cn.lqz.unmannedinspectionsystem.service;

import cn.lqz.unmannedinspectionsystem.pojo.dto.DetectDTO;
import org.springframework.web.multipart.MultipartFile;

public interface DetectService {

    String detect(DetectDTO detectDTO);
}
