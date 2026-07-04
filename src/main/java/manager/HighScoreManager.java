package manager;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Handles persistent high scores.
 *
 * File format:
 * username,game,score
 * Example:
 * CongSi,BLACKJACK,1200
 */
public class HighScoreManager {

    public static final String BLACKJACK = "BLACKJACK";
    public static final String SNAKE = "SNAKE";
    public static final String TTT = "Tic-Tac-Toe";
    public static final String WAM = "Wack-a-Mole";

    private static final String SCORES_FILE = "data/high_scores.txt";

    public static class ScoreEntry {
        private final String username;
        private final String game;
        private final int score;

        public ScoreEntry(String username, String game, int score) {
            this.username = username;
            this.game = game;
            this.score = score;
        }

        public String getUsername() {
            return username;
        }

        public String getGame() {
            return game;
        }

        public int getScore() {
            return score;
        }
    }

    /**
     * Ensures that the data folder and high score file exist.
     * If the file does not exist or is empty, default scores are created.
     */
    private static void ensureScoreFileExists() {
        try {
            File dataDir = new File("data");
            if (!dataDir.exists()) {
                dataDir.mkdirs();
            }

            File file = new File(SCORES_FILE);
            if (!file.exists()) {
                file.createNewFile();
                writeDefaultScores();
            } else if (file.length() == 0) {
                writeDefaultScores();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Writes default leaderboard scores into the score file.
     */
    private static void writeDefaultScores() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(SCORES_FILE, false))) {
            writer.write("DEFAULT," + BLACKJACK + ",1000");
            writer.newLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Loads all saved scores from the high score file.
     * return list of all score entries stored in the file
     */
    public static List<ScoreEntry> loadScores() {
        ensureScoreFileExists();
        List<ScoreEntry> scores = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(SCORES_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) {
                    continue;
                }

                String[] parts = line.split(",");
                if (parts.length != 3) {
                    continue;
                }

                String username = parts[0].trim();
                String game = parts[1].trim().toUpperCase();
                int score;
                try {
                    score = Integer.parseInt(parts[2].trim());
                } catch (NumberFormatException ex) {
                    continue;
                }

                scores.add(new ScoreEntry(username, game, score));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return scores;
    }

    /**
     * Returns the top scores for a specific game.
     * Scores are sorted from highest to lowest.
     * return list of top score entries
     */
    public static List<ScoreEntry> getTopScores(String game, int limit) {
        if (game == null || game.trim().isEmpty()) {
            return new ArrayList<>();
        }
        String gameName = game.toUpperCase();
        List<ScoreEntry> filtered = new ArrayList<>();

        for (ScoreEntry entry : loadScores()) {
            if (entry.getGame().equals(gameName)) {
                filtered.add(entry);
            }
        }

        filtered.sort(Comparator.comparingInt(ScoreEntry::getScore).reversed());

        if (filtered.size() > limit) {
            return new ArrayList<>(filtered.subList(0, limit));
        }
        return filtered;
    }

    /**
     * Saves a new score entry into the leaderboard file.
     * player username
     * game name
     * player score
     */
    public static void saveScore(String username, String game, int score) {
        ensureScoreFileExists();

        if (username == null || username.trim().isEmpty()) {
            username = "Guest";
        }

        if (game == null || game.trim().isEmpty()) {
            return;
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(SCORES_FILE, true))) {
            writer.write(username.trim() + "," + game.toUpperCase() + "," + score);
            writer.newLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
