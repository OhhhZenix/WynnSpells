package dev.zenix.wynnspells.client.event;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;

public interface UseItemEvent {

	Event<UseItemEvent> HANDLER = EventFactory.createArrayBacked(UseItemEvent.class, (listeners) -> (player, hand) -> {
		for (UseItemEvent callback : listeners) {
			return callback.useItem(player, hand);
		}
		return false;
	});

	boolean useItem(Player player, InteractionHand hand);
}
