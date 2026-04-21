package dev.zenix.wynnspells.client.mixin;

import dev.zenix.wynnspells.client.event.ContinueDestroyBlockEvent;
import dev.zenix.wynnspells.client.event.PlayerAttackEvent;
import dev.zenix.wynnspells.client.event.StartDestroyBlockEvent;
import dev.zenix.wynnspells.client.event.UseItemEvent;
import dev.zenix.wynnspells.client.event.UseItemOnEvent;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(MultiPlayerGameMode.class)
public class MultiPlayerGameModeMixin {

	@Inject(method = "attack(Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/world/entity/Entity;)V", at = @At("HEAD"), cancellable = true)
	private void attack(Player player, Entity target, CallbackInfo ci) {
		boolean shouldCancel = PlayerAttackEvent.HANDLER.invoker().attack(player, target);
		if (shouldCancel) {
			ci.cancel();
		}
	}

	@Inject(method = "startDestroyBlock(Lnet/minecraft/core/BlockPos;Lnet/minecraft/core/Direction;)Z", at = @At("HEAD"), cancellable = true)
	private void startDestroyBlock(BlockPos position, Direction direction, CallbackInfoReturnable<Boolean> cir) {
		boolean shouldCancel = StartDestroyBlockEvent.HANDLER.invoker().startDestroyBlock(position, direction);
		if (shouldCancel) {
			cir.cancel();
		}
	}

	@Inject(method = "continueDestroyBlock(Lnet/minecraft/core/BlockPos;Lnet/minecraft/core/Direction;)Z", at = @At("HEAD"), cancellable = true)
	private void continueDestroyBlock(BlockPos position, Direction direction, CallbackInfoReturnable<Boolean> cir) {
		boolean shouldCancel = ContinueDestroyBlockEvent.HANDLER.invoker().continueDestroyBlock(position, direction);
		if (shouldCancel) {
			cir.cancel();
		}
	}

	@Inject(method = "useItem(Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/world/InteractionHand;)Lnet/minecraft/world/InteractionResult;", at = @At("HEAD"), cancellable = true)
	private void useItem(Player player, InteractionHand hand, CallbackInfoReturnable<InteractionResult> cir) {
		boolean shouldCancel = UseItemEvent.HANDLER.invoker().useItem(player, hand);
		if (shouldCancel) {
			cir.cancel();
		}
	}

	@Inject(method = "useItemOn(Lnet/minecraft/client/player/LocalPlayer;Lnet/minecraft/world/InteractionHand;Lnet/minecraft/world/phys/BlockHitResult;)Lnet/minecraft/world/InteractionResult;", at = @At("HEAD"), cancellable = true)
	private void useItemOn(LocalPlayer player, InteractionHand hand, BlockHitResult result,
			CallbackInfoReturnable<InteractionResult> cir) {
		boolean shouldCancel = UseItemOnEvent.HANDLER.invoker().useItemOn(player, hand, result);
		if (shouldCancel) {
			cir.cancel();
		}
	}

	@Inject(method = "interact(Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/world/entity/Entity;Lnet/minecraft/world/InteractionHand;)Lnet/minecraft/world/InteractionResult;", at = @At("HEAD"), cancellable = true)
	private void interact(Player player, Entity target, InteractionHand hand,
			CallbackInfoReturnable<InteractionResult> cir) {
		// PlayerInteractEvent
		// cir.cancel();
	}

	@Inject(method = "interactAt(Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/world/entity/Entity;Lnet/minecraft/world/phys/EntityHitResult;Lnet/minecraft/world/InteractionHand;)Lnet/minecraft/world/InteractionResult;", at = @At("HEAD"), cancellable = true)
	private void interactAt(Player player, Entity target, EntityHitResult ray, InteractionHand hand,
			CallbackInfoReturnable<InteractionResult> cir) {
		// PlayerInteractAtEvent
		// cir.cancel();
	}
}
