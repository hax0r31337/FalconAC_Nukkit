package me.liuli.falcon.manager;

public enum CheckCategory {
    COMBAT, MOVEMENT, WORLD, MISC;

    public float vl = 500, minusVl = 1, passMinus = 1, flagVl = 100, warnVl = 300;
    public PunishResult result = PunishResult.NONE;
}
