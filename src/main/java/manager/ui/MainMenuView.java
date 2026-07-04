package manager.ui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
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
        root.getStyleClass().add("main-menu-root");
        root.setPadding(new Insets(28));

        VBox leaderboardPane = createLeaderboardPane();
        VBox gameArea = createGameArea();

        HBox contents = new HBox(30, leaderboardPane, gameArea);
        contents.setAlignment(Pos.CENTER);
        HBox.setHgrow(gameArea, Priority.ALWAYS);

        root.setCenter(contents);
        return root;
    }

    /**
     * Creates the leaderboard section.
     */
    private static VBox createLeaderboardPane() {
        VBox pane = new VBox(18);
        pane.getStyleClass().add("leaderboard-pane");
        pane.setPrefSize(330, 590);
        pane.setMinWidth(330);
        pane.setMaxWidth(330);
        pane.setPadding(new Insets(22));

        Label title = new Label("LEADERBOARD");
        title.getStyleClass().add("leaderboard-title");

        Label subtitle = new Label("Top 5 High Scores");
        subtitle.getStyleClass().add("leaderboard-subtitle");

        VBox blackjackScores = createScoreSection("Blackjack", HighScoreManager.BLACKJACK);

        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);

        Label userLabel = new Label("Logged in as: " + getDisplayUsername());
        userLabel.getStyleClass().add("leaderboard-user");

        pane.getChildren().addAll(title, subtitle, blackjackScores, spacer, userLabel);
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

        scoreGrid.add(rankHeader, 0, 0);
        scoreGrid.add(nameHeader, 1, 0);
        scoreGrid.add(scoreHeader, 2, 0);

        List<ScoreEntry> scores = HighScoreManager.getTopScores(gameName, 5);
        for (int i = 0; i < 5; i++) {
            Label rank = new Label(String.valueOf(i + 1));
            Label username = new Label("---");
            Label score = new Label("---");

            if (i < scores.size()) {
                ScoreEntry entry = scores.get(i);
                username.setText(entry.getUsername());
                score.setText(String.valueOf(entry.getScore()));
            }

            rank.getStyleClass().add("score-text");
            username.getStyleClass().add("score-text");
            score.getStyleClass().add("score-text");

            username.setPrefWidth(130);
            score.setPrefWidth(65);
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
        Label title = new Label("GAME MANAGER");
        title.getStyleClass().add("main-menu-title");

        Label subtitle = new Label("Choose a game to play");
        subtitle.getStyleClass().add("main-menu-subtitle");

        Button blackjackButton = createMenuButton("Blackjack");
        Button snakeButton = createMenuButton("Snake");
        Button button3 = createMenuButton("Coming Soon");
        Button button4 = createMenuButton("Coming Soon");

        snakeButton.setDisable(true);
        button3.setDisable(true);
        button4.setDisable(true);

        blackjackButton.setOnAction(e -> {
            SoundManager.playClick();
            SceneRouter.showBlackjackMenu();
        });

        VBox buttons = new VBox(18, blackjackButton, snakeButton, button3, button4);
        buttons.setAlignment(Pos.CENTER);

        VBox gameArea = new VBox(14, title, subtitle, buttons);
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
