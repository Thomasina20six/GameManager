
import blackjack.model.Deck;
import blackjack.model.Hand;
import javafx.application.Application;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import shared.SceneRouter;
import shared.SoundManager;

public class Main extends Application {
    @Override
    public void start(Stage stage) {
        Font.loadFont(getClass().getResourceAsStream("/font/ARCADEPI.TTF"), 10);
        SceneRouter.setStage(stage);
        stage.iconifiedProperty().addListener((obs, wasIconified, iconified) ->
                SoundManager.notifyWindowIconified(iconified));
        SoundManager.init();
        SceneRouter.showLogin();
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
    
}
