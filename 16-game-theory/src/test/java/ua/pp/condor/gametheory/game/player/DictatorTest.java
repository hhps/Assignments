package ua.pp.condor.gametheory.game.player;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class DictatorTest {

    private static Player player;

    @BeforeClass
    public static void setUpClass() throws Exception {
        player = new Dictator(false, false, 0);
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
        player = null;
    }

    @Test
    public void testGetType() throws Exception {
        assertEquals(PlayerType.DICTATOR, player.getType());
    }

    @Test
    public void testMakeDecision() throws Exception {
        OpponentMoves opponentMoves = mock(OpponentMoves.class);

        when(opponentMoves.isCooperator()).thenReturn(false);
        assertTrue(player.makeDecision(opponentMoves));

        when(opponentMoves.isCooperator()).thenReturn(true);
        assertFalse(player.makeDecision(opponentMoves));
    }
}
