package dev.zenix.wynnspells.client.spell;

import dev.zenix.wynnspells.client.WynnSpellsClient;
import dev.zenix.wynnspells.client.config.ClothConfig;
import dev.zenix.wynnspells.client.core.Utils;
import dev.zenix.wynnspells.client.event.MinecraftEvents;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

public class SpellCaster {

  private static final AtomicBoolean running = new AtomicBoolean(true);
  private final Queue<KeyMapping> keys = new ArrayDeque<>();
  private final Queue<Boolean> clicks = new ArrayDeque<>();
  private final Set<KeyMapping> previousPressedKeys = new HashSet<>();
  private final Map<KeyMapping, Long> keysTimer = new HashMap<>();
  private int previousSlot = -1;
  private long lastClickTime = 0;
  private final Thread executor;
  private final Minecraft mc;

  public SpellCaster(Minecraft mc) {
    this.executor = new Thread(this::run);
    this.mc = mc;
  }

  public void start() {
    executor.start();
    MinecraftEvents.START_ATTACK.register(this::onStartAttack);
    MinecraftEvents.START_USE_ITEM.register(this::onStartUseItem);
  }

  public void stop() {
    running.set(false);
  }

  private void resetState() {
    LocalPlayer player = mc.player;

    if (player == null) return;

    int currentSlot = player.getInventory().getSelectedSlot();

    if (previousSlot == currentSlot) return;

    keys.clear();
    clicks.clear();
    previousSlot = currentSlot;
    lastClickTime = 0;
  }

  private void addKey(KeyMapping key) {
    ClothConfig config = WynnSpellsClient.getInstance().getConfig();

    if (keys.size() >= Utils.KEY_LIMIT) {
      Utils.sendNotification(
          Component.literal("Cast ignored: try slowing down a bit."),
          config.shouldNotifyBusyCast());
      return;
    }

    if (config.isWeaponOnlyCasting() && !Utils.isWeapon(mc)) {
      return;
    }

    keys.offer(key);
  }

  private void processKeys(KeyMapping key) {
    if (key == null) return;

    ClothConfig config = WynnSpellsClient.getInstance().getConfig();
    boolean repeat = config.getRepeatHeldKeys();

    long now = System.nanoTime();

    if (repeat) {
      boolean pressed = key.isDown();

      // Released
      if (!pressed) {
        if (previousPressedKeys.remove(key)) {
          keysTimer.remove(key);
        }
        return;
      }

      // First press
      if (!previousPressedKeys.contains(key)) {
        previousPressedKeys.add(key);
        keysTimer.put(key, now);
        addKey(key);
        return;
      }

      // Held repeat
      long threshold = TimeUnit.MILLISECONDS.toNanos(config.getRepeatThreshold());
      long last = keysTimer.getOrDefault(key, 0L);

      if (now - last >= threshold) {
        addKey(key);
        keysTimer.put(key, now);
      }

    } else {
      if (key.consumeClick()) {
        addKey(key);
      }
    }
  }

  private void processKeys() {
    processKeys(WynnSpellsClient.FIRST_SPELL_KEY);
    processKeys(WynnSpellsClient.SECOND_SPELL_KEY);
    processKeys(WynnSpellsClient.THIRD_SPELL_KEY);
    processKeys(WynnSpellsClient.FOURTH_SPELL_KEY);
    processKeys(WynnSpellsClient.MELEE_KEY);
  }

  private void convertKeysToClicks() {
    if (!clicks.isEmpty())
      return;

    if (keys.isEmpty())
      return;

    KeyMapping keyMapping = keys.poll();
    for (boolean click : Utils.getClicks(keyMapping)) {
      clicks.offer(click);
    }
  }

  private void processClicks() {
    if (clicks.isEmpty()) return;

    long now = System.nanoTime();
    long delay = Utils.getClickDelay();

    if (now - lastClickTime < delay) return;

    Boolean click = clicks.poll();

    if (click == null) return;

    if (click ^ Utils.isArcher(mc)) {
      Utils.sendInteractPacket(mc); // right click
    } else {
      Utils.sendAttackPacket(mc); // left click
    }

    lastClickTime = now;
  }

  private void run() {
    while (running.get()) {
      resetState();
      processKeys();
      convertKeysToClicks();
      processClicks();
    }
  }

  private boolean isCasting() {
    long now = System.nanoTime();
    long delay = Utils.getClickDelay();
    long tolerance = delay * Utils.CLICK_LIMIT;
    return !clicks.isEmpty() || now < lastClickTime + (delay + tolerance);
  }

  private boolean handleVanillaAction(boolean isAttack) {
    if (!isCasting()) return false;

    boolean isArcher = Utils.isArcher(mc);
    boolean isNormalAttack = isAttack && !isArcher;
    boolean isUseAttack = !isAttack && isArcher;

    if (!isNormalAttack && !isUseAttack) return false;

    addKey(WynnSpellsClient.MELEE_KEY);
    return true;
  }

  private void onStartAttack(CallbackInfoReturnable<Boolean> cir) {
    if (handleVanillaAction(true)) {
      cir.setReturnValue(true);
    }
  }

  private void onStartUseItem(CallbackInfo ci) {
    if (handleVanillaAction(false)) {
      ci.cancel();
    }
  }
}
