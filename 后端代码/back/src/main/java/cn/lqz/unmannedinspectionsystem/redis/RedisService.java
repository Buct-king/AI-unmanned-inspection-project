package cn.lqz.unmannedinspectionsystem.redis;

import cn.lqz.unmannedinspectionsystem.pojo.dto.UserTokenDTO;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class RedisService {

    private static Map<String,UserTokenDTO> userTokenDTOMap = new HashMap<>();

    /**
     * 获取UserTokenDTO
     * @param token
     * @return
     */
    public UserTokenDTO getUserTokenDTO(String token){
        return userTokenDTOMap.getOrDefault(token,null);
    }

    /**
     * 存储UserTokenDTO
     * @param token
     * @param userTokenDTO
     */
    public void saveUserTokenDTO(String token,UserTokenDTO userTokenDTO){
        userTokenDTOMap.put(token,userTokenDTO);
    }

    /**
     * 删除UserTokenDTO
     * @param token
     */
    public void deleteUserTokenDTO(String token){
        userTokenDTOMap.remove(token);
    }

}
