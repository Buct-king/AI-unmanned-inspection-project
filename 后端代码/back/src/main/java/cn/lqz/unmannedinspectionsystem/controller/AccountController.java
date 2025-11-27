package cn.lqz.unmannedinspectionsystem.controller;

import cn.lqz.unmannedinspectionsystem.pojo.dto.LoginDTO;
import cn.lqz.unmannedinspectionsystem.pojo.dto.UserChangePasswordDTO;
import cn.lqz.unmannedinspectionsystem.pojo.dto.UserTokenDTO;
import cn.lqz.unmannedinspectionsystem.pojo.vo.ResponseVO;
import cn.lqz.unmannedinspectionsystem.service.AccountService;
import cn.lqz.unmannedinspectionsystem.utils.ResponseUtils;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/account")
@RequiredArgsConstructor
public class AccountController {
    private final AccountService accountService;

    /**
     * 登录
     * @param loginDTO
     * @return
     */
    @PostMapping("/login")
    public ResponseVO<UserTokenDTO> login(@Valid LoginDTO loginDTO){
        return ResponseUtils.success(accountService.login(loginDTO));
    }

    /**
     * 登出
     * @param userId
     * @return
     */
    @PostMapping("/logout")
    public ResponseVO logout(@NotNull Long userId){
        accountService.logout(userId);
        return ResponseUtils.success();
    }

    /**
     * 修改密码
     * @param userChangePasswordDTO
     * @return
     */
    @PostMapping("/changePassword")
    public ResponseVO changePassword(@Valid UserChangePasswordDTO userChangePasswordDTO){
        accountService.changePassword(userChangePasswordDTO);
        return ResponseUtils.success();
    }

}
