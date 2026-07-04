package blackjack.logic;

import blackjack.model.Card;
import blackjack.model.Hand;
import blackjack.model.Rank;
import blackjack.model.Suit;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SimpleStrategyTest {

    @Test
    void shouldHit_whenBelowThreshold() {
        SimpleStrategy bot = new SimpleStrategy(17);
        Hand hand = handWithValue16();
        assertTrue(bot.shouldHit(hand));
    }

    @Test
    void shouldStand_whenAtOrAboveThreshold() {
        SimpleStrategy bot = new SimpleStrategy(17);
        Hand hand = new Hand();
        hand.addCard(new Card(Suit.C, Rank.NINE));
        hand.addCard(new Card(Suit.C, Rank.EIGHT));
        assertFalse(bot.shouldHit(hand));
    }

    private static Hand handWithValue16() {
        Hand hand = new Hand();
        hand.addCard(new Card(Suit.S, Rank.TEN));
        hand.addCard(new Card(Suit.S, Rank.SIX));
        return hand;
    }
}
