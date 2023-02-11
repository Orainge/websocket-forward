package com.orainge.websocket_forward.server;

import com.orainge.websocket_forward.ApplicationContextUtils;
import org.springframework.beans.factory.config.YamlPropertiesFactoryBean;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.io.ClassPathResource;
import org.springframework.web.context.support.WebApplicationContextUtils;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import java.util.Objects;

@SpringBootApplication
public class WebsocketServerApplication extends SpringBootServletInitializer {
    @Bean
    public static PropertySourcesPlaceholderConfigurer properties() {
        PropertySourcesPlaceholderConfigurer pspc = new PropertySourcesPlaceholderConfigurer();
        YamlPropertiesFactoryBean yaml = new YamlPropertiesFactoryBean();
        yaml.setResources(new ClassPathResource[]{
                new ClassPathResource("server-config.yml")
        });
        pspc.setProperties(Objects.requireNonNull(yaml.getObject()));
        return pspc;
    }

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(WebsocketServerApplication.class);
    }

    /**
     * Tomcat 方式启动初始化 applicationContext
     */
    @Override
    public void onStartup(ServletContext servletContext) throws ServletException {
        super.onStartup(servletContext);
        ApplicationContextUtils.applicationContext = WebApplicationContextUtils.getRequiredWebApplicationContext(servletContext);
    }

    /**
     * Spring Boot 本地启动
     */
    public static void main(String[] args) {
        ApplicationContextUtils.applicationContext = SpringApplication.run(WebsocketServerApplication.class, args);
    }
}
