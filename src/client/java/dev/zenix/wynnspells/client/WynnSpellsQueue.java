package dev.zenix.wynnspells.client;

public class WynnSpellsQueue {

    private final WynnSpellsIntent intent;

    public WynnSpellsQueue(WynnSpellsIntent intent) {
        this.intent = intent;
    }

    public WynnSpellsIntent getIntent() {
        return intent;
    }
}
