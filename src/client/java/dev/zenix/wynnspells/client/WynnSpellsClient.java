package dev.zenix.wynnspells.client;

import java.util.HashSet;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

import org.lwjgl.glfw.GLFW;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;

public class WynnSpellsClient implements ClientModInitializer {

    public enum Intent {
        MELEE,
        FIRST_SPELL,
        SECOND_SPELL,
        THIRD_SPELL,
        FOURTH_SPELL,
    }

    private static WynnSpellsClient instance;
    private BlockingQueue<Intent> intentQueue = new LinkedBlockingQueue<>();
    private AtomicBoolean running = new AtomicBoolean(true);
    private HashSet<String> lastKeyPressed = new HashSet<>();

    private KeyBinding firstSpellKey;
    private KeyBinding secondSpellKey;
    private KeyBinding thirdSpellKey;
    private KeyBinding fourthSpellKey;
    private KeyBinding meleeKey;
    private KeyBinding configKey;

    @Override
    public void onInitializeClient() {
        instance = this;
        firstSpellKey = KeyBindingHelper
                .registerKeyBinding(
                        new KeyBinding("key.wynnspells.first", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_UNKNOWN,
                                "key.category.wynnspells"));
        secondSpellKey = KeyBindingHelper
                .registerKeyBinding(
                        new KeyBinding("key.wynnspells.second", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_UNKNOWN,
                                "key.category.wynnspells"));
        thirdSpellKey = KeyBindingHelper
                .registerKeyBinding(
                        new KeyBinding("key.wynnspells.third", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_UNKNOWN,
                                "key.category.wynnspells"));
        fourthSpellKey = KeyBindingHelper
                .registerKeyBinding(
                        new KeyBinding("key.wynnspells.fourth", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_UNKNOWN,
                                "key.category.wynnspells"));
        meleeKey = KeyBindingHelper
                .registerKeyBinding(
                        new KeyBinding("key.wynnspells.melee", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_UNKNOWN,
                                "key.category.wynnspells"));
        ClientLifecycleEvents.CLIENT_STARTED.register(client -> onClientStart(client));
        ClientLifecycleEvents.CLIENT_STOPPING.register(client -> onClientStop(client));
        ClientTickEvents.END_CLIENT_TICK.register(client -> onClientEndTick(client));
    }

    public static WynnSpellsClient getInstance() {
        return instance;
    }

    private void onClientStart(MinecraftClient client) {
        Thread processorThread = new Thread(new WynnSpellsRunnable(intentQueue, running));
        processorThread.setDaemon(true);
        processorThread.start();
    }

    private void onClientStop(MinecraftClient client) {
        running.set(false);
    }

    private void onClientEndTick(MinecraftClient client) {
        processKeyBinding(firstSpellKey, Intent.FIRST_SPELL);
        processKeyBinding(secondSpellKey, Intent.SECOND_SPELL);
        processKeyBinding(thirdSpellKey, Intent.THIRD_SPELL);
        processKeyBinding(fourthSpellKey, Intent.FOURTH_SPELL);
        processKeyBinding(meleeKey, Intent.MELEE);
    }

    private void processKeyBinding(KeyBinding key, Intent intent) {
        if (key == null)
            return;

        String keyId = key.getTranslationKey();
        boolean pressed = key.isPressed();
        if (pressed) {
            if (!lastKeyPressed.contains(keyId)) {
                intentQueue.add(intent);
                lastKeyPressed.add(keyId);
            }
        } else {
            lastKeyPressed.remove(keyId);
        }
    }

}
