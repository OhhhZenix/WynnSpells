package dev.zenix.wynnspells.client.mixin;

import dev.zenix.wynnspells.client.event.MinecraftEvents;
import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Minecraft.class)
public class MinecraftMixin {

  @Inject(method = "startAttack", at = @At("HEAD"), cancellable = true)
  private void startAttack(CallbackInfoReturnable<Boolean> cir) {
    MinecraftEvents.START_ATTACK.invoker().startAttack(cir);
  }

  @Inject(method = "startUseItem", at = @At("HEAD"), cancellable = true)
  private void startUseItem(CallbackInfo ci) {
    MinecraftEvents.START_USE_ITEM.invoker().startUseItem(ci);
  }
}
