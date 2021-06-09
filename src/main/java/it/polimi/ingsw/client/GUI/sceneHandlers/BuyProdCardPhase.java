package it.polimi.ingsw.client.GUI.sceneHandlers;

import it.polimi.ingsw.client.GUI.GuiController;
import it.polimi.ingsw.client.model.NubPlayer;
import it.polimi.ingsw.misc.BiElement;
import it.polimi.ingsw.misc.Storage;
import it.polimi.ingsw.model.ResourcesWallet;
import it.polimi.ingsw.model.market.Resource;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.ImageCursor;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ToggleButton;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;
import java.util.*;

import static it.polimi.ingsw.client.GUI.sceneHandlers.ScenesEnum.*;
import static it.polimi.ingsw.model.market.Resource.*;
import static it.polimi.ingsw.misc.Storage.*;

public class BuyProdCardPhase extends PhaseHandler{
    private Map<ScenesEnum, Scene> sceneMap = new HashMap<>();
    private ResourcesWallet wallet = new ResourcesWallet();

    @FXML
    private Label listCostLbl, qtyStoneLbl, qtyShieldLbl, qtyCoinLbl, qtyServantLbl, stoneLbl, servantLbl, coinLbl,
            shieldLbl;
    @FXML
    private Button stoneSubBtn, stoneAddBtn, servantSubBtn, servantAddBtn, coinSubBtn, coinAddBtn, shieldSubBtn,
            shieldAddBtn, confirmPaymentBtn;
    @FXML
    private ImageView shelf1MB, shelf21MB, shelf22MB, shelf31MB, shelf32MB, shelf33MB;
    @FXML
    private ToggleButton shelf1Toggle, shelf21Toggle, shelf22Toggle, shelf31Toggle, shelf32Toggle, shelf33Toggle;


    public BuyProdCardPhase(GuiController controller, Stage stage){
        super(controller, stage);
        List<ScenesEnum> allPaths = new ArrayList<>(Arrays.asList(CHOOSE_PAYMENT, CHOOSE_LEADERS));
        for (ScenesEnum path : allPaths) {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/" + path.getPath()));
            loader.setController(this);

            try {
                Scene scene = new Scene(loader.load());
                scene.getStylesheets().addAll(this.getClass().getResource("/fxml/style.css").toExternalForm());
                scene.setCursor(new ImageCursor(new Image("/img/ui/cursor.png"), 5, 5));
                sceneMap.put(path, scene);
            } catch (IOException e) {
                System.err.println("Loader cannot load " + path);
            }
        }

        buildGeneralSceneMap(sceneMap);
    }

    @Override
    public boolean setScene(ScenesEnum sceneName) {
        if (!sceneMap.containsKey(sceneName)) {
            return false;
        }
        stage.setScene(sceneMap.get(sceneName));
        return true;
    }

    public void choosePayment(int cardId, int stack) {
        Stage popUpStage = new Stage(StageStyle.TRANSPARENT);
        popUpStage.initOwner(stage);
        popUpStage.initModality(Modality.APPLICATION_MODAL);
        popUpStage.centerOnScreen();
        popUpStage.setScene(getScene(CHOOSE_PAYMENT));
        popUpStage.show();

        NubPlayer player = controller.getPlayer();
        Map<BiElement<Resource, Storage>, Integer> loot = player.getLootchest();

        //showing loot status
        loot.forEach((x, y) -> {
            switch (x.getFirstValue()) {
                case COIN -> qtyCoinLbl.setText("x" + y);
                case SERVANT -> qtyServantLbl.setText("x" + y);
                case SHIELD -> qtyShieldLbl.setText("x" + y);
                case STONE -> qtyStoneLbl.setText("x" + y);
            }
        });

        //set Images in war
        Resource res = player.getResourceFromStorage(WAREHOUSE_SMALL);
        if (res != null) {
            shelf1MB.setImage(new Image("/img/pawns/" + res + ".png"));
        } else {
            shelf1Toggle.setDisable(true);
        }

        res = player.getResourceFromStorage(WAREHOUSE_MID);
        int qty = player.getQtyInStorage(res, WAREHOUSE_MID);

        if (res != null && qty == 2) {
            shelf21MB.setImage(new Image("/img/pawns/" + res + ".png"));
            shelf22MB.setImage(new Image("/img/pawns/" + res + ".png"));
        } else if (res != null && qty == 1) {
            shelf21MB.setImage(new Image("/img/pawns/" + res + ".png"));
            shelf22MB.setDisable(true);
        } else {
            shelf21MB.setDisable(true);
            shelf22MB.setDisable(true);
        }

        res = player.getResourceFromStorage(WAREHOUSE_LARGE);
        qty = player.getQtyInStorage(res, WAREHOUSE_LARGE);

        if (res != null) {
            if (qty == 3) {
                shelf31MB.setImage(new Image("/img/pawns/" + res + ".png"));
                shelf32MB.setImage(new Image("/img/pawns/" + res + ".png"));
                shelf33MB.setImage(new Image("/img/pawns/" + res + ".png"));
            } else if (qty==2) {
                shelf31MB.setImage(new Image("/img/pawns/" + res + ".png"));
                shelf32MB.setImage(new Image("/img/pawns/" + res + ".png"));
                shelf33MB.setDisable(true);
            } else {
                shelf31MB.setImage(new Image("/img/pawns/" + res + ".png"));
                shelf32MB.setDisable(true);
                shelf33MB.setDisable(true);
            }
        } else {
            shelf31MB.setDisable(true);
            shelf32MB.setDisable(true);
            shelf33MB.setDisable(true);
        }

        //choose from war
        List<Resource> fromWar = new ArrayList<>();
        List<ToggleButton> resWarMidToggles = new ArrayList<>(Arrays.asList(shelf21Toggle,shelf22Toggle));
        List<ToggleButton> resWarLargeToggles = new ArrayList<>(Arrays.asList(shelf31Toggle, shelf32Toggle,
                shelf33Toggle));

        shelf1Toggle.setOnAction(event ->{
            if (shelf1Toggle.isSelected()) {
                fromWar.add(player.getResourceFromStorage(WAREHOUSE_SMALL));
            } else {
                fromWar.remove(player.getResourceFromStorage(WAREHOUSE_SMALL));
            }
        });

        resWarMidToggles.forEach(x -> x.setOnAction(event -> {
            if (x.isSelected()) {
                fromWar.add(player.getResourceFromStorage(WAREHOUSE_MID));
            } else {
                fromWar.remove(player.getResourceFromStorage(WAREHOUSE_MID));
            }
        }));

        resWarLargeToggles.forEach(x -> x.setOnAction(event -> {
            if (x.isSelected()) {
                fromWar.add(player.getResourceFromStorage(WAREHOUSE_LARGE));
            } else {
                fromWar.remove(player.getResourceFromStorage(WAREHOUSE_LARGE));
            }
        }));


        //choose from loot
        List<Resource> fromLoot = new ArrayList<>();
        stoneAddBtn.setOnAction(actionEvent -> {
            stoneLbl.setText(String.valueOf((Integer.parseInt(stoneLbl.getText()) + 1)));
            fromLoot.add(STONE);
        });
        stoneSubBtn.setOnAction(actionEvent -> {
            if (Integer.parseInt(stoneLbl.getText()) != 0) {
                stoneLbl.setText(String.valueOf((Integer.parseInt(stoneLbl.getText()) - 1)));
                fromLoot.remove(STONE);
            }
        });
        servantAddBtn.setOnAction(actionEvent -> {
            servantLbl.setText(String.valueOf((Integer.parseInt(servantLbl.getText()) + 1)));
            fromLoot.add(SERVANT);
        });
        servantSubBtn.setOnAction(actionEvent -> {
            if (Integer.parseInt(servantLbl.getText()) != 0) {
                servantLbl.setText(String.valueOf((Integer.parseInt(servantLbl.getText()) - 1)));
                fromLoot.remove(SERVANT);
            }
        });
        coinAddBtn.setOnAction(actionEvent -> {
                coinLbl.setText(String.valueOf((Integer.parseInt(coinLbl.getText()) + 1)));
                fromLoot.add(COIN);
        });
        coinSubBtn.setOnAction(actionEvent -> {
            if (Integer.parseInt(coinLbl.getText()) != 0) {
                coinLbl.setText(String.valueOf((Integer.parseInt(coinLbl.getText()) - 1)));
                fromLoot.remove(COIN);
            }
        });

        shieldAddBtn.setOnAction(actionEvent -> {
                shieldLbl.setText(String.valueOf((Integer.parseInt(shieldLbl.getText()) + 1)));
                fromLoot.add(SHIELD);
        });

        shieldSubBtn.setOnAction(actionEvent -> {
            if (Integer.parseInt(shieldLbl.getText()) != 0) {
                shieldLbl.setText(String.valueOf((Integer.parseInt(shieldLbl.getText()) - 1)));
                fromLoot.remove(SHIELD);
            }
        });

        confirmPaymentBtn.setOnAction(actionEvent -> {
            wallet.setLootchestTray(fromLoot);
            wallet.setWarehouseTray(fromWar);

            stage.close();

            controller.buyProductionCard(cardId, stack, null, wallet);
        });
    }

    private void chooseLeader(){

    }


}
