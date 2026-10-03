package dev.zenix.wynnspells.client;

import com.mojang.blaze3d.platform.InputConstants;
import dev.zenix.wynnspells.WynnSpells;
import dev.zenix.wynnspells.client.config.ClothConfig;
import dev.zenix.wynnspells.client.config.ConfigScreen;
import dev.zenix.wynnspells.client.core.PingTracker;
import dev.zenix.wynnspells.client.core.UpdateChecker;
import dev.zenix.wynnspells.client.spell.SpellCaster;
import java.util.concurrent.atomic.AtomicBoolean;
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

  private static final KeyMapping.Category KEY_CATEGORY =
      KeyMapping.Category.register(Identifier.fromNamespaceAndPath(WynnSpells.MOD_ID, "all"));

  public static final KeyMapping FIRST_SPELL_KEY =
      KeyMappingHelper.registerKeyMapping(
          new KeyMapping(
              "key.wynnspells.first",
              InputConstants.Type.KEYBOARD,
              InputConstants.UNKNOWN.getValue(),
              WynnSpellsClient.KEY_CATEGORY));

  public static final KeyMapping SECOND_SPELL_KEY =
      KeyMappingHelper.registerKeyMapping(
          new KeyMapping(
              "key.wynnspells.second",
              InputConstants.Type.KEYBOARD,
              InputConstants.UNKNOWN.getValue(),
              WynnSpellsClient.KEY_CATEGORY));

  public static final KeyMapping THIRD_SPELL_KEY =
      KeyMappingHelper.registerKeyMapping(
          new KeyMapping(
              "key.wynnspells.third",
              InputConstants.Type.KEYBOARD,
              InputConstants.UNKNOWN.getValue(),
              WynnSpellsClient.KEY_CATEGORY));

  public static final KeyMapping FOURTH_SPELL_KEY =
      KeyMappingHelper.registerKeyMapping(
          new KeyMapping(
              "key.wynnspells.fourth",
              InputConstants.Type.KEYBOARD,
              InputConstants.UNKNOWN.getValue(),
              WynnSpellsClient.KEY_CATEGORY));

  public static final KeyMapping MELEE_KEY =
      KeyMappingHelper.registerKeyMapping(
          new KeyMapping(
              "key.wynnspells.melee",
              InputConstants.Type.KEYBOARD,
              InputConstants.UNKNOWN.getValue(),
              WynnSpellsClient.KEY_CATEGORY));

  public static final KeyMapping CONFIG_KEY =
      KeyMappingHelper.registerKeyMapping(
          new KeyMapping(
              "key.wynnspells.config",
              InputConstants.Type.KEYBOARD,
              InputConstants.UNKNOWN.getValue(),
              KEY_CATEGORY));

  private static final AtomicBoolean running = new AtomicBoolean(true);
  private static WynnSpellsClient instance = null;
  private ClothConfig config;
  private PingTracker pingTracker;

  public static WynnSpellsClient getInstance() {
    return instance;
  }

  public static boolean isRunning() {
    return running.get();
  }

  @Override
  public void onInitializeClient() {
    instance = this;
    ClientLifecycleEvents.CLIENT_STARTED.register(this::onStart);
    ClientLifecycleEvents.CLIENT_STOPPING.register(this::onStop);
    ClientTickEvents.END_CLIENT_TICK.register(this::processConfigKey);
  }

  private void onStart(Minecraft mc) {
    loadConfig();

    pingTracker = new PingTracker(mc);
    pingTracker.start();

    SpellCaster spellCaster = new SpellCaster(mc);
    spellCaster.start();

    UpdateChecker updateChecker = new UpdateChecker();
    updateChecker.start();
  }

  private void onStop(Minecraft mc) {
    running.set(false);
  }

  private void processConfigKey(Minecraft mc) {
    if (CONFIG_KEY.consumeClick()) {
      mc.setScreenAndShow(ConfigScreen.create(mc.gui.screen()));
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
