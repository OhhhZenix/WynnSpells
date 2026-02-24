package dev.zenix.wynnspells.client.event;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.util.Hand;

public interface SwingHandEvent {

	Event<SwingHandEvent> EVENT = EventFactory.createArrayBacked(SwingHandEvent.class,
			(listeners) -> (player, hand) -> {
				for (SwingHandEvent listener : listeners) {
					return listener.swingHand(player, hand);
				}
				return false;
			});

	// true == cancel
	// false == not cancel
	boolean swingHand(ClientPlayerEntity player, Hand hand);
}
