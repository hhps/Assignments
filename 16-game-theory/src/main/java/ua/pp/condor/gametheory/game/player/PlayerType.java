package ua.pp.condor.gametheory.game.player;

public enum PlayerType {
    COOPERATOR,
    DEFECTOR,
    RANDOM,
    TIT_4_TAT,
    MODERATE, //если в сребнем его противник больше обманывает, то и он обманывает
    DICTATOR, // If opponent on average cooperates then cheat, if opponent on average cheats then cooperate.
    FRENEMY, //нет только если ты последние два раза сказал да
    PRUDENT
}
