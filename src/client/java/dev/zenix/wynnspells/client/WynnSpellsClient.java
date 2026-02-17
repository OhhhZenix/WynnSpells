package dev.zenix.wynnspells.client;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

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

    private static WynnSpellsClient instance = null;
    private BlockingQueue<WynnSpellsQueue> queueList = new LinkedBlockingQueue<>();
    private AtomicBoolean running = new AtomicBoolean(true);
    private WynnSpellsConfig config = null;
    private ItemStack previousItem = null;
    private KeyBinding firstSpellKey;
    private KeyBinding secondSpellKey;
    private KeyBinding thirdSpellKey;
    private KeyBinding fourthSpellKey;
    private KeyBinding meleeKey;
    private KeyBinding configKey;

    @Override
    public void onInitializeClient() {
        instance = this;
        loadConfig();

        var category = Category.create(Identifier.of("wynnspells", "all"));
        firstSpellKey = KeyBindingHelper.registerKeyBinding(new KeyBinding("key.wynnspells.first",
                InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_UNKNOWN, category));
        secondSpellKey = KeyBindingHelper.registerKeyBinding(new KeyBinding("key.wynnspells.second",
                InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_UNKNOWN, category));
        thirdSpellKey = KeyBindingHelper.registerKeyBinding(new KeyBinding("key.wynnspells.third",
                InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_UNKNOWN, category));
        fourthSpellKey = KeyBindingHelper.registerKeyBinding(new KeyBinding("key.wynnspells.fourth",
                InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_UNKNOWN, category));
        meleeKey = KeyBindingHelper.registerKeyBinding(new KeyBinding("key.wynnspells.melee",
                InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_UNKNOWN, category));
        configKey = KeyBindingHelper.registerKeyBinding(new KeyBinding("key.wynnspells.config",
                InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_UNKNOWN, category));
        ClientLifecycleEvents.CLIENT_STARTED.register(client -> onClientStart(client));
        ClientLifecycleEvents.CLIENT_STOPPING.register(client -> onClientStop(client));
        ClientTickEvents.END_CLIENT_TICK.register(client -> onClientEndTick(client));
    }

    public static WynnSpellsClient getInstance() {
        return instance;
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
        processIntentKey(firstSpellKey, WynnSpellsIntent.FIRST_SPELL, false);
        processIntentKey(secondSpellKey, WynnSpellsIntent.SECOND_SPELL, false);
        processIntentKey(thirdSpellKey, WynnSpellsIntent.THIRD_SPELL, false);
        processIntentKey(fourthSpellKey, WynnSpellsIntent.FOURTH_SPELL, false);
        processIntentKey(meleeKey, WynnSpellsIntent.MELEE, false);
        processConfigKey();
    }

    private void processIntentKey(KeyBinding key, WynnSpellsIntent intent, boolean repeatable) {
        if (key == null)
            return;

        if (!key.isPressed())
            return;

        if (!repeatable)
            key.setPressed(false);

        MinecraftClient client = MinecraftClient.getInstance();
        if (client == null || client.options == null) {
            return;
        }

        // does our player even exists?
        if (client.player == null) {
            return;
        }

        // if item different, lets clear it
        ItemStack itemInMainHand = client.player.getMainHandStack();
        if (itemInMainHand != previousItem) {
            previousItem = itemInMainHand;
            queueList.clear();
        }

        // should we keep casting?
        if (queueList.size() >= config.getQueueLimit()) {
            return;
        }

        queueList.add(new WynnSpellsQueue(intent, client.options.sneakKey.isPressed()));
    }

    private void processConfigKey() {
        if (configKey == null)
            return;

        if (!configKey.isPressed())
            return;

        // TODO: Open config screen
        configKey.setPressed(false);
    }
}
