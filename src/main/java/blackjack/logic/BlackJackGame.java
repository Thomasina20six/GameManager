package blackjack.logic;

import blackjack.model.*;
import java.util.List;
import java.util.ArrayList;
import shared.utils.EncryptionUtil;

public class BlackJackGame {
    public static final int STARTING_BANKROLL = 1000;

    /** Prepended to AES-encrypted save blobs (clipboard / shared save text). */
    public static final String ENCRYPTED_SAVE_PREFIX = "BJENC1:";

    private HumanPlayer humanPlayer;
    private BotPlayer bot1;
    private BotPlayer bot2;
    private Dealer dealer;
    private Deck deck;
    private GamePhase phase;
    private String statusMessage;

    public BlackJackGame(String username) {
        humanPlayer = new HumanPlayer(username, STARTING_BANKROLL);
        bot1 = new BotPlayer("(Computer 1)", STARTING_BANKROLL, new SimpleStrategy(16));
        bot2 = new BotPlayer("(Computer 2)", STARTING_BANKROLL, new SimpleStrategy(17));
        dealer = new Dealer();
        deck = new Deck();
        phase = GamePhase.BETTING;
        statusMessage = "BETTING";
    }

    public void startNewRound(int humanBet) {
        if (humanPlayer.getMoney() <= 0) {
            statusMessage = "Game over: you're out of money.";
            phase = GamePhase.GAME_OVER;
            return;
        }

        deck = new Deck();
        humanPlayer.clearHand();
        bot1.clearHand();
        bot2.clearHand();
        dealer.clearHand();

        int bot1Bet = Math.min(10, bot1.getMoney());
        int bot2Bet = Math.min(10, bot2.getMoney());

        if (!humanPlayer.canBet(humanBet)) {
            statusMessage = "Invalid bet amount.";
            phase = GamePhase.BETTING;
            return;
        }

        // Only place bets after all validations pass, so we don't partially deduct money.
        humanPlayer.placeBet(humanBet);
        if (bot1Bet > 0) {
            bot1.placeBet(bot1Bet);
        }
        if (bot2Bet > 0) {
            bot2.placeBet(bot2Bet);
        }

        if (humanPlayer.getCurrentBet() == 0) {
            statusMessage = "Bet could not be placed.";
            phase = GamePhase.BETTING;
            return;
        }

        humanPlayer.addCard(deck.drawCard());
        humanPlayer.addCard(deck.drawCard());

        if (bot1.getCurrentBet() > 0) {
            bot1.addCard(deck.drawCard());
            bot1.addCard(deck.drawCard());
        }

        if (bot2.getCurrentBet() > 0) {
            bot2.addCard(deck.drawCard());
            bot2.addCard(deck.drawCard());
        }

        dealer.addCard(deck.drawCard());
        dealer.addCard(deck.drawCard());

        phase = GamePhase.HUMAN_TURN;
        statusMessage = "Your turn";
        if (bot1.getMoney() <= 0) {
            statusMessage += " Bot 1 is out of money and will sit out.";
        }
        if (bot2.getMoney() <= 0) {
            statusMessage += " Bot 2 is out of money and will sit out.";
        }
    }

    public void humanHit() {
        if (phase != GamePhase.HUMAN_TURN) {
            return;
        }
        humanPlayer.addCard(deck.drawCard());

        if (humanPlayer.isBust()) {
            statusMessage = "You busted!";
            phase = GamePhase.BOT1_TURN;
        } else {
            statusMessage = "You drew a card.";
        }
    }

    public void humanStand() {
        if (phase != GamePhase.HUMAN_TURN) {
            return;
        }

        humanPlayer.stand();
        phase = GamePhase.BOT1_TURN;
        statusMessage = "You stand.";
    }

    /**
     * One decision for bot 1: draw a card if strategy says hit, otherwise stand and advance phase.
     * Call repeatedly while {@link #getPhase()} is {@code BOT1_TURN} to resolve the full turn.
     */
    public void bot1PlayNextStep() {
        if (phase != GamePhase.BOT1_TURN) {
            return;
        }
        if (bot1.getCurrentBet() == 0) {
            statusMessage = bot1.getName() + " sits out.";
            phase = GamePhase.BOT2_TURN;
            return;
        }
        if (bot1.isBust()) {
            statusMessage = bot1.getName() + " busted!";
            bot1.stand();
            phase = GamePhase.BOT2_TURN;
            return;
        }
        if (bot1.shouldHit()) {
            bot1.addCard(deck.drawCard());
            if (bot1.isBust()) {
                statusMessage = bot1.getName() + " busted!";
                bot1.stand();
                phase = GamePhase.BOT2_TURN;
            } else {
                statusMessage = bot1.getName() + " hits.";
            }
            return;
        }
        bot1.stand();
        statusMessage = bot1.getName() + " stands.";
        phase = GamePhase.BOT2_TURN;
    }

    public void bot2PlayNextStep() {
        if (phase != GamePhase.BOT2_TURN) {
            return;
        }
        if (bot2.getCurrentBet() == 0) {
            statusMessage = bot2.getName() + " sits out.";
            phase = GamePhase.DEALER_TURN;
            return;
        }
        if (bot2.isBust()) {
            statusMessage = bot2.getName() + " busted!";
            bot2.stand();
            phase = GamePhase.DEALER_TURN;
            return;
        }
        if (bot2.shouldHit()) {
            bot2.addCard(deck.drawCard());
            if (bot2.isBust()) {
                statusMessage = bot2.getName() + " busted!";
                bot2.stand();
                phase = GamePhase.DEALER_TURN;
            } else {
                statusMessage = bot2.getName() + " hits.";
            }
            return;
        }
        bot2.stand();
        statusMessage = bot2.getName() + " stands.";
        phase = GamePhase.DEALER_TURN;
    }

    public void dealerPlayNextStep() {
        if (phase != GamePhase.DEALER_TURN) {
            return;
        }
        if (humanPlayer.isBust() && bot1.isBust() && bot2.isBust()) {
            statusMessage = "All players busted!";
            resolveRound();
            phase = GamePhase.ROUND_OVER;
            return;
        }
        if (dealer.isBust()) {
            statusMessage = "Dealer busted!";
            resolveRound();
            phase = GamePhase.ROUND_OVER;
            return;
        }
        if (dealer.shouldHit()) {
            dealer.addCard(deck.drawCard());
            if (dealer.isBust()) {
                statusMessage = "Dealer busted!";
                resolveRound();
                phase = GamePhase.ROUND_OVER;
            } else {
                statusMessage = "Dealer hits.";
            }
            return;
        }
        statusMessage = "Dealer stands.";
        resolveRound();
        phase = GamePhase.ROUND_OVER;
    }

    /** Loops {@link #dealerPlayNextStep()} until the dealer phase ends; used by tests. */
    public void playDealerTurn() {
        while (phase == GamePhase.DEALER_TURN) {
            dealerPlayNextStep();
        }
    }

    /** For UI turn banners before automated steps. */
    public void setStatusMessage(String message) {
        this.statusMessage = message;
    }

    public void resolveRound() {
        resolveParticipant(humanPlayer);
        if (bot1.getCurrentBet() > 0) {
            resolveParticipant(bot1);
        }
        if (bot2.getCurrentBet() > 0) {
            resolveParticipant(bot2);
        }

        statusMessage = statusMessage + " Round over.";
		
        if (humanPlayer.getMoney() <= 0) {
            statusMessage += " Game over: you're out of money.";
            phase = GamePhase.GAME_OVER;
        }
    }

    /**
     * Clears all hands and returns to betting (after a finished round). Money is unchanged.
     */
    public void clearTableForNextBet() {
        if (phase == GamePhase.GAME_OVER) {
            return;
        }
        humanPlayer.clearHand();
        bot1.clearHand();
        bot2.clearHand();
        dealer.clearHand();
        deck = new Deck();
        phase = GamePhase.BETTING;
        statusMessage = "Place your bet.";
    }

    /**
     * New deck, empty hands, and human + both bots set to {@link #STARTING_BANKROLL}. Dealer hand cleared.
     * Use when the human is broke and chooses to continue.
     */
    public void startOverWithFreshBankrolls() {
        humanPlayer.clearHand();
        bot1.clearHand();
        bot2.clearHand();
        dealer.clearHand();
        humanPlayer.resetBankroll(STARTING_BANKROLL);
        bot1.resetBankroll(STARTING_BANKROLL);
        bot2.resetBankroll(STARTING_BANKROLL);
        deck = new Deck();
        phase = GamePhase.BETTING;
        statusMessage = "Place your bet.";
    }

    private void resolveParticipant(Participant participant) {
        if (participant.isBust()) {
            participant.loseBet();
            return;
        }

        if (dealer.isBust()) {
            participant.winBet();
            return;
        }

        int playerValue = participant.getHandValue();
        int dealerValue = dealer.getHandValue();

        if (playerValue > dealerValue) {
            participant.winBet();
        } else if (playerValue < dealerValue) {
            participant.loseBet();
        } else {
            participant.tieBet();
        }
    }

    // Export methods from now
    private String exportDeck() {
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < deck.size(); i++) {
            sb.append(cardToCode(deck.getCards().get(i)));

            if (i < deck.size() - 1) {
                sb.append(",");
            }
        }
        return sb.toString();
    }
    private String exportHand(Participant participant) {
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < participant.getHand().getCards().size(); i++) {
            sb.append(cardToCode(participant.getHand().getCards().get(i)));

            if (i < participant.getHand().getCards().size() - 1) {
                sb.append(",");
            }
        }

        return sb.toString();
    }

    private String cardToCode(Card card) {
        return card.toString();
    }

    public String exportSaveState() {
        StringBuilder sb = new StringBuilder();

        sb.append("BJ1").append("|"); // version marker
        sb.append(humanPlayer.getName()).append("|");
        sb.append(phase.name()).append("|");
        sb.append(statusMessage).append("|");

        sb.append(humanPlayer.getMoney()).append("|");
        sb.append(humanPlayer.getCurrentBet()).append("|");
        sb.append(exportHand(humanPlayer)).append("|");

        sb.append(bot1.getMoney()).append("|");
        sb.append(bot1.getCurrentBet()).append("|");
        sb.append(exportHand(bot1)).append("|");

        sb.append(bot2.getMoney()).append("|");
        sb.append(bot2.getCurrentBet()).append("|");
        sb.append(exportHand(bot2)).append("|");

        sb.append(exportHand(dealer)).append("|");

        sb.append(exportDeck());

        return sb.toString();
    }

    /**
     * Save text for the player to copy: same data as {@link #exportSaveState()} but encrypted so the file
     * does not reveal card codes, names, or money in plain text.
     */
    public String exportEncryptedSaveState() {
        return ENCRYPTED_SAVE_PREFIX + EncryptionUtil.encrypt(exportSaveState());
    }

    // getter methods
    public HumanPlayer getHumanPlayer() {
        return humanPlayer;
    }

    public BotPlayer getBot1() {
        return bot1;
    }

    public BotPlayer getBot2() {
        return bot2;
    }

    public Dealer getDealer() {
        return dealer;
    }

    public GamePhase getPhase() {
        return phase;
    }

    public String getStatusMessage() {
        return statusMessage;
    }

    private static Card parseCard(String code) {
        if (code == null || code.isEmpty()) {
            throw new IllegalArgumentException("Empty card code");
        }
        int star = code.indexOf('*');
        if (star < 1 || star == code.length() - 1) {
            throw new IllegalArgumentException("Bad card code: " + code);
        }
        String rankPart = code.substring(0, star);
        String suitPart = code.substring(star + 1);
        Rank r = Rank.valueOf(rankPart);
        Suit s = Suit.valueOf(suitPart);
        return new Card(s, r);
    }
    private static List<Card> parseCardList(String field) {
        List<Card> out = new ArrayList<>();
        if (field == null || field.isEmpty()) {
            return out;
        }
        for (String token : field.split(",", -1)) {
            if (!token.isEmpty()) {
                out.add(parseCard(token));
            }
        }
        return out;
    }
    public static BlackJackGame importFromSaveString(String save) {
        if (save == null) {
            throw new IllegalArgumentException("Save text is null.");
        }
        String trim = save.trim();
        if (trim.isEmpty()) {
            throw new IllegalArgumentException("Save text is empty.");
        }
        String pipePayload = trim;
        if (trim.startsWith(ENCRYPTED_SAVE_PREFIX)) {
            String b64 = trim.substring(ENCRYPTED_SAVE_PREFIX.length()).trim();
            try {
                pipePayload = EncryptionUtil.decrypt(b64);
            } catch (RuntimeException e) {
                throw new IllegalArgumentException("Could not read save (invalid or corrupted encrypted data).");
            }
        }
        return importFromPlainPipeSave(pipePayload);
    }

    private static BlackJackGame importFromPlainPipeSave(String trim) {
        String[] p = trim.split("\\|", -1);
        if (p.length != 15) {
            throw new IllegalArgumentException("Expected 15 fields (BJ1 format), got " + p.length);
        }
        if (!"BJ1".equals(p[0])) {
            throw new IllegalArgumentException("Unknown save version: " + p[0]);
        }
        String humanName = p[1];
        GamePhase loadedPhase = GamePhase.valueOf(p[2]);
        String loadedStatus = p[3];

        int hMoney = Integer.parseInt(p[4]);
        int hBet = Integer.parseInt(p[5]);
        List<Card> hHand = parseCardList(p[6]);

        int b1Money = Integer.parseInt(p[7]);
        int b1Bet = Integer.parseInt(p[8]);
        List<Card> b1Hand = parseCardList(p[9]);

        int b2Money = Integer.parseInt(p[10]);
        int b2Bet = Integer.parseInt(p[11]);
        List<Card> b2Hand = parseCardList(p[12]);

        List<Card> dealerHand = parseCardList(p[13]);
        List<Card> deckOrder = parseCardList(p[14]);
        BlackJackGame game = new BlackJackGame(humanName);
        game.phase = loadedPhase;
        game.statusMessage = loadedStatus;
        game.humanPlayer.restoreFromSave(hMoney, hBet, hHand);
        game.bot1.restoreFromSave(b1Money, b1Bet, b1Hand);
        game.bot2.restoreFromSave(b2Money, b2Bet, b2Hand);
        game.dealer.restoreFromSave(0, 0, dealerHand);
        game.deck.replaceOldDeck(deckOrder);
        return game;
    }
}
