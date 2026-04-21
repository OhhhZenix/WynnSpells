package dev.zenix.wynnspells.client.event;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.phys.BlockHitResult;

public interface UseItemOnEvent {

	Event<UseItemOnEvent> HANDLER = EventFactory.createArrayBacked(UseItemOnEvent.class,
			(listeners) -> (player, hand, result) -> {
				for (UseItemOnEvent callback : listeners) {
					return callback.useItemOn(player, hand, result);
				}
				return false;
			});

	boolean useItemOn(LocalPlayer player, InteractionHand hand, BlockHitResult result);
}
