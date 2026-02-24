package dev.zenix.wynnspells.client;

import dev.zenix.wynnspells.WynnSpells;
import java.util.List;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.toast.SystemToast;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.c2s.play.HandSwingC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerInputC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerInteractItemC2SPacket;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.PlayerInput;

public class WynnSpellsUtils {

	public static void sendPacket(MinecraftClient client, Packet<?> packet) {
		if (client == null)
			return;

		ClientPlayNetworkHandler networkHandler = client.getNetworkHandler();
		if (networkHandler == null)
			return;

		networkHandler.sendPacket(packet);
	}

	public static void sendAttackPacket(MinecraftClient client) {
		WynnSpellsUtils.sendPacket(client, new HandSwingC2SPacket(Hand.MAIN_HAND));
	}

	public static void sendInteractPacket(MinecraftClient client) {
		WynnSpellsUtils.sendPacket(client,
				new PlayerInteractItemC2SPacket(Hand.MAIN_HAND, 0, client.player.getYaw(), client.player.getPitch()));
	}

	public static void sendSneakingPacket(MinecraftClient client, boolean isSneaking) {
		PlayerInput playerInput = new PlayerInput(client.options.forwardKey.isPressed(),
				client.options.backKey.isPressed(), client.options.leftKey.isPressed(),
				client.options.rightKey.isPressed(), client.options.jumpKey.isPressed(), isSneaking,
				client.options.sprintKey.isPressed());

		WynnSpellsUtils.sendPacket(client, new PlayerInputC2SPacket(playerInput));
	}

	public static boolean isArcher(MinecraftClient client) {
		if (client == null || client.player == null)
			return false;

		ItemStack heldItem = client.player.getMainHandStack();
		if (heldItem == null)
			return false;

		List<Text> tooltip = heldItem.getTooltip(Item.TooltipContext.DEFAULT, client.player, TooltipType.BASIC);
		if (tooltip == null || tooltip.isEmpty())
			return false;

		for (Text line : tooltip) {
			if (line.getString().contains("Archer/Hunter"))
				return true;
		}

		return false;
	}

	public static void sendNotification(Text description, Boolean shouldSend) {
		if (!shouldSend) {
			return;
		}

		SystemToast.add(MinecraftClient.getInstance().getToastManager(), SystemToast.Type.WORLD_BACKUP,
				Text.of(WynnSpells.MOD_NAME), description);
	}

	public static long getAutoDelay() {
		long rtt = WynnSpellsClient.getInstance().getPingTracker().getLastPing();
		long oneWay = rtt / 2;
		long msPerTick = 1000L / 20L;
		long jitterMargin = 30L;
		long delay = msPerTick + jitterMargin;
		if (delay < oneWay)
			delay = 0;
		WynnSpells.LOGGER.info("Auto Delay: {}", delay);
		return delay;
	}

	public static void refreshKeyBindings() {
		KeyBinding.updateKeysByCode();
		WynnSpells.LOGGER.debug("Refreshed keybinds.");
	}

	public static void saveKeyBindings() {
		MinecraftClient.getInstance().options.write();
		WynnSpells.LOGGER.debug("Saved keybinds.");
	}

	public static void refreshAndSaveKeyBindings() {
		refreshKeyBindings();
		saveKeyBindings();
	}
}
