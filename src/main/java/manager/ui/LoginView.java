package manager.ui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import manager.User;
import shared.SceneRouter;
import shared.SoundManager;
import shared.Session;

public class LoginView {

    public static Scene createScene() {

        // title
        Label titleLabel = new Label("GAME MANAGER");
        titleLabel.getStyleClass().add("title-label");

        Label subtitle1Label = new Label("------+ WELCOME BACK! +------");
        subtitle1Label.getStyleClass().add("subtitle-label-green");

        Label subtitle2Label = new Label("----------- LOGIN -----------");
        subtitle2Label.getStyleClass().add("subtitle-label-gold");

        // username and password fields
        Label usernameLabel = new Label("USERNAME");
        usernameLabel.getStyleClass().add("field-label");

        TextField usernameField = new TextField();
        usernameField.setPromptText("Enter username");
        usernameField.getStyleClass().add("pixel-text-field");
        usernameField.setOnMousePressed(e -> SoundManager.playClick());

        Label passwordLabel = new Label("PASSWORD");
        passwordLabel.getStyleClass().add("field-label");

        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Enter password");
        passwordField.getStyleClass().add("pixel-text-field");
        passwordField.setOnMousePressed(e -> SoundManager.playClick());

        // message label for errors
        Label messageLabel = new Label();
        messageLabel.getStyleClass().add("error-label");

        //login and create account buttons
        Button loginButton = new Button("LOGIN");
        loginButton.getStyleClass().add("primary-button");
        loginButton.setOnMousePressed(e -> SoundManager.playClick());

        Button createAccountButton = new Button("CREATE ACCOUNT");
        createAccountButton.getStyleClass().add("secondary-button");
        createAccountButton.setOnMousePressed(e -> SoundManager.playClick());


        // login actions
        loginButton.setOnAction(e -> {
            String username = usernameField.getText().trim();
            String password = passwordField.getText().trim();

            // empty field validation
            if (username.isEmpty() || password.isEmpty()) {
                messageLabel.setText("Please enter both username and password.");
                return;
            }
            if (User.login(username, password)) {
                messageLabel.setText("");
                Session.setUsername(username);
                SceneRouter.showMainMenu();
            } else {
                messageLabel.setText("Invalid username or password.");
            }
        });

        // go to create account scene
        createAccountButton.setOnAction(e -> SceneRouter.showCreateAccount());

        VBox form = new VBox(12);
        form.setAlignment(Pos.CENTER);
        form.setPadding(new Insets(30));
        form.getStyleClass().add("pixel-panel-content");

        form.getChildren().addAll(
            titleLabel,
            subtitle1Label,
            subtitle2Label,
            usernameLabel,
            usernameField,
            passwordLabel,
            passwordField,
            loginButton,
            createAccountButton,
            messageLabel
        );

        StackPane panelOuter = new StackPane(form);
        panelOuter.getStyleClass().add("pixel-panel-outer");
        panelOuter.setMaxWidth(400);
        panelOuter.setMaxHeight(500);

        panelOuter.setPrefSize(400, 500);

        StackPane root = new StackPane();
        root.setPadding(new Insets(40));
        root.getChildren().addAll(panelOuter);
        root.getStyleClass().add("login-root");

        Scene scene = new Scene(root, 750, 550);
        // link stylesheets
        scene.getStylesheets().add(LoginView.class.getResource("/css/app.css").toExternalForm());
        scene.getStylesheets().add(LoginView.class.getResource("/css/manager.css").toExternalForm());
        return scene;
    }
}