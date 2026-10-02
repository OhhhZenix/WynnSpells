package dev.zenix.wynnspells.client.config;

import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;

@Config(name = "wynnspells")
public class ClothConfig implements ConfigData {

  static final class Defaults {
    // General
    public static final boolean NOTIFY_UPDATES = true;
    public static final boolean NOTIFY_BUSY_CAST = false;
    public static final boolean WEAPON_ONLY_CASTING = true;
    public static final boolean BLOCK_CLICKS = true;

    // Timings
    public static final boolean USE_AUTO_DELAY = true;
    public static final int AUTO_DELAY_TOLERANCE = 10;
    public static final int MANUAL_DELAY = 100;
    public static final int PING_LOOK_BACK = 0;

    // Inputs
    public static final boolean REPEAT_HELD_KEYS = true;
    public static final int REPEAT_THRESHOLD = 250;
  }

  // General
  private boolean notifyUpdates = Defaults.NOTIFY_UPDATES;
  private boolean notifyBusyCast = Defaults.NOTIFY_BUSY_CAST;
  private boolean weaponOnlyCasting = Defaults.WEAPON_ONLY_CASTING;
  private boolean blockClicks = Defaults.BLOCK_CLICKS;

  // Timings
  private boolean useAutoDelay = Defaults.USE_AUTO_DELAY;
  private int autoDelayTolerance = Defaults.AUTO_DELAY_TOLERANCE;
  private int manualDelay = Defaults.MANUAL_DELAY;
  private int pingLookBack = Defaults.PING_LOOK_BACK;

  // Inputs
  private boolean repeatHeldKeys = Defaults.REPEAT_HELD_KEYS;
  private int repeatThreshold = Defaults.REPEAT_THRESHOLD;

  public boolean isNotifyUpdates() {
    return notifyUpdates;
  }

  public void setNotifyUpdates(boolean notifyUpdates) {
    this.notifyUpdates = notifyUpdates;
  }

  public boolean isNotifyBusyCast() {
    return notifyBusyCast;
  }

  public void setNotifyBusyCast(boolean notifyBusyCast) {
    this.notifyBusyCast = notifyBusyCast;
  }

  public boolean isWeaponOnlyCasting() {
    return weaponOnlyCasting;
  }

  public void setWeaponOnlyCasting(boolean weaponOnlyCasting) {
    this.weaponOnlyCasting = weaponOnlyCasting;
  }

  public boolean isBlockClicks() {
    return blockClicks;
  }

  public void setBlockClicks(boolean blockClicks) {
    this.blockClicks = blockClicks;
  }

  public boolean isUseAutoDelay() {
    return useAutoDelay;
  }

  public void setUseAutoDelay(boolean useAutoDelay) {
    this.useAutoDelay = useAutoDelay;
  }

  public int getAutoDelayTolerance() {
    return autoDelayTolerance;
  }

  public void setAutoDelayTolerance(int autoDelayTolerance) {
    this.autoDelayTolerance = autoDelayTolerance;
  }

  public int getManualDelay() {
    return manualDelay;
  }

  public void setManualDelay(int manualDelay) {
    this.manualDelay = manualDelay;
  }

  public int getPingLookBack() {
    return pingLookBack;
  }

  public void setPingLookBack(int pingLookBack) {
    this.pingLookBack = pingLookBack;
  }

  public boolean isRepeatHeldKeys() {
    return repeatHeldKeys;
  }

  public void setRepeatHeldKeys(boolean repeatHeldKeys) {
    this.repeatHeldKeys = repeatHeldKeys;
  }

  public int getRepeatThreshold() {
    return repeatThreshold;
  }

  public void setRepeatThreshold(int repeatThreshold) {
    this.repeatThreshold = repeatThreshold;
  }
}
