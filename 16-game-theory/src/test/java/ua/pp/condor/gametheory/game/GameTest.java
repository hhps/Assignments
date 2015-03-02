package ua.pp.condor.gametheory.game;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import ua.pp.condor.gametheory.game.GameRequest.Participant;
import ua.pp.condor.gametheory.game.player.PlayerParameters;
import ua.pp.condor.gametheory.game.player.PlayerType;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class GameTest {

    private static GameRequest request;

    @BeforeClass
    public static void setUpClass() throws Exception {
        request = new GameRequest();
        request.setNumberOfRounds(3);

        Points firstPoints = new Points();
        firstPoints.setCooperateCooperate(6);
        firstPoints.setCooperateDefect(0);
        firstPoints.setDefectCooperate(9);
        firstPoints.setDefectDefect(0);
        request.setFirstPoints(firstPoints);

        Points secondPoints = new Points();
        secondPoints.setCooperateCooperate(6);
        secondPoints.setCooperateDefect(0);
        secondPoints.setDefectCooperate(9);
        secondPoints.setDefectDefect(0);
        request.setSecondPoints(secondPoints);

        List<Participant> participants = new ArrayList<>(2);
        request.setParticipants(participants);

        Participant firstParticipant = new Participant();
        PlayerParameters firstPlayerParameters = new PlayerParameters();
        firstParticipant.setParameters(firstPlayerParameters);
        firstParticipant.setAmount(1);
        participants.add(firstParticipant);

        Participant secondParticipant = new Participant();
        PlayerParameters secondPlayerParameters = new PlayerParameters();
        secondParticipant.setParameters(secondPlayerParameters);
        secondParticipant.setAmount(1);
        participants.add(secondParticipant);
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
        request = null;
    }

    private static void setTypes(PlayerType firstPlayerType, PlayerType secondPlayerType) {
        List<Participant> participants = request.getParticipants();

        Participant firstParticipant = participants.get(0);
        PlayerParameters firstPlayerParameters = firstParticipant.getParameters();
        firstPlayerParameters.setType(firstPlayerType);

        Participant secondParticipant = participants.get(1);
        PlayerParameters secondPlayerParameters = secondParticipant.getParameters();
        secondPlayerParameters.setType(secondPlayerType);
    }

    private static Integer[] playGame(PlayerType firstPlayerType, PlayerType secondPlayerType) {
        setTypes(firstPlayerType, secondPlayerType);
        List<Integer> scores = new Game(request).play();
        Integer[] intScores = new Integer[scores.size()];
        return scores.toArray(intScores);
    }

    @Test
    public void testPlayCooperatorCooperator() throws Exception {
        Integer[] scores = playGame(PlayerType.COOPERATOR, PlayerType.COOPERATOR);
        assertArrayEquals(new Integer[] {12, 24, 36}, scores);
    }

    @Test
    public void testPlayCooperatorDefector() throws Exception {
        Integer[] scores = playGame(PlayerType.COOPERATOR, PlayerType.DEFECTOR);
        assertArrayEquals(new Integer[] {9, 18, 27}, scores);
    }

    @Test
    public void testPlayDefectorCooperator() throws Exception {
        Integer[] scores = playGame(PlayerType.DEFECTOR, PlayerType.COOPERATOR);
        assertArrayEquals(new Integer[] {9, 18, 27}, scores);
    }

    @Test
    public void testPlayDefectorDefector() throws Exception {
        Integer[] scores = playGame(PlayerType.DEFECTOR, PlayerType.DEFECTOR);
        assertArrayEquals(new Integer[] {0, 0, 0}, scores);
    }
}
