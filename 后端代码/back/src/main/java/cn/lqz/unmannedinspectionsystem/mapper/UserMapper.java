package cn.lqz.unmannedinspectionsystem.mapper;

import cn.lqz.unmannedinspectionsystem.annotations.AutoFill;
import cn.lqz.unmannedinspectionsystem.enums.OperationTypeEnum;
import cn.lqz.unmannedinspectionsystem.pojo.dto.UserPageQueryDTO;
import cn.lqz.unmannedinspectionsystem.pojo.entity.User;
import cn.lqz.unmannedinspectionsystem.pojo.vo.UserVO;
import com.github.pagehelper.Page;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface UserMapper {
    @Select("select * from user where phone_number=#{phoneNumber}")
    User findByPhoneNumber(String phoneNumber);

    @Select("select * from user where user_id=#{userId}")
    User findByUserId(Long userId);

    /**
     * 新增
     * @param user
     */
    @AutoFill(OperationTypeEnum.INSERT)
    void insert(User user);
    /**
     * 修改
     * @param user
     */
    @AutoFill(OperationTypeEnum.UPDATE)
    void update(User user);

    Page<UserVO> findBatch(UserPageQueryDTO userPageQueryDTO);
}
