package dev.zenix.wynnspells.client.spell;

import dev.zenix.wynnspells.WynnSpells;
import dev.zenix.wynnspells.client.WynnSpellsClient;
import dev.zenix.wynnspells.client.core.Utils;
import dev.zenix.wynnspells.client.event.MinecraftEvents;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

public class SpellCaster {

  private static final AtomicBoolean running = new AtomicBoolean(true);
  private static final AtomicLong lastClick = new AtomicLong(Long.MAX_VALUE);
  private static final BlockingQueue<Boolean> queue = new LinkedBlockingQueue<>();
  private final Thread executor;
  private final Minecraft mc;

  public SpellCaster(Minecraft mc) {
    this.executor = new Thread(this::executeClicks);
    this.mc = mc;
  }

  public void start() {
    executor.start();
    MinecraftEvents.START_ATTACK.register(this::onStartAttack);
    MinecraftEvents.START_USE_ITEM.register(this::onStartUseItem);
    ClientTickEvents.END_CLIENT_TICK.register(this::processKeys);
  }

  public void stop() {
    running.set(false);
  }

  public boolean isCasting() {
    return !queue.isEmpty();
  }

  public void addClicks(Collection<Boolean> clicks) {
    queue.addAll(clicks);
  }

  private boolean handleVanillaAction(boolean isAttack) {
    if (isCasting()) return false;

    boolean isNormalAttack = isAttack && !Utils.isArcher(mc);
    boolean isUseAttack = !isAttack && Utils.isArcher(mc);

    if (isNormalAttack || isUseAttack) {
      addClicks(Utils.getClicks(WynnSpellsClient.MELEE_KEY));
    }

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

  private void processSpell(KeyMapping keyMapping) {
    if (keyMapping.consumeClick()) {

      List<Boolean> spellCombo = Utils.getClicks(keyMapping);

      if (keyMapping == WynnSpellsClient.MELEE_KEY) {
        addClicks(spellCombo);
      } else {
        if (!isCasting()) return;

        addClicks(spellCombo);
      }
    }
  }

  private void processKeys(Minecraft mc) {
    processSpell(WynnSpellsClient.FIRST_SPELL_KEY);
    processSpell(WynnSpellsClient.SECOND_SPELL_KEY);
    processSpell(WynnSpellsClient.THIRD_SPELL_KEY);
    processSpell(WynnSpellsClient.FOURTH_SPELL_KEY);
    processSpell(WynnSpellsClient.MELEE_KEY);
  }

  private void executeClicks() {
    while (running.get()) {
      try {
        if (mc.player == null) continue;

        long now = System.nanoTime();
        if (now - SpellCaster.lastClick.get() < Utils.getClickDelay()) continue;

        boolean click = queue.take();
        Utils.sendClick(mc, click);

        SpellCaster.lastClick.set(System.nanoTime());
      } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
        WynnSpells.LOGGER.trace(e.getMessage());
      }
    }
  }
}
