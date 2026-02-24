package dev.zenix.wynnspells.client.mixin;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import dev.zenix.wynnspells.client.event.SwingHandEvent;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.util.Hand;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(MinecraftClient.class)
public class MinecraftClientMixin {

	@WrapWithCondition(method = "doAttack()Z", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerEntity;swingHand(Lnet/minecraft/util/Hand;)V"))
	private boolean onSwingHand(ClientPlayerEntity player, Hand hand) {
		boolean result = SwingHandEvent.EVENT.invoker().swingHand(player, hand);
		return !result;
	}
}
