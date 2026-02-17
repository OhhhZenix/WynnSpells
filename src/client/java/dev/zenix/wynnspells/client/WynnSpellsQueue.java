package dev.zenix.wynnspells.client;

public class WynnSpellsQueue {

    private WynnSpellsIntent intent;
    private boolean isSneaking;

    public WynnSpellsQueue(WynnSpellsIntent intent, boolean isSneaking) {
        this.intent = intent;
        this.isSneaking = isSneaking;
    }

    public WynnSpellsIntent getIntent() {
        return intent;
    }

    public boolean isSneaking() {
        return isSneaking;
    }
}
