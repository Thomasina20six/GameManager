package shared;

import javafx.scene.media.AudioClip;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

public class SoundManager {
    /** Which looping background track should play when unmuted (after minimize or mute toggle). */
    public enum ActiveBgm {
        NONE,
        BLACKJACK
    }

    // mouse click sound effect
    private static final AudioClip clickSound =
            new AudioClip(SoundManager.class.getResource("/audios/mouseclick.wav").toExternalForm());

    // blackjack background music
    private static final Media blackjackBackgroundMusic =
            new Media(SoundManager.class.getResource("/audios/blackjack_bg_music.mp3").toExternalForm());
    private static final MediaPlayer blackjackBackgroundMediaPlayer = new MediaPlayer(blackjackBackgroundMusic);

    // mute
    private static boolean muted = false;
    private static boolean windowMinimized = false;
    private static boolean pausedDueToMinimize = false;

    private static ActiveBgm activeBgm = ActiveBgm.NONE;

    public static void setMuted(boolean m) {
        muted = m;
        if (muted) {
            blackjackBackgroundMediaPlayer.pause();
        } else {
            if (!windowMinimized) {
                resumeBackgroundMusicForActiveContext();
            }
        }
    }

    public static boolean isMuted() {
        return muted;
    }

    /** Call when leaving all gameplay (e.g. main menu) so unmute does not restart old BGM. */
    public static void clearActiveBgm() {
        activeBgm = ActiveBgm.NONE;
    }

    private static boolean anyBackgroundPlaying() {
        return blackjackBackgroundMediaPlayer.getStatus() == MediaPlayer.Status.PLAYING;
    }

    private static void resumeBackgroundMusicForActiveContext() {
        if (isMuted()) {
            return;
        }
        switch (activeBgm) {
            case BLACKJACK:
                blackjackBackgroundMediaPlayer.setCycleCount(MediaPlayer.INDEFINITE);
                blackjackBackgroundMediaPlayer.setVolume(0.5);
                blackjackBackgroundMediaPlayer.play();
                break;
            case NONE:
            default:
                break;
        }
    }

    public static void init() {
        // Warm up audio engine
        clickSound.play();
    }

    public static void playClick() {
        if (!isMuted()) {
            clickSound.play();
        }
    }

    public static void playBlackjackBackgroundMusic() {
        activeBgm = ActiveBgm.BLACKJACK;
        if (!isMuted()) {
            blackjackBackgroundMediaPlayer.setCycleCount(MediaPlayer.INDEFINITE);
            blackjackBackgroundMediaPlayer.setVolume(0.5);
            blackjackBackgroundMediaPlayer.play();
        }
    }

    public static void stopBlackjackBackgroundMusic() {
        blackjackBackgroundMediaPlayer.stop();
        pausedDueToMinimize = false;
    }

    // pause background music when the window is minimized
    public static void notifyWindowIconified(boolean iconified) {
        windowMinimized = iconified;
        if (iconified) {
            if (anyBackgroundPlaying()) {
                blackjackBackgroundMediaPlayer.pause();
                if (!isMuted()) {
                    pausedDueToMinimize = true;
                }
            }
        } else {
            if (pausedDueToMinimize && !isMuted()) {
                resumeBackgroundMusicForActiveContext();
            }
            pausedDueToMinimize = false;
        }
    }
}
