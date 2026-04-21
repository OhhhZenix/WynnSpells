package dev.zenix.wynnspells.client;

import dev.zenix.wynnspells.WynnSpells;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.GsonConfigSerializer;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.Identifier;

public class WynnSpellsClient implements ClientModInitializer {

	private static final KeyMapping.Category KEY_CATEGORY = KeyMapping.Category.register(
			Identifier.fromNamespaceAndPath(WynnSpells.MOD_ID, "all"));

	// public static final KeyBinding FIRST_SPELL_KEY =
	// KeyBindingHelper.registerKeyBinding(
	// new KeyBinding("key.wynnspells.first", InputUtil.Type.KEYSYM,
	// GLFW.GLFW_KEY_UNKNOWN, KEY_CATEGORY));

	// public static final KeyBinding SECOND_SPELL_KEY =
	// KeyBindingHelper.registerKeyBinding(
	// new KeyBinding("key.wynnspells.second", InputUtil.Type.KEYSYM,
	// GLFW.GLFW_KEY_UNKNOWN, KEY_CATEGORY));

	// public static final KeyBinding THIRD_SPELL_KEY =
	// KeyBindingHelper.registerKeyBinding(
	// new KeyBinding("key.wynnspells.third", InputUtil.Type.KEYSYM,
	// GLFW.GLFW_KEY_UNKNOWN, KEY_CATEGORY));

	// public static final KeyBinding FOURTH_SPELL_KEY =
	// KeyBindingHelper.registerKeyBinding(
	// new KeyBinding("key.wynnspells.fourth", InputUtil.Type.KEYSYM,
	// GLFW.GLFW_KEY_UNKNOWN, KEY_CATEGORY));

	// public static final KeyBinding MELEE_KEY =
	// KeyBindingHelper.registerKeyBinding(
	// new KeyBinding("key.wynnspells.melee", InputUtil.Type.KEYSYM,
	// GLFW.GLFW_KEY_UNKNOWN, KEY_CATEGORY));

	// public static final KeyBinding CONFIG_KEY =
	// KeyBindingHelper.registerKeyBinding(
	// new KeyBinding("key.wynnspells.config", InputUtil.Type.KEYSYM,
	// GLFW.GLFW_KEY_UNKNOWN, KEY_CATEGORY));

	private static WynnSpellsClient instance = null;
	private ClothConfig config;
	private UpdateChecker updateChecker;

	public static WynnSpellsClient getInstance() {
		return instance;
	}

	@Override
	public void onInitializeClient() {
		instance = this;
		loadConfig();
		ClientLifecycleEvents.CLIENT_STARTED.register(this::onClientStart);
		ClientLifecycleEvents.CLIENT_STOPPING.register(this::onClientStop);
		ClientTickEvents.END_CLIENT_TICK.register(this::onClientEndTick);
	}

	private void loadConfig() {
		AutoConfig.register(ClothConfig.class, GsonConfigSerializer::new);
		config = AutoConfig.getConfigHolder(ClothConfig.class).getConfig();
		WynnSpells.LOGGER.info("Config loaded successfully");
	}

	public ClothConfig getConfig() {
		return config;
	}

	public void saveConfig() {
		WynnSpells.LOGGER.debug("Saving configuration");
		AutoConfig.getConfigHolder(ClothConfig.class).save();
	}

	private void onClientStart(Minecraft client) {
		updateChecker = new UpdateChecker();
		updateChecker.start();

		// pingTracker = new PingTracker(client);
		// pingTracker.start();

		// threadCaster = new Caster(client);
		// threadCaster.start();
	}

	private void onClientStop(Minecraft client) {
		updateChecker.stop();
	}

	private void onClientEndTick(Minecraft client) {
		processConfigKey(client);
	}

	private void processConfigKey(Minecraft client) {
		// if (!WynnSpellsClient.CONFIG_KEY.isPressed())
		// return;

		// WynnSpellsClient.CONFIG_KEY.setPressed(false);
		// client.setScreen(ConfigScreen.create(client.currentScreen));
	}
}
