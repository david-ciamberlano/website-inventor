package it.alfrescoinaction.lab.awsi.controller;

import it.alfrescoinaction.lab.awsi.domain.Downloadable;
import it.alfrescoinaction.lab.awsi.service.WebPageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.ServletResponse;
import java.io.IOException;
import java.io.InputStream;

@Controller
public class Proxy {

    private final WebPageService webPageService;

    @Autowired
    public Proxy(WebPageService webPageService) {
        this.webPageService = webPageService;
    }

    //Downloads
    @RequestMapping(value = "proxy/d/{id}", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<InputStreamResource> download(ServletResponse response, @PathVariable("id") String id) throws IOException {
        Downloadable<InputStream> fileDownloadable = webPageService.getDownloadable(id);

        return ResponseEntity.ok()
                .header("content-disposition", "inline; filename=\"" + fileDownloadable.getName() + "\"")
                .contentLength(fileDownloadable.getContentLength())
                .contentType(MediaType.parseMediaType(fileDownloadable.getMimeType()))
                .body(new InputStreamResource(fileDownloadable.getContent()));
    }


    // renditions
    @RequestMapping(value = "proxy/r/{type}/{id}", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<byte[]> rendition(ServletResponse response,
                                            @PathVariable("type") String type,
                                            @PathVariable("id") String id) throws IOException {
        Downloadable<byte[]> rend;
        switch(type) {
            case "thumb": {
                rend = webPageService.getRendition("doclib", id);
                break;
            }

            case "preview": {
                rend = webPageService.getRendition("imgpreview", id);
                break;
            }

            default:
                rend = webPageService.getRendition(type, id);
        }



        return ResponseEntity.ok()
                .contentLength(rend.getContentLength())
                .contentType(MediaType.parseMediaType(rend.getMimeType()))
                .body(rend.getContent());

    }

}
