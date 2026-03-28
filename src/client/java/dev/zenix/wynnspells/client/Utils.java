package dev.zenix.wynnspells.client;

import dev.zenix.wynnspells.WynnSpells;
import java.util.List;
import java.util.Map;
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

public class Utils {

	public static long MS_PER_TICK = 1000L / 20L;
	public static int KEY_LIMIT = 1;
	private static final Map<String, String> ITEM_ENCODINGS_BY_CLASS_TYPE = Map.of("Archer", "Archer/Hunter", "Warrior",
			"Warrior/Knight", "Mage", "Mage/Dark Wizard", "Assassin", "Assassin/Ninja", "Shaman", "Shaman/Skyseer");
	private static final Map<String, String> ITEM_ENCODINGS_BY_WEAPON_TYPE = Map.of("Archer", "󐀂󐀁󏿿󏿿󏿿󏿿󏿬",
			"Warrior", "󐀂󐀁󏿿󏿿󏿿󏿿󏿿󏿿󏿠", "Mage", "󐀂󐀁󏿿󏿿󏿿󏿿󏿿󏿦", "Assassin",
			"󐀂󐀁󏿿󏿿󏿿󏿿󏿿󏿿󏿿󏿚", "Shaman", "󐀂󐀁󏿿󏿿󏿿󏿿󏿿󏿿󏿢");

	public static void sendPacket(MinecraftClient client, Packet<?> packet) {
		if (client == null)
			return;

		ClientPlayNetworkHandler networkHandler = client.getNetworkHandler();
		if (networkHandler == null)
			return;

		networkHandler.sendPacket(packet);
	}

	public static void sendAttackPacket(MinecraftClient client) {
		Utils.sendPacket(client, new HandSwingC2SPacket(Hand.MAIN_HAND));
	}

	public static void sendInteractPacket(MinecraftClient client) {
		Utils.sendPacket(client,
				new PlayerInteractItemC2SPacket(Hand.MAIN_HAND, 0, client.player.getYaw(), client.player.getPitch()));
	}

	public static void sendSneakingPacket(MinecraftClient client, boolean isSneaking) {
		PlayerInput playerInput = new PlayerInput(client.options.forwardKey.isPressed(),
				client.options.backKey.isPressed(), client.options.leftKey.isPressed(),
				client.options.rightKey.isPressed(), client.options.jumpKey.isPressed(), isSneaking,
				client.options.sprintKey.isPressed());

		Utils.sendPacket(client, new PlayerInputC2SPacket(playerInput));
	}

	public static boolean mainHandItemHasTooltipText(MinecraftClient client, String searchText) {
		if (client == null || client.player == null || searchText == null || searchText.isEmpty())
			return false;

		ItemStack heldItem = client.player.getMainHandStack();
		if (heldItem == null || heldItem.isEmpty())
			return false;

		List<Text> tooltip = heldItem.getTooltip(Item.TooltipContext.DEFAULT, client.player, TooltipType.BASIC);
		if (tooltip == null || tooltip.isEmpty())
			return false;

		for (Text line : tooltip) {
			if (line.getString().contains(searchText))
				return true;
		}

		return false;
	}

	public static boolean isArcher(MinecraftClient client) {
		return mainHandItemHasTooltipText(client, ITEM_ENCODINGS_BY_CLASS_TYPE.get("Archer"))
				|| mainHandItemHasTooltipText(client, ITEM_ENCODINGS_BY_WEAPON_TYPE.get("Archer"));
	}

	public static boolean isWarrior(MinecraftClient client) {
		return mainHandItemHasTooltipText(client, ITEM_ENCODINGS_BY_CLASS_TYPE.get("Warrior"))
				|| mainHandItemHasTooltipText(client, ITEM_ENCODINGS_BY_WEAPON_TYPE.get("Warrior"));
	}

	public static boolean isMage(MinecraftClient client) {
		return mainHandItemHasTooltipText(client, ITEM_ENCODINGS_BY_CLASS_TYPE.get("Mage"))
				|| mainHandItemHasTooltipText(client, ITEM_ENCODINGS_BY_WEAPON_TYPE.get("Mage"));
	}

	public static boolean isAssassin(MinecraftClient client) {
		return mainHandItemHasTooltipText(client, ITEM_ENCODINGS_BY_CLASS_TYPE.get("Assassin"))
				|| mainHandItemHasTooltipText(client, ITEM_ENCODINGS_BY_WEAPON_TYPE.get("Assassin"));
	}

	public static boolean isShaman(MinecraftClient client) {
		return mainHandItemHasTooltipText(client, ITEM_ENCODINGS_BY_CLASS_TYPE.get("Shaman"))
				|| mainHandItemHasTooltipText(client, ITEM_ENCODINGS_BY_WEAPON_TYPE.get("Shaman"));
	}

	public static boolean isWeapon(MinecraftClient client) {
		return isArcher(client) || isWarrior(client) || isMage(client) || isAssassin(client) || isShaman(client);
	}

	public static void sendNotification(Text description, Boolean shouldSend) {
		if (!shouldSend) {
			return;
		}

		SystemToast.add(MinecraftClient.getInstance().getToastManager(), SystemToast.Type.WORLD_BACKUP,
				Text.of(WynnSpells.MOD_NAME), description);
	}

	public static long getAutoDelay() {
		WynnSpellsClient client = WynnSpellsClient.getInstance();
		long rtt = client.getPingTracker().getLastPing();
		long oneWay = rtt / 2;
		long jitter = MS_PER_TICK / 2;
		long tolerance = client.getConfig().getAutoDelayTolerance();
		long margin = tolerance + (tolerance * (oneWay / MS_PER_TICK));
		long delay = MS_PER_TICK + jitter + margin;
		WynnSpells.LOGGER.debug("Auto Delay: {}", delay);
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

	public static boolean[] keyToClicks(KeyBinding key, boolean isArcher) {
		if (key.equals(WynnSpellsClient.MELEE_KEY)) {
			return isArcher ? new boolean[]{true} : new boolean[]{false};
		} else if (key.equals(WynnSpellsClient.FIRST_SPELL_KEY)) {
			return isArcher ? new boolean[]{false, true, false} : new boolean[]{true, false, true};
		} else if (key.equals(WynnSpellsClient.SECOND_SPELL_KEY)) {
			return isArcher ? new boolean[]{false, false, false} : new boolean[]{true, true, true};
		} else if (key.equals(WynnSpellsClient.THIRD_SPELL_KEY)) {
			return isArcher ? new boolean[]{false, true, true} : new boolean[]{true, false, false};
		} else if (key.equals(WynnSpellsClient.FOURTH_SPELL_KEY)) {
			return isArcher ? new boolean[]{false, false, true} : new boolean[]{true, true, false};
		}

		return new boolean[0];
	}
}
