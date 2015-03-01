package ua.pp.condor.gametheory.game;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ua.pp.condor.gametheory.game.GameRequest.Participant;
import ua.pp.condor.gametheory.game.player.Player;
import ua.pp.condor.gametheory.game.player.PlayerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Game {

    private static final Logger log = LoggerFactory.getLogger(Game.class);

    private final Points firstPoints;
    private final Points secondPoints;
    private final int numberOfRounds;
    private final double chanceOfMisunderstanding;
    private final List<Player> players;

    public Game(GameRequest request) {
        firstPoints = request.getFirstPoints();
        secondPoints = request.getSecondPoints();
        numberOfRounds = request.getNumberOfRounds();
        chanceOfMisunderstanding = request.getChanceOfMisunderstanding();

        int wholeAmount = 0;
        for (Participant participant : request.getParticipants()) {
            wholeAmount += participant.getAmount();
        }
        List<Player> players = new ArrayList<>(wholeAmount);
        for (Participant participant : request.getParticipants()) {
            final int amount = participant.getAmount();
            for (int i = 0; i < amount; i++) {
                Player player = PlayerFactory.getPlayer(participant.getParameters());
                players.add(player);
            }
        }
        this.players = players;
        log.info("Game constructed: {}", request);
    }

    public List<Integer> play() {
        List<Integer> scores = new ArrayList<>(numberOfRounds);
        int gameScore = 0;
        for (int i = 0; i < numberOfRounds; i++) {
            int score = playRound(isOneOfTheLatestMoves(i));
            scores.add(score);
            gameScore += score;
        }
        log.info("Game score: {}", gameScore);
        return scores;
    }

    private int playRound(boolean isOneOfTheLatestMoves) {
        Collections.shuffle(players);
        Player firstPlayer;
        Player secondPlayer;
        final int playersSize = players.size();
        int roundScore = 0;
        for (int i = 0; i < playersSize - 1; i += 2) {
            firstPlayer = players.get(i);
            secondPlayer = players.get(i + 1);
            int score = playInteraction(firstPlayer, secondPlayer, isOneOfTheLatestMoves);
            roundScore += score;
        }
        return roundScore;
    }

    private int playInteraction(Player firstPlayer, Player secondPlayer, boolean isOneOfTheLatestMoves) {
        boolean first = move(firstPlayer, secondPlayer, isOneOfTheLatestMoves);
        boolean second = move(secondPlayer, firstPlayer, isOneOfTheLatestMoves);
        return calculateScore(first, second);
    }

    private boolean move(Player player, Player opponent, boolean isOneOfTheLatestMoves) {
        boolean decision = player.makeMove(opponent, isOneOfTheLatestMoves);
        decision = possibleMisunderstanding(decision);
        opponent.saveOpponentMove(player, decision);
        return decision;
    }

    private int calculateScore(boolean first, boolean second) {
        int score = 0;
        if (first && second) {
            score += firstPoints.getCooperateCooperate();
            score += secondPoints.getCooperateCooperate();
        } else if (first && !second) {
            score += firstPoints.getCooperateDefect();
            score += secondPoints.getDefectCooperate();
        } else if (!first && second) {
            score += firstPoints.getDefectCooperate();
            score += secondPoints.getCooperateDefect();
        } else {
            score += firstPoints.getDefectDefect();
            score += secondPoints.getDefectDefect();
        }
        return score;
    }

    private boolean possibleMisunderstanding(boolean move) {
        if (chanceOfMisunderstanding > 0) {
            return Math.random() < chanceOfMisunderstanding ? !move : move;
        }
        return move;
    }

    private boolean isOneOfTheLatestMoves(int roundNumber) {
        return roundNumber >= (int) (numberOfRounds * 0.9);
    }
}
