package it.polimi.ingsw.client.GUI.sceneHandlers;

import it.polimi.ingsw.client.GUI.GuiController;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.util.HashMap;
import java.util.Map;


public abstract class PhaseHandler {
    protected GuiController controller;
    protected Stage stage;

    private Map<ScenesEnum, Scene> generalSceneMap = new HashMap<>();


    public PhaseHandler(GuiController controller, Stage stage){
        this.controller = controller;
        this.stage = stage;
    }

    public abstract boolean setScene(ScenesEnum sceneName);


    /**
     * @param sceneName name of the scene wanted (see {@link ScenesEnum})
     * @return the scene requested through its name
     */
    public Scene getScene(ScenesEnum sceneName){ return generalSceneMap.get(sceneName); }


    /**
     * Each PhaseHandler's subclass has to register all the scenes it produces through this method.
     * @param partialMap map of the scene name ({@link ScenesEnum}) and the Scene created by the subclass
     */
    protected void buildGeneralSceneMap(Map<ScenesEnum, Scene> partialMap){
        generalSceneMap.putAll(partialMap);
    }

}
