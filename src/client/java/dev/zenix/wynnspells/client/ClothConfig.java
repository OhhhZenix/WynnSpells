package dev.zenix.wynnspells.client;

import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;

@Config(name = "wynnspells")
public class ClothConfig implements ConfigData {

	private boolean notifyUpdates = getDefaultNotifyUpdates();
	private boolean notifyBusyCast = getDefaultNotifyBusyCast();
	private boolean weaponOnlyCasting = getDefaultWeaponOnlyCasting();
	private boolean useAutoDelay = getDefaultUseAutoDelay();
	private int autoDelayTolerance = getDefaultAutoDelayTolerance();
	private int manualDelay = getDefaultManualDelay();
	private int bufferLimit = getDefaultBufferLimit();

	public static boolean getDefaultNotifyUpdates() {
		return true;
	}

	public static boolean getDefaultNotifyBusyCast() {
		return true;
	}

	public static boolean getDefaultWeaponOnlyCasting() {
		return true;
	}

	public boolean isWeaponOnlyCasting() {
		return weaponOnlyCasting;
	}

	public void setWeaponOnlyCasting(boolean enabled) {
		weaponOnlyCasting = enabled;
	}

	public static boolean getDefaultUseAutoDelay() {
		return true;
	}

	public static int getDefaultAutoDelayTolerance() {
		return 10;
	}

	public static int getDefaultManualDelay() {
		return 100;
	}

	public static int getDefaultBufferLimit() {
		return 1;
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

	public int getBufferLimit() {
		return bufferLimit;
	}

	public void setBufferLimit(int limit) {
		bufferLimit = limit;
	}
}
