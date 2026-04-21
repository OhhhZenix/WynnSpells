package dev.zenix.wynnspells.client.event;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;

public interface PlayerInteractEvent {

	Event<PlayerInteractEvent> HANDLER = EventFactory.createArrayBacked(PlayerInteractEvent.class,
			(listeners) -> (player, target, hand) -> {
				for (PlayerInteractEvent callback : listeners) {
					return callback.interact(player, target, hand);
				}
				return false;
			});

	boolean interact(Player player, Entity target, InteractionHand hand);
}
