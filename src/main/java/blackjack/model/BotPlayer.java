package blackjack.model;

import blackjack.logic.BotStrategy;

public class BotPlayer extends Participant {
    private BotStrategy strategy;

    public BotPlayer(String name, int startingMoney, BotStrategy strategy) {
        super(name, startingMoney);
        this.strategy = strategy;
    }

    public boolean shouldHit() {
        return strategy.shouldHit(getHand());
    }
}