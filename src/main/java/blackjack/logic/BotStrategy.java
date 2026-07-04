package blackjack.logic;

import blackjack.model.Hand;

public interface BotStrategy {
    boolean shouldHit(Hand hand);
}
