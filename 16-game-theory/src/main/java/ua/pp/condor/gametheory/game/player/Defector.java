package ua.pp.condor.gametheory.game.player;

class Defector extends Player {

    Defector() {
        super(true, true, 0);
    }

    @Override
    public PlayerType getType() {
        return PlayerType.DEFECTOR;
    }

    @Override
    protected boolean makeDecision(OpponentMoves movesOfCurrentOpponent) {
        return false;
    }
}
