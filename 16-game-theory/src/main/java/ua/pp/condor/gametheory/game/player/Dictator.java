package ua.pp.condor.gametheory.game.player;

class Dictator extends Player {

    Dictator(boolean isAgressive, boolean isOpportunist, double chanceOfForgiveness) {
        super(isAgressive, isOpportunist, chanceOfForgiveness);
    }

    @Override
    public PlayerType getType() {
        return PlayerType.DICTATOR;
    }

    @Override
    protected boolean makeDecision(OpponentMoves movesOfCurrentOpponent) {
        return !movesOfCurrentOpponent.isCooperator();
    }
}
