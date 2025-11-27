package cn.lqz.unmannedinspectionsystem.service;

import cn.lqz.unmannedinspectionsystem.pojo.dto.UserPageQueryDTO;
import cn.lqz.unmannedinspectionsystem.pojo.entity.User;
import cn.lqz.unmannedinspectionsystem.pojo.vo.PageResultVO;

public interface UserService {
    /**
     * 分页查询用户列表
     * @param userPageQueryDTO
     * @return
     */
    PageResultVO loadUserList(UserPageQueryDTO userPageQueryDTO);

    /**
     * 新增或修改
     * @param user
     */
    void createOrUpdate(User user);
}
