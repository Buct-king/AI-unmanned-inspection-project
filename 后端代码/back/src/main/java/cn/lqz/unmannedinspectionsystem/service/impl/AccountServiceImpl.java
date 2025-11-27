package cn.lqz.unmannedinspectionsystem.service.impl;

import cn.lqz.unmannedinspectionsystem.context.BaseContext;
import cn.lqz.unmannedinspectionsystem.enums.ResponseCodeEnum;
import cn.lqz.unmannedinspectionsystem.enums.UserRoleTypeEnum;
import cn.lqz.unmannedinspectionsystem.enums.UserStatusEnum;
import cn.lqz.unmannedinspectionsystem.exceptions.BaseException;
import cn.lqz.unmannedinspectionsystem.mapper.UserMapper;
import cn.lqz.unmannedinspectionsystem.pojo.dto.LoginDTO;
import cn.lqz.unmannedinspectionsystem.pojo.dto.UserChangePasswordDTO;
import cn.lqz.unmannedinspectionsystem.pojo.dto.UserTokenDTO;
import cn.lqz.unmannedinspectionsystem.pojo.entity.User;
import cn.lqz.unmannedinspectionsystem.redis.RedisService;
import cn.lqz.unmannedinspectionsystem.service.AccountService;
import cn.lqz.unmannedinspectionsystem.utils.StringUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {
    private final UserMapper userMapper;
    private final RedisService redisService;
    /**
     * 登录
     * @param loginDTO
     * @return
     */
    @Override
    public UserTokenDTO login(LoginDTO loginDTO) {
        log.info("用户登录：{}",loginDTO);

        // 1.用户是否存在
        User user = userMapper.findByPhoneNumber(loginDTO.getPhoneNumber());
        if(null==user){
            throw new BaseException(ResponseCodeEnum.CODE_400.getCode(),"用户不存在");
        }
        // 2.密码是否正确
        if(!user.getPassword().equals(loginDTO.getPassword())){
            throw new BaseException(ResponseCodeEnum.CODE_400.getCode(),"账号或密码错误");
        }
        // 3.用户是否被禁用
        if(!UserStatusEnum.ENABLE.getStatus().equals(user.getStatus())){
            throw new BaseException(ResponseCodeEnum.CODE_400.getCode(),"用户已被禁用");
        }
        // 4.用户是否有权登录
        if(!UserRoleTypeEnum.ADMIN.getType().equals(user.getRoleType())){
            throw new BaseException(ResponseCodeEnum.CODE_400.getCode(),"用户无权登录");
        }
        // 5.查redis，看用户是否已在线
        String token = StringUtils.getUserToken(user.getUserId());
        if(redisService.getUserTokenDTO(token)!=null){
            throw new BaseException(ResponseCodeEnum.CODE_400.getCode(),"用户已登录");
        }
        // 6.生成tokenDTO
        UserTokenDTO userTokenDTO = new UserTokenDTO();
        BeanUtils.copyProperties(user,userTokenDTO);
        userTokenDTO.setToken(token);
        // 7.存入redis
        redisService.saveUserTokenDTO(token,userTokenDTO);
        return userTokenDTO;
    }

    /**
     * 登出
     * @param userId
     */
    @Override
    public void logout(Long userId) {
        log.info("用户登出：{}",userId);
        // 1.用户是否在线
        String token = StringUtils.getUserToken(userId);
        UserTokenDTO userTokenDTO = redisService.getUserTokenDTO(token);
        if(userTokenDTO==null){
            throw new BaseException(ResponseCodeEnum.CODE_600);
        }
        // 2.用户是否有权操作
        if(!userTokenDTO.getUserId().equals(userId)){
            throw new BaseException(ResponseCodeEnum.CODE_600);
        }
        // 3.清除Redis用户状态
        redisService.deleteUserTokenDTO(token);
    }

    /**
     * 用户密码修改
     * @param userChangePasswordDTO
     */
    @Override
    public void changePassword(UserChangePasswordDTO userChangePasswordDTO) {
        log.info("用户修改密码：{}",userChangePasswordDTO.getUserId());
        // 是否有修改权限
        UserTokenDTO userTokenDTO = BaseContext.getUserTokenDTO();
        if(!userTokenDTO.getUserId().equals(userChangePasswordDTO.getUserId())){
            throw new BaseException(ResponseCodeEnum.CODE_600);
        }
        // 旧密码校验
        User user = userMapper.findByUserId(userChangePasswordDTO.getUserId());
        if(null==user){
            throw new BaseException(ResponseCodeEnum.CODE_400.getCode(),"用户已被删除");
        }
        if(!user.getPassword().equals(userChangePasswordDTO.getOldPassword())){
            throw new BaseException(ResponseCodeEnum.CODE_400.getCode(),"旧密码输入错误");
        }
        // 修改密码
        user.setPassword(userChangePasswordDTO.getNewPassword());
        userMapper.update(user);
    }
}
