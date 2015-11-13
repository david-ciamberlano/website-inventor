package it.alfrescoinaction.lab.awsi.domain;

import org.apache.chemistry.opencmis.client.api.Session;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * Created by david on 13/11/15.
 */

@Component
@Scope("session")
public class SiteProperties {

    private String siteId;
    private String themeName;
    private Map<String,String> searchFields;
    private Map<String,String> metadata;

}
