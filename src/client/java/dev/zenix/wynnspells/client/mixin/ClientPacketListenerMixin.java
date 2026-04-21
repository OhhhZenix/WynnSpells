package dev.zenix.wynnspells.client.mixin;

import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.protocol.ping.ClientboundPongResponsePacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPacketListener.class)
public class ClientPacketListenerMixin {

	@Inject(method = "handlePongResponse(Lnet/minecraft/network/protocol/ping/ClientboundPongResponsePacket;)V", at = @At("RETURN"))
	private void handlePongResponse(ClientboundPongResponsePacket packet, CallbackInfo ci) {
		// PongReceivedEvent event = new PongReceivedEvent(packet.time());
		// MixinHelper.post(event);
	}
}
