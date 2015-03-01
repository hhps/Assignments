package ua.pp.condor.gametheory.game.player;

import java.util.HashMap;
import java.util.Map;

public abstract class Player {

    private final boolean isAgressive;
    private final boolean isOpportunist;
    private final double chanceOfForgiveness;

    private final Map<Player, OpponentMoves> opponentsMoves = new HashMap<>();

    protected Player(boolean isAgressive, boolean isOpportunist, double chanceOfForgiveness) {
        this.isAgressive = isAgressive;
        this.isOpportunist = isOpportunist;
        this.chanceOfForgiveness = chanceOfForgiveness;
    }

    public abstract PlayerType getType();

    public boolean makeMove(Player opponent, boolean isOneOfTheLatestMoves) {
        if (isOneOfTheLatestMoves && isOpportunist) {
            return false;
        }
        OpponentMoves movesOfCurrentOpponent = opponentsMoves.get(opponent);
        if (isUnknown(movesOfCurrentOpponent)) {
            return !isAgressive;
        }
        boolean decision = makeDecision(movesOfCurrentOpponent); //FIXME pass unmodifable value
        if (canBeForgiven(decision)) {
            return Math.random() < chanceOfForgiveness ? !decision : decision;
        }
        return decision;
    }

    protected abstract boolean makeDecision(OpponentMoves movesOfCurrentOpponent);

    private boolean isUnknown(OpponentMoves movesOfCurrentOpponent) {
        return (movesOfCurrentOpponent == null || movesOfCurrentOpponent.isEmpty()) && getType() != PlayerType.RANDOM;
    }

    private boolean canBeForgiven(boolean decision) {
        return !decision && chanceOfForgiveness > 0;
    }

    public void saveOpponentMove(Player opponent, boolean move) {
        OpponentMoves movesOfCurrentOpponent = opponentsMoves.get(opponent);
        if (movesOfCurrentOpponent == null) {
            movesOfCurrentOpponent = new OpponentMoves();
            opponentsMoves.put(opponent, movesOfCurrentOpponent);
        }
        movesOfCurrentOpponent.add(move);
    }
}
