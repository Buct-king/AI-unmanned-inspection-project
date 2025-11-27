package cn.lqz.unmannedinspectionsystem.interceptor;

import cn.lqz.unmannedinspectionsystem.context.BaseContext;
import cn.lqz.unmannedinspectionsystem.enums.ResponseCodeEnum;
import cn.lqz.unmannedinspectionsystem.exceptions.BaseException;
import cn.lqz.unmannedinspectionsystem.pojo.dto.UserTokenDTO;
import cn.lqz.unmannedinspectionsystem.redis.RedisService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * 用户token拦截器
 */
@Component
@Slf4j
public class UserTokenInterceptor implements HandlerInterceptor {
    @Resource
    private RedisService redisService;
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String uri = request.getRequestURI();
        System.out.println("拦截到的URI: " + uri);
        // 放行流媒体、视频、模型等相关接口
        if (uri.contains("/camera/stream") ||
            uri.contains("/camera/start") ||
            uri.contains("/camera/stop") ||
            uri.contains("/camera/status") ||
            uri.contains("/video/detect/list") ||
            uri.contains("/video/raw/list") ||
            uri.contains("/video/detect/") ||
            uri.contains("/video/raw/") ||
            uri.contains("/model_pt/") ||
            uri.contains("/yolo/models") ||
            uri.contains("/yolo/model/status")) {
            return true;
        }
        //判断当前拦截到的是Controller的方法还是其他资源
        if (!(handler instanceof HandlerMethod)) {
            //当前拦截到的不是动态方法，直接放行
            return true;
        }
        // 取出请求头中的token
        String token = request.getHeader("token");
        // 取出redis中的信息
        UserTokenDTO userTokenDTO = redisService.getUserTokenDTO(token);
        if (null==userTokenDTO){
            throw new BaseException(ResponseCodeEnum.CODE_401);
        } else{
            // 存入BaseContext
            BaseContext.setUserTokenDTO(userTokenDTO);
        }
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        // 清除ThreadLocal信息，防止OOM
        BaseContext.removeUserTokenDTO();
    }
}
