package ua.pp.condor.gametheory.game.player;

class Tit4Tat extends Player {

    Tit4Tat(boolean isAgressive, boolean isOpportunist, double chanceOfForgiveness) {
        super(isAgressive, isOpportunist, chanceOfForgiveness);
    }

    @Override
    public PlayerType getType() {
        return PlayerType.TIT_4_TAT;
    }

    @Override
    protected boolean makeDecision(OpponentMoves movesOfCurrentOpponent) {
        return movesOfCurrentOpponent.last();
    }
}
