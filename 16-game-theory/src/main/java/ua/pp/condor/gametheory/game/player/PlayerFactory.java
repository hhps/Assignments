package ua.pp.condor.gametheory.game.player;

public final class PlayerFactory {

    private PlayerFactory() {
    }

    public static Player getPlayer(PlayerParameters parameters) {
        final PlayerType type = parameters.getType();
        final boolean isAgressive = parameters.isAgressive();
        final boolean isOpportunist = parameters.isOpportunist();
        final double chanceOfForgiveness = parameters.getChanceOfForgiveness();

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
