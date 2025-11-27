package cn.lqz.unmannedinspectionsystem.utils;


import cn.lqz.unmannedinspectionsystem.enums.ResponseCodeEnum;
import cn.lqz.unmannedinspectionsystem.pojo.vo.ResponseVO;

/**
 * 响应数据(结果)最外层对象工具
 *
 */
public class ResponseUtils {
    /**
     * 操作成功：自定义提示信息+数据
     *
     * @param msg    提示信息
     * @param object 对象
     */
    public static <T> ResponseVO<T> success(String msg, T object) {
        ResponseVO<T> responseVo = new ResponseVO<>();
        responseVo.setInfo(msg);
        responseVo.setCode(ResponseCodeEnum.CODE_200.getCode());
        responseVo.setData(object);
        return responseVo;
    }

    /**
     * 操作成功：默认提示信息+数据
     *
     * @param object 对象
     */
    public static <T> ResponseVO<T> success(T object) {
        String message = ResponseCodeEnum.CODE_200.getMsg();
        return success(message, object);
    }


    /**
     * 操作成功：默认提示信息
     */
    public static ResponseVO success() {
        return success(null);
    }

    /**
     * 操作失败：自定义状态码+提示信息
     *
     * @param code 错误码
     * @param msg  提示信息
     */
    public static ResponseVO error(Integer code, String msg) {
        ResponseVO responseVo = new ResponseVO();
        responseVo.setInfo(msg);
        responseVo.setCode(code);
        return responseVo;
    }

    /**
     * 操作失败：默认状态码+提示信息
     *
     * @param msg 提示信息
     */
    public static ResponseVO error(String msg) {
        Integer code = ResponseCodeEnum.CODE_400.getCode();
        return error(code, msg);
    }

}


