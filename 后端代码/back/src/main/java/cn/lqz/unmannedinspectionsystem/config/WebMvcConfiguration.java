package cn.lqz.unmannedinspectionsystem.config;


import cn.lqz.unmannedinspectionsystem.enums.ResponseCodeEnum;
import cn.lqz.unmannedinspectionsystem.exceptions.BaseException;
import cn.lqz.unmannedinspectionsystem.interceptor.UserTokenInterceptor;
import jakarta.annotation.Resource;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

/**
 * WebMvc配置类
 */
@Configuration
public class WebMvcConfiguration implements WebMvcConfigurer {
    @Resource
    public UserTokenInterceptor userTokenInterceptor;


    /**
     * 配置全局格式化器，用于转换前端提交的表单形式数据
     * @param registry
     */
    @Override
    public void addFormatters(FormatterRegistry registry) {
        // 将"yyyy-MM-dd"形式字符串转LocalDate
        registry.addConverter(new Converter<String, LocalDate>() {
            private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

            @Override
            public LocalDate convert(String source) {
                try {
                    return LocalDate.parse(source, dateFormatter);
                } catch (DateTimeParseException e) {
                    throw new BaseException(ResponseCodeEnum.CODE_600.getCode(),"日期格式错误，请使用'yyyy-MM-dd'形式");
                }
            }
        });

        // 将"yyyy-MM-dd HH:mm:ss"形式字符串转LocalDateTime
        registry.addConverter(new Converter<String, LocalDateTime>() {
            private final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

            @Override
            public LocalDateTime convert(String source) {
                try {
                    return LocalDateTime.parse(source, dateTimeFormatter);
                } catch (DateTimeParseException e) {
                    throw new BaseException(ResponseCodeEnum.CODE_600.getCode(),"日期格式错误，请使用'yyyy-MM-dd HH:mm:ss'形式");
                }
            }
        });

        // 将"HH:mm:ss"形式字符串转LocalTime
        registry.addConverter(new Converter<String, LocalTime>() {
            private final DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");

            @Override
            public LocalTime convert(String source) {
                try {
                    return LocalTime.parse(source, timeFormatter);
                } catch (DateTimeParseException e) {
                    throw new BaseException(ResponseCodeEnum.CODE_600.getCode(),"日期格式错误，请使用'HH:mm:ss'形式");
                }
            }
        });
    }



    /**
     * 配置拦截器
     * @param registry
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(userTokenInterceptor)
                .addPathPatterns("/**")
                .excludePathPatterns("/account/login");
                //.excludePathPatterns("/account/getCheckCode")
                //.excludePathPatterns("/account/submitCheckCode")
                //.excludePathPatterns("/account/forgetAndChangePassword");
    }
}
