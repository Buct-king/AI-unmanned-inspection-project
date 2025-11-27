package cn.lqz.unmannedinspectionsystem.aspect;

import cn.lqz.unmannedinspectionsystem.constants.PageQueryAutoFillConstants;
import cn.lqz.unmannedinspectionsystem.enums.ResponseCodeEnum;
import cn.lqz.unmannedinspectionsystem.exceptions.BaseException;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;

/**
 * 分页请求自动填充参数切面类
 */
@Aspect
@Component
@Slf4j
public class PageQueryAutoFillAspect {
    @Pointcut("execution(* cn.lqz.unmannedinspectionsystem.controller..*.*(..))&&@annotation(cn.lqz.unmannedinspectionsystem.annotations.PageQueryAutoFill)")
    public void pointcut(){}

    @Before("pointcut()")
    public void pageQueryAutoFill(JoinPoint joinPoint){
        // 获取请求对象
        Object pageDTO = joinPoint.getArgs()[0];
        try{
            Field pageNo = pageDTO.getClass().getSuperclass().getDeclaredField(PageQueryAutoFillConstants.PAGE_NO);
            Field pageSize = pageDTO.getClass().getSuperclass().getDeclaredField(PageQueryAutoFillConstants.PAGE_SIZE);

            pageNo.setAccessible(true);
            pageSize.setAccessible(true);

            if (null==pageNo.get(pageDTO)){
                pageNo.set(pageDTO,PageQueryAutoFillConstants.PAGE_NO_DEFAULT);
            }
            if (null==pageSize.get(pageDTO)){
                pageSize.set(pageDTO,PageQueryAutoFillConstants.PAGE_SIZE_DEFAULT);
            }
        }catch (Exception e){
            log.error("分页请求参数自动填充错误");
            throw new BaseException(ResponseCodeEnum.CODE_500);
        }
    }
}
