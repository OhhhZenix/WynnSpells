package dev.zenix.wynnspells.client;

import dev.zenix.wynnspells.WynnSpells;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.GsonConfigSerializer;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.util.Identifier;
import org.lwjgl.glfw.GLFW;

public class WynnSpellsClient implements ClientModInitializer {

	private static final KeyBinding.Category KEY_CATEGORY = KeyBinding.Category
			.create(Identifier.of(WynnSpells.MOD_ID, "all"));
	public static final KeyBinding FIRST_SPELL_KEY = KeyBindingHelper.registerKeyBinding(
			new KeyBinding("key.wynnspells.first", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_UNKNOWN, KEY_CATEGORY));
	public static final KeyBinding SECOND_SPELL_KEY = KeyBindingHelper.registerKeyBinding(
			new KeyBinding("key.wynnspells.second", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_UNKNOWN, KEY_CATEGORY));
	public static final KeyBinding THIRD_SPELL_KEY = KeyBindingHelper.registerKeyBinding(
			new KeyBinding("key.wynnspells.third", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_UNKNOWN, KEY_CATEGORY));
	public static final KeyBinding FOURTH_SPELL_KEY = KeyBindingHelper.registerKeyBinding(
			new KeyBinding("key.wynnspells.fourth", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_UNKNOWN, KEY_CATEGORY));
	public static final KeyBinding MELEE_KEY = KeyBindingHelper.registerKeyBinding(
			new KeyBinding("key.wynnspells.melee", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_UNKNOWN, KEY_CATEGORY));
	public static final KeyBinding CONFIG_KEY = KeyBindingHelper.registerKeyBinding(
			new KeyBinding("key.wynnspells.config", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_UNKNOWN, KEY_CATEGORY));

	private static WynnSpellsClient instance = null;
	private ClothConfig config;
	private UpdateChecker updateChecker;
	private PingTracker pingTracker;
	private Caster caster;

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

	private void onClientStart(MinecraftClient client) {
		updateChecker = new UpdateChecker();
		updateChecker.start();

		pingTracker = new PingTracker(client);
		pingTracker.start();

		caster = new Caster(client);
		caster.start();
	}

	private void onClientStop(MinecraftClient client) {
		updateChecker.stop();
		pingTracker.stop();
		caster.stop();
	}

	private void onClientEndTick(MinecraftClient client) {
		processConfigKey(client);
		caster.processIntentKeys();
	}

	private void processConfigKey(MinecraftClient client) {
		if (!WynnSpellsClient.CONFIG_KEY.isPressed())
			return;

		WynnSpellsClient.CONFIG_KEY.setPressed(false);
		client.setScreen(ConfigScreen.create(client.currentScreen));
	}

	public PingTracker getPingTracker() {
		return pingTracker;
	}
}
