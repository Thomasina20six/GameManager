package shared;

import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import shared.SceneRouter;
import javafx.scene.layout.BorderPane;
import javafx.geometry.Pos;
import shared.SoundManager;
import shared.Session;
import blackjack.BlackJackSave;
import blackjack.BlackjackView;
import blackjack.logic.BlackJackGame;
import manager.HighScoreManager;

public class Toolbar {

    public static Button saveButton;

    public static BorderPane create() {
        BorderPane toolbar = new BorderPane();
        toolbar.getStyleClass().add("app-toolbar");

        String u = Session.getUsername();
        Label usernameLabel = new Label(u);
        toolbar.setLeft(usernameLabel);

		Button mainMenu = new Button("Main Menu");
		mainMenu.setFocusTraversable(false);
		mainMenu.setOnAction(e -> {
			saveBlackjackScoreIfPlaying();

			SoundManager.stopBlackjackBackgroundMusic();
			SoundManager.clearActiveBgm();

			SceneRouter.showMainMenu();
		});

        Button muteButton = new Button(SoundManager.isMuted() ? "Unmute" : "Mute");
        muteButton.setFocusTraversable(false);
        muteButton.setOnAction(e -> {
            boolean nextState = !SoundManager.isMuted();
            SoundManager.setMuted(nextState);
            muteButton.setText(nextState ? "Unmute" : "Mute");
        });

		Button logoutButton = new Button("Logout");
		logoutButton.setFocusTraversable(false);
		logoutButton.setOnAction(e -> {
			saveBlackjackScoreIfPlaying();

			SoundManager.stopBlackjackBackgroundMusic();
			SoundManager.clearActiveBgm();

			Session.clear();
			SceneRouter.showLogin();
		});

        saveButton = new Button("Save");
        saveButton.setFocusTraversable(false);
        saveButton.setOnMousePressed(e -> SoundManager.playClick());
        saveButton.setOnAction(e -> {
            String s = BlackJackSave.getSaveString();
            if (s == null) {
                return;
            }
            BlackjackView.pauseAutomatedRound();
            try {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Save game");
                alert.setHeaderText(
                        "Copy this encrypted save string (Load Game from Blackjack menu to restore)");
                TextArea area = new TextArea(s);
                area.setEditable(false);
                area.setWrapText(true);
                area.selectAll();
				area.requestFocus();
                area.setPrefRowCount(14);
                area.setMaxWidth(Double.MAX_VALUE);
                VBox box = new VBox(8, area);
                VBox.setVgrow(area, Priority.ALWAYS);
                alert.getDialogPane().setContent(box);
                alert.getDialogPane().setPrefWidth(560);
                alert.showAndWait();
            } finally {
                BlackjackView.finishSaveDialogAndResumeRound();
            }
        });

        HBox rightButtons = new HBox(10);
        rightButtons.getChildren().addAll(saveButton, mainMenu, muteButton, logoutButton);
        rightButtons.setAlignment(Pos.CENTER_RIGHT);
        toolbar.setRight(rightButtons);

        refreshSaveButton();
        return toolbar;
    }

    public static void refreshSaveButton(){
        if(saveButton == null){
            return;
        }
        boolean show = BlackJackSave.canSave();
        saveButton.setVisible(show);
        saveButton.setManaged(show);
    }
    
    private static void saveBlackjackScoreIfPlaying() {
		BlackJackGame game = BlackJackSave.getCurrentGame();

		if (game != null) {
			HighScoreManager.saveScore(
					Session.getUsername(),
					HighScoreManager.BLACKJACK,
					game.getHumanPlayer().getMoney()
			);
		}
	}
}
