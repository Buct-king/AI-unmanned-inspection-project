package cn.lqz.unmannedinspectionsystem.context;

import cn.lqz.unmannedinspectionsystem.pojo.dto.UserTokenDTO;

public class BaseContext {
    private static ThreadLocal<UserTokenDTO> threadLocal = new ThreadLocal<>();

    public static void setUserTokenDTO(UserTokenDTO userTokenDTO){threadLocal.set(userTokenDTO);}

    public static UserTokenDTO getUserTokenDTO(){return threadLocal.get();}

    public static void removeUserTokenDTO(){threadLocal.remove();}
}
