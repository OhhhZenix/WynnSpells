package dev.zenix.wynnspells.client;

import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.Text;

public class ConfigScreen {

	public static Screen create(Screen parent) {
		ConfigBuilder builder = ConfigBuilder.create().setParentScreen(parent).setTitle(Text.of("WynnSpells"));
		ConfigEntryBuilder entryBuilder = builder.entryBuilder();

		WynnSpellsClient wynnSpellsClient = WynnSpellsClient.getInstance();
		ClothConfig config = wynnSpellsClient.getConfig();
		builder.setSavingRunnable(wynnSpellsClient::saveConfig);

		// General
		ConfigCategory generalCategory = builder.getOrCreateCategory(Text.of("General"));
		generalCategory
				.addEntry(entryBuilder.startBooleanToggle(Text.of("Notify Updates"), config.shouldNotifyUpdates())
						.setTooltip(Text.of("To enable or disable update notifications."))
						.setDefaultValue(ClothConfig.getDefaultNotifyUpdates())
						.setSaveConsumer(config::setNotifyUpdates).build());
		generalCategory
				.addEntry(entryBuilder.startBooleanToggle(Text.of("Notify Busy Cast"), config.shouldNotifyBusyCast())
						.setTooltip(Text.of("To enable or disable busy cast notifications."))
						.setDefaultValue(ClothConfig.getDefaultNotifyBusyCast())
						.setSaveConsumer(config::setNotifyBusyCast).build());
		generalCategory.addEntry(entryBuilder.startBooleanToggle(Text.of("Weapon Only"), config.isWeaponOnlyCasting())
				.setTooltip(Text.of("Allow casting keybinds only when a weapon is held."))
				.setDefaultValue(ClothConfig.getDefaultWeaponOnlyCasting())
				.setSaveConsumer(config::setWeaponOnlyCasting).build());
		generalCategory.addEntry(entryBuilder.startBooleanToggle(Text.of("Block Clicks"), config.getBlockClicks())
				.setTooltip(Text.of("Block left or right clicks while a spell is casting."))
				.setDefaultValue(ClothConfig.getDefaultBlockClicks()).setSaveConsumer(config::setBlockClicks).build());
		generalCategory.addEntry(entryBuilder.startBooleanToggle(Text.of("Use Auto Delay"), config.shouldUseAutoDelay())
				.setTooltip(Text.of("Automatically calculates the most optimal delay for you."))
				.setDefaultValue(ClothConfig.getDefaultUseAutoDelay()).setSaveConsumer(config::setUseAutoDelay)
				.build());
		generalCategory
				.addEntry(entryBuilder.startBooleanToggle(Text.of("Repeat Held Keys"), config.getRepeatHeldKeys())
						.setTooltip(Text.of("Allow action of a held key to be repeated."))
						.setDefaultValue(ClothConfig.getDefaultRepeatHeldKeys())
						.setSaveConsumer(config::setRepeatHeldKeys).build());
		generalCategory.addEntry(entryBuilder
				.startIntField(Text.of("Auto Delay Tolerance"), config.getAutoDelayTolerance())
				.setTooltip(Text.of("Milliseconds of error allowed per calculation. More is accurate. Less is faster."))
				.setDefaultValue(ClothConfig.getDefaultAutoDelayTolerance()).setSaveConsumer(value -> {
					int clamped = Math.max(0, value);
					config.setAutoDelayTolerance(clamped);
				}).build());
		generalCategory.addEntry(entryBuilder.startIntField(Text.of("Manual Delay"), config.getManualDelay())
				.setTooltip(Text.of("The delay between clicks. This value is ignored if auto delay is enabled."))
				.setDefaultValue(ClothConfig.getDefaultManualDelay()).setSaveConsumer(value -> {
					int clamped = Math.max(0, value);
					config.setManualDelay(clamped);
				}).build());
		generalCategory.addEntry(entryBuilder.startIntField(Text.of("Repeat Threshold"), config.getRepeatThreshold())
				.setTooltip(Text.of(
						"The delay in milliseconds before the pressed key is counted as held key to repeat same action."))
				.setDefaultValue(ClothConfig.getDefaultRepeatThreshold()).setSaveConsumer(value -> {
					int clamped = Math.max(0, value);
					config.setRepeatThreshold(clamped);
				}).build());
		generalCategory.addEntry(entryBuilder.startIntField(Text.of("Buffer Limit"), config.getBufferLimit())
				.setTooltip(
						Text.of("The amount of actions that is tolorated before it is ignored. Reduces key ghosting."))
				.setDefaultValue(ClothConfig.getDefaultBufferLimit()).setSaveConsumer(value -> {
					int clamped = Math.max(0, value);
					config.setBufferLimit(clamped);
				}).build());

		ConfigCategory keybindsCategory = builder.getOrCreateCategory(Text.of("Keybinds"));
		addKeybind(keybindsCategory, entryBuilder, WynnSpellsClient.CONFIG_KEY);
		addKeybind(keybindsCategory, entryBuilder, WynnSpellsClient.MELEE_KEY);
		addKeybind(keybindsCategory, entryBuilder, WynnSpellsClient.FIRST_SPELL_KEY);
		addKeybind(keybindsCategory, entryBuilder, WynnSpellsClient.SECOND_SPELL_KEY);
		addKeybind(keybindsCategory, entryBuilder, WynnSpellsClient.THIRD_SPELL_KEY);
		addKeybind(keybindsCategory, entryBuilder, WynnSpellsClient.FOURTH_SPELL_KEY);

		return builder.build();
	}

	private static void addKeybind(ConfigCategory category, ConfigEntryBuilder entryBuilder, KeyBinding keyBinding) {
		category.addEntry(entryBuilder
				.startKeyCodeField(Text.translatable(keyBinding.getId()),
						InputUtil.fromTranslationKey(keyBinding.getBoundKeyTranslationKey()))
				.setTooltip(Text.of("Keybind for " + Text.translatable(keyBinding.getId()).getString()))
				.setDefaultValue(keyBinding.getDefaultKey()).setKeySaveConsumer(value -> {
					keyBinding.setBoundKey(value);
					Utils.refreshAndSaveKeyBindings();
				}).build());
	}
}
