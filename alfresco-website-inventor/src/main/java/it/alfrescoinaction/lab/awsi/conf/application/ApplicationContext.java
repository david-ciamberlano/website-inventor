package it.alfrescoinaction.lab.awsi.conf.application;

import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.converter.ByteArrayHttpMessageConverter;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.servlet.view.InternalResourceViewResolver;

import java.util.List;

@EnableWebMvc
@Configuration
@ComponentScan(basePackages = { "it.alfrescoinaction.lab.awsi.controller",
        "it.alfrescoinaction.lab.awsi.service",
        "it.alfrescoinaction.lab.awsi.repository" })
public class ApplicationContext extends WebMvcConfigurerAdapter {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/resource/**").addResourceLocations("/resources/");
    }

    @Bean
    public InternalResourceViewResolver jspViewResolver() {
        InternalResourceViewResolver bean = new InternalResourceViewResolver();
        bean.setPrefix("/WEB-INF/views/themes/default/");
        bean.setSuffix(".jsp");
        return bean;
    }

//    @Override
//    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
//        converters.add(customByteArrayHttpMessageConverter());
//        super.configureMessageConverters(converters);
//    }
//
//    @Bean
//    public HttpMessageConverter customByteArrayHttpMessageConverter() {
//        HttpMessageConverter converter = new ByteArrayHttpMessageConverter();
//        converter.getSupportedMediaTypes().add("image/jpg");
//        converter.getSupportedMediaTypes().add("image/png");
//
//        return converter;
//    }



    @Bean
    public PropertyPlaceholderConfigurer getPropertyPlaceholderConfigurer()
    {
        PropertyPlaceholderConfigurer ppc = new PropertyPlaceholderConfigurer();
        ppc.setLocation(new FileSystemResource(System.getProperty("catalina.base") + "/conf/awsi.properties"));
        ppc.setIgnoreUnresolvablePlaceholders(false);
        return ppc;
    }
}
