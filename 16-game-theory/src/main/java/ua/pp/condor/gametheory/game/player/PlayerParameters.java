package ua.pp.condor.gametheory.game.player;

public class PlayerParameters {

    private PlayerType type;
    private boolean isAgressive;
    private boolean isOpportunist;
    private double chanceOfForgiveness;

    public PlayerType getType() {
        return type;
    }

    public void setType(PlayerType type) {
        this.type = type;
    }

    public boolean isAgressive() {
        return isAgressive;
    }

    public void setAgressive(boolean isAgressive) {
        this.isAgressive = isAgressive;
    }

    public boolean isOpportunist() {
        return isOpportunist;
    }

    public void setOpportunist(boolean isOpportunist) {
        this.isOpportunist = isOpportunist;
    }

    public double getChanceOfForgiveness() {
        return chanceOfForgiveness;
    }

    public void setChanceOfForgiveness(double chanceOfForgiveness) {
        this.chanceOfForgiveness = chanceOfForgiveness;
    }
}
