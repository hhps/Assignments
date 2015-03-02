package ua.pp.condor.gametheory.game.player;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class Tit4TatTest {

    private static Player player;

    @BeforeClass
    public static void setUpClass() throws Exception {
        player = new Tit4Tat(false, false, 0);
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
        player = null;
    }

    @Test
    public void testGetType() throws Exception {
        assertEquals(PlayerType.TIT_4_TAT, player.getType());
    }

    @Test
    public void testMakeDecision() throws Exception {
        OpponentMoves opponentMoves = mock(OpponentMoves.class);

        when(opponentMoves.last()).thenReturn(false);
        assertFalse(player.makeDecision(opponentMoves));

        when(opponentMoves.last()).thenReturn(true);
        assertTrue(player.makeDecision(opponentMoves));
    }
}
