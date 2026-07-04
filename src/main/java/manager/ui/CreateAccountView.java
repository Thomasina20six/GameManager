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

public class CreateAccountView {

    public static Scene createScene() {

        // title
        Label titleLabel = new Label("GAME MANAGER");
        titleLabel.getStyleClass().add("title-label");

        Label subtitle1Label = new Label("------+ WELCOME NEW PLAYER! +------");
        subtitle1Label.getStyleClass().add("subtitle-label-green");

        Label subtitle2Label = new Label("----------- CREATE ACCOUNT -----------");
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
        Button createAccountButton = new Button("CREATE ACCOUNT");
        createAccountButton.getStyleClass().add("primary-button");
        createAccountButton.setOnMousePressed(e -> SoundManager.playClick());

        Button backToLoginButton = new Button("BACK TO LOGIN");
        backToLoginButton.getStyleClass().add("secondary-button");
        backToLoginButton.setOnAction(e -> SceneRouter.showLogin());
        backToLoginButton.setOnMousePressed(e -> SoundManager.playClick());


        // login actions
        createAccountButton.setOnAction(e -> {
            String username = usernameField.getText().trim();
            String password = passwordField.getText().trim();

            // empty field validation
            if (username.isEmpty() || password.isEmpty()) {
                messageLabel.setText("Please enter both username and password.");
                return;
            }
            if(!User.createAccount(username, password)){
                messageLabel.setText("Failed to create account.");
            }
            else{
                messageLabel.getStyleClass().add("success-label");
                messageLabel.setText("Account created successfully. Click \"Back to Login\".");
            }

        });

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
            createAccountButton,
            backToLoginButton,
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