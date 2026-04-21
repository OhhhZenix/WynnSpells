package dev.zenix.wynnspells.client.event;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.EntityHitResult;

public interface PlayerInteractAtEvent {

	Event<PlayerInteractAtEvent> HANDLER = EventFactory.createArrayBacked(PlayerInteractAtEvent.class,
			(listeners) -> (player, target, ray, hand) -> {
				for (PlayerInteractAtEvent callback : listeners) {
					return callback.interactAt(player, target, ray, hand);
				}
				return false;
			});

	boolean interactAt(Player player, Entity target, EntityHitResult ray, InteractionHand hand);
}
