package dev.zenix.wynnspells.client.event;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.network.protocol.ping.ClientboundPongResponsePacket;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

public interface ClientPacketListenerEvents {

  Event<HandlePongResponse> HANDLE_PONG_RESPONSE =
      EventFactory.createArrayBacked(
          HandlePongResponse.class,
          listeners ->
              (packet, ci) -> {
                for (HandlePongResponse listener : listeners) {
                  listener.handlePongResponse(packet, ci);
                }
              });

  interface HandlePongResponse {
    void handlePongResponse(ClientboundPongResponsePacket packet, CallbackInfo ci);
  }
}
