package dev.zenix.wynnspells.client.config;

import com.mojang.blaze3d.platform.InputConstants;
import dev.zenix.wynnspells.WynnSpells;
import dev.zenix.wynnspells.client.WynnSpellsClient;
import dev.zenix.wynnspells.client.core.Utils;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class ConfigScreen {

  public static Screen create(Screen parent) {
    ConfigBuilder builder =
        ConfigBuilder.create()
            .setParentScreen(parent)
            .setTitle(Component.nullToEmpty(WynnSpells.MOD_NAME));
    ConfigEntryBuilder entryBuilder = builder.entryBuilder();

    WynnSpellsClient wynnSpellsClient = WynnSpellsClient.getInstance();
    ClothConfig config = wynnSpellsClient.getConfig();
    builder.setSavingRunnable(wynnSpellsClient::saveConfig);

    // General
    ConfigCategory generalCategory = builder.getOrCreateCategory(Component.nullToEmpty("General"));
    generalCategory.addEntry(
        entryBuilder
            .startBooleanToggle(Component.nullToEmpty("Notify Updates"), config.isNotifyUpdates())
            .setTooltip(Component.nullToEmpty("To enable or disable update notifications."))
            .setDefaultValue(ClothConfig.Defaults.NOTIFY_UPDATES)
            .setSaveConsumer(config::setNotifyUpdates)
            .build());
    generalCategory.addEntry(
        entryBuilder
            .startBooleanToggle(
                Component.nullToEmpty("Notify Busy Cast"), config.isNotifyBusyCast())
            .setTooltip(Component.nullToEmpty("To enable or disable busy cast notifications."))
            .setDefaultValue(ClothConfig.Defaults.NOTIFY_BUSY_CAST)
            .setSaveConsumer(config::setNotifyBusyCast)
            .build());
    generalCategory.addEntry(
        entryBuilder
            .startBooleanToggle(Component.nullToEmpty("Weapon Only"), config.isWeaponOnlyCasting())
            .setTooltip(Component.nullToEmpty("Allow casting keybinds only when a weapon is held."))
            .setDefaultValue(ClothConfig.Defaults.WEAPON_ONLY_CASTING)
            .setSaveConsumer(config::setWeaponOnlyCasting)
            .build());
    generalCategory.addEntry(
        entryBuilder
            .startBooleanToggle(Component.nullToEmpty("Block Clicks"), config.isBlockClicks())
            .setTooltip(
                Component.nullToEmpty("Block left or right clicks while a spell is casting."))
            .setDefaultValue(ClothConfig.Defaults.BLOCK_CLICKS)
            .setSaveConsumer(config::setBlockClicks)
            .build());

    ConfigCategory timingsCategory = builder.getOrCreateCategory(Component.nullToEmpty("Timings"));
    timingsCategory.addEntry(
        entryBuilder
            .startBooleanToggle(Component.nullToEmpty("Use Auto Delay"), config.isUseAutoDelay())
            .setTooltip(
                Component.nullToEmpty("Automatically calculates the most optimal delay for you."))
            .setDefaultValue(ClothConfig.Defaults.USE_AUTO_DELAY)
            .setSaveConsumer(config::setUseAutoDelay)
            .build());
    timingsCategory.addEntry(
        entryBuilder
            .startIntField(
                Component.nullToEmpty("Auto Delay Tolerance"), config.getAutoDelayTolerance())
            .setTooltip(
                Component.nullToEmpty(
                    "Milliseconds of error allowed per calculation. More is accurate. Less is faster."))
            .setDefaultValue(ClothConfig.Defaults.AUTO_DELAY_TOLERANCE)
            .setSaveConsumer(
                value -> {
                  int clamped = Math.max(0, value);
                  config.setAutoDelayTolerance(clamped);
                })
            .build());
    timingsCategory.addEntry(
        entryBuilder
            .startIntField(Component.nullToEmpty("Manual Delay"), config.getManualDelay())
            .setTooltip(
                Component.nullToEmpty(
                    "The delay between clicks. This value is ignored if auto delay is enabled."))
            .setDefaultValue(ClothConfig.Defaults.MANUAL_DELAY)
            .setSaveConsumer(
                value -> {
                  int clamped = Math.max(0, value);
                  config.setManualDelay(clamped);
                })
            .build());
    timingsCategory.addEntry(
        entryBuilder
            .startIntField(Component.nullToEmpty("Ping Look Back"), config.getPingLookBack())
            .setTooltip(
                Component.nullToEmpty(
                    "This value determines how your average ping is calculated. The higher the smoother."))
            .setDefaultValue(ClothConfig.Defaults.PING_LOOK_BACK)
            .setSaveConsumer(
                value -> {
                  int clamped = Math.max(1, value);
                  config.setPingLookBack(clamped);
                })
            .build());

    ConfigCategory inputsCategory = builder.getOrCreateCategory(Component.nullToEmpty("Inputs"));
    inputsCategory.addEntry(
        entryBuilder
            .startBooleanToggle(
                Component.nullToEmpty("Repeat Held Keys"), config.isRepeatHeldKeys())
            .setTooltip(Component.nullToEmpty("Allow action of a held key to be repeated."))
            .setDefaultValue(ClothConfig.Defaults.REPEAT_HELD_KEYS)
            .setSaveConsumer(config::setRepeatHeldKeys)
            .build());
    inputsCategory.addEntry(
        entryBuilder
            .startIntField(Component.nullToEmpty("Repeat Threshold"), config.getRepeatThreshold())
            .setTooltip(
                Component.nullToEmpty(
                    "The delay in milliseconds before the pressed key is counted as held key to repeat same action."))
            .setDefaultValue(ClothConfig.Defaults.REPEAT_THRESHOLD)
            .setSaveConsumer(
                value -> {
                  int clamped = Math.max(0, value);
                  config.setRepeatThreshold(clamped);
                })
            .build());
    addKeybind(inputsCategory, entryBuilder, WynnSpellsClient.CONFIG_KEY);
    addKeybind(inputsCategory, entryBuilder, WynnSpellsClient.MELEE_KEY);
    addKeybind(inputsCategory, entryBuilder, WynnSpellsClient.FIRST_SPELL_KEY);
    addKeybind(inputsCategory, entryBuilder, WynnSpellsClient.SECOND_SPELL_KEY);
    addKeybind(inputsCategory, entryBuilder, WynnSpellsClient.THIRD_SPELL_KEY);
    addKeybind(inputsCategory, entryBuilder, WynnSpellsClient.FOURTH_SPELL_KEY);

    return builder.build();
  }

  private static void addKeybind(
      ConfigCategory category, ConfigEntryBuilder entryBuilder, KeyMapping keyBinding) {
    category.addEntry(
        entryBuilder
            .startKeyCodeField(
                Component.translatable(keyBinding.getName()),
                InputConstants.getKey(keyBinding.saveString()))
            .setTooltip(
                Component.nullToEmpty(
                    "Keybind for " + Component.translatable(keyBinding.getName()).getString()))
            .setDefaultValue(keyBinding.getDefaultKey())
            .setKeySaveConsumer(
                value -> {
                  keyBinding.setKey(value);
                  Utils.refreshAndSaveKeyBindings();
                })
            .build());
  }
}
