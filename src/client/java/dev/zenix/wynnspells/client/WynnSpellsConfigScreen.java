package dev.zenix.wynnspells.client;

import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.Text;

public class WynnSpellsConfigScreen {

    public static Screen create(Screen parent) {
        ConfigBuilder builder = ConfigBuilder.create()
            .setParentScreen(parent)
            .setTitle(Text.of("WynnSpells"));
        ConfigEntryBuilder entryBuilder = builder.entryBuilder();

        WynnSpellsClient wynnSpellsClient = WynnSpellsClient.getInstance();
        WynnSpellsConfig config = wynnSpellsClient.getConfig();
        builder.setSavingRunnable(wynnSpellsClient::saveConfig);

        // General
        ConfigCategory generalCategory = builder.getOrCreateCategory(
            Text.of("General")
        );
        generalCategory.addEntry(
            entryBuilder
                .startBooleanToggle(
                    Text.of("Notify Updates"),
                    config.shouldNotifyUpdates()
                )
                .setTooltip(
                    Text.of("To enable or disable update notifications.")
                )
                .setDefaultValue(WynnSpellsConfig.getDefaultNotifyUpdates())
                .setSaveConsumer(value -> config.setNotifyUpdates(value))
                .build()
        );
        generalCategory.addEntry(
            entryBuilder
                .startBooleanToggle(
                    Text.of("Notify Busy Cast"),
                    config.shouldNotifyBusyCast()
                )
                .setTooltip(
                    Text.of("To enable or disable busy cast notifications.")
                )
                .setDefaultValue(WynnSpellsConfig.getDefaultNotifyBusyCast())
                .setSaveConsumer(value -> config.setNotifyBusyCast(value))
                .build()
        );
        generalCategory.addEntry(
            entryBuilder
                .startBooleanToggle(
                    Text.of("Use Auto Delay"),
                    config.shouldUseAutoDelay()
                )
                .setTooltip(
                    Text.of(
                        "Automatically calculates the most optimal delay for you."
                    )
                )
                .setDefaultValue(WynnSpellsConfig.getDefaultUseAutoDelay())
                .setSaveConsumer(value -> config.setUseAutoDelay(value))
                .build()
        );
        generalCategory.addEntry(
            entryBuilder
                .startIntField(Text.of("Manual Delay"), config.getManualDelay())
                .setTooltip(
                    Text.of(
                        "The delay between clicks. This value is ignored if auto delay is enabled."
                    )
                )
                .setDefaultValue(WynnSpellsConfig.getDefaultManualDelay())
                .setSaveConsumer(value -> config.setManualDelay(value))
                .build()
        );
        generalCategory.addEntry(
            entryBuilder
                .startIntField(Text.of("Buffer Limit"), config.getBufferLimit())
                .setTooltip(
                    Text.of(
                        "The amount of actions that is tolorated before it is ignored. Reduces key ghosting."
                    )
                )
                .setDefaultValue(WynnSpellsConfig.getDefaultBufferLimit())
                .setSaveConsumer(value -> config.setBufferLimit(value))
                .build()
        );

        ConfigCategory keybindsCategory = builder.getOrCreateCategory(
            Text.of("Keybinds")
        );
        addKeybind(keybindsCategory, entryBuilder, WynnSpellsClient.CONFIG_KEY);
        addKeybind(keybindsCategory, entryBuilder, WynnSpellsClient.MELEE_KEY);
        addKeybind(
            keybindsCategory,
            entryBuilder,
            WynnSpellsClient.FIRST_SPELL_KEY
        );
        addKeybind(
            keybindsCategory,
            entryBuilder,
            WynnSpellsClient.SECOND_SPELL_KEY
        );
        addKeybind(
            keybindsCategory,
            entryBuilder,
            WynnSpellsClient.THIRD_SPELL_KEY
        );
        addKeybind(
            keybindsCategory,
            entryBuilder,
            WynnSpellsClient.FOURTH_SPELL_KEY
        );

        return builder.build();
    }

    private static void addKeybind(
        ConfigCategory category,
        ConfigEntryBuilder entryBuilder,
        KeyBinding keyBinding
    ) {
        category.addEntry(
            entryBuilder
                .startKeyCodeField(
                    Text.translatable(keyBinding.getId()),
                    InputUtil.fromTranslationKey(
                        keyBinding.getBoundKeyTranslationKey()
                    )
                )
                .setTooltip(
                    Text.of(
                        "Keybind for " +
                            Text.translatable(keyBinding.getId()).getString()
                    )
                )
                .setDefaultValue(keyBinding.getDefaultKey())
                .setKeySaveConsumer(value -> {
                    keyBinding.setBoundKey(value);
                    WynnSpellsUtils.refreshAndSaveKeyBindings();
                })
                .build()
        );
    }
}
