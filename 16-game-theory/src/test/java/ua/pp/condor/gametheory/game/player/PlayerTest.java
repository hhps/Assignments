package ua.pp.condor.gametheory.game.player;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.lang.reflect.Field;
import java.util.Map;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class PlayerTest {

    @Mock
    private PlayerParameters parameters;

    @Test
    public void testIsOpportunistFlag() throws Exception {
        when(parameters.getType()).thenReturn(PlayerType.DICTATOR);
        Player player = PlayerFactory.getPlayer(parameters);
        Player opponent = PlayerFactory.getPlayer(parameters);
        assertTrue(player.makeMove(opponent, false));
        assertTrue(player.makeMove(opponent, true));

        when(parameters.isOpportunist()).thenReturn(true);
        player = PlayerFactory.getPlayer(parameters);
        assertTrue(player.makeMove(opponent, false));
        assertFalse(player.makeMove(opponent, true));
    }

    @Test
    public void testIsAgressiveFlag() throws Exception {
        when(parameters.getType()).thenReturn(PlayerType.MODERATE);
        Player player = PlayerFactory.getPlayer(parameters);
        Player opponent = PlayerFactory.getPlayer(parameters);
        assertTrue(player.makeMove(opponent, false));
        player.saveOpponentMove(opponent, false);
        assertFalse(player.makeMove(opponent, true));

        when(parameters.isAgressive()).thenReturn(true);
        player = PlayerFactory.getPlayer(parameters);
        assertFalse(player.makeMove(opponent, false));
        player.saveOpponentMove(opponent, true);
        assertTrue(player.makeMove(opponent, false));
    }

    @Test
    public void testSaveOpponentMove() throws Exception {
        when(parameters.getType()).thenReturn(PlayerType.TIT_4_TAT);
        Player player = PlayerFactory.getPlayer(parameters);
        Player opponent = PlayerFactory.getPlayer(parameters);

        Field field = Player.class.getDeclaredField("opponentsMoves");
        field.setAccessible(true);
        @SuppressWarnings("unchecked")
        Map<Player, OpponentMoves> opponentsMoves = (Map<Player, OpponentMoves>) field.get(player);
        assertTrue(opponentsMoves.isEmpty());

        player.saveOpponentMove(opponent, true);
        assertFalse(opponentsMoves.isEmpty());
        OpponentMoves moves = opponentsMoves.get(opponent);
        assertEquals(1, moves.count());
        assertTrue(moves.last());
    }
}
