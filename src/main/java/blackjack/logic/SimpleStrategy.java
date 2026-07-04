package blackjack.logic;

import blackjack.model.Hand;

public class SimpleStrategy implements BotStrategy {
    private int threshold;

    public SimpleStrategy(int threshold) {
        this.threshold = threshold;
    }

    @Override
    public boolean shouldHit(Hand hand) {
        return hand.getBestValue() < threshold;
    }
}
