package dev.zenix.wynnspells.client.spell;

import dev.zenix.wynnspells.client.WynnSpellsClient;
import dev.zenix.wynnspells.client.config.ClothConfig;
import dev.zenix.wynnspells.client.core.Utils;
import dev.zenix.wynnspells.client.event.MinecraftEvents;
import java.util.*;
import java.util.concurrent.ArrayBlockingQueue;
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
  private final Queue<KeyMapping> keys = new ArrayBlockingQueue<>(1);
  private final Queue<Boolean> clicks = new ArrayBlockingQueue<>(3);
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

  private boolean handleVanillaAction(boolean isAttack) {
//    if (!isCasting()) return false;
//
//    boolean isNormalAttack = isAttack && !Utils.isArcher(mc);
//    boolean isUseAttack = !isAttack && Utils.isArcher(mc);
//
//    if (isNormalAttack || isUseAttack) {
//      addKey(WynnSpellsClient.MELEE_KEY);
//    }

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

  private void resetState() {
    LocalPlayer player = mc.player;

    if (player == null) return;

    int currentSlot = player.getInventory().getSelectedSlot();

    if (previousSlot == currentSlot) return;

    keys.clear();
    clicks.clear();
    previousSlot = currentSlot;
  }

  private void processClicks() {
    if (clicks.isEmpty()) return;

    long now = System.nanoTime();
    long delay = Utils.getClickDelay();

    if (now - lastClickTime < delay) return;

    boolean click = clicks.poll();

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
      processClicks();
    }
  }
}
