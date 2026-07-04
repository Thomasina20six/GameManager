package blackjack.logic;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class BlackJackGameEncryptedSaveTest {

    private static final String SAMPLE_PLAIN_SAVE =
            "BJ1|bob|HUMAN_TURN|Your turn|900|100|ACE*H|1000|0||1000|0||KING*D|TWO*C,THREE*C";

    @Test
    void exportEncryptedThenImport_roundTripsBackToSamePlainState() {
        BlackJackGame game = BlackJackGame.importFromSaveString(SAMPLE_PLAIN_SAVE);

        String encrypted = game.exportEncryptedSaveState();
        assertTrue(encrypted.startsWith(BlackJackGame.ENCRYPTED_SAVE_PREFIX));

        BlackJackGame loaded = BlackJackGame.importFromSaveString(encrypted);
        assertEquals(SAMPLE_PLAIN_SAVE, loaded.exportSaveState());
    }

    @Test
    void importFromSaveString_invalidEncryptedPayload_throwsHelpfulError() {
        String bad = BlackJackGame.ENCRYPTED_SAVE_PREFIX + "not-base64";
        assertThrows(IllegalArgumentException.class, () -> BlackJackGame.importFromSaveString(bad));
    }
}

