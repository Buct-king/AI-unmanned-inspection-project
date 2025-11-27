package cn.lqz.unmannedinspectionsystem.controller;

import cn.lqz.unmannedinspectionsystem.pojo.dto.UserPageQueryDTO;
import cn.lqz.unmannedinspectionsystem.pojo.entity.User;
import cn.lqz.unmannedinspectionsystem.pojo.vo.PageResultVO;
import cn.lqz.unmannedinspectionsystem.pojo.vo.ResponseVO;
import cn.lqz.unmannedinspectionsystem.service.UserService;
import cn.lqz.unmannedinspectionsystem.utils.ResponseUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    /**
     * 加载用户列表
     * @param userPageQueryDTO
     * @return
     */
    @PostMapping("/loadList")
    public ResponseVO<PageResultVO> loadUserList(UserPageQueryDTO userPageQueryDTO){
        return ResponseUtils.success(userService.loadUserList(userPageQueryDTO));
    }

    /**
     * 新增或修改用户信息
     * @param user
     * @return
     */
    @PostMapping("/createOrUpdate")
    public ResponseVO createOrUpdate(User user){
        userService.createOrUpdate(user);
        return ResponseUtils.success();
    }
}
