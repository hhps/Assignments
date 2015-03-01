package ua.pp.condor.gametheory.game;

import ua.pp.condor.gametheory.game.player.PlayerParameters;

import java.util.List;

public class GameRequest {

    public static class Participant {

        private PlayerParameters parameters;
        private int amount;

        public PlayerParameters getParameters() {
            return parameters;
        }

        public void setParameters(PlayerParameters parameters) {
            this.parameters = parameters;
        }

        public int getAmount() {
            return amount;
        }

        public void setAmount(int amount) {
            this.amount = amount;
        }
    }

    private Points firstPoints;
    private Points secondPoints;
    private int numberOfRounds;
    private double chanceOfMisunderstanding;
    private List<Participant> participants;

    public Points getFirstPoints() {
        return firstPoints;
    }

    public void setFirstPoints(Points firstPoints) {
        this.firstPoints = firstPoints;
    }

    public Points getSecondPoints() {
        return secondPoints;
    }

    public void setSecondPoints(Points secondPoints) {
        this.secondPoints = secondPoints;
    }

    public int getNumberOfRounds() {
        return numberOfRounds;
    }

    public void setNumberOfRounds(int numberOfRounds) {
        this.numberOfRounds = numberOfRounds;
    }

    public double getChanceOfMisunderstanding() {
        return chanceOfMisunderstanding;
    }

    public void setChanceOfMisunderstanding(double chanceOfMisunderstanding) {
        this.chanceOfMisunderstanding = chanceOfMisunderstanding;
    }

    public List<Participant> getParticipants() {
        return participants;
    }

    public void setParticipants(List<Participant> participants) {
        this.participants = participants;
    }
}
