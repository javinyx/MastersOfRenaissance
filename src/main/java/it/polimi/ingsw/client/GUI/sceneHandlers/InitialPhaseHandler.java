package it.polimi.ingsw.client.GUI.sceneHandlers;

import it.polimi.ingsw.client.GUI.GuiController;
import it.polimi.ingsw.misc.BiElement;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.*;

import static it.polimi.ingsw.client.GUI.sceneHandlers.ScenesEnum.*;

/**
 * This class is the handler for the welcome, connection, registration, waiting room, leaders choice and resource choice
 * action during the initial phase.
 */
public class InitialPhaseHandler extends PhaseHandler {
    private String ip;
    private Integer port;
    private String nickname;

    private Map<ScenesEnum, Scene> sceneMap = new HashMap<>();

    @FXML
    private Button  playBtn, quitBtn;


    public InitialPhaseHandler(GuiController controller, Stage stage){
        super(controller, stage);

        List<ScenesEnum> allPaths = new ArrayList<>(Arrays.asList(WELCOME, CONNECTION, REGISTRATION));
        for(ScenesEnum path : allPaths){
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/" + path.getPath()));
            Scene scene = null;
            try {
                scene = new Scene(loader.load());
            }catch(IOException e){
                System.err.println("Loader cannot load " + path);
            }
            sceneMap.put(path, scene);
        }

        buildGeneralSceneMap(sceneMap);
    }

    public void start(){
        playBtn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {

            }
        });
    }

    /**
     * @return a {@link BiElement} containing the IP as first value and the Port as second.
     */
    public BiElement<String, Integer> getIpAndPort(){
        return null;
    }

    public String getNickName(){
        return null;
    }

}