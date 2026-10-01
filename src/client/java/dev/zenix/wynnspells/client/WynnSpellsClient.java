package dev.zenix.wynnspells.client;

import com.mojang.blaze3d.platform.InputConstants;
import dev.zenix.wynnspells.WynnSpells;
import dev.zenix.wynnspells.client.config.ClothConfig;
import dev.zenix.wynnspells.client.config.ConfigScreen;
import dev.zenix.wynnspells.client.core.PingTracker;
import dev.zenix.wynnspells.client.core.UpdateChecker;
import dev.zenix.wynnspells.client.spell.SpellQueue;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.GsonConfigSerializer;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.Identifier;

public class WynnSpellsClient implements ClientModInitializer {

  public static final KeyMapping.Category KEY_CATEGORY =
      KeyMapping.Category.register(Identifier.fromNamespaceAndPath(WynnSpells.MOD_ID, "all"));

  public static final KeyMapping CONFIG_KEY =
      KeyMappingHelper.registerKeyMapping(
          new KeyMapping(
              "key.wynnspells.config",
              InputConstants.Type.KEYBOARD,
              InputConstants.UNKNOWN.getValue(),
              KEY_CATEGORY));

  private static WynnSpellsClient instance = null;
  private ClothConfig config;
  private UpdateChecker updateChecker;
  private PingTracker pingTracker;
  private SpellQueue spellQueue;

  public static WynnSpellsClient getInstance() {
    return instance;
  }

  @Override
  public void onInitializeClient() {
    instance = this;
    ClientLifecycleEvents.CLIENT_STARTED.register(this::onStart);
    ClientLifecycleEvents.CLIENT_STOPPING.register(this::onStop);
    ClientTickEvents.END_CLIENT_TICK.register(this::processConfigKey);
  }

  private void onStart(Minecraft client) {
    loadConfig();

    updateChecker = new UpdateChecker();
    updateChecker.start();

    pingTracker = new PingTracker(client);
    pingTracker.start();

    spellQueue = new SpellQueue(client);
    spellQueue.start();
  }

  private void onStop(Minecraft client) {
    updateChecker.stop();
    pingTracker.stop();
    spellQueue.stop();
  }

  private void processConfigKey(Minecraft client) {
    if (CONFIG_KEY.consumeClick()) {
      client.setScreenAndShow(ConfigScreen.create(client.gui.screen()));
    }
  }

  public ClothConfig getConfig() {
    return config;
  }

  private void loadConfig() {
    AutoConfig.register(ClothConfig.class, GsonConfigSerializer::new);
    config = AutoConfig.getConfigHolder(ClothConfig.class).getConfig();
    WynnSpells.LOGGER.info("Config loaded successfully");
  }

  public void saveConfig() {
    WynnSpells.LOGGER.debug("Saving configuration");
    AutoConfig.getConfigHolder(ClothConfig.class).save();
  }

  public PingTracker getPingTracker() {
    return pingTracker;
  }
}
