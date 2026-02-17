package dev.zenix.wynnspells.client;

public class WynnSpellsQueue {

    private WynnSpellsIntent intent;
    private boolean isSneaked;

    public WynnSpellsQueue(WynnSpellsIntent intent, boolean isSneaked) {
        this.intent = intent;
        this.isSneaked = isSneaked;
    }

    public WynnSpellsIntent getIntent() {
        return intent;
    }

    public boolean isSneaked() {
        return isSneaked;
    }
}
