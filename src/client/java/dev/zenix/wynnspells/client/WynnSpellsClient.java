package dev.zenix.wynnspells.client;

import java.util.concurrent.LinkedBlockingDeque;
import org.lwjgl.glfw.GLFW;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.GsonConfigSerializer;
import net.fabricmc.api.ClientModInitializer;
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
    private WynnSpellsConfig config = null;

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
        loadConfig();
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

    private void onClientEndTick(MinecraftClient client) {
        processConfigKey();
        processIntentKey(client, firstSpellKey, WynnSpellsIntent.FIRST_SPELL);
        processIntentKey(client, secondSpellKey, WynnSpellsIntent.SECOND_SPELL);
        processIntentKey(client, thirdSpellKey, WynnSpellsIntent.THIRD_SPELL);
        processIntentKey(client, fourthSpellKey, WynnSpellsIntent.FOURTH_SPELL);
        processIntentKey(client, meleeKey, WynnSpellsIntent.MELEE);
        processQueue(client);
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

    private void processConfigKey() {
        if (configKey == null)
            return;

        if (!configKey.isPressed())
            return;

        // TODO: Open config screen
        configKey.setPressed(false);
    }

    private void processQueue(MinecraftClient client) {
        WynnSpellsQueue queue = queueList.poll();

        if (queue == null)
            return;

        WynnSpellsIntent intent = queue.getIntent();
        switch (intent) {
            case MELEE:
                if (WynnSpellsUtils.isArcher(client)) {
                    WynnSpellsUtils.sendInteractPacket(client);
                } else {
                    WynnSpellsUtils.sendAttackPacket(client);
                }
                break;
            case FIRST_SPELL:
                if (WynnSpellsUtils.isArcher(client)) {
                    // L-R-L
                    WynnSpellsUtils.sendAttackPacket(client);
                    WynnSpellsUtils.sendInteractPacket(client);
                    WynnSpellsUtils.sendAttackPacket(client);

                } else {
                    // R-L-R
                    WynnSpellsUtils.sendInteractPacket(client);
                    WynnSpellsUtils.sendAttackPacket(client);
                    WynnSpellsUtils.sendInteractPacket(client);

                }
                break;
            case SECOND_SPELL:
                if (WynnSpellsUtils.isArcher(client)) {
                    // L-L-L
                    WynnSpellsUtils.sendAttackPacket(client);
                    WynnSpellsUtils.sendAttackPacket(client);
                    WynnSpellsUtils.sendAttackPacket(client);

                } else {
                    // R-R-R
                    WynnSpellsUtils.sendInteractPacket(client);
                    WynnSpellsUtils.sendInteractPacket(client);
                    WynnSpellsUtils.sendInteractPacket(client);
                }
                break;
            case THIRD_SPELL:
                if (WynnSpellsUtils.isArcher(client)) {
                    // L-R-R
                    WynnSpellsUtils.sendAttackPacket(client);
                    WynnSpellsUtils.sendInteractPacket(client);
                    WynnSpellsUtils.sendInteractPacket(client);
                } else {
                    // R-L-L
                    WynnSpellsUtils.sendInteractPacket(client);
                    WynnSpellsUtils.sendAttackPacket(client);
                    WynnSpellsUtils.sendAttackPacket(client);
                }
                break;
            case FOURTH_SPELL:
                if (WynnSpellsUtils.isArcher(client)) {
                    // L-L-R
                    WynnSpellsUtils.sendAttackPacket(client);
                    WynnSpellsUtils.sendAttackPacket(client);
                    WynnSpellsUtils.sendInteractPacket(client);
                } else {
                    // R-R-L
                    WynnSpellsUtils.sendInteractPacket(client);
                    WynnSpellsUtils.sendInteractPacket(client);
                    WynnSpellsUtils.sendAttackPacket(client);
                }
                break;
        }

    }
}
