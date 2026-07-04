package blackjack.logic;

import blackjack.model.GamePhase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class BlackJackGameStartRoundTest {

    private BlackJackGame game;

    @BeforeEach
    void newGame() {
        game = new BlackJackGame("tester");
    }

    @Test
    void startNewRound_zeroOrNegativeBet_staysInBetting_doesNotChargePlayer() {
        int before = game.getHumanPlayer().getMoney();
        game.startNewRound(0);
        assertEquals(GamePhase.BETTING, game.getPhase());
        assertEquals(before, game.getHumanPlayer().getMoney());
        assertEquals(0, game.getHumanPlayer().getCurrentBet());

        game.startNewRound(-25);
        assertEquals(GamePhase.BETTING, game.getPhase());
        assertEquals(before, game.getHumanPlayer().getMoney());
    }

    @Test
    void startNewRound_betAboveBalance_staysInBetting() {
        game.startNewRound(BlackJackGame.STARTING_BANKROLL + 1);
        assertEquals(GamePhase.BETTING, game.getPhase());
        assertEquals(BlackJackGame.STARTING_BANKROLL, game.getHumanPlayer().getMoney());
        assertEquals(0, game.getHumanPlayer().getCurrentBet());
    }

    @Test
    void startNewRound_validBet_movesToHumanTurn_andDealsTwoCardsToEachActiveSeat() {
        int humanBet = 100;
        game.startNewRound(humanBet);

        assertEquals(GamePhase.HUMAN_TURN, game.getPhase());
        assertEquals("Your turn", game.getStatusMessage());

        assertEquals(2, game.getHumanPlayer().getHand().getCards().size());
        assertEquals(2, game.getDealer().getHand().getCards().size());
        assertEquals(2, game.getBot1().getHand().getCards().size());
        assertEquals(2, game.getBot2().getHand().getCards().size());

        assertEquals(humanBet, game.getHumanPlayer().getCurrentBet());
        assertTrue(game.getHumanPlayer().getMoney() < BlackJackGame.STARTING_BANKROLL);
    }

    @Test
    void humanHit_beforeRoundStarted_doesNotAddCards() {
        game.humanHit();
        assertEquals(GamePhase.BETTING, game.getPhase());
        assertEquals(0, game.getHumanPlayer().getHand().getCards().size());
    }
}
