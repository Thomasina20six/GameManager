package blackjack.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class HumanPlayerTest {

    @Test
    void placeBet_deductsMoneyAndSetsCurrentBet() {
        HumanPlayer p = new HumanPlayer("Test", 1000);
        assertTrue(p.placeBet(100));
        assertEquals(900, p.getMoney());
        assertEquals(100, p.getCurrentBet());
    }

    @Test
    void placeBet_rejectsInvalidAmount() {
        HumanPlayer p = new HumanPlayer("Test", 100);
        assertFalse(p.placeBet(0));
        assertFalse(p.placeBet(-10));
        assertFalse(p.placeBet(150));
        assertEquals(100, p.getMoney());
        assertEquals(0, p.getCurrentBet());
    }

    @Test
    void winBet_returnsStakePlusWinnings() {
        HumanPlayer p = new HumanPlayer("Test", 1000);
        p.placeBet(100);
        p.winBet();
        assertEquals(1100, p.getMoney());
        assertEquals(0, p.getCurrentBet());
    }

    @Test
    void loseBet_clearsBetWithoutRefund() {
        HumanPlayer p = new HumanPlayer("Test", 1000);
        p.placeBet(100);
        p.loseBet();
        assertEquals(900, p.getMoney());
        assertEquals(0, p.getCurrentBet());
    }

    @Test
    void tieBet_returnsOriginalStake() {
        HumanPlayer p = new HumanPlayer("Test", 1000);
        p.placeBet(100);
        p.tieBet();
        assertEquals(1000, p.getMoney());
        assertEquals(0, p.getCurrentBet());
    }
}
