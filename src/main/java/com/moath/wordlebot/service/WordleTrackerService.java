package com.moath.wordlebot.service;

import com.moath.wordlebot.common.Constants;
import com.moath.wordlebot.model.Player;
import com.moath.wordlebot.repository.PlayerRepository;
import com.moath.wordlebot.repository.WordleResultRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import com.moath.wordlebot.model.WordleResult;


@Service
@RequiredArgsConstructor
public class WordleTrackerService {
    private final PlayerRepository playerRepository;
    private final WordleResultRepository resultRepository;

    @Transactional
    public boolean recordResult(String telegramId, String username, int tries) {
        if (telegramId == null || telegramId.isEmpty()) {
            throw new IllegalArgumentException("Telegram ID cannot be null or empty");
        }

        // Get or create the player
        Player player = playerRepository.findByTelegramId(telegramId)
                .orElseGet(() -> {
                    Player newPlayer = new Player();
                    newPlayer.setUsername(username);
                    newPlayer.setTelegramId(telegramId);
                    newPlayer.setScore(0);
                    return playerRepository.save(newPlayer);
                });

        LocalDateTime now = LocalDateTime.now();

        // Check if player already submitted today
        List<WordleResult> existingResults = resultRepository.findAllByPlayerAndDateOnly(player, now);

        if (!existingResults.isEmpty()) {
            return false; // Already submitted
        }

        // Create new result with full timestamp
        WordleResult result = new WordleResult();
        result.setPlayer(player);
        result.setDate(now);
        result.setTries(tries);
        result.setMissed(false);
        resultRepository.save(result);

        return true; // Successfully recorded
    }

    @Transactional
    @Scheduled(cron = "0 59 23 * * *")
    public void processEndOfDay() {
        LocalDateTime now = LocalDateTime.now();

        // Get today's results using date-only comparison
        List<WordleResult> todayResults = resultRepository.findByDateOnly(now);
        List<Player> allPlayers = playerRepository.findAll();

        Optional<WordleResult> maxTries = todayResults.stream()
                .max(Comparator.comparing(WordleResult::getTries));

        for (Player player : allPlayers) {
            boolean played = todayResults.stream()
                    .anyMatch(r -> r.getPlayer().getId().equals(player.getId()));

            if (!played) {
                WordleResult missedResult = new WordleResult();
                missedResult.setPlayer(player);
                missedResult.setDate(now);
                missedResult.setMissed(true);
                missedResult.setTries(0);
                resultRepository.save(missedResult);

                player.setScore(player.getScore() + 2);
                playerRepository.save(player);
            } else if (maxTries.isPresent() &&
                    todayResults.stream()
                            .anyMatch(r -> r.getPlayer().getId().equals(player.getId()) &&
                                    r.getTries().equals(maxTries.get().getTries()))) {
                player.setScore(player.getScore() + 1);
                playerRepository.save(player);
            }
        }
    }

    public String getLeaderboard() {
        List<Player> players = playerRepository.findAll();
        players.sort(Comparator.comparing(Player::getScore).reversed());
        Constants constants = new Constants();

        StringBuilder leaderboard = new StringBuilder("🏆 Wordle Leaderboard 🏆\n\n");
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

        for (int i = 0; i < players.size(); i++) {
            Player player = players.get(i);

            String lastPlayed = resultRepository.findLastPlayedByPlayer(player)
                            .map(result -> result.getDate().format(formatter))
                                    .orElse("Never");

            leaderboard.append(String.format("%d. %s: %d %s (Last played: %s)\n",
                    i + 1, player.getUsername(), player.getScore(), constants.SCORE_TITLE, lastPlayed));

            // Add separator if this isn't the last player and there's more than one player
            if (players.size() > 1 && i < players.size() - 1) {
                leaderboard.append("-------------\n");
            }
        }
        return leaderboard.toString();
    }

    public String getHelp() {
        return "Available commands: \n /leaderboard";
    }
}