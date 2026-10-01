package dev.zenix.wynnspells.client.spell;

import net.minecraft.client.Minecraft;

public class SpellCaster implements Runnable {

  private final Minecraft mc;

  public SpellCaster(Minecraft mc) {
    this.mc = mc;
  }

  @Override
  public void run() {}

  //  private Minecraft client;
  //
  //  public SpellCaster(Minecraft client) {
  //    this.client = client;
  //    MinecraftEvents.START_ATTACK.register(this::onStartAttack);
  //    MinecraftEvents.START_USE_ITEM.register(this::onStartUseItem);
  //  }
  //
  //  public void start() {}
  //
  //  public void stop() {}
  //
  //  private void onStartAttack(CallbackInfoReturnable<Boolean> cir) {
  //    //        if (handleVanillaAction(true)) {
  //    //            cir.setReturnValue(true);
  //    //        }
  //  }
  //
  //  private void onStartUseItem(CallbackInfo ci) {
  //    //        if (handleVanillaAction(false)) {
  //    //            ci.cancel();
  //    //        }
  //  }
}
