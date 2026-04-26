package dev.zenix.wynnspells.client;

import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class ConfigScreen {

	public static Screen create(Screen parent) {
		ConfigBuilder builder = ConfigBuilder.create().setParentScreen(parent)
				.setTitle(Component.literal("WynnSpells"));
		ConfigEntryBuilder entryBuilder = builder.entryBuilder();

		WynnSpellsClient wynnSpellsClient = WynnSpellsClient.getInstance();
		ClothConfig config = wynnSpellsClient.getConfig();
		builder.setSavingRunnable(wynnSpellsClient::saveConfig);

		// General
		ConfigCategory generalCategory = builder.getOrCreateCategory(Component.literal("General"));
		generalCategory.addEntry(
				entryBuilder.startBooleanToggle(Component.literal("Notify Updates"), config.shouldNotifyUpdates())
						.setTooltip(Component.literal("To enable or disable update notifications."))
						.setDefaultValue(ClothConfig.getDefaultNotifyUpdates())
						.setSaveConsumer(config::setNotifyUpdates).build());
		generalCategory.addEntry(
				entryBuilder.startBooleanToggle(Component.literal("Notify Busy Cast"), config.shouldNotifyBusyCast())
						.setTooltip(Component.literal("To enable or disable busy cast notifications."))
						.setDefaultValue(ClothConfig.getDefaultNotifyBusyCast())
						.setSaveConsumer(config::setNotifyBusyCast).build());
		generalCategory.addEntry(
				entryBuilder.startBooleanToggle(Component.literal("Weapon Only"), config.isWeaponOnlyCasting())
						.setTooltip(Component.literal("Allow casting keybinds only when a weapon is held."))
						.setDefaultValue(ClothConfig.getDefaultWeaponOnlyCasting())
						.setSaveConsumer(config::setWeaponOnlyCasting).build());
		generalCategory.addEntry(entryBuilder
				.startBooleanToggle(Component.literal("Block Clicks"), config.getBlockClicks())
				.setTooltip(Component.literal("Block left or right clicks while a spell is casting."))
				.setDefaultValue(ClothConfig.getDefaultBlockClicks()).setSaveConsumer(config::setBlockClicks).build());
		generalCategory.addEntry(
				entryBuilder.startBooleanToggle(Component.literal("Use Auto Delay"), config.shouldUseAutoDelay())
						.setTooltip(Component.literal("Automatically calculates the most optimal delay for you."))
						.setDefaultValue(ClothConfig.getDefaultUseAutoDelay()).setSaveConsumer(config::setUseAutoDelay)
						.build());
		generalCategory.addEntry(
				entryBuilder.startBooleanToggle(Component.literal("Repeat Held Keys"), config.getRepeatHeldKeys())
						.setTooltip(Component.literal("Allow action of a held key to be repeated."))
						.setDefaultValue(ClothConfig.getDefaultRepeatHeldKeys())
						.setSaveConsumer(config::setRepeatHeldKeys).build());
		generalCategory.addEntry(entryBuilder
				.startIntField(Component.literal("Auto Delay Tolerance"), config.getAutoDelayTolerance())
				.setTooltip(Component
						.literal("Milliseconds of error allowed per calculation. More is accurate. Less is faster."))
				.setDefaultValue(ClothConfig.getDefaultAutoDelayTolerance()).setSaveConsumer(value -> {
					int clamped = Math.max(0, value);
					config.setAutoDelayTolerance(clamped);
				}).build());
		generalCategory.addEntry(entryBuilder.startIntField(Component.literal("Manual Delay"), config.getManualDelay())
				.setTooltip(
						Component.literal("The delay between clicks. This value is ignored if auto delay is enabled."))
				.setDefaultValue(ClothConfig.getDefaultManualDelay()).setSaveConsumer(value -> {
					int clamped = Math.max(0, value);
					config.setManualDelay(clamped);
				}).build());
		generalCategory.addEntry(entryBuilder
				.startIntField(Component.literal("Repeat Threshold"), config.getRepeatThreshold())
				.setTooltip(Component.literal(
						"The delay in milliseconds before the pressed key is counted as held key to repeat same action."))
				.setDefaultValue(ClothConfig.getDefaultRepeatThreshold()).setSaveConsumer(value -> {
					int clamped = Math.max(0, value);
					config.setRepeatThreshold(clamped);
				}).build());

		// ConfigCategory keybindsCategory =
		// builder.getOrCreateCategory(Component.literal("Keybinds"));
		// addKeybind(keybindsCategory, entryBuilder, WynnSpellsClient.CONFIG_KEY);
		// addKeybind(keybindsCategory, entryBuilder, WynnSpellsClient.MELEE_KEY);
		// addKeybind(keybindsCategory, entryBuilder, WynnSpellsClient.FIRST_SPELL_KEY);
		// addKeybind(keybindsCategory, entryBuilder,
		// WynnSpellsClient.SECOND_SPELL_KEY);
		// addKeybind(keybindsCategory, entryBuilder, WynnSpellsClient.THIRD_SPELL_KEY);
		// addKeybind(keybindsCategory, entryBuilder,
		// WynnSpellsClient.FOURTH_SPELL_KEY);

		return builder.build();
	}

	// private static void addKeybind(ConfigCategory category, ConfigEntryBuilder
	// entryBuilder, KeyMapping key) {
	// category.addEntry(entryBuilder
	// .startKeyCodeField(Text.translatable(keyBinding.getId()),
	// InputUtil.fromTranslationKey(keyBinding.getBoundKeyTranslationKey()))
	// .setTooltip(Text.of("Keybind for " +
	// Text.translatable(keyBinding.getId()).getString()))
	// .setDefaultValue(keyBinding.getDefaultKey()).setKeySaveConsumer(value -> {
	// keyBinding.setBoundKey(value);
	// Utils.refreshAndSaveKeyBindings();
	// }).build());
	// }
}