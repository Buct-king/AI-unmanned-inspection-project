package cn.lqz.unmannedinspectionsystem.handler;

import cn.lqz.unmannedinspectionsystem.enums.ResponseCodeEnum;
import cn.lqz.unmannedinspectionsystem.exceptions.BaseException;
import cn.lqz.unmannedinspectionsystem.pojo.vo.ResponseVO;
import cn.lqz.unmannedinspectionsystem.utils.ResponseUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;


@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
    /**
     * 业务异常
     * @param baseException
     * @return
     */
    @ExceptionHandler
    public ResponseVO baseExceptionHandler(BaseException baseException){
      log.error("异常信息",baseException);
      return ResponseUtils.error(baseException.getCode(),baseException.getMessage());
    }

    /**
     * JSR303入参校验异常：表单提交，离散接收
     * @param constraintViolationException
     * @return
     */
    @ExceptionHandler
    public ResponseVO constraintViolationExceptionHandler(ConstraintViolationException constraintViolationException){
        log.error("异常信息",constraintViolationException);
        return ResponseUtils.error(ResponseCodeEnum.CODE_600.getCode(),ResponseCodeEnum.CODE_600.getMsg());
    }
    /**
     * JSR303入参校验异常：表单提交，封装对象接收
     * @param bindException
     * @return
     */
    @ExceptionHandler
    public ResponseVO bindExceptionHandler(BindException bindException){
        log.error("异常信息：",bindException);
        return ResponseUtils.error(ResponseCodeEnum.CODE_600.getCode(),ResponseCodeEnum.CODE_600.getMsg());
    }

    /**
     * JSR303入参校验异常：json提交，封装对象接收
     * @param methodArgumentNotValidException
     * @return
     */
    @ExceptionHandler
    public ResponseVO methodArgumentNotValidExceptionHandler(MethodArgumentNotValidException methodArgumentNotValidException){
        log.error("异常信息",methodArgumentNotValidException);
        return ResponseUtils.error(ResponseCodeEnum.CODE_600.getCode(),ResponseCodeEnum.CODE_600.getMsg());
    }

    /**
     * 其它异常
     * @param e
     * @return
     */
    @ExceptionHandler
    public Object exceptionHandler(Exception e, HttpServletRequest request){
        String contentType = request.getHeader("Accept");
        if (contentType != null && contentType.contains("multipart/x-mixed-replace")) {
            // 视频流接口，直接抛出异常让Spring处理
            throw new RuntimeException(e);
        }
        log.error("异常信息",e);
        return ResponseUtils.error(ResponseCodeEnum.CODE_500.getCode(),ResponseCodeEnum.CODE_500.getMsg());
    }
}
