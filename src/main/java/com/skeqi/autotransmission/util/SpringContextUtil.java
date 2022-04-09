package com.skeqi.autotransmission.util;



import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.EmbeddedValueResolverAware;
import org.springframework.stereotype.Component;
import org.springframework.util.StringValueResolver;

/**
 * Description: Spring代码形式注入bean
 * User: zhouzhou
 * Date: 2018-09-07
 * Time: 11:36
 */
@Component
public class SpringContextUtil implements ApplicationContextAware ,EmbeddedValueResolverAware {


    /**
     *     Spring应用上下文环境
      */
    private static ApplicationContext applicationContext;

    private static StringValueResolver stringValueResolver;

    /**
     * 实现ApplicationContextAware接口的回调方法。设置上下文环境
     *
     * @param applicationContext
     */
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) {
        SpringContextUtil.applicationContext = applicationContext;
    }

    /**
     * @return ApplicationContext
     */
    public static ApplicationContext getApplicationContext() {
        return applicationContext;
    }

    /**
     * 获取对象
     *
     * @param name
     * @return Object
     * @throws BeansException
     */
    public static Object getBean(String name) throws BeansException {
        return applicationContext.getBean(name);
    }


    /**
     * 动态获取配置文件中的值
     *
     * @param name
     * @return
     */
    public static String getPropertiesValue(String name) {
        try {
            name = "${" + name + "}";
            return stringValueResolver.resolveStringValue(name);
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public void setEmbeddedValueResolver(StringValueResolver stringValueResolver) {
        SpringContextUtil.stringValueResolver = stringValueResolver;
    }
}