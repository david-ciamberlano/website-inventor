package it.alfrescoinaction.lab.awsi.conf.application;

import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.web.servlet.config.annotation.*;
import org.thymeleaf.spring4.SpringTemplateEngine;
import org.thymeleaf.spring4.view.ThymeleafViewResolver;
import org.thymeleaf.templateresolver.ServletContextTemplateResolver;

@EnableWebMvc
@Configuration
@ComponentScan(basePackages = { "it.alfrescoinaction.lab.awsi.controller",
        "it.alfrescoinaction.lab.awsi.service",
        "it.alfrescoinaction.lab.awsi.repository" })
public class AppConfiguration extends WebMvcConfigurerAdapter {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/resource/**").addResourceLocations("/resources/");
    }

    @Bean
    public ServletContextTemplateResolver templateResolver() {
        ServletContextTemplateResolver sctr = new ServletContextTemplateResolver();

        sctr.setPrefix("/WEB-INF/views/themes/default/");
        sctr.setSuffix(".html");
        sctr.setTemplateMode("HTML5");
        sctr.setCacheable(false);

        return sctr;
    }

    @Bean
    public SpringTemplateEngine templateEngine() {
        SpringTemplateEngine ste = new SpringTemplateEngine();
        ste.setTemplateResolver(templateResolver());

        return ste;
    }

    @Bean
    public ThymeleafViewResolver thymeleafViewResolver() {
        ThymeleafViewResolver tlvr = new ThymeleafViewResolver();
        tlvr.setTemplateEngine(templateEngine());

        return tlvr;
    }


    @Bean
    public PropertyPlaceholderConfigurer getPropertyPlaceholderConfigurer()
    {
        PropertyPlaceholderConfigurer ppc = new PropertyPlaceholderConfigurer();
        ppc.setLocation(new FileSystemResource(System.getProperty("catalina.base") + "/conf/awsi.properties"));
        ppc.setIgnoreUnresolvablePlaceholders(false);
        return ppc;
    }
}
