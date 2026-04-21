package dev.zenix.wynnspells.client.event;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;

public interface ContinueDestroyBlockEvent {

	Event<ContinueDestroyBlockEvent> HANDLER = EventFactory.createArrayBacked(ContinueDestroyBlockEvent.class,
			(listeners) -> (position, direction) -> {
				for (ContinueDestroyBlockEvent callback : listeners) {
					return callback.continueDestroyBlock(position, direction);
				}
				return false;
			});

	boolean continueDestroyBlock(BlockPos position, Direction direction);
}
