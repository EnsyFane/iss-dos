package controllers;

import domain.models.User;
import javafx.stage.Stage;
import service.IClientObserver;
import service.IDOSService;

import java.io.Serializable;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class dummy extends UnicastRemoteObject implements IClientObserver, Serializable {
    public dummy() throws RemoteException {
    }

    public void init(IDOSService service, Stage userPage, User user) {

    }
}
