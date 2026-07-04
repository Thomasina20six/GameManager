package blackjack;

import blackjack.logic.BlackJackGame;
import blackjack.model.Card;
import blackjack.model.GamePhase;
import blackjack.model.Hand;
import blackjack.model.Rank;
import blackjack.model.Suit;
import java.util.ArrayList;
import java.util.List;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;

/** Builds card ImageViews and hand rows for the blackjack table UI. */
public final class CardImageUtil {

    private static String suitFolder(Suit s) {
        return switch (s) {
            case H -> "hearts";
            case D-> "diamonds";
            case C -> "clubs";
            case S -> "spades";
        };
    }

    private static String rankFile(Rank r) {
        return switch (r) {
            case ACE -> "a";
            case JACK -> "j";
            case QUEEN -> "q";
            case KING -> "k";
            case TEN -> "10";
            case TWO -> "2";
            case THREE -> "3";
            case FOUR -> "4";
            case FIVE -> "5";
            case SIX -> "6";
            case SEVEN -> "7";
            case EIGHT -> "8";
            case NINE -> "9";
        };
    }

    private static String cardImagePath(Card c) {
        return "/images/" + suitFolder(c.getSuit()) + "/" + rankFile(c.getRank()) + ".png";
    }

    public static ImageView cardImageView(Card c, double w, double h) {
        Image img = new Image(CardImageUtil.class.getResourceAsStream(cardImagePath(c)));
        ImageView iv = new ImageView(img);
        iv.setFitWidth(w);
        iv.setFitHeight(h);
        iv.setPreserveRatio(true);
        return iv;
    }

    public static ImageView cardBackView(double w, double h) {
        Image img = new Image(CardImageUtil.class.getResourceAsStream("/images/card-back.png"));
        ImageView iv = new ImageView(img);
        iv.setFitWidth(w);
        iv.setFitHeight(h);
        iv.setPreserveRatio(true);
        return iv;
    }

    private static final double CARD_OVERLAP_FRACTION = 0.25;

    private static Pane overlappedCardRow(double cardW, double cardH, List<ImageView> views) {
        Pane pane = new Pane();
        double step = cardW * CARD_OVERLAP_FRACTION;
        for (int i = 0; i < views.size(); i++) {
            ImageView iv = views.get(i);
            iv.setLayoutX(i * step);
            iv.setLayoutY(0);
            pane.getChildren().add(iv);
        }
        double w = views.isEmpty() ? 0 : (views.size() - 1) * step + cardW;
        pane.setPrefSize(Math.max(w, cardW), cardH);
        pane.setMaxSize(Math.max(w, cardW), cardH);
        return pane;
    }

    /** Overlapping cards (first card at back, last on top) to save horizontal space. */
    public static Pane handRow(Hand hand, double cardW, double cardH) {
        List<ImageView> views = new ArrayList<>();
        for (Card c : hand.getCards()) {
            views.add(cardImageView(c, cardW, cardH));
        }
        return overlappedCardRow(cardW, cardH, views);
    }

    /** True when the dealer's second card is face-down in the UI. */
    public static boolean isDealerHoleHidden(BlackJackGame game) {
        Hand hand = game.getDealer().getHand();
        List<Card> cards = hand.getCards();
        GamePhase phase = game.getPhase();
        return cards.size() >= 2
                && (phase == GamePhase.HUMAN_TURN
                || phase == GamePhase.BOT1_TURN
                || phase == GamePhase.BOT2_TURN
                || phase == GamePhase.BETTING);
    }

    public static Pane dealerRow(BlackJackGame game, double cardW, double cardH) {
        Hand hand = game.getDealer().getHand();
        List<Card> cards = hand.getCards();
        List<ImageView> views = new ArrayList<>();
        boolean hideHole = isDealerHoleHidden(game);
        for (int i = 0; i < cards.size(); i++) {
            if (hideHole && i == 1) {
                views.add(cardBackView(cardW, cardH));
            } else {
                views.add(cardImageView(cards.get(i), cardW, cardH));
            }
        }
        return overlappedCardRow(cardW, cardH, views);
    }
}
