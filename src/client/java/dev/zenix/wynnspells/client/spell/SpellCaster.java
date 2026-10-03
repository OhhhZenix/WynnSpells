package dev.zenix.wynnspells.client.spell;

import dev.zenix.wynnspells.client.WynnSpellsClient;
import dev.zenix.wynnspells.client.config.ClothConfig;
import dev.zenix.wynnspells.client.core.Utils;
import dev.zenix.wynnspells.client.event.MinecraftEvents;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

public class SpellCaster extends Thread {

  private final Queue<KeyMapping> keys = new LinkedBlockingQueue<>();
  private final Queue<Boolean> clicks = new LinkedBlockingQueue<>();
  private final Set<KeyMapping> previousPressedKeys = ConcurrentHashMap.newKeySet();
  private final Map<KeyMapping, Long> keysTimer = new ConcurrentHashMap<>();
  private int previousSlot = -1;
  private long lastClickTime = 0;
  private final Minecraft mc;

  public SpellCaster(Minecraft mc) {
    this.mc = mc;
    MinecraftEvents.START_ATTACK.register(this::onStartAttack);
    MinecraftEvents.START_USE_ITEM.register(this::onStartUseItem);
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
          Component.literal("Cast ignored: try slowing down a bit."), config.isNotifyBusyCast());
      return;
    }

    if (config.isWeaponOnlyCasting() && !Utils.isWeapon(mc)) {
      return;
    }

    keys.add(key);
  }

  private void processKeys(KeyMapping key) {
    if (key == null) return;

    ClothConfig config = WynnSpellsClient.getInstance().getConfig();
    boolean repeat = config.isRepeatHeldKeys();

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
    if (!clicks.isEmpty()) return;

    if (keys.isEmpty()) return;

    KeyMapping keyMapping = keys.remove();
    clicks.addAll(Utils.getClicks(keyMapping));
  }

  private void processClicks() {
    if (clicks.isEmpty()) return;

    long now = System.nanoTime();
    long delay = Utils.getClickDelay();
    if (now - lastClickTime < delay) return;

    Boolean click = clicks.remove();
    if (click == null) return;

    Utils.sendClick(mc, click);
    lastClickTime = now;
  }

  @Override
  public void run() {
    while (WynnSpellsClient.isRunning()) {
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
