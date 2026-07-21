package dev.zenix.wynnspells.client;

import com.mojang.blaze3d.platform.InputConstants;
import dev.zenix.wynnspells.WynnSpells;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class ConfigScreen {

	public static Screen create(Screen parent) {
		ConfigBuilder builder = ConfigBuilder.create().setParentScreen(parent)
				.setTitle(Component.nullToEmpty(WynnSpells.MOD_NAME));
		ConfigEntryBuilder entryBuilder = builder.entryBuilder();

		WynnSpellsClient wynnSpellsClient = WynnSpellsClient.getInstance();
		ClothConfig config = wynnSpellsClient.getConfig();
		builder.setSavingRunnable(wynnSpellsClient::saveConfig);

		// General
		ConfigCategory generalCategory = builder.getOrCreateCategory(Component.nullToEmpty("General"));
		generalCategory.addEntry(
				entryBuilder.startBooleanToggle(Component.nullToEmpty("Notify Updates"), config.shouldNotifyUpdates())
						.setTooltip(Component.nullToEmpty("To enable or disable update notifications."))
						.setDefaultValue(ClothConfig.getDefaultNotifyUpdates())
						.setSaveConsumer(config::setNotifyUpdates).build());
		generalCategory.addEntry(entryBuilder
				.startBooleanToggle(Component.nullToEmpty("Notify Busy Cast"), config.shouldNotifyBusyCast())
				.setTooltip(Component.nullToEmpty("To enable or disable busy cast notifications."))
				.setDefaultValue(ClothConfig.getDefaultNotifyBusyCast()).setSaveConsumer(config::setNotifyBusyCast)
				.build());
		generalCategory.addEntry(
				entryBuilder.startBooleanToggle(Component.nullToEmpty("Weapon Only"), config.isWeaponOnlyCasting())
						.setTooltip(Component.nullToEmpty("Allow casting keybinds only when a weapon is held."))
						.setDefaultValue(ClothConfig.getDefaultWeaponOnlyCasting())
						.setSaveConsumer(config::setWeaponOnlyCasting).build());
		generalCategory.addEntry(entryBuilder
				.startBooleanToggle(Component.nullToEmpty("Block Clicks"), config.getBlockClicks())
				.setTooltip(Component.nullToEmpty(
						"Block left or right mouse clicks while a spell sequence is in progress."))
				.setDefaultValue(ClothConfig.getDefaultBlockClicks()).setSaveConsumer(config::setBlockClicks).build());
		generalCategory.addEntry(
				entryBuilder.startBooleanToggle(Component.nullToEmpty("Use Auto Delay"), config.shouldUseAutoDelay())
						.setTooltip(Component.nullToEmpty(
								"Use ping-based timing instead of Manual Delay. Experimental; manual delay is recommended for now."))
						.setDefaultValue(ClothConfig.getDefaultUseAutoDelay()).setSaveConsumer(config::setUseAutoDelay)
						.build());
		generalCategory.addEntry(
				entryBuilder.startBooleanToggle(Component.nullToEmpty("Repeat Held Melee Key"), config.getRepeatHeldKeys())
						.setTooltip(Component.nullToEmpty(
								"Hold the melee key to repeat attacks, similar to holding down the mouse button."))
						.setDefaultValue(ClothConfig.getDefaultRepeatHeldKeys())
						.setSaveConsumer(config::setRepeatHeldKeys).build());
		generalCategory.addEntry(entryBuilder
				.startIntField(Component.nullToEmpty("Auto Delay Tolerance"), config.getAutoDelayTolerance())
				.setTooltip(Component.nullToEmpty(
						"Milliseconds of error allowed per calculation. More is accurate. Less is faster."))
				.setDefaultValue(ClothConfig.getDefaultAutoDelayTolerance()).setSaveConsumer(value -> {
					int clamped = Math.max(0, value);
					config.setAutoDelayTolerance(clamped);
				}).build());
		generalCategory.addEntry(entryBuilder
				.startIntField(Component.nullToEmpty("Manual Delay"), config.getManualDelay())
				.setTooltip(Component.nullToEmpty(
						"Milliseconds between each click in a spell sequence. Start around 100 ms and adjust for your connection."))
				.setDefaultValue(ClothConfig.getDefaultManualDelay()).setSaveConsumer(value -> {
					int clamped = Math.max(Utils.MIN_MANUAL_DELAY_MS, value);
					config.setManualDelay(clamped);
				}).build());
		generalCategory.addEntry(entryBuilder
				.startIntField(Component.nullToEmpty("Repeat Threshold"), config.getRepeatThreshold())
				.setTooltip(Component.nullToEmpty(
						"The delay in milliseconds before the pressed key is counted as held key to repeat same action."))
				.setDefaultValue(ClothConfig.getDefaultRepeatThreshold()).setSaveConsumer(value -> {
					int clamped = Math.max(0, value);
					config.setRepeatThreshold(clamped);
				}).build());

		ConfigCategory keybindsCategory = builder.getOrCreateCategory(Component.nullToEmpty("Keybinds"));
		addKeybind(keybindsCategory, entryBuilder, WynnSpellsClient.CONFIG_KEY);
		addKeybind(keybindsCategory, entryBuilder, WynnSpellsClient.MELEE_KEY);
		addKeybind(keybindsCategory, entryBuilder, WynnSpellsClient.FIRST_SPELL_KEY);
		addKeybind(keybindsCategory, entryBuilder, WynnSpellsClient.SECOND_SPELL_KEY);
		addKeybind(keybindsCategory, entryBuilder, WynnSpellsClient.THIRD_SPELL_KEY);
		addKeybind(keybindsCategory, entryBuilder, WynnSpellsClient.FOURTH_SPELL_KEY);

		return builder.build();
	}

	private static void addKeybind(ConfigCategory category, ConfigEntryBuilder entryBuilder, KeyMapping keyBinding) {
		category.addEntry(entryBuilder
				.startKeyCodeField(Component.translatable(keyBinding.getName()),
						InputConstants.getKey(keyBinding.saveString()))
				.setTooltip(Component
						.nullToEmpty("Keybind for " + Component.translatable(keyBinding.getName()).getString()))
				.setDefaultValue(keyBinding.getDefaultKey()).setKeySaveConsumer(value -> {
					keyBinding.setKey(value);
					Utils.refreshAndSaveKeyBindings();
				}).build());
	}
}
