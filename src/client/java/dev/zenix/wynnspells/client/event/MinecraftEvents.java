package dev.zenix.wynnspells.client.event;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

public interface MinecraftEvents {

  Event<StartAttack> START_ATTACK =
      EventFactory.createArrayBacked(
          StartAttack.class,
          listeners ->
              cir -> {
                for (StartAttack listener : listeners) {
                  listener.startAttack(cir);
                }
              });

  Event<StartUseItem> START_USE_ITEM =
      EventFactory.createArrayBacked(
          StartUseItem.class,
          listeners ->
              ci -> {
                for (StartUseItem listener : listeners) {
                  listener.startUseItem(ci);
                }
              });

  interface StartAttack {
    void startAttack(CallbackInfoReturnable<Boolean> cir);
  }

  interface StartUseItem {
    void startUseItem(CallbackInfo ci);
  }
}
