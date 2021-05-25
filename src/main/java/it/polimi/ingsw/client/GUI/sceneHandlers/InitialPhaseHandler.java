package it.polimi.ingsw.client.GUI.sceneHandlers;

import it.polimi.ingsw.client.GUI.GuiController;
import it.polimi.ingsw.misc.BiElement;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.ImageCursor;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.*;

import static it.polimi.ingsw.client.GUI.sceneHandlers.ScenesEnum.*;

/**
 * This class is the handler for the welcome, connection, registration, waiting room, leaders choice and resource choice
 * action during the initial phase.
 */
public class InitialPhaseHandler extends PhaseHandler {
    private String ip, port;
    private String nickname, gameSize;

    private Map<ScenesEnum, Scene> sceneMap = new HashMap<>();

    @FXML
    private Button  playBtn, quitBtn, connectBtn, localModeBtn;
    @FXML private TextField ipField, portField, nickNameField;
    @FXML private Button singlePlayerBtn, twoPlayerBtn, threePlayerBtn, fourPlayerBtn;


    public InitialPhaseHandler(GuiController controller, Stage stage){
        super(controller, stage);

        List<ScenesEnum> allPaths = new ArrayList<>(Arrays.asList(WELCOME, CONNECTION, REGISTRATION));
        for(ScenesEnum path : allPaths){
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/" + path.getPath()));
            loader.setController(this);
            try {
                Scene scene = new Scene(loader.load());
                scene.getStylesheets().addAll(this.getClass().getResource("/fxml/style.css").toExternalForm());
                ImageCursor cursor = new ImageCursor(new Image("/img/ui/cursor.png"), 5, 5);
                scene.setCursor(cursor);
                sceneMap.put(path, scene);
            }catch(IOException e){
                System.err.println("Loader cannot load " + path);
            }
        }

        buildGeneralSceneMap(sceneMap);
    }

    @FXML
    public void start(){
        playBtn.setOnAction(actionEvent -> {
            try {
                controller.setup();
            } catch (IOException e) {
                System.err.println("IOException");
            }
        });

        quitBtn.setOnAction(actionEvent -> {
            System.exit(0);
        });
    }

    /**
     * @return a {@link BiElement} containing the IP as first value and the Port as second.
     */
    public void retrieveIpAndPort(){
        connectBtn.setOnAction(actionEvent -> {
            if(ipField.getText().length()>0 && ipField.getText().length()>0) {
                ip = ipField.getText();
                port = portField.getText();
            }else{
                ip = "localhost";
                port = "27001";
            }
            controller.setIpAndPort(ip, port);
        });


        localModeBtn.setOnAction(actionEvent -> {
            ip = "0";
            port = "0";
            controller.setIpAndPort(ip, port);
        });
    }


    public BiElement<String, Integer> getNickNameAndGameSize(){
        singlePlayerBtn.setOnAction(actionEvent -> {
            nickname = nickNameField.getText();
            gameSize = singlePlayerBtn.getText();
        });

        twoPlayerBtn.setOnAction(actionEvent -> {
            nickname = nickNameField.getText();
            gameSize = twoPlayerBtn.getText();
        });

        threePlayerBtn.setOnAction(actionEvent -> {
            nickname = nickNameField.getText();
            gameSize = threePlayerBtn.getText();
        });

        fourPlayerBtn.setOnAction(actionEvent -> {
            nickname = nickNameField.getText();
            gameSize = fourPlayerBtn.getText();
        });

        //check for correctness of nickname

        return new BiElement<>(nickname, Integer.parseInt(gameSize));
    }

}