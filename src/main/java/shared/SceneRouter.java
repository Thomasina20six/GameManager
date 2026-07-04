package shared;
import javafx.stage.Stage;
import manager.ui.CreateAccountView;
import manager.ui.LoginView;
import manager.ui.MainMenuView;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import blackjack.BlackJackSave;
import blackjack.BlackjackMenuView;
import blackjack.BlackjackView;
import blackjack.logic.BlackJackGame;

public class SceneRouter {
    private static Stage stage;
    private static BorderPane rootLayout;
    private static Scene mainScene;

    public static void setStage(Stage s) {
        stage = s;
    }

    /** One {@link BorderPane}: toolbar on top, all screens swap {@link BorderPane#setCenter(javafx.scene.Node)} only. */
    private static void ensureMainShell() {
        if (mainScene == null) {
            initRootLayout();
        }
    }

    private static void unlockStageSize() {
        stage.setResizable(true);
        stage.setMinWidth(0);
        stage.setMinHeight(0);
        stage.setMaxWidth(Double.MAX_VALUE);
        stage.setMaxHeight(Double.MAX_VALUE);
    }

    private static void lockStageSize(double width, double height) {
        stage.setResizable(false);
        stage.setMinWidth(width);
        stage.setMaxWidth(width);
        stage.setMinHeight(height);
        stage.setMaxHeight(height);
    }

    public static void showLogin() {
        clearBlackjackSave();
        mainScene = null;
        rootLayout = null;
        unlockStageSize();
        stage.setTitle("Login");
        stage.setScene(LoginView.createScene());
        stage.sizeToScene();
    }

    public static void showCreateAccount() {
        clearBlackjackSave();
        mainScene = null;
        rootLayout = null;
        unlockStageSize();
        stage.setTitle("Create Account");
        stage.setScene(CreateAccountView.createScene());
        stage.sizeToScene();
    }

    public static void showMainMenu() {
        ensureMainShell();
        clearBlackjackSave();
        stage.setTitle("Main Menu");
        SoundManager.stopBlackjackBackgroundMusic();
        SoundManager.clearActiveBgm();
        rootLayout.setCenter(MainMenuView.createView());
        unlockStageSize();
        stage.setScene(mainScene);
        stage.sizeToScene();
        stage.show();
    }

    public static void showBlackjackMenu() {
        ensureMainShell();
        clearBlackjackSave();
        SoundManager.playBlackjackBackgroundMusic();
        stage.setTitle("Blackjack Menu");
        rootLayout.setCenter(BlackjackMenuView.createView());
        unlockStageSize();
        stage.setScene(mainScene);
        stage.sizeToScene();
        stage.show();
    }

    public static void showBlackjack() {
        ensureMainShell();
        SoundManager.playBlackjackBackgroundMusic();
        stage.setTitle("Blackjack");
        rootLayout.setCenter(BlackjackView.createView());
        lockStageSize(BlackjackView.SCENE_WIDTH, BlackjackView.SCENE_HEIGHT);
        stage.setScene(mainScene);
        stage.sizeToScene();
        stage.show();
    }

    public static void showBlackjack(BlackJackGame game) {
        ensureMainShell();
        SoundManager.playBlackjackBackgroundMusic();
        stage.setTitle("Blackjack");
        rootLayout.setCenter(BlackjackView.createView(game));
        lockStageSize(BlackjackView.SCENE_WIDTH, BlackjackView.SCENE_HEIGHT);
        stage.setScene(mainScene);
        stage.sizeToScene();
        stage.show();
    }

    private static void clearBlackjackSave() {
        BlackJackSave.clear();
        Toolbar.refreshSaveButton();
    }

    private static void initRootLayout() {
        rootLayout = new BorderPane();
        rootLayout.setTop(Toolbar.create());
        mainScene = new Scene(rootLayout, 900, 650);
        mainScene.getStylesheets().add(SceneRouter.class.getResource("/css/app.css").toExternalForm());
        mainScene.getStylesheets().add(SceneRouter.class.getResource("/css/manager.css").toExternalForm());
        mainScene.getStylesheets().add(SceneRouter.class.getResource("/css/blackjack.css").toExternalForm());
        stage.setScene(mainScene);
    }
}
