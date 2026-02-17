package dev.zenix.wynnspells.client;

import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;

@Config(name = "wynnspells")
public class WynnSpellsConfig implements ConfigData {

    private int delayMillis = 1000 * 30;
    private int queueLimit = 10;

    public int getDelayMillis() {
        return delayMillis;
    }

    public int getQueueLimit() {
        return queueLimit;
    }
}
