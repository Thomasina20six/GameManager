package blackjack.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DealerTest {

    @Test
    void shouldHit_whenHandBelowHardSeventeen() {
        Dealer dealer = new Dealer();
        dealer.addCard(new Card(Suit.H, Rank.TEN));
        dealer.addCard(new Card(Suit.H, Rank.SIX));
        assertTrue(dealer.shouldHit());
    }

    @Test
    void shouldStand_onHardSeventeen() {
        Dealer dealer = new Dealer();
        dealer.addCard(new Card(Suit.H, Rank.TEN));
        dealer.addCard(new Card(Suit.H, Rank.SEVEN));
        assertFalse(dealer.shouldHit());
    }

    @Test
    void shouldHit_onSoftSeventeen_dealerHitsSoftSeventeenPerSpec() {
        Dealer dealer = new Dealer();
        dealer.addCard(new Card(Suit.H, Rank.ACE));
        dealer.addCard(new Card(Suit.H, Rank.SIX));
        assertTrue(dealer.shouldHit());
    }
}
