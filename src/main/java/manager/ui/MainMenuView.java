package manager.ui;

import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.control.Separator;
import manager.HighScoreManager;
import manager.HighScoreManager.ScoreEntry;
import shared.SceneRouter;
import shared.Session;
import shared.SoundManager;

import java.util.List;

public class MainMenuView {

    /**
     * Creates the main menu screen layout.
     */
    public static Node createView() {
        BorderPane root = new BorderPane();
        root.setPadding(new Insets(10));
        root.getStyleClass().add("mainmenu-root");

        VBox mainBox = new VBox(25);
        mainBox.setPadding(new Insets(20, 30, 20, 20));
        mainBox.setAlignment(Pos.CENTER);

        Label title = new Label("----- MAIN MENU -----");
        title.getStyleClass().add("title-label");

        Label welcome = new Label("-----+ WELCOME BACK, " + getDisplayUsername() + " +-----");
        welcome.getStyleClass().add("subtitle-label-green");

        HBox content = new HBox(45);
        content.setAlignment(Pos.CENTER);

        VBox leaderboardPane = createLeaderboardPane();
        leaderboardPane.getStyleClass().add("pixel-panel-outer");
        VBox gameArea = createGameArea();
        gameArea.getStyleClass().add("pixel-panel-outer");

        content.getChildren().addAll(leaderboardPane, gameArea);

        Label footer = new Label("-------- ★ CHOOSE A GAME TO BEGIN ★ --------");
        footer.getStyleClass().add("subtitle-label-gold");

        mainBox.getChildren().addAll(title, welcome, content, footer);
        root.setCenter(mainBox);
        root.getStylesheets().add(LoginView.class.getResource("/css/app.css").toExternalForm());
        root.getStylesheets().add(LoginView.class.getResource("/css/manager.css").toExternalForm());
        return root;
    }

    /**
     * Creates the leaderboard section.
     */
    private static VBox createLeaderboardPane() {
        VBox pane = new VBox(18);
        pane.setPrefSize(470, 590);
        pane.setPadding(new Insets(22));
        pane.setAlignment(Pos.CENTER);

        Label title = new Label("+---- LEADERBOARD ----+");
        title.getStyleClass().add("leaderboard-title");

        HBox firstRow = new HBox(12);
        VBox blackjackScores = createScoreSection("Blackjack", HighScoreManager.BLACKJACK);
        VBox snakeScores = createScoreSection("Snake", HighScoreManager.SNAKE);
        firstRow.getChildren().addAll(blackjackScores, snakeScores);

        HBox secondRow = new HBox(12);
        VBox tttScores = createScoreSection("Tic-Tac-Toe", HighScoreManager.TTT);
        VBox wamScores = createScoreSection("Wack-a-Mole", HighScoreManager.WAM);
        secondRow.getChildren().addAll(tttScores, wamScores);

        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);

        pane.getChildren().addAll(title, firstRow, secondRow, spacer);
        return pane;
    }

    /**
     * Creates one score section for a game.
     */
    private static VBox createScoreSection(String displayName, String gameName) {
        VBox section = new VBox(8);
        section.getStyleClass().add("score-section");

        Label sectionTitle = new Label(displayName);
        sectionTitle.getStyleClass().add("score-section-title");

        GridPane scoreGrid = new GridPane();
        scoreGrid.setHgap(12);
        scoreGrid.setVgap(6);

        Label rankHeader = new Label("#");
        Label nameHeader = new Label("Player");
        Label scoreHeader = new Label("Score");
        rankHeader.getStyleClass().add("score-header");
        nameHeader.getStyleClass().add("score-header");
        scoreHeader.getStyleClass().add("score-header");
        nameHeader.setPrefWidth(80);
        scoreHeader.setPrefWidth(70);
        scoreHeader.setAlignment(Pos.CENTER_RIGHT);

        scoreGrid.add(rankHeader, 0, 0);
        scoreGrid.add(nameHeader, 1, 0);
        scoreGrid.add(scoreHeader, 2, 0);

        Label separator = new Label("------------------------------"); 
        separator.getStyleClass().add("score-separator");
        scoreGrid.add(separator, 0, 1, 3, 1);

        List<ScoreEntry> scores = HighScoreManager.getTopScores(gameName, 5);
        for (int i = 1; i <= 5; i++) {
            Label rank = new Label(String.valueOf(i));
            Label username = new Label("---");
            Label score = new Label("---");

            if (i < scores.size()) {
                ScoreEntry entry = scores.get(i - 1);
                username.setText(entry.getUsername());
                score.setText(String.valueOf(entry.getScore()));
            }

            if(username.getText().equals(getDisplayUsername())) {
                rank.getStyleClass().add("score-text-highlight");
                username.getStyleClass().add("score-text-highlight");
                score.getStyleClass().add("score-text-highlight");
            } 
            else {
                rank.getStyleClass().add("score-text");
                username.getStyleClass().add("score-text");
                score.getStyleClass().add("score-text");
            }

            username.setPrefWidth(70);
            score.setPrefWidth(70);
            score.setAlignment(Pos.CENTER_RIGHT);

            scoreGrid.add(rank, 0, i + 1);
            scoreGrid.add(username, 1, i + 1);
            scoreGrid.add(score, 2, i + 1);
        }

        section.getChildren().addAll(sectionTitle, scoreGrid);
        return section;
    }

    /**
     * Creates the game launcher area.
     */
    private static VBox createGameArea() {
        Label title = new Label("+---- SELECT GAME ----+");
        title.getStyleClass().add("leaderboard-title");

        Button blackjackButton = createMenuButton("BLACKJACK");
        Button snakeButton = createMenuButton("SNAKE");
        Button tttButton = createMenuButton("TIC-TAC-TOE");
        Button wackAMoleButton = createMenuButton("WACK-A-MOLE");


        blackjackButton.setOnMousePressed(e -> SoundManager.playClick());
        snakeButton.setOnMousePressed(e -> SoundManager.playClick());
        tttButton.setOnMousePressed(e -> SoundManager.playClick());
        wackAMoleButton.setOnMousePressed(e -> SoundManager.playClick());

        blackjackButton.getStyleClass().add("game-button");
        snakeButton.getStyleClass().add("game-button");
        tttButton.getStyleClass().add("game-button");
        wackAMoleButton.getStyleClass().add("game-button");

        snakeButton.setDisable(true);
        tttButton.setDisable(true);
        wackAMoleButton.setDisable(true);

        blackjackButton.setOnAction(e -> {
            SoundManager.playClick();
            SceneRouter.showBlackjackMenu();
        });

        VBox buttons = new VBox(18, blackjackButton, snakeButton, tttButton, wackAMoleButton);
        buttons.setAlignment(Pos.CENTER);

        VBox gameArea = new VBox(14, title, buttons);
        gameArea.getStyleClass().add("game-launcher-pane");
        gameArea.setAlignment(Pos.CENTER);
        gameArea.setPadding(new Insets(35));
        gameArea.setPrefSize(470, 590);
        return gameArea;
    }

    /**
     * Creates a styled menu button.
     */
    private static Button createMenuButton(String text) {
        Button button = new Button(text);
        button.getStyleClass().add("main-menu-button");
        button.setPrefSize(240, 56);
        return button;
    }

    /**
     * Returns the current logged-in username.
     */
    private static String getDisplayUsername() {
        String username = Session.getUsername();
        if (username == null || username.trim().isEmpty()) {
            return "Guest";
        }
        return username;
    }
}
