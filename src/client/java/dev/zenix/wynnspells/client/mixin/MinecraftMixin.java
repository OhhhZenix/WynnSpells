package dev.zenix.wynnspells.client.mixin;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import dev.zenix.wynnspells.client.event.PlayerStartAttackEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.InteractionHand;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(Minecraft.class)
public class MinecraftMixin {

	@WrapWithCondition(method = "startAttack()Z", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/player/LocalPlayer;swing(Lnet/minecraft/world/InteractionHand;)V"))
	private boolean startAttack(LocalPlayer localPlayer, InteractionHand hand) {
		boolean result = PlayerStartAttackEvent.HANDLER.invoker().startAttack(localPlayer, hand);
		return !result;
	}
}