package it.alfrescoinaction.lab.awsi.controller;

import it.alfrescoinaction.lab.awsi.domain.WebPage;
import it.alfrescoinaction.lab.awsi.exceptions.ConnectionException;
import it.alfrescoinaction.lab.awsi.exceptions.InvalidParameterException;
import it.alfrescoinaction.lab.awsi.exceptions.ObjectNotFoundException;
import it.alfrescoinaction.lab.awsi.exceptions.PageNotFoundException;
import it.alfrescoinaction.lab.awsi.service.WebPageService;

import org.apache.chemistry.opencmis.commons.exceptions.CmisBaseException;
import org.apache.chemistry.opencmis.commons.exceptions.CmisObjectNotFoundException;
import org.apache.chemistry.opencmis.commons.exceptions.CmisRuntimeException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;


@Controller
public class MainController {

    private static final Logger logger = LogManager.getLogger(MainController.class);

    private final WebPageService webPageService;

    @Autowired
    public MainController (WebPageService webPageService) {
        this.webPageService = webPageService;
    }

    @RequestMapping("/{siteid}")
    public String homepage(Model model, @PathVariable("siteid") String site) {
        model.addAttribute("test","test");
        return "forward:/" + site + "/page/home";
    }

    @RequestMapping("/{siteid}/page/{id}")
    public String pageById(Model model,
                           HttpServletRequest request, @PathVariable("siteid") String siteId,
                           @PathVariable("id") String id) {

        if(logger.isDebugEnabled()){
            logger.debug("Requested page: " + id);
        }

        WebPage wp = webPageService.buildWebPage(siteId, id);
        model.addAttribute("page", wp);
        model.addAttribute("siteid", siteId);
        model.addAttribute("sitename", wp.getSiteName());
        model.addAttribute("sitetitle", wp.getSiteTitle());
        model.addAttribute("sitedescription", wp.getSiteDescription());

        if(logger.isDebugEnabled()){
            logger.debug("Page Ready: " + id);
        }
        return "index";
    }


    //------------- EXCEPTION -------------

    @ExceptionHandler(PageNotFoundException.class)
    public ModelAndView handlePageNotFoundError(HttpServletRequest req, PageNotFoundException exc) {
        ModelAndView mav = new ModelAndView();
        mav.addObject("", exc.getPageId());
        mav.addObject("exception", exc);
        mav.addObject("utl",req.getRequestURL());
        mav.setViewName("error");

        return mav;
    }

    @ExceptionHandler(ObjectNotFoundException.class)
    public ModelAndView handleObjectNotFoundError(HttpServletRequest req, ObjectNotFoundException exc) {
        ModelAndView mav = new ModelAndView();
        mav.addObject("invalid_page", exc.getPageId());
        mav.addObject("exception", exc);
        mav.addObject("url",req.getRequestURL());
        mav.setViewName("error");

        return mav;
    }

    @ExceptionHandler(CmisBaseException.class)
    public ModelAndView handleCmisBaseError(HttpServletRequest req, CmisObjectNotFoundException exc) {
        ModelAndView mav = new ModelAndView();
        mav.addObject("Invalid Page", exc.getMessage());
        mav.addObject("exception", exc);
        mav.addObject("utl",req.getRequestURL());
        mav.setViewName("error");

        return mav;
    }

    @ExceptionHandler(CmisRuntimeException.class)
    public ModelAndView handleCmisRuntimeError(HttpServletRequest req, CmisRuntimeException exc) {
        ModelAndView mav = new ModelAndView();
        mav.addObject("Invalid Page", exc.getMessage());
        mav.addObject("exception", exc);
        mav.addObject("utl",req.getRequestURL());
        mav.setViewName("error");

        return mav;
    }

    @ExceptionHandler(ConnectionException.class)
    public ModelAndView handleConnectionError(HttpServletRequest req, PageNotFoundException exc) {
        ModelAndView mav = new ModelAndView();
        mav.addObject("Connection exception", exc.getMessage());
        mav.addObject("exception", exc);
        mav.addObject("utl",req.getRequestURL());
        mav.setViewName("error");

        return mav;
    }

    @ExceptionHandler(InvalidParameterException.class)
    public ModelAndView handleInvParamException(HttpServletRequest req, InvalidParameterException exc) {
        ModelAndView mav = new ModelAndView();
        mav.addObject("Connection exception", exc.getMessage());
        mav.addObject("exception", exc);
        mav.addObject("utl",req.getRequestURL());
        mav.setViewName("error");

        return mav;
    }

    @ExceptionHandler(IOException.class)
    public ModelAndView handleIOException(HttpServletRequest req, IOException exc) {
        ModelAndView mav = new ModelAndView();
        mav.addObject("Connection exception", exc.getMessage());
        mav.addObject("exception", exc);
        mav.addObject("utl",req.getRequestURL());
        mav.setViewName("error");

        return mav;
    }

    @ExceptionHandler(BeanCreationException.class)
    public ModelAndView handleBeanCreationException(HttpServletRequest req, BeanCreationException exc) {
        ModelAndView mav = new ModelAndView();
        mav.addObject("Connection exception", exc.getMessage());
        mav.addObject("exception", exc);
        mav.addObject("utl",req.getRequestURL());
        mav.setViewName("error");

        return mav;
    }




    //------------- PRIVATE -------------


}