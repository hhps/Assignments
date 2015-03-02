package ua.pp.condor.gametheory.game.player;

class Cooperator extends Player {

    Cooperator() {
        super(false, false, 0);
    }

    @Override
    public PlayerType getType() {
        return PlayerType.COOPERATOR;
    }

    @Override
    protected boolean makeDecision(OpponentMoves movesOfCurrentOpponent) {
        return true;
    }
}
