package dev.zenix.wynnspells.client.mixin;

import dev.zenix.wynnspells.WynnSpells;
import dev.zenix.wynnspells.client.WynnSpellsPingPong;
import net.minecraft.client.network.PingMeasurer;
import net.minecraft.network.packet.s2c.query.PingResultS2CPacket;
import net.minecraft.util.Util;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PingMeasurer.class)
public class PingMeasurerMixin {

	@Inject(method = "onPingResult", at = @At("RETURN"))
	private void onPingResult(PingResultS2CPacket packet, CallbackInfo callbackInfo) {
		long currentTime = Util.getMeasuringTimeMs();
		long startTime = packet.startTime();
		WynnSpells.LOGGER.debug("Current Time: {}", currentTime);
		WynnSpells.LOGGER.debug("Start Time: {}", startTime);
		WynnSpells.LOGGER.debug("Ping: {}", currentTime - startTime);
		WynnSpellsPingPong.onCallback(startTime);
	}
}
