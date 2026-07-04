package blackjack.logic;

import blackjack.model.GamePhase;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class BlackJackGameRoundResolutionTest {

    @Test
    void dealerStands_humanBeatsDealer_humanWinsBet() {
        // Human: 21 (A+K), Dealer: 20 (10+Q). Human has $900 with a $100 bet already placed.
        String save =
                "BJ1|bob|DEALER_TURN|Dealer turn|900|100|ACE*H,KING*H|1000|0||1000|0||TEN*D,QUEEN*D|";

        BlackJackGame game = BlackJackGame.importFromSaveString(save);
        game.playDealerTurn();

        assertEquals(GamePhase.ROUND_OVER, game.getPhase());
        assertEquals(1100, game.getHumanPlayer().getMoney()); // + (100 * 2) from winBet()
        assertEquals(0, game.getHumanPlayer().getCurrentBet());
        assertTrue(game.getStatusMessage().contains("Round over."));
    }

    @Test
    void dealerStands_push_tieReturnsStake() {
        // Human: 20 (10+Q), Dealer: 20 (10+Q). Human has $900 with a $100 bet already placed.
        String save =
                "BJ1|bob|DEALER_TURN|Dealer turn|900|100|TEN*H,QUEEN*H|1000|0||1000|0||TEN*D,QUEEN*D|";

        BlackJackGame game = BlackJackGame.importFromSaveString(save);
        game.playDealerTurn();

        assertEquals(GamePhase.ROUND_OVER, game.getPhase());
        assertEquals(1000, game.getHumanPlayer().getMoney()); // tieBet() refunds original stake
        assertEquals(0, game.getHumanPlayer().getCurrentBet());
    }

    @Test
    void dealerAlreadyBusts_anyNonBustHumanWins() {
        // Dealer: 22 (10+Q+2), Human: 18 (10+8). Human has $900 with a $100 bet already placed.
        String save =
                "BJ1|bob|DEALER_TURN|Dealer turn|900|100|TEN*H,EIGHT*H|1000|0||1000|0||TEN*D,QUEEN*D,TWO*H|";

        BlackJackGame game = BlackJackGame.importFromSaveString(save);
        game.playDealerTurn();

        assertEquals(GamePhase.ROUND_OVER, game.getPhase());
        assertEquals(1100, game.getHumanPlayer().getMoney());
        assertEquals(0, game.getHumanPlayer().getCurrentBet());
        assertTrue(game.getStatusMessage().contains("Dealer busted!"));
    }
}

