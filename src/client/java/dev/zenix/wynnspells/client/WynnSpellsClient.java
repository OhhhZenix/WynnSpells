package dev.zenix.wynnspells.client;

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

    private KeyBinding firstSpellKey;
    private KeyBinding secondSpellKey;
    private KeyBinding thirdSpellKey;
    private KeyBinding fourthSpellKey;
    private KeyBinding meleeKey;
    private KeyBinding configKey;

    @Override
    public void onInitializeClient() {
        instance = this;
        final String category = "key.category.wynnspells";
        firstSpellKey = KeyBindingHelper
                .registerKeyBinding(
                        new KeyBinding("key.wynnspells.first", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_UNKNOWN,
                                category));
        secondSpellKey = KeyBindingHelper
                .registerKeyBinding(
                        new KeyBinding("key.wynnspells.second", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_UNKNOWN,
                                category));
        thirdSpellKey = KeyBindingHelper
                .registerKeyBinding(
                        new KeyBinding("key.wynnspells.third", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_UNKNOWN,
                                category));
        fourthSpellKey = KeyBindingHelper
                .registerKeyBinding(
                        new KeyBinding("key.wynnspells.fourth", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_UNKNOWN,
                                category));
        meleeKey = KeyBindingHelper
                .registerKeyBinding(
                        new KeyBinding("key.wynnspells.melee", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_UNKNOWN,
                                category));
        configKey = KeyBindingHelper
                .registerKeyBinding(
                        new KeyBinding("key.wynnspells.config", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_UNKNOWN,
                                category));
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
        processIntentKey(firstSpellKey, Intent.FIRST_SPELL, false);
        processIntentKey(secondSpellKey, Intent.SECOND_SPELL, false);
        processIntentKey(thirdSpellKey, Intent.THIRD_SPELL, false);
        processIntentKey(fourthSpellKey, Intent.FOURTH_SPELL, false);
        processIntentKey(meleeKey, Intent.MELEE, false);
        // Only process melee if no spell keys are pressed
        // if (!isAnySpellKeyPressed()) {
        // processIntentKey(meleeKey, Intent.MELEE, true);
        // }
        processConfigKey();
    }

    private boolean isAnySpellKeyPressed() {
        return (firstSpellKey != null && firstSpellKey.isPressed()) ||
                (secondSpellKey != null && secondSpellKey.isPressed()) ||
                (thirdSpellKey != null && thirdSpellKey.isPressed()) ||
                (fourthSpellKey != null && fourthSpellKey.isPressed());
    }

    private void processIntentKey(KeyBinding key, Intent intent, boolean repeatable) {
        if (key == null)
            return;

        if (!key.isPressed())
            return;

        if (!repeatable)
            key.setPressed(false);

        intentQueue.add(intent);
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
