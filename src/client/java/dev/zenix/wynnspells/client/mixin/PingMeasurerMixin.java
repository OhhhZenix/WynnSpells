package dev.zenix.wynnspells.client.mixin;

import dev.zenix.wynnspells.client.event.PingResultEvent;
import net.minecraft.client.network.PingMeasurer;
import net.minecraft.network.packet.s2c.query.PingResultS2CPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PingMeasurer.class)
public class PingMeasurerMixin {

	@Inject(method = "onPingResult", at = @At("RETURN"))
	private void onPingResult(PingResultS2CPacket packet, CallbackInfo callbackInfo) {
		PingResultEvent.EVENT.invoker().onPingResult(packet, callbackInfo);
	}
}
