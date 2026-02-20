package dev.zenix.wynnspells.client.mixin;

import net.minecraft.client.network.PingMeasurer;
import net.minecraft.network.packet.s2c.query.PingResultS2CPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import dev.zenix.wynnspells.client.WynnSpellsPingPong;

@Mixin(PingMeasurer.class)
public class WynnSpellsClientPacketListener {

    @Inject(method = "onPingResult(Lnet/minecraft/network/packet/s2c/query/PingResultS2CPacket;)V",
            at = @At("RETURN"))
    private void onPingResultPost(PingResultS2CPacket packet, CallbackInfo ci) {
        WynnSpellsPingPong.onCallback(packet.startTime());
    }
}
