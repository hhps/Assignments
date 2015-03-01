package ua.pp.condor.gametheory.game.player;

class Random extends Player {

    Random(boolean isOpportunist, double chanceOfForgiveness) {
        super(false, isOpportunist, chanceOfForgiveness);
    }

    @Override
    public PlayerType getType() {
        return PlayerType.RANDOM;
    }

    @Override
    protected boolean makeDecision(OpponentMoves movesOfCurrentOpponent) {
        return Math.random() < 0.5;
    }
}
