package dev.zenix.wynnspells.client.spell;

import dev.zenix.wynnspells.client.event.MinecraftEvents;
import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

public final class SpellCaster {

  private Minecraft client;

  public SpellCaster(Minecraft client) {
    this.client = client;
    MinecraftEvents.START_ATTACK.register(this::onStartAttack);
    MinecraftEvents.START_USE_ITEM.register(this::onStartUseItem);
  }

  public void start() {}

  public void stop() {}

  private void onStartAttack(CallbackInfoReturnable<Boolean> cir) {
    //        if (handleVanillaAction(true)) {
    //            cir.setReturnValue(true);
    //        }
  }

  private void onStartUseItem(CallbackInfo ci) {
    //        if (handleVanillaAction(false)) {
    //            ci.cancel();
    //        }
  }
}
