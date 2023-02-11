package com.orainge.websocket_forward;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

@Component
public class ApplicationContextUtils implements ApplicationContextAware {
    public static ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        ApplicationContextUtils.applicationContext = applicationContext;
    }

    /**
     * 通过 name 获取 Bean
     *
     * @param name Bean 的名称
     * @return Bean
     */
    public static Object getBean(String name) {
        if (applicationContext == null) {
            return null;
        }

        return applicationContext.getBean(name);
    }

    /**
     * 通过 class 获取 Bean
     *
     * @param clazz class 类型
     * @return Bean
     */
    public static <T> T getBeanByClass(Class<T> clazz) {
        if (applicationContext == null) {
            return null;
        }
        return applicationContext.getBean(clazz);
    }

    /**
     * 通过 name 以及 Clazz 返回指定的 Bean
     *
     * @param name  Bean 的名称
     * @param clazz class 类型
     * @return Bean
     */

    public static <T> T getBean(String name, Class<T> clazz) {
        if (applicationContext == null) {
            return null;
        }
        return applicationContext.getBean(name, clazz);
    }
}