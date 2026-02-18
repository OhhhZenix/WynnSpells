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

    public static final String MOD_ID = "wynnspells";
    public static final Logger LOGGER = LogManager.getLogger(MOD_ID);

    private static WynnSpellsClient instance = null;
    private WynnSpellsConfig config = null;

    private AtomicBoolean running = new AtomicBoolean(true);
    private LinkedBlockingDeque<WynnSpellsQueue> queueList = new LinkedBlockingDeque<>();
    private ItemStack previousItem = null;

    private final Category KEY_CATEGORY = Category.create(Identifier.of("wynnspells", "all"));
    private KeyBinding firstSpellKey =
            KeyBindingHelper.registerKeyBinding(new KeyBinding("key.wynnspells.first",
                    InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_UNKNOWN, KEY_CATEGORY));
    private KeyBinding secondSpellKey =
            KeyBindingHelper.registerKeyBinding(new KeyBinding("key.wynnspells.second",
                    InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_UNKNOWN, KEY_CATEGORY));
    private KeyBinding thirdSpellKey =
            KeyBindingHelper.registerKeyBinding(new KeyBinding("key.wynnspells.third",
                    InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_UNKNOWN, KEY_CATEGORY));
    private KeyBinding fourthSpellKey =
            KeyBindingHelper.registerKeyBinding(new KeyBinding("key.wynnspells.fourth",
                    InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_UNKNOWN, KEY_CATEGORY));
    private KeyBinding meleeKey =
            KeyBindingHelper.registerKeyBinding(new KeyBinding("key.wynnspells.melee",
                    InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_UNKNOWN, KEY_CATEGORY));
    private KeyBinding configKey =
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
        Configurator.setLevel(LOGGER.getName(), Level.DEBUG);
    }

    private void loadConfig() {
        AutoConfig.register(WynnSpellsConfig.class, GsonConfigSerializer::new);
        config = AutoConfig.getConfigHolder(WynnSpellsConfig.class).getConfig();
        // LOGGER.info("Config loaded successfully");
    }

    public WynnSpellsConfig getConfig() {
        return config;
    }

    public void saveConfig() {
        // LOGGER.debug("Saving configuration");
        AutoConfig.getConfigHolder(WynnSpellsConfig.class).save();
    }

    private void onClientStart(MinecraftClient client) {
        Thread processorThread = new Thread(new WynnSpellsRunnable(queueList, running));
        processorThread.setDaemon(true);
        processorThread.start();
    }

    private void onClientStop(MinecraftClient client) {
        running.set(false);
    }

    private void onClientEndTick(MinecraftClient client) {
        processConfigKey(client);
        processIntentKey(client, firstSpellKey, WynnSpellsIntent.FIRST_SPELL);
        processIntentKey(client, secondSpellKey, WynnSpellsIntent.SECOND_SPELL);
        processIntentKey(client, thirdSpellKey, WynnSpellsIntent.THIRD_SPELL);
        processIntentKey(client, fourthSpellKey, WynnSpellsIntent.FOURTH_SPELL);
        processIntentKey(client, meleeKey, WynnSpellsIntent.MELEE);
    }

    private void processIntentKey(MinecraftClient client, KeyBinding key, WynnSpellsIntent intent) {
        if (key == null)
            return;

        if (!key.isPressed())
            return;

        if (queueList.size() >= config.getQueueLimit()) {
            return;
        }

        if (client == null || client.player == null) {
            return;
        }

        ItemStack itemInMainHand = client.player.getMainHandStack();
        if (itemInMainHand != previousItem) {
            previousItem = itemInMainHand;
            queueList.clear();;
        }

        key.setPressed(false);
        queueList.add(new WynnSpellsQueue(intent));
    }

    private void processConfigKey(MinecraftClient client) {
        if (configKey == null)
            return;

        if (!configKey.isPressed())
            return;

        configKey.setPressed(false);
        client.setScreen(WynnSpellsConfigScreen.create(client.currentScreen));
    }
}
