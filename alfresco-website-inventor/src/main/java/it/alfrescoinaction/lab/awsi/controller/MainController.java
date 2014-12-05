package it.alfrescoinaction.lab.awsi.controller;


import it.alfrescoinaction.lab.awsi.service.RemoteConnection;
import org.apache.chemistry.opencmis.client.api.Session;
import org.apache.chemistry.opencmis.client.api.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class MainController {

    @Autowired
    private RemoteConnection remoteConnection;

    @RequestMapping("/test")
    public String test (Model model) {

        model.addAttribute("hello","Hello David");
        model.addAttribute("bean", remoteConnection.test());
        return "test";
    }


    // GETTER & SETTER


    public RemoteConnection getRemoteConnection() {
        return remoteConnection;
    }

    public void setRemoteConnection(RemoteConnection remoteConnection) {
        this.remoteConnection = remoteConnection;
    }
}
