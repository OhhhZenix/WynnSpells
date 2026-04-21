package dev.zenix.wynnspells.client.event;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;

public interface StartDestroyBlockEvent {

	Event<StartDestroyBlockEvent> HANDLER = EventFactory.createArrayBacked(StartDestroyBlockEvent.class,
			(listeners) -> (position, direction) -> {
				for (StartDestroyBlockEvent callback : listeners) {
					return callback.startDestroyBlock(position, direction);
				}
				return false;
			});

	boolean startDestroyBlock(BlockPos position, Direction direction);
}
