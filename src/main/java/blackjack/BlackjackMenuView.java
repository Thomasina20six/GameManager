package blackjack;

import blackjack.logic.BlackJackGame;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.VBox;
import shared.SceneRouter;
import shared.Toolbar;

public class BlackjackMenuView {

    /** Center content only — toolbar is on the shared shell. */
    public static Node createView() {
        VBox center = new VBox(12);
        center.setAlignment(Pos.CENTER);
        center.setPadding(new Insets(30));
        Label title = new Label("CHOOSE AN OPTION");
        title.getStyleClass().add("blackjack-menu-title");
        Button newGameButton = new Button("New Game");
        newGameButton.getStyleClass().addAll("secondary-button", "blackjack-menu-button");

        newGameButton.setOnAction(e -> SceneRouter.showBlackjack());

        Button loadGameButton = new Button("Load Game");
        loadGameButton.getStyleClass().addAll("secondary-button", "blackjack-menu-button");

        loadGameButton.setOnAction(e -> {
            Dialog<ButtonType> dialog = new Dialog<>();
            dialog.setTitle("Load game");
            dialog.setHeaderText("Paste your save string (encrypted BJENC1 or legacy plain BJ1)");
            ButtonType loadButton = new ButtonType("Load", ButtonBar.ButtonData.OK_DONE);
            dialog.getDialogPane().getButtonTypes().addAll(loadButton, ButtonType.CANCEL);
            TextArea area = new TextArea();
            area.setPromptText("Paste encrypted save (BJENC1:…) or old plain save here…");
            area.setWrapText(true);
            area.setPrefRowCount(14);
            dialog.getDialogPane().setContent(new VBox(8, area));
            dialog.showAndWait().ifPresent(result -> {
                if (result != loadButton) {
                    return;
                }
                try {
                    String pasted = area.getText();
                    BlackJackGame game = BlackJackGame.importFromSaveString(pasted);
                    SceneRouter.showBlackjack(game);
                    BlackJackSave.register(game);
                    Toolbar.refreshSaveButton();
                } catch (IllegalArgumentException ex) {
                    new Alert(Alert.AlertType.ERROR, ex.getMessage()).showAndWait();
                }
            });
        });

        center.getChildren().addAll(title, newGameButton, loadGameButton);
        center.getStyleClass().add("blackjack-menu-root");
        return center;
    }
}
