package dev.zenix.wynnspells.client;

import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.atomic.AtomicBoolean;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.config.Configurator;
import org.lwjgl.glfw.GLFW;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.GsonConfigSerializer;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.option.KeyBinding.Category;
import net.minecraft.client.util.InputUtil;
import net.minecraft.item.ItemStack;

import net.minecraft.util.Identifier;

public class WynnSpellsClient implements ClientModInitializer {

    public static final String MOD_NAME = "WynnSpells";
    public static final String MOD_ID = "wynnspells";
    public static final Logger LOGGER = LogManager.getLogger(MOD_ID);

    private static WynnSpellsClient instance = null;
    private WynnSpellsConfig config = null;

    private AtomicBoolean running = new AtomicBoolean(true);
    private LinkedBlockingDeque<WynnSpellsIntent> buffer = new LinkedBlockingDeque<>();
    private ItemStack previousItem = null;

    private final Category KEY_CATEGORY = Category.create(Identifier.of("wynnspells", "all"));
    private final KeyBinding FIRST_SPELL_KEY =
            KeyBindingHelper.registerKeyBinding(new KeyBinding("key.wynnspells.first",
                    InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_UNKNOWN, KEY_CATEGORY));
    private final KeyBinding SECOND_SPELL_KEY =
            KeyBindingHelper.registerKeyBinding(new KeyBinding("key.wynnspells.second",
                    InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_UNKNOWN, KEY_CATEGORY));
    private final KeyBinding THIRD_SPELL_KEY =
            KeyBindingHelper.registerKeyBinding(new KeyBinding("key.wynnspells.third",
                    InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_UNKNOWN, KEY_CATEGORY));
    private final KeyBinding FOURTH_SPELL_KEY =
            KeyBindingHelper.registerKeyBinding(new KeyBinding("key.wynnspells.fourth",
                    InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_UNKNOWN, KEY_CATEGORY));
    private final KeyBinding MELEE_KEY =
            KeyBindingHelper.registerKeyBinding(new KeyBinding("key.wynnspells.melee",
                    InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_UNKNOWN, KEY_CATEGORY));
    private final KeyBinding CONFIG_KEY =
            KeyBindingHelper.registerKeyBinding(new KeyBinding("key.wynnspells.config",
                    InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_UNKNOWN, KEY_CATEGORY));

    @Override
    public void onInitializeClient() {
        instance = this;
        setupLogger();
        loadConfig();
        ClientLifecycleEvents.CLIENT_STARTED.register(client -> onClientStart(client));
        ClientLifecycleEvents.CLIENT_STOPPING.register(client -> onClientStop(client));
        ClientTickEvents.END_CLIENT_TICK.register(client -> onClientEndTick(client));
    }

    public static WynnSpellsClient getInstance() {
        return instance;
    }

    private void setupLogger() {
        Configurator.setLevel(LOGGER.getName(), Level.INFO);
    }

    private void loadConfig() {
        AutoConfig.register(WynnSpellsConfig.class, GsonConfigSerializer::new);
        config = AutoConfig.getConfigHolder(WynnSpellsConfig.class).getConfig();
        LOGGER.info("Config loaded successfully");
    }

    public WynnSpellsConfig getConfig() {
        return config;
    }

    public void saveConfig() {
        LOGGER.debug("Saving configuration");
        AutoConfig.getConfigHolder(WynnSpellsConfig.class).save();
    }

    private void onClientStart(MinecraftClient client) {
        WynnSpellsPingPong.start();

        Thread wynnSpells = new Thread(new WynnSpellsCaster(buffer, running));
        wynnSpells.setDaemon(true);
        wynnSpells.start();

        Thread updateChecker = new Thread(new WynnSpellsUpdateChecker(running));
        updateChecker.setDaemon(true);
        updateChecker.start();
    }

    private void onClientStop(MinecraftClient client) {
        WynnSpellsPingPong.stop();
        running.set(false);
    }

    private void onClientEndTick(MinecraftClient client) {
        processConfigKey(client);
        processIntentKey(client, FIRST_SPELL_KEY, WynnSpellsIntent.FIRST_SPELL);
        processIntentKey(client, SECOND_SPELL_KEY, WynnSpellsIntent.SECOND_SPELL);
        processIntentKey(client, THIRD_SPELL_KEY, WynnSpellsIntent.THIRD_SPELL);
        processIntentKey(client, FOURTH_SPELL_KEY, WynnSpellsIntent.FOURTH_SPELL);
        processIntentKey(client, MELEE_KEY, WynnSpellsIntent.MELEE);
    }

    private void processIntentKey(MinecraftClient client, KeyBinding key, WynnSpellsIntent intent) {
        if (key == null)
            return;

        if (!key.isPressed())
            return;

        if (buffer.size() >= config.getBufferLimit()) {
            return;
        }

        if (client == null || client.player == null) {
            return;
        }

        ItemStack itemInMainHand = client.player.getMainHandStack();
        if (itemInMainHand != previousItem) {
            previousItem = itemInMainHand;
            buffer.clear();;
        }

        key.setPressed(false);
        buffer.add(intent);
    }

    private void processConfigKey(MinecraftClient client) {
        if (CONFIG_KEY == null)
            return;

        if (!CONFIG_KEY.isPressed())
            return;

        CONFIG_KEY.setPressed(false);
        client.setScreen(WynnSpellsConfigScreen.create(client.currentScreen));
    }
}
