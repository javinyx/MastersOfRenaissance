package it.polimi.ingsw.client.GUI.sceneHandlers;

import it.polimi.ingsw.client.GUI.GuiController;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.util.HashMap;
import java.util.Map;

/**
 * The Phase handler which handles all the different game phases.
 */
public abstract class PhaseHandler {
    /**
     * The main GUI Controller.
     */
    protected GuiController controller;
    /**
     * The main JavaFX Stage.
     */
    protected Stage stage;

    private Map<ScenesEnum, Scene> generalSceneMap = new HashMap<>();

    /**
     * Instantiates a new Phase handler.
     *
     * @param controller the controller
     * @param stage      the stage
     */
    public PhaseHandler(GuiController controller, Stage stage){
        this.controller = controller;
        this.stage = stage;
    }

    /**
     * Sets the scene on the selected stage.
     *
     * @param sceneName the scene name
     * @return the scene
     */
    public abstract boolean setScene(ScenesEnum sceneName);

    /**
     * Gets the scene from ScenesEnum.
     *
     * @param sceneName name of the requested scene (see {@link ScenesEnum})
     * @return the scene requested through its name
     */
    public Scene getScene(ScenesEnum sceneName){ return generalSceneMap.get(sceneName); }

    /**
     * Each PhaseHandler's subclass has to register all the scenes it produces through this method.
     *
     * @param partialMap map of the scene name ({@link ScenesEnum}) and the Scene created by the subclass
     */
    protected void buildGeneralSceneMap(Map<ScenesEnum, Scene> partialMap){
        generalSceneMap.putAll(partialMap);
    }
}
