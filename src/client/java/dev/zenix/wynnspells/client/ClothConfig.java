package dev.zenix.wynnspells.client;

import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;

@Config(name = "wynnspells")
public class ClothConfig implements ConfigData {

	private boolean notifyUpdates = getDefaultNotifyUpdates();
	private boolean notifyBusyCast = getDefaultNotifyBusyCast();

	private boolean weaponOnlyCasting = getDefaultWeaponOnlyCasting();
	private boolean blockClicks = getDefaultBlockClicks();
	private boolean useAutoDelay = getDefaultUseAutoDelay();
	private boolean repeatHeldKeys = getDefaultRepeatHeldKeys();

	private int autoDelayTolerance = getDefaultAutoDelayTolerance();
	private int manualDelay = getDefaultManualDelay();
	private int repeatThreshold = getDefaultRepeatThreshold();

	public static boolean getDefaultNotifyUpdates() {
		return true;
	}

	public static boolean getDefaultNotifyBusyCast() {
		return false;
	}

	public static boolean getDefaultWeaponOnlyCasting() {
		return true;
	}

	public static boolean getDefaultBlockClicks() {
		return true;
	}

	public static boolean getDefaultUseAutoDelay() {
		return true;
	}

	public static boolean getDefaultRepeatHeldKeys() {
		return true;
	}

	public static int getDefaultAutoDelayTolerance() {
		return 10;
	}

	public static int getDefaultManualDelay() {
		return 100;
	}

	public static int getDefaultRepeatThreshold() {
		return 250;
	}

	public boolean shouldNotifyUpdates() {
		return notifyUpdates;
	}

	public void setNotifyUpdates(boolean enabled) {
		notifyUpdates = enabled;
	}

	public boolean shouldNotifyBusyCast() {
		return notifyBusyCast;
	}

	public void setNotifyBusyCast(boolean enabled) {
		notifyBusyCast = enabled;
	}

	public boolean isWeaponOnlyCasting() {
		return weaponOnlyCasting;
	}

	public void setWeaponOnlyCasting(boolean enabled) {
		weaponOnlyCasting = enabled;
	}

	public boolean getBlockClicks() {
		return blockClicks;
	}

	public void setBlockClicks(boolean value) {
		blockClicks = value;
	}

	public boolean getRepeatHeldKeys() {
		return repeatHeldKeys;
	}

	public void setRepeatHeldKeys(boolean value) {
		repeatHeldKeys = value;
	}

	public boolean shouldUseAutoDelay() {
		return useAutoDelay;
	}

	public void setUseAutoDelay(boolean enabled) {
		useAutoDelay = enabled;
	}

	public int getAutoDelayTolerance() {
		return autoDelayTolerance;
	}

	public void setAutoDelayTolerance(int tolerance) {
		autoDelayTolerance = tolerance;
	}

	public int getManualDelay() {
		return manualDelay;
	}

	public void setManualDelay(int delay) {
		manualDelay = delay;
	}

	public int getRepeatThreshold() {
		return repeatThreshold;
	}

	public void setRepeatThreshold(int threshold) {
		repeatThreshold = threshold;
	}
}
