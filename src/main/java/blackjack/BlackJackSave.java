package blackjack;

import blackjack.logic.BlackJackGame;

public class BlackJackSave {

    private static BlackJackGame currentGame;

    public static void register(BlackJackGame game) {
        currentGame = game;
    }

    /** Active table game for toolbar / resume helpers (may be null). */
    public static BlackJackGame getCurrentGame() {
        return currentGame;
    }

    public static void clear(){
        currentGame = null;
    }
    public static boolean canSave(){
        return currentGame != null;
    }
    public static String getSaveString(){
        if(currentGame == null){
            return null;
        }
        return currentGame.exportEncryptedSaveState();
    }
}