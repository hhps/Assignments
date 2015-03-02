package ua.pp.condor.gametheory.game.player;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class FrenemyTest {

    private static Player player;

    @BeforeClass
    public static void setUpClass() throws Exception {
        player = new Frenemy(false, false, 0);
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
        player = null;
    }

    @Test
    public void testGetType() throws Exception {
        assertEquals(PlayerType.FRENEMY, player.getType());
    }

    @Test
    public void testMakeDecisionWithEnoughOpponentMovesCount() throws Exception {
        OpponentMoves opponentMoves = mock(OpponentMoves.class);
        when(opponentMoves.count()).thenReturn(3);

        when(opponentMoves.last()).thenReturn(false);
        when(opponentMoves.penultimate()).thenReturn(false);
        assertTrue(player.makeDecision(opponentMoves));

        when(opponentMoves.last()).thenReturn(false);
        when(opponentMoves.penultimate()).thenReturn(true);
        assertTrue(player.makeDecision(opponentMoves));

        when(opponentMoves.last()).thenReturn(true);
        when(opponentMoves.penultimate()).thenReturn(false);
        assertTrue(player.makeDecision(opponentMoves));

        when(opponentMoves.last()).thenReturn(true);
        when(opponentMoves.penultimate()).thenReturn(true);
        assertFalse(player.makeDecision(opponentMoves));
    }

    @Test
    public void testMakeDecisionWithoutEnoughOpponentMovesCount() throws Exception {
        OpponentMoves opponentMoves = mock(OpponentMoves.class);
        when(opponentMoves.count()).thenReturn(1);

        when(opponentMoves.last()).thenReturn(false);
        when(opponentMoves.penultimate()).thenReturn(false);
        assertTrue(player.makeDecision(opponentMoves));

        when(opponentMoves.last()).thenReturn(false);
        when(opponentMoves.penultimate()).thenReturn(true);
        assertTrue(player.makeDecision(opponentMoves));

        when(opponentMoves.last()).thenReturn(true);
        when(opponentMoves.penultimate()).thenReturn(false);
        assertTrue(player.makeDecision(opponentMoves));

        when(opponentMoves.last()).thenReturn(true);
        when(opponentMoves.penultimate()).thenReturn(true);
        assertTrue(player.makeDecision(opponentMoves));
    }
}
