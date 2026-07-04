package blackjack.model;

public class Dealer extends Participant {
    public Dealer() {
        super("Dealer", 0);
    }

    public boolean shouldHit() {
        return getHandValue() < 17 || getHand().isSoft17();
    }
}