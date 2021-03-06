package io.sufeng.context.utils;

import io.sufeng.context.domain.entity.People;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.Optional;

/**
 * @Author zhangchao
 * @Date 2019/4/26 17:13
 * @Version v1.0
 */
public class ResourceUtils {

    public static final String SESSION_USER = "SESSION_USER";

    public static People getCurrentPeople(){
        return (People) getRequest().getSession().getAttribute(SESSION_USER);
    }

    public static void setLoginPeople(People people){
        getRequest().getSession().setAttribute(SESSION_USER,people);
    }

    public static Boolean isLogin(){
        return Optional.ofNullable(getCurrentPeople()).isPresent();
    }

    public static HttpServletRequest getRequest() {
        ServletRequestAttributes attrs = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        return attrs.getRequest();
    }
}
