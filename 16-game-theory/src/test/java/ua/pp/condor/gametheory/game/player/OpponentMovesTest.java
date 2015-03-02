package ua.pp.condor.gametheory.game.player;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.junit.Assert.*;

public class OpponentMovesTest {

    private static OpponentMoves moves;

    @BeforeClass
    public static void setUpClass() throws Exception {
        moves = new OpponentMoves();
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
        moves = null;
    }

    @Before
    public void setUp() throws Exception {
        moves.clear();
    }

    @Test
    public void testIsEmpty() throws Exception {
        assertTrue(moves.isEmpty());

        moves.add(true);
        assertFalse(moves.isEmpty());
    }

    @Test
    public void testCount() throws Exception {
        assertEquals(0, moves.count());

        moves.add(true);
        assertEquals(1, moves.count());
        moves.add(false);
        assertEquals(2, moves.count());
        moves.add(true);
        assertEquals(3, moves.count());
    }

    @Test
    public void testCooperationCount() throws Exception {
        assertEquals(0, moves.cooperationCount());

        moves.add(true);
        assertEquals(1, moves.cooperationCount());
        moves.add(false);
        assertEquals(1, moves.cooperationCount());
        moves.add(true);
        assertEquals(2, moves.cooperationCount());
    }

    @Test
    public void testClear() throws Exception {
        assertTrue(moves.isEmpty());
        assertEquals(0, moves.count());
        assertEquals(0, moves.cooperationCount());

        moves.add(true);
        assertFalse(moves.isEmpty());
        assertEquals(1, moves.count());
        assertEquals(1, moves.cooperationCount());

        moves.clear();
        assertTrue(moves.isEmpty());
        assertEquals(0, moves.count());
        assertEquals(0, moves.cooperationCount());
    }

    @Test(expected = IllegalStateException.class)
    public void testLastWithClearObject() throws Exception {
        moves.last();
    }

    @Test
    public void testLast() throws Exception {
        moves.add(true);
        assertTrue(moves.last());

        moves.add(false);
        assertFalse(moves.last());

        moves.add(true);
        assertTrue(moves.last());
    }

    @Test(expected = IllegalStateException.class)
    public void testPenultimateWithClearObject() throws Exception {
        moves.penultimate();
    }

    @Test(expected = IllegalStateException.class)
    public void testPenultimateWithOneMove() throws Exception {
        moves.add(true);
        moves.penultimate();
    }

    @Test
    public void testPenultimate() throws Exception {
        moves.add(true);
        moves.add(true);
        assertTrue(moves.penultimate());

        moves.add(false);
        assertTrue(moves.penultimate());

        moves.add(false);
        assertFalse(moves.penultimate());

        moves.add(true);
        assertFalse(moves.penultimate());
    }

    @Test
    public void testIsCooperator() throws Exception {
        assertFalse(moves.isCooperator());

        moves.add(true);
        assertTrue(moves.isCooperator());

        moves.clear();
        moves.add(false);
        assertFalse(moves.isCooperator());

        moves.add(true);
        assertFalse(moves.isCooperator());

        moves.add(true);
        assertTrue(moves.isCooperator());
    }
}
