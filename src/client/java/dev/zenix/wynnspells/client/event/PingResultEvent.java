package dev.zenix.wynnspells.client.event;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.network.packet.s2c.query.PingResultS2CPacket;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

public interface PingResultEvent {

	Event<PingResultEvent> EVENT = EventFactory.createArrayBacked(PingResultEvent.class,
			(listeners) -> (packet, callbackInfo) -> {
				for (PingResultEvent listener : listeners) {
					listener.onPingResult(packet, callbackInfo);
				}
			});

	void onPingResult(PingResultS2CPacket packet, CallbackInfo callbackInfo);
}
