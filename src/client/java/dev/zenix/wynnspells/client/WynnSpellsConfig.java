package dev.zenix.wynnspells.client;

import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;

@Config(name = "wynnspells")
public class WynnSpellsConfig implements ConfigData {

    // TODO: add notifications settings
    // TODO: add auto calculation method
    private int delayMillis = getDefaultDelayMillis();
    private int queueLimit = getDefaultQueueLimit();

    public static int getDefaultDelayMillis() {
        return 100;
    }

    public int getDelayMillis() {
        return delayMillis;
    }

    public void setDelayMillis(int delay) {
        delayMillis = delay;
    }

    public static int getDefaultQueueLimit() {
        return 10;
    }

    public int getQueueLimit() {
        return queueLimit;
    }

    public void setQueueLimit(int limit) {
        queueLimit = limit;
    }
}
