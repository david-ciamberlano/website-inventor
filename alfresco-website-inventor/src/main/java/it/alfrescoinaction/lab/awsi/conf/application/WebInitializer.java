package it.alfrescoinaction.lab.awsi.conf.application;


import org.springframework.web.servlet.support.AbstractAnnotationConfigDispatcherServletInitializer;

public class WebInitializer extends AbstractAnnotationConfigDispatcherServletInitializer {

    @Override
    protected Class<?>[] getServletConfigClasses() {
    return new Class[] { ApplicationContext.class };
    }

    @Override
    protected String[] getServletMappings() {
    return new String[] { "/" };
    }

    @Override
    protected Class<?>[] getRootConfigClasses() {
    return null;
    }

}
