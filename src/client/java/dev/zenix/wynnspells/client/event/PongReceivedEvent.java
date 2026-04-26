package dev.zenix.wynnspells.client.event;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;

public interface PongReceivedEvent {

	Event<PongReceivedEvent> HANDLER = EventFactory.createArrayBacked(PongReceivedEvent.class,
			(listeners) -> (time) -> {
				for (PongReceivedEvent callback : listeners) {
					callback.handlePongResponse(time);
				}
			});

	void handlePongResponse(long time);
}
