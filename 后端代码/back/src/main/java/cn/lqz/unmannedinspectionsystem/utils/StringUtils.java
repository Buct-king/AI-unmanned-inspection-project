package cn.lqz.unmannedinspectionsystem.utils;

import org.apache.commons.codec.digest.DigestUtils;

public class StringUtils {
    /**
     * 根据用户id生成token
     * @param userId
     * @return
     */
    public static String getUserToken(Long userId){
        return DigestUtils.md5Hex(String.valueOf(userId));
    }
}
