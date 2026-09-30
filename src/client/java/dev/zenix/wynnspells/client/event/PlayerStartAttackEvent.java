package dev.zenix.wynnspells.client.event;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.component.SwingAnimation;

public interface PlayerStartAttackEvent {

  Event<PlayerStartAttackEvent> HANDLER =
      EventFactory.createArrayBacked(
          PlayerStartAttackEvent.class,
          (listeners) ->
              (player, hand, animation, sendToSwingingEntity) -> {
                for (PlayerStartAttackEvent callback : listeners) {
                  return callback.startAttack(player, hand, animation, sendToSwingingEntity);
                }
                return false;
              });

  boolean startAttack(
      LocalPlayer localPlayer,
      InteractionHand hand,
      SwingAnimation animation,
      boolean sendToSwingingEntity);
}
