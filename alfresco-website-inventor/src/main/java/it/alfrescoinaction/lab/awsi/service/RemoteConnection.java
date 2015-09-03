package it.alfrescoinaction.lab.awsi.service;


import org.apache.chemistry.opencmis.client.api.Session;

public interface RemoteConnection {

    void openSession ();

    Session getSession();
}
