package dev.zenix.wynnspells.client;

import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

public class WynnSpellsConfigScreen {

    public static Screen create(Screen parent) {
        ConfigBuilder builder =
                ConfigBuilder.create().setParentScreen(parent).setTitle(Text.of("WynnSpells"));
        ConfigEntryBuilder entryBuilder = builder.entryBuilder();

        WynnSpellsClient wynnSpellsClient = WynnSpellsClient.getInstance();
        WynnSpellsConfig config = wynnSpellsClient.getConfig();
        builder.setSavingRunnable(wynnSpellsClient::saveConfig);

        // config.wynnspells.general.delay.title
        // config.wynnspells.general.delay.description
        // General
        ConfigCategory generalCategory = builder.getOrCreateCategory(Text.of("General"));
        generalCategory
                .addEntry(entryBuilder.startIntField(Text.of("Delay"), config.getDelayMillis())
                        .setTooltip(Text.of("The delay between actions of each queue block."))
                        .setDefaultValue(WynnSpellsConfig.getDefaultDelayMillis())
                        .setSaveConsumer(value -> config.setDelayMillis(value)).build());
        generalCategory
                .addEntry(entryBuilder.startIntField(Text.of("Queue Limit"), config.getQueueLimit())
                        .setTooltip(Text.of("The amount of actions that can be in a queue."))
                        .setDefaultValue(WynnSpellsConfig.getDefaultQueueLimit())
                        .setSaveConsumer(value -> config.setQueueLimit(value)).build());

        // TODO: Add keybinding

        return builder.build();
    }
}
