package blackjack;

import blackjack.logic.BlackJackGame;
import blackjack.model.Hand;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.Cursor;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.TextAlignment;
import shared.Session;
import shared.SoundManager;
import shared.Toolbar;
import javafx.application.Platform;
import blackjack.model.GamePhase;
import java.util.ArrayList;
import javafx.animation.PauseTransition;
import javafx.util.Duration;

public class BlackjackView {
    /** Pause before each participant acts after their turn banner is shown. */
    private static final Duration BOT_DEALER_INTRO_PAUSE = Duration.millis(2000);
    /** Pause between consecutive hits for the same participant. */
    private static final Duration BETWEEN_CARD_ACTION_PAUSE = Duration.millis(2000);
    /** Pause after a participant finishes before the next turn banner. */
    private static final Duration BETWEEN_PARTICIPANTS_PAUSE = Duration.millis(3000);

    /** Target window size when the table is shown ({@link shared.SceneRouter#showBlackjack}). */
    public static final double SCENE_WIDTH = 950;
    public static final double SCENE_HEIGHT = 800;

    /** Latest table: show "Click to continue" over cards (set in {@link #buildTable}). */
    private static Runnable showContinueOverlayAction;

    private static Node buildTable(BlackJackGame game) {
        BorderPane page = new BorderPane();

        BorderPane gameLayer = new BorderPane();
        gameLayer.getStyleClass().add("blackjack-game-root");

        BlackJackSave.register(game);
        Toolbar.refreshSaveButton();

        // Table Area
        VBox tableArea = new VBox(12);
        tableArea.setAlignment(Pos.TOP_CENTER);
        gameLayer.setCenter(tableArea);

        // Chips add to this before you press "Bet"
        int[] stagedBet = { 0 };

        Label moneyLabel = new Label();
        moneyLabel.getStyleClass().add("blackjack-money-label");
        moneyLabel.setMouseTransparent(true);
        var player = game.getHumanPlayer();
        int displayTotal = player.getMoney() + player.getCurrentBet();
        moneyLabel.setText(String.format("Wallet:$%d", displayTotal));

        Label bettingLabel = new Label();
        bettingLabel.getStyleClass().add("blackjack-money-label");
        bettingLabel.setMinWidth(150);

        Label statusLabel = new Label();
        statusLabel.getStyleClass().add("blackjack-status-label");
        statusLabel.setMaxWidth(250);

        Label roundResultLabel = new Label();
        roundResultLabel.getStyleClass().add("blackjack-round-result");
        roundResultLabel.setWrapText(true);
        roundResultLabel.setMaxWidth(250);
        roundResultLabel.setTextAlignment(TextAlignment.CENTER);

        Button hitBtn = new Button("Hit");
        Button standBtn = new Button("Stand");
        Button betBtn = new Button("Bet");
        Button undoBtn = new Button("Undo All");
        for (Button b : new Button[] { hitBtn, standBtn, betBtn, undoBtn}) {
            b.getStyleClass().add("blackjack-control-button");
        }

        Button playAgainBtn = new Button("Play Again");
        playAgainBtn.getStyleClass().add("blackjack-play-again");

        StackPane startOverOverlay =
                buildStartOverOverlay(
                        game,
                        tableArea,
                        stagedBet,
                        moneyLabel,
                        bettingLabel,
                        statusLabel,
                        roundResultLabel,
                        playAgainBtn,
                        hitBtn,
                        standBtn,
                        betBtn,
                        undoBtn);

        HBox actions = new HBox(8, hitBtn, standBtn, betBtn, undoBtn);
        actions.setAlignment(Pos.CENTER_RIGHT);
        actions.setPadding(new Insets(0, 0, 0, 16));

        // Buttons Actions
        hitBtn.setOnAction(e -> {
            SoundManager.playClick();
            game.humanHit();
            if (game.getPhase() == GamePhase.BOT1_TURN) {
                playOutRemainingRoundAnimated(
                        game,
                        tableArea,
                        stagedBet,
                        moneyLabel,
                        bettingLabel,
                        statusLabel,
                        roundResultLabel,
                        playAgainBtn,
                        hitBtn,
                        standBtn,
                        betBtn,
                        undoBtn,
                        startOverOverlay);
            } else {
                syncBottomBar(
                        game,
                        tableArea,
                        stagedBet,
                        moneyLabel,
                        bettingLabel,
                        statusLabel,
                        roundResultLabel,
                        playAgainBtn,
                        hitBtn,
                        standBtn,
                        betBtn,
                        undoBtn,
                        startOverOverlay);
            }
        });
        standBtn.setOnAction(e -> {
            SoundManager.playClick();
            game.humanStand();
            playOutRemainingRoundAnimated(
                    game,
                    tableArea,
                    stagedBet,
                    moneyLabel,
                    bettingLabel,
                    statusLabel,
                    roundResultLabel,
                    playAgainBtn,
                    hitBtn,
                    standBtn,
                    betBtn,
                    undoBtn,
                    startOverOverlay);
        });
        betBtn.setOnAction(e -> {
            SoundManager.playClick();
            int pocket = game.getHumanPlayer().getMoney();
            int amount = stagedBet[0];
            amount = Math.min(amount, pocket);
            if (amount <= 0 || !game.getHumanPlayer().canBet(amount)) {
                syncBottomBar(
                        game,
                        tableArea,
                        stagedBet,
                        moneyLabel,
                        bettingLabel,
                        statusLabel,
                        roundResultLabel,
                        playAgainBtn,
                        hitBtn,
                        standBtn,
                        betBtn,
                        undoBtn,
                        startOverOverlay);
                return;
            }
            game.startNewRound(amount);
            stagedBet[0] = 0;
            syncBottomBar(
                    game,
                    tableArea,
                    stagedBet,
                    moneyLabel,
                    bettingLabel,
                    statusLabel,
                    roundResultLabel,
                    playAgainBtn,
                    hitBtn,
                    standBtn,
                    betBtn,
                    undoBtn,
                    startOverOverlay);
        });
        undoBtn.setOnAction(e -> {
            SoundManager.playClick();
            stagedBet[0] = 0;
            syncBottomBar(
                    game,
                    tableArea,
                    stagedBet,
                    moneyLabel,
                    bettingLabel,
                    statusLabel,
                    roundResultLabel,
                    playAgainBtn,
                    hitBtn,
                    standBtn,
                    betBtn,
                    undoBtn,
                    startOverOverlay);
        });

        playAgainBtn.setOnAction(e -> {
            SoundManager.playClick();
            if (game.getPhase() == GamePhase.ROUND_OVER) {
                game.clearTableForNextBet();
                syncBottomBar(
                        game,
                        tableArea,
                        stagedBet,
                        moneyLabel,
                        bettingLabel,
                        statusLabel,
                        roundResultLabel,
                        playAgainBtn,
                        hitBtn,
                        standBtn,
                        betBtn,
                        undoBtn,
                        startOverOverlay);
            }
        });

        // Status + round stay stacked (not VBox); betting sits left with a gap before this block.
        StackPane statusRoundRegion = new StackPane(statusLabel, roundResultLabel);
        statusRoundRegion.setMinHeight(52);
        StackPane.setAlignment(statusLabel, Pos.CENTER);
        StackPane.setAlignment(roundResultLabel, Pos.CENTER);

        Region spacerBeforeButtons = new Region();
        HBox.setHgrow(spacerBeforeButtons, Priority.ALWAYS);

        final int bettingToStatusGap = 28;
        HBox bottom = new HBox(bettingToStatusGap, bettingLabel, statusRoundRegion, spacerBeforeButtons, actions);
        bottom.setAlignment(Pos.CENTER_LEFT);
        bottom.setMinHeight(52);
        bottom.getStyleClass().add("blackjack-game-bottom");

        // Sync bottom bar with model after each action
        syncBottomBar(
                game,
                tableArea,
                stagedBet,
                moneyLabel,
                bettingLabel,
                statusLabel,
                roundResultLabel,
                playAgainBtn,
                hitBtn,
                standBtn,
                betBtn,
                undoBtn,
                startOverOverlay);

        HBox chipButtons = new HBox(10,
                createChipButton("/images/chips/005.png", "1"),
                createChipButton("/images/chips/025.png", "5"),
                createChipButton("/images/chips/017.png", "10"),
                createChipButton("/images/chips/021.png", "25"),
                createChipButton("/images/chips/029.png", "100"));
        chipButtons.setAlignment(Pos.CENTER_RIGHT);
        chipButtons.setPadding(new Insets(0, 18, 10, 18));

        // Chip buttons actions
        int[] chipValues = { 1, 5, 10, 25, 100 }; // one per button, left to right
        int i = 0;
        for (var node : chipButtons.getChildren()) {
            if (node instanceof Button chip) {
                int add = chipValues[i++];
                chip.setOnAction(e -> {
                    SoundManager.playClick();
                    stagedBet[0] += add;
                    // optional: cap to what player can afford
                    int max = game.getHumanPlayer().getMoney();
                    if (stagedBet[0] > max) {
                        stagedBet[0] = max;
                    }
                    syncBottomBar(
                            game,
                            tableArea,
                            stagedBet,
                            moneyLabel,
                            bettingLabel,
                            statusLabel,
                            roundResultLabel,
                            playAgainBtn,
                            hitBtn,
                            standBtn,
                            betBtn,
                            undoBtn,
                            startOverOverlay);
                });
            }
        }

        BorderPane chipRow = new BorderPane();
        chipRow.setRight(chipButtons);
        chipRow.setPickOnBounds(false);
        gameLayer.setBottom(chipRow);

        gameLayer.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        StackPane tableShell = new StackPane(gameLayer, startOverOverlay, moneyLabel, playAgainBtn);
        StackPane.setAlignment(startOverOverlay, Pos.CENTER);
        StackPane.setAlignment(moneyLabel, Pos.BOTTOM_LEFT);
        StackPane.setMargin(moneyLabel, new Insets(0, 0, 10, 20));
        StackPane.setAlignment(playAgainBtn, Pos.TOP_RIGHT);
        StackPane.setMargin(playAgainBtn, new Insets(25, 18, 0, 0));

        StackPane continueOverlay =
                buildAutomatedContinueOverlay(
                        game,
                        tableArea,
                        stagedBet,
                        moneyLabel,
                        bettingLabel,
                        statusLabel,
                        roundResultLabel,
                        playAgainBtn,
                        hitBtn,
                        standBtn,
                        betBtn,
                        undoBtn,
                        startOverOverlay);
        StackPane tableWithOverlay = new StackPane(tableShell, continueOverlay);
        page.setCenter(tableWithOverlay);

        showContinueOverlayAction = () -> {
            continueOverlay.setVisible(true);
            continueOverlay.setManaged(true);
            continueOverlay.toFront();
        };
        if (isAutomatedPlayPhase(game.getPhase())) {
            showContinueOverlayAction.run();
        }

        page.setBottom(bottom);

        return page;
    }

    /** New game using logged-in session name. */
    public static Node createView() {
        return buildTable(new BlackJackGame(Session.getUsername()));
    }

    /** Table UI backed by an existing game state (e.g. loaded save). */
    public static Node createView(BlackJackGame game) {
        if (game == null) {
            return createView();
        }
        return buildTable(game);
    }

    private static Button createChipButton(String resourcePath, String denominationText) {
        Image img = new Image(BlackjackView.class.getResourceAsStream(resourcePath));
        ImageView view = new ImageView(img);
        view.setPreserveRatio(true);
        view.setFitWidth(44);
        view.setFitHeight(44);

        Label valueLabel = new Label(denominationText);
        valueLabel.getStyleClass().add("chip-value-label");
        valueLabel.setMouseTransparent(true);

        StackPane graphic = new StackPane(view, valueLabel);
        StackPane.setAlignment(valueLabel, Pos.CENTER);

        Button btn = new Button();
        btn.setGraphic(graphic);
        btn.getStyleClass().add("chip-button");
        return btn;
    }

    private static final ArrayList<PauseTransition> activeRoundPauses = new ArrayList<>();

    private static void runAfter(Duration delay, Runnable action) {
        PauseTransition pause = new PauseTransition(delay);
        pause.setOnFinished(ev -> {
            activeRoundPauses.remove(pause);
            action.run();
        });
        activeRoundPauses.add(pause);
        pause.play();
    }

    public static void pauseAutomatedRound() {
        // Copy so we are not modifying the list while a transition's onFinished runs.
        for (PauseTransition p : new ArrayList<>(activeRoundPauses)) {
            p.pause();
        }
    }

    public static void resumeAutomatedRound() {
        for (PauseTransition p : new ArrayList<>(activeRoundPauses)) {
            p.play();
        }
    }

    /**
     * After the save dialog closes: if bots/dealer were running, their {@link PauseTransition}s are often
     * left in a bad state; we stop them and let the user tap the overlay to restart the sequence.
     * Otherwise we simply resume pauses (e.g. human turn / betting).
     */
    public static void finishSaveDialogAndResumeRound() {
        BlackJackGame g = BlackJackSave.getCurrentGame();
        if (g == null) {
            resumeAutomatedRound();
            return;
        }
        if (isAutomatedPlayPhase(g.getPhase())) {
            for (PauseTransition p : new ArrayList<>(activeRoundPauses)) {
                p.stop();
            }
            activeRoundPauses.clear();
            if (showContinueOverlayAction != null) {
                Platform.runLater(() -> showContinueOverlayAction.run());
            }
        } else {
            resumeAutomatedRound();
        }
    }

    private static boolean isAutomatedPlayPhase(GamePhase ph) {
        return ph == GamePhase.BOT1_TURN
                || ph == GamePhase.BOT2_TURN
                || ph == GamePhase.DEALER_TURN;
    }

    private static StackPane buildStartOverOverlay(
            BlackJackGame game,
            VBox tableArea,
            int[] stagedBet,
            Label moneyLabel,
            Label bettingLabel,
            Label statusLabel,
            Label roundResultLabel,
            Button playAgainBtn,
            Button hitBtn,
            Button standBtn,
            Button betBtn,
            Button undoBtn) {
        Region scrim = new Region();
        scrim.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        scrim.getStyleClass().add("blackjack-start-over-scrim");
        Label message =
                new Label(
                        "You're out of money.\nStart over restores your wallet and both bots to $"
                                + BlackJackGame.STARTING_BANKROLL
                                + ".");
        message.getStyleClass().add("blackjack-start-over-message");
        message.setWrapText(true);
        message.setTextAlignment(TextAlignment.CENTER);
        Button startOverBtn = new Button("Start Over");
        startOverBtn.getStyleClass().add("blackjack-start-over-button");
        VBox panel = new VBox(14, message, startOverBtn);
        panel.setAlignment(Pos.CENTER);
        panel.getStyleClass().add("blackjack-start-over-panel");
        StackPane root = new StackPane(scrim, panel);
        StackPane.setAlignment(panel, Pos.CENTER);
        root.getStyleClass().add("blackjack-start-over-root");
        root.setPickOnBounds(true);
        root.setVisible(false);
        root.setManaged(false);
        startOverBtn.setOnAction(
                e -> {
                    SoundManager.playClick();
                    game.startOverWithFreshBankrolls();
                    stagedBet[0] = 0;
                    syncBottomBar(
                            game,
                            tableArea,
                            stagedBet,
                            moneyLabel,
                            bettingLabel,
                            statusLabel,
                            roundResultLabel,
                            playAgainBtn,
                            hitBtn,
                            standBtn,
                            betBtn,
                            undoBtn,
                            root);
                });
        return root;
    }

    private static StackPane buildAutomatedContinueOverlay(
            BlackJackGame game,
            VBox tableArea,
            int[] stagedBet,
            Label moneyLabel,
            Label bettingLabel,
            Label statusLabel,
            Label roundResultLabel,
            Button playAgainBtn,
            Button hitBtn,
            Button standBtn,
            Button betBtn,
            Button undoBtn,
            StackPane startOverOverlay) {
        Region scrim = new Region();
        scrim.getStyleClass().add("blackjack-continue-scrim");
        Label tap = new Label("Click Anywhere\nto Continue");
        tap.getStyleClass().add("blackjack-continue-label");
        tap.setTextAlignment(TextAlignment.CENTER);
        StackPane overlay = new StackPane(scrim, tap);
        StackPane.setAlignment(tap, Pos.CENTER);
        overlay.getStyleClass().add("blackjack-continue-root");
        overlay.setPickOnBounds(true);
        overlay.setCursor(Cursor.HAND);
        overlay.setVisible(false);
        overlay.setManaged(false);
        overlay.setOnMousePressed(e -> {
            if (!overlay.isVisible()) {
                return;
            }
            SoundManager.playClick();
            overlay.setVisible(false);
            overlay.setManaged(false);
            e.consume();
            playOutRemainingRoundAnimated(
                    game,
                    tableArea,
                    stagedBet,
                    moneyLabel,
                    bettingLabel,
                    statusLabel,
                    roundResultLabel,
                    playAgainBtn,
                    hitBtn,
                    standBtn,
                    betBtn,
                    undoBtn,
                    startOverOverlay);
        });
        return overlay;
    }

    /**
     * Runs bots and dealer with pauses: turn banner → 2s → actions (2s between each hit) → 3s → next participant.
     */
    private static void playOutRemainingRoundAnimated(
            BlackJackGame game,
            VBox tableArea,
            int[] stagedBet,
            Label moneyLabel,
            Label bettingLabel,
            Label statusLabel,
            Label roundResultLabel,
            Button playAgainBtn,
            Button hitBtn,
            Button standBtn,
            Button betBtn,
            Button undoBtn,
            StackPane startOverOverlay) {
        Runnable sync =
                () ->
                        syncBottomBar(
                                game,
                                tableArea,
                                stagedBet,
                                moneyLabel,
                                bettingLabel,
                                statusLabel,
                                roundResultLabel,
                                playAgainBtn,
                                hitBtn,
                                standBtn,
                                betBtn,
                                undoBtn,
                                startOverOverlay);
        scheduleNextParticipant(game, sync);
    }

    private static void scheduleNextParticipant(BlackJackGame game, Runnable sync) {
        GamePhase p = game.getPhase();
        if (p == GamePhase.BOT1_TURN) {
            game.setStatusMessage(game.getBot1().getName() + "'s turn.");
            sync.run();
            runAfter(BOT_DEALER_INTRO_PAUSE, () -> runBot1Steps(game, sync));
        } else if (p == GamePhase.BOT2_TURN) {
            game.setStatusMessage(game.getBot2().getName() + "'s turn.");
            sync.run();
            runAfter(BOT_DEALER_INTRO_PAUSE, () -> runBot2Steps(game, sync));
        } else if (p == GamePhase.DEALER_TURN) {
            game.setStatusMessage("Dealer's turn.");
            sync.run();
            runAfter(BOT_DEALER_INTRO_PAUSE, () -> runDealerSteps(game, sync));
        } else {
            sync.run();
        }
    }

    private static void runBot1Steps(BlackJackGame game, Runnable sync) {
        game.bot1PlayNextStep();
        sync.run();
        if (game.getPhase() == GamePhase.BOT1_TURN) {
            runAfter(BETWEEN_CARD_ACTION_PAUSE, () -> runBot1Steps(game, sync));
        } else {
            runAfter(BETWEEN_PARTICIPANTS_PAUSE, () -> scheduleNextParticipant(game, sync));
        }
    }

    private static void runBot2Steps(BlackJackGame game, Runnable sync) {
        game.bot2PlayNextStep();
        sync.run();
        if (game.getPhase() == GamePhase.BOT2_TURN) {
            runAfter(BETWEEN_CARD_ACTION_PAUSE, () -> runBot2Steps(game, sync));
        } else {
            runAfter(BETWEEN_PARTICIPANTS_PAUSE, () -> scheduleNextParticipant(game, sync));
        }
    }

    private static void runDealerSteps(BlackJackGame game, Runnable sync) {
        game.dealerPlayNextStep();
        sync.run();
        if (game.getPhase() == GamePhase.DEALER_TURN) {
            runAfter(BETWEEN_CARD_ACTION_PAUSE, () -> runDealerSteps(game, sync));
        }
    }

    private static void syncBottomBar(
        BlackJackGame game,
        VBox tableArea,
        int[] stagedBet,
        Label moneyLabel,
        Label bettingLabel,
        Label statusLabel,
        Label roundResultLabel,
        Button playAgainBtn,
        Button hitBtn,
        Button standBtn,
        Button betBtn,
        Button undoBtn,
        StackPane startOverOverlay
    ) {
        var player = game.getHumanPlayer();
        int pocket = player.getMoney();
        int onTable = player.getCurrentBet();
        int displayTotal = pocket + onTable;
        int staged = stagedBet[0];
        GamePhase phase = game.getPhase();

        moneyLabel.setText(String.format("Wallet: $%d", displayTotal));

        if (onTable > 0) {
            bettingLabel.setText(String.format("Bet: $%d", onTable));
        } else if (phase == GamePhase.BETTING || phase == GamePhase.ROUND_OVER) {
            bettingLabel.setText(String.format("Betting: $%d", staged));
        } else {
            bettingLabel.setText("");
        }
        boolean showBettingLine = !bettingLabel.getText().isEmpty();
        bettingLabel.setVisible(showBettingLine);
        bettingLabel.setManaged(showBettingLine);

        boolean statusPhases =
                phase == GamePhase.BETTING
                        || phase == GamePhase.HUMAN_TURN
                        || phase == GamePhase.BOT1_TURN
                        || phase == GamePhase.BOT2_TURN
                        || phase == GamePhase.DEALER_TURN;
        statusLabel.setVisible(statusPhases);
        statusLabel.setManaged(statusPhases);
        if (statusPhases) {
            statusLabel.setText(game.getStatusMessage());
        } else {
            statusLabel.setText("");
        }

        boolean showRoundBanner = (phase == GamePhase.ROUND_OVER || phase == GamePhase.GAME_OVER);
        roundResultLabel.setVisible(showRoundBanner);
        roundResultLabel.setManaged(showRoundBanner);
        if (phase == GamePhase.ROUND_OVER) {
            roundResultLabel.setText(humanVersusDealerOutcome(game));
        } else if (phase == GamePhase.GAME_OVER) {
            roundResultLabel.setText(game.getStatusMessage());
        } else {
            roundResultLabel.setText("");
        }

        boolean humanTurn = (phase == GamePhase.HUMAN_TURN);
        boolean canStage = (phase == GamePhase.BETTING) || (phase == GamePhase.ROUND_OVER);
        boolean gameOver = (phase == GamePhase.GAME_OVER);

        // Update button states
        hitBtn.setDisable(gameOver || !humanTurn);
        standBtn.setDisable(gameOver || !humanTurn);
        betBtn.setDisable(gameOver || !canStage || pocket <= 0 || staged <= 0);
        undoBtn.setDisable(gameOver || !canStage || staged <= 0);

        playAgainBtn.setDisable(gameOver || phase != GamePhase.ROUND_OVER);

        boolean showStartOver =
                displayTotal <= 0
                        && (phase == GamePhase.BETTING
                                || phase == GamePhase.ROUND_OVER
                                || phase == GamePhase.GAME_OVER);
        startOverOverlay.setVisible(showStartOver);
        startOverOverlay.setManaged(showStartOver);

        // Refresh table area
        refreshTableArea(tableArea, game, showSeatTitleRow(phase));
    }

    /** Name + hand total only after a bet starts the round (not while betting / between rounds). */
    private static boolean showSeatTitleRow(GamePhase phase) {
        return phase == GamePhase.HUMAN_TURN
                || phase == GamePhase.BOT1_TURN
                || phase == GamePhase.BOT2_TURN
                || phase == GamePhase.DEALER_TURN
                || phase == GamePhase.ROUND_OVER;
    }

    private static void refreshTableArea(VBox tableArea, BlackJackGame game, boolean showSeatTitles) {
        tableArea.getChildren().clear();
        tableArea.setAlignment(Pos.TOP_CENTER);

        // Card ImageView size (tweak w/h to match table art; ~25% larger than before)
        double w = 96, h = 132;

        // Dealer
        VBox dealerBlock = new VBox(6);
        dealerBlock.setAlignment(Pos.CENTER);
        if (showSeatTitles) {
            Label dealerName = new Label("Dealer");
            dealerName.getStyleClass().add("blackjack-player-name");
            Label dealerValue = new Label(dealerHandValueCaption(game));
            dealerValue.getStyleClass().add("blackjack-hand-value");
            HBox dealerHeader = new HBox(8, dealerName, dealerValue);
            dealerHeader.setAlignment(Pos.CENTER);
            dealerBlock.getChildren().addAll(dealerHeader, CardImageUtil.dealerRow(game, w, h));
        } else {
            dealerBlock.getChildren().add(CardImageUtil.dealerRow(game, w, h));
        }
        dealerBlock.setPadding(new Insets(0, 0, 32, 0));

        // Three equal-width columns so each hand stays centered over the felt seat art (PNG rectangles).
        HBox seatsRow = new HBox(0);
        seatsRow.setAlignment(Pos.BOTTOM_CENTER);
        seatsRow.setMinHeight(200);
        seatsRow.setPadding(new Insets(10, 0, 0, 0));

        String bot1WalletLine =
                showSeatTitles
                        ? String.format(
                                "Wallet: $%d",
                                game.getBot1().getMoney() + game.getBot1().getCurrentBet())
                        : null;
        String bot2WalletLine =
                showSeatTitles
                        ? String.format(
                                "Wallet: $%d",
                                game.getBot2().getMoney() + game.getBot2().getCurrentBet())
                        : null;

        StackPane seatBot1 =
                playerSeatCell(
                        game.getBot1().getName(),
                        game.getBot1().getHand(),
                        w,
                        h,
                        showSeatTitles,
                        bot1WalletLine);
        HBox.setHgrow(seatBot1, Priority.ALWAYS);
        seatBot1.setMinWidth(0);

        StackPane seatHuman =
                playerSeatCell(
                        game.getHumanPlayer().getName(),
                        game.getHumanPlayer().getHand(),
                        w,
                        h,
                        showSeatTitles,
                        null);
        HBox.setHgrow(seatHuman, Priority.ALWAYS);
        seatHuman.setMinWidth(0);

        StackPane seatBot2 =
                playerSeatCell(
                        game.getBot2().getName(),
                        game.getBot2().getHand(),
                        w,
                        h,
                        showSeatTitles,
                        bot2WalletLine);
        HBox.setHgrow(seatBot2, Priority.ALWAYS);
        seatBot2.setMinWidth(0);

        seatsRow.getChildren().addAll(seatBot1, seatHuman, seatBot2);

        tableArea.getChildren().addAll(dealerBlock, seatsRow);

        tableArea.setSpacing(18);
        tableArea.setPadding(new Insets(220, 48, 0, 48));
    }

    /**
     * Horizontally centers a fixed-width hand row: elastic regions shrink equally as the hand grows,
     * so the fan stays on the same axis (no drift vs the felt rectangles).
     */
    private static HBox handRowCenteredInSeat(Pane handPane) {
        Region west = new Region();
        Region east = new Region();
        HBox.setHgrow(west, Priority.ALWAYS);
        HBox.setHgrow(east, Priority.ALWAYS);
        west.setMinWidth(0);
        east.setMinWidth(0);
        HBox row = new HBox(west, handPane, east);
        row.setAlignment(Pos.CENTER);
        row.setMinWidth(0);
        row.setMaxWidth(Double.MAX_VALUE);
        return row;
    }

    /**
     * One seat: cards on top; under them (when {@code showSeatTitles}) same name + value row as dealer,
     * using {@code blackjack-player-name} / {@code blackjack-hand-value}. Optional {@code balanceLine}
     * (e.g. bot wallet) is centered on the line below.
     */
    private static StackPane playerSeatCell(
            String name,
            Hand hand,
            double w,
            double h,
            boolean showSeatTitles,
            String balanceLine) {
        StackPane cell = new StackPane();
        Pane cards = CardImageUtil.handRow(hand, w, h);
        HBox centeredRow = handRowCenteredInSeat(cards);
        VBox seat = new VBox(6);
        seat.setAlignment(Pos.TOP_CENTER);
        seat.getChildren().add(centeredRow);
        if (showSeatTitles) {
            Label nameLbl = new Label(name);
            nameLbl.getStyleClass().add("blackjack-player-name");
            Label valueLbl = new Label(seatHandValueCaption(hand));
            valueLbl.getStyleClass().add("blackjack-hand-value");
            HBox titleRow = new HBox(8, nameLbl, valueLbl);
            titleRow.setAlignment(Pos.CENTER);
            if (balanceLine != null && !balanceLine.isEmpty()) {
                Label balLbl = new Label(balanceLine);
                balLbl.getStyleClass().add("blackjack-seat-balance");
                VBox titleBlock = new VBox(4, titleRow, balLbl);
                titleBlock.setAlignment(Pos.TOP_CENTER);
                seat.getChildren().add(titleBlock);
            } else {
                seat.getChildren().add(titleRow);
            }
        }
        seat.setMaxWidth(Double.MAX_VALUE);
        seat.prefWidthProperty().bind(cell.widthProperty());
        cell.getChildren().add(seat);
        StackPane.setAlignment(seat, Pos.BOTTOM_CENTER);
        return cell;
    }

    private static String humanVersusDealerOutcome(BlackJackGame game) {
        var human = game.getHumanPlayer();
        var dealer = game.getDealer();
        if (human.isBust()) {
            return "Dealer wins! You busted!";
        }
        if (dealer.isBust()) {
            return "You win! Dealer busted!";
        }
        int hv = human.getHandValue();
        int dv = dealer.getHandValue();
        if (hv > dv) {
            return "You Win!";
        }
        if (hv < dv) {
            return "Dealer wins!";
        }
        return "Push -_-";
    }

    private static String seatHandValueCaption(Hand hand) {
        if (hand.getCards().isEmpty()) {
            return "";
        }
        return Integer.toString(hand.getBestValue());
    }

    /** Matches visible cards: up-card only while hole is hidden; full total otherwise. */
    private static String dealerHandValueCaption(BlackJackGame game) {
        var hand = game.getDealer().getHand();
        var cards = hand.getCards();
        if (cards.isEmpty()) {
            return "";
        }
        if (CardImageUtil.isDealerHoleHidden(game)) {
            return cards.get(0).getValue() + " + ?";
        }
        return Integer.toString(hand.getBestValue());
    }
}
