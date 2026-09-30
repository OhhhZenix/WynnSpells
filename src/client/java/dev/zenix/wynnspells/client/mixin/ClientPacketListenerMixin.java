package dev.zenix.wynnspells.client.mixin;

import dev.zenix.wynnspells.client.event.ClientPacketListenerEvents;
import dev.zenix.wynnspells.client.event.PongReceivedEvent;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.protocol.ping.ClientboundPongResponsePacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPacketListener.class)
public class ClientPacketListenerMixin {

  @Inject(method = "handlePongResponse", at = @At("RETURN"))
  public void handlePongResponse(ClientboundPongResponsePacket packet, CallbackInfo ci) {
    PongReceivedEvent.HANDLER.invoker().handlePongResponse(packet.time());
    ClientPacketListenerEvents.HANDLE_PONG_RESPONSE.invoker().handlePongResponse(packet, ci);
  }
}
