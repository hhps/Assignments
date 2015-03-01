package ua.pp.condor.gametheory.game.player;

class Frenemy extends Player {

    Frenemy(boolean isAgressive, boolean isOpportunist, double chanceOfForgiveness) {
        super(isAgressive, isOpportunist, chanceOfForgiveness);
    }

    @Override
    public PlayerType getType() {
        return PlayerType.FRENEMY;
    }

    @Override
    protected boolean makeDecision(OpponentMoves movesOfCurrentOpponent) {
        if (movesOfCurrentOpponent.count() >= 2) {
            return !(movesOfCurrentOpponent.last() && movesOfCurrentOpponent.penultimate());
        }
        return true;
    }
}
