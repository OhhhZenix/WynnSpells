package dev.zenix.wynnspells.client.event;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Hand;

public interface InteractItemEvent {

	Event<InteractItemEvent> EVENT = EventFactory.createArrayBacked(InteractItemEvent.class,
			(listeners) -> (player, hand) -> {
				for (InteractItemEvent listener : listeners) {
					return listener.interactItem(player, hand);
				}
				return false;
			});

	// true == cancel
	// false == not cancel
	boolean interactItem(PlayerEntity player, Hand hand);
}
