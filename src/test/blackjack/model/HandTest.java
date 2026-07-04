package blackjack.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class HandTest {

    @Test
    void bestValue_countsAceAsElevenWhenItDoesNotBust() {
        Hand hand = new Hand();
        hand.addCard(new Card(Suit.H, Rank.ACE));
        hand.addCard(new Card(Suit.H, Rank.KING));
        assertEquals(21, hand.getBestValue());
        assertFalse(hand.isBust());
    }

    @Test
    void bestValue_softAceAdjustsWhenWouldBust() {
        Hand hand = new Hand();
        hand.addCard(new Card(Suit.H, Rank.ACE));
        hand.addCard(new Card(Suit.H, Rank.SIX));
        assertEquals(17, hand.getBestValue());
        hand.addCard(new Card(Suit.D, Rank.KING));
        assertEquals(17, hand.getBestValue());
        assertFalse(hand.isBust());
    }

    @Test
    void isBust_whenTotalOverTwentyOne() {
        Hand hand = new Hand();
        hand.addCard(new Card(Suit.H, Rank.KING));
        hand.addCard(new Card(Suit.H, Rank.QUEEN));
        hand.addCard(new Card(Suit.H, Rank.JACK));
        assertTrue(hand.isBust());
    }
}
