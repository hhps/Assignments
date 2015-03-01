package ua.pp.condor.gametheory.game.player;

public final class PlayerFactory {

    private PlayerFactory() {
    }

    public static Player getPlayer(PlayerType type, boolean isAgressive,
                                   boolean isOpportunist, double chanceOfForgiveness) {
        switch (type) {
            case COOPERATOR:
                return new Cooperator();
            case DEFECTOR:
                return new Defector();
            case RANDOM:
                return new Random(isOpportunist, chanceOfForgiveness);
            case TIT_4_TAT:
                return new Tit4Tat(isAgressive, isOpportunist, chanceOfForgiveness);
            case MODERATE:
                return new Moderate(isAgressive, isOpportunist, chanceOfForgiveness);
            case DICTATOR:
                return new Dictator(isAgressive, isOpportunist, chanceOfForgiveness);
            case FRENEMY:
                return new Frenemy(isAgressive, isOpportunist, chanceOfForgiveness);
            case PRUDENT:
                return new Prudent(isAgressive, isOpportunist, chanceOfForgiveness);
            default:
                throw new IllegalArgumentException("Unknown player type");
        }
    }
}
