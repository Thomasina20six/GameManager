package blackjack.model;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class DeckTest {

    @Test
    void drawCard_returnsCardsInListOrder() {
        Card first = new Card(Suit.H, Rank.TWO);
        Card second = new Card(Suit.H, Rank.THREE);
        Card third = new Card(Suit.H, Rank.FOUR);
        Deck deck = new Deck();
        deck.replaceOldDeck(Arrays.asList(first, second, third));

        assertEquals(3, deck.size());
        assertEquals(first, deck.drawCard());
        assertEquals(second, deck.drawCard());
        assertEquals(third, deck.drawCard());
        assertEquals(0, deck.size());
    }

    @Test
    void replaceOldDeck_restoresExactDrawSequence() {
        Deck deck = new Deck();
        List<Card> ordered = Arrays.asList(
                new Card(Suit.D, Rank.ACE),
                new Card(Suit.D, Rank.KING));
        deck.replaceOldDeck(ordered);

        Card drawn1 = deck.drawCard();
        assertEquals(Rank.ACE, drawn1.getRank());
        assertEquals(Suit.D, drawn1.getSuit());
        Card drawn2 = deck.drawCard();
        assertEquals(Rank.KING, drawn2.getRank());
    }
}
