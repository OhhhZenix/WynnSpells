package dev.zenix.wynnspells.client;

import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;

@Config(name = "wynnspells")
public class WynnSpellsConfig implements ConfigData {

    private boolean notifyUpdates = getDefaultNotifyUpdates();
    private boolean notifyBusyCast = getDefaultNotifyBusyCast();
    private boolean useAutoDelay = getDefaultUseAutoDelay();
    private int delayMillis = getDefaultDelayMillis();
    private int bufferLimit = getDefaultBufferLimit();

    public static boolean getDefaultNotifyUpdates() {
        return true;
    }

    public boolean shouldNotifyUpdates() {
        return notifyUpdates;
    }

    public void setNotifyUpdates(boolean enabled) {
        notifyUpdates = enabled;
    }

    public static boolean getDefaultUseAutoDelay() {
        return true;
    }

    public boolean shouldUseAutoDelay() {
        return useAutoDelay;
    }

    public void setUseAutoDelay(boolean enabled) {
        useAutoDelay = enabled;
    }

    public static boolean getDefaultNotifyBusyCast() {
        return true;
    }

    public boolean shouldNotifyBusyCast() {
        return notifyBusyCast;
    }

    public void setNotifyBusyCast(boolean enabled) {
        notifyBusyCast = enabled;
    }

    public static int getDefaultDelayMillis() {
        return 100;
    }

    public int getDelayMillis() {
        return delayMillis;
    }

    public void setDelayMillis(int delay) {
        delayMillis = delay;
    }

    public static int getDefaultBufferLimit() {
        return 1;
    }

    public int getBufferLimit() {
        return bufferLimit;
    }

    public void setBufferLimit(int limit) {
        bufferLimit = limit;
    }
}
