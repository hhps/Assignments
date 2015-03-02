package ua.pp.condor.gametheory.game.player;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.internal.util.reflection.Whitebox;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class PlayerFactoryTest {

    @Mock
    private PlayerParameters parameters;

    @Test
    public void testGetCooperator() throws Exception {
        when(parameters.getType()).thenReturn(PlayerType.COOPERATOR);
        Player player = PlayerFactory.getPlayer(parameters);
        assertEquals(Cooperator.class, player.getClass());
    }

    @Test
    public void testGetDefector() throws Exception {
        when(parameters.getType()).thenReturn(PlayerType.DEFECTOR);
        Player player = PlayerFactory.getPlayer(parameters);
        assertEquals(Defector.class, player.getClass());
    }

    @Test
    public void testGetRandom() throws Exception {
        when(parameters.getType()).thenReturn(PlayerType.RANDOM);
        Player player = PlayerFactory.getPlayer(parameters);
        assertEquals(Random.class, player.getClass());
    }

    @Test
    public void testGetTit4Tat() throws Exception {
        when(parameters.getType()).thenReturn(PlayerType.TIT_4_TAT);
        Player player = PlayerFactory.getPlayer(parameters);
        assertEquals(Tit4Tat.class, player.getClass());
    }

    @Test
    public void testGetModerate() throws Exception {
        when(parameters.getType()).thenReturn(PlayerType.MODERATE);
        Player player = PlayerFactory.getPlayer(parameters);
        assertEquals(Moderate.class, player.getClass());
    }

    @Test
    public void testGetDictator() throws Exception {
        when(parameters.getType()).thenReturn(PlayerType.DICTATOR);
        Player player = PlayerFactory.getPlayer(parameters);
        assertEquals(Dictator.class, player.getClass());
    }

    @Test
    public void testGetFrenemy() throws Exception {
        when(parameters.getType()).thenReturn(PlayerType.FRENEMY);
        Player player = PlayerFactory.getPlayer(parameters);
        assertEquals(Frenemy.class, player.getClass());
    }

    @Test
    public void testGetPrudent() throws Exception {
        when(parameters.getType()).thenReturn(PlayerType.PRUDENT);
        Player player = PlayerFactory.getPlayer(parameters);
        assertEquals(Prudent.class, player.getClass());
    }
}
