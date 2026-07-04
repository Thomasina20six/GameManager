package blackjack.logic;

import blackjack.model.GamePhase;
import blackjack.model.Rank;
import blackjack.model.Suit;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class BlackJackGameSaveStateTest {

    private static final String SAMPLE_PLAIN_SAVE =
            "BJ1|bob|HUMAN_TURN|Your turn|900|100|ACE*H|1000|0||1000|0||KING*D|TWO*C,THREE*C";

    @Test
    void importThenExport_roundTrip_matchesOriginalPlainSave() {
        BlackJackGame game = BlackJackGame.importFromSaveString(SAMPLE_PLAIN_SAVE);
        assertEquals(SAMPLE_PLAIN_SAVE, game.exportSaveState());
    }

    @Test
    void import_restoresPhaseMoneyAndHumanHand() {
        BlackJackGame game = BlackJackGame.importFromSaveString(SAMPLE_PLAIN_SAVE);
        assertEquals(GamePhase.HUMAN_TURN, game.getPhase());
        assertEquals(900, game.getHumanPlayer().getMoney());
        assertEquals(100, game.getHumanPlayer().getCurrentBet());
        assertEquals("bob", game.getHumanPlayer().getName());
        var cards = game.getHumanPlayer().getHand().getCards();
        assertEquals(1, cards.size());
        assertEquals(Rank.ACE, cards.get(0).getRank());
        assertEquals(Suit.H, cards.get(0).getSuit());
    }

    @Test
    void importFromSaveString_wrongVersionMarker_throws() {
        String bad = SAMPLE_PLAIN_SAVE.replace("BJ1", "XX1");
        assertThrows(IllegalArgumentException.class, () -> BlackJackGame.importFromSaveString(bad));
    }

    @Test
    void importFromSaveString_null_throws() {
        assertThrows(IllegalArgumentException.class, () -> BlackJackGame.importFromSaveString(null));
    }
}
