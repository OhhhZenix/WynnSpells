package dev.zenix.wynnspells.client.event;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;

public interface PlayerAttackEvent {

	Event<PlayerAttackEvent> HANDLER = EventFactory.createArrayBacked(PlayerAttackEvent.class,
			(listeners) -> (player, target) -> {
				for (PlayerAttackEvent callback : listeners) {
					if (!callback.attack(player, target)) {
						return false;
					}
				}
				return true;
			});

	boolean attack(Player player, Entity target);

}
