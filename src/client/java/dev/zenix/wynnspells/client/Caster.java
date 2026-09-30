package dev.zenix.wynnspells.client;

import dev.zenix.wynnspells.client.event.*;
import java.util.*;
import java.util.concurrent.TimeUnit;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

public class Caster {

  private final Minecraft mc;

  private final Deque<Boolean> clicks = new ArrayDeque<>();
  private final Deque<KeyMapping> keys = new ArrayDeque<>();

  private final Set<KeyMapping> previousPressedKeys = new HashSet<>();
  private final Map<KeyMapping, Long> keysTimer = new HashMap<>();

  private volatile boolean running = true;
  private int previousSlot = -1;
  private long lastClickTime = 0;

  public Caster(Minecraft mc) {
    this.mc = mc;
    MinecraftEvents.START_ATTACK.register(this::onStartAttack);
    MinecraftEvents.START_USE_ITEM.register(this::onStartUseItem);
  }

  public void start() {
    Thread thread = new Thread(this::run);
    thread.setDaemon(true);
    thread.start();
  }

  public void stop() {
    running = false;
  }

  // =========================
  // Core Loop
  // =========================

  private void run() {
    while (running) {
      tick();
    }
  }

  private void tick() {
    if (mc == null || mc.player == null) return;

    resetState();
    processKeys();
    processIntents();
    processClicks();
  }

  // =========================
  // Casting State
  // =========================

  private boolean isCasting() {
    long now = System.nanoTime();
    long delay = Utils.getClickDelay();
    long tolerance = delay * 3;
    return !clicks.isEmpty() || now < lastClickTime + (delay + tolerance);
  }

  private boolean handleVanillaAction(boolean isAttack) {
    if (!isCasting()) return false;

    boolean isNormalAttack = isAttack && !Utils.isArcher(mc);
    boolean isUseAttack = !isAttack && Utils.isArcher(mc);

    if (isNormalAttack || isUseAttack) {
      addKey(WynnSpellsClient.MELEE_KEY);
    }

    return true;
  }

  // =========================
  // Event Hooks
  // =========================

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

  // =========================
  // State Reset
  // =========================

  private void resetState() {
    int currentSlot = mc.player.getInventory().getSelectedSlot();

    if (previousSlot == currentSlot) return;

    previousSlot = currentSlot;

    clicks.clear();
    keys.clear();
    lastClickTime = 0;
  }

  // =========================
  // Click Processing
  // =========================

  private void processClicks() {
    if (clicks.isEmpty()) return;

    long now = System.nanoTime();
    long delay = Utils.getClickDelay();

    if (now - lastClickTime < delay) return;

    boolean click = clicks.poll();

    if (click) {
      Utils.sendInteractPacket(mc); // right click
    } else {
      Utils.sendAttackPacket(mc); // left click
    }

    lastClickTime = now;
  }

  // =========================
  // Intent Processing
  // =========================

  private void processIntents() {
    if (!clicks.isEmpty()) return;

    if (keys.isEmpty()) return;

    KeyMapping key = keys.poll();
    boolean isArcher = Utils.isArcher(mc);

    for (boolean click : Utils.keyToClicks(key, isArcher)) {
      clicks.add(click);
    }
  }

  // =========================
  // Key Handling
  // =========================

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

  private void processKey(KeyMapping key) {
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
    processKey(WynnSpellsClient.MELEE_KEY);
    processKey(WynnSpellsClient.FIRST_SPELL_KEY);
    processKey(WynnSpellsClient.SECOND_SPELL_KEY);
    processKey(WynnSpellsClient.THIRD_SPELL_KEY);
    processKey(WynnSpellsClient.FOURTH_SPELL_KEY);
  }
}
