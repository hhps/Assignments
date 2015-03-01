package ua.pp.condor.gametheory.game.player;

class Moderate extends Player {

    Moderate(boolean isAgressive, boolean isOpportunist, double chanceOfForgiveness) {
        super(isAgressive, isOpportunist, chanceOfForgiveness);
    }

    @Override
    public PlayerType getType() {
        return PlayerType.MODERATE;
    }

    @Override
    protected boolean makeDecision(OpponentMoves movesOfCurrentOpponent) {
        return movesOfCurrentOpponent.isCooperator();
    }
}
