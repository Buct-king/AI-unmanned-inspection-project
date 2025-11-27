package cn.lqz.unmannedinspectionsystem.service;

import cn.lqz.unmannedinspectionsystem.pojo.dto.LoginDTO;
import cn.lqz.unmannedinspectionsystem.pojo.dto.UserChangePasswordDTO;
import cn.lqz.unmannedinspectionsystem.pojo.dto.UserTokenDTO;

public interface AccountService {
    /**
     * 登录
     * @param loginDTO
     * @return
     */
    UserTokenDTO login(LoginDTO loginDTO);

    /**
     * 登出
     * @param userId
     */
    void logout(Long userId);

    /**
     * 用户密码修改
     * @param userChangePasswordDTO
     */
    void changePassword(UserChangePasswordDTO userChangePasswordDTO);
}
