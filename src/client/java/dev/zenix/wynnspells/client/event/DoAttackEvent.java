package dev.zenix.wynnspells.client.event;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.util.Hand;

public interface DoAttackEvent {

	Event<DoAttackEvent> EVENT = EventFactory.createArrayBacked(DoAttackEvent.class, (listeners) -> (player, hand) -> {
		for (DoAttackEvent listener : listeners) {
			return listener.swingHand(player, hand);
		}
		return false;
	});

	// true == cancel
	// false == not cancel
	boolean swingHand(ClientPlayerEntity player, Hand hand);
}
