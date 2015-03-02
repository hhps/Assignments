package ua.pp.condor.gametheory.game.player;

class Prudent extends Player {

    Prudent(boolean isAgressive, boolean isOpportunist, double chanceOfForgiveness) {
        super(isAgressive, isOpportunist, chanceOfForgiveness);
    }

    @Override
    public PlayerType getType() {
        return PlayerType.PRUDENT;
    }

    @Override
    protected boolean makeDecision(OpponentMoves movesOfCurrentOpponent) {
        if (movesOfCurrentOpponent.isCooperator()) {
            return movesOfCurrentOpponent.last() || Math.random() < 0.75;
        }
        return movesOfCurrentOpponent.last() && Math.random() < 0.25;
    }
}
