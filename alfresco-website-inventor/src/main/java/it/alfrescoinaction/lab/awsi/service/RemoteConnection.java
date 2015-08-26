package it.alfrescoinaction.lab.awsi.service;


import org.apache.chemistry.opencmis.client.api.Session;

import java.util.Optional;

public interface RemoteConnection {

    void openSession ();

    Session getSession();
}
