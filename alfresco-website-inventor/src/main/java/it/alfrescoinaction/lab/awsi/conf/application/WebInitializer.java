package it.alfrescoinaction.lab.awsi.conf.application;


import org.springframework.web.servlet.support.AbstractAnnotationConfigDispatcherServletInitializer;

public class WebInitializer extends AbstractAnnotationConfigDispatcherServletInitializer {

    @Override
    protected Class<?>[] getServletConfigClasses() {
    return new Class[] { AppConfiguration.class };
    }

    @Override
    protected String[] getServletMappings() {
    return new String[] { "/" };
    }

    @Override
    protected Class<?>[] getRootConfigClasses() {
        return new Class[] { RootConfiguration.class };
    }

}
