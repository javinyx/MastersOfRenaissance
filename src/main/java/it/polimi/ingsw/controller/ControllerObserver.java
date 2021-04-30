package it.polimi.ingsw.controller;

import it.polimi.ingsw.model.market.Resource;

import java.util.List;

public interface ControllerObserver {

    public void organizeResourceAction(List<Resource> organizedRes);

}
