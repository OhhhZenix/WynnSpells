package dev.zenix.wynnspells.client.event;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.InteractionHand;

public interface PlayerStartAttackEvent {

	Event<PlayerStartAttackEvent> HANDLER = EventFactory.createArrayBacked(PlayerStartAttackEvent.class,
			(listeners) -> (player, hand) -> {
				for (PlayerStartAttackEvent callback : listeners) {
					return callback.startAttack(player, hand);
				}
				return false;
			});

	boolean startAttack(LocalPlayer localPlayer, InteractionHand hand);
}
