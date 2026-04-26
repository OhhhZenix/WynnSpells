package dev.zenix.wynnspells.client;

import dev.zenix.wynnspells.WynnSpells;
import java.util.List;
import java.util.Map;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.toasts.SystemToast;
import net.minecraft.client.gui.components.toasts.SystemToast.SystemToastId;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ServerboundPlayerInputPacket;
import net.minecraft.network.protocol.game.ServerboundSwingPacket;
import net.minecraft.network.protocol.game.ServerboundUseItemPacket;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Input;
import net.minecraft.world.item.Item.TooltipContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

public class Utils {

	public static long MS_PER_TICK = 1000L / 20L;
	public static int KEY_LIMIT = 1;

	private static final Map<String, String> ITEM_ENCODINGS_BY_CLASS_TYPE = Map.of("Archer", "Archer/Hunter", "Warrior",
			"Warrior/Knight", "Mage", "Mage/Dark Wizard", "Assassin", "Assassin/Ninja", "Shaman", "Shaman/Skyseer");

	private static final Map<String, String> ITEM_ENCODINGS_BY_WEAPON_TYPE = Map.of("Archer", "󐀂󐀁󏿿󏿿󏿿󏿿󏿬",
			"Warrior", "󐀂󐀁󏿿󏿿󏿿󏿿󏿿󏿿󏿠", "Mage", "󐀂󐀁󏿿󏿿󏿿󏿿󏿿󏿦", "Assassin",
			"󐀂󐀁󏿿󏿿󏿿󏿿󏿿󏿿󏿿󏿚", "Shaman", "󐀂󐀁󏿿󏿿󏿿󏿿󏿿󏿿󏿢");

	public static void sendPacket(Minecraft client, Packet<?> packet) {
		client.getConnection().send(packet);
	}

	public static void sendAttackPacket(Minecraft client) {
		sendPacket(client, new ServerboundSwingPacket(InteractionHand.MAIN_HAND));
	}

	public static void sendInteractPacket(Minecraft client) {
		sendPacket(client, new ServerboundUseItemPacket(InteractionHand.MAIN_HAND, 0, client.player.getYRot(),
				client.player.getXRot()));
	}

	public static void sendSneakingPacket(Minecraft client, boolean isSneaking) {
		// TODO: actually fix the inputs
		Input input = new Input(false, false, false, false, false, isSneaking, false);
		sendPacket(client, new ServerboundPlayerInputPacket(input));
	}

	public static boolean mainHandItemHasTooltipText(Minecraft client, String searchText) {
		if (client == null || client.player == null || searchText == null || searchText.isEmpty())
			return false;

		ItemStack heldItem = client.player.getMainHandItem();
		if (heldItem == null || heldItem.isEmpty())
			return false;

		List<Component> tooltip = heldItem.getTooltipLines(TooltipContext.of(client.level), client.player,
				TooltipFlag.NORMAL);
		if (tooltip == null || tooltip.isEmpty())
			return false;

		for (Component line : tooltip) {
			if (line.getString().contains(searchText))
				return true;
		}

		return false;
	}

	public static boolean isArcher(Minecraft client) {
		return mainHandItemHasTooltipText(client, ITEM_ENCODINGS_BY_CLASS_TYPE.get("Archer"))
				|| mainHandItemHasTooltipText(client, ITEM_ENCODINGS_BY_WEAPON_TYPE.get("Archer"));
	}

	public static boolean isWarrior(Minecraft client) {
		return mainHandItemHasTooltipText(client, ITEM_ENCODINGS_BY_CLASS_TYPE.get("Warrior"))
				|| mainHandItemHasTooltipText(client, ITEM_ENCODINGS_BY_WEAPON_TYPE.get("Warrior"));
	}

	public static boolean isMage(Minecraft client) {
		return mainHandItemHasTooltipText(client, ITEM_ENCODINGS_BY_CLASS_TYPE.get("Mage"))
				|| mainHandItemHasTooltipText(client, ITEM_ENCODINGS_BY_WEAPON_TYPE.get("Mage"));
	}

	public static boolean isAssassin(Minecraft client) {
		return mainHandItemHasTooltipText(client, ITEM_ENCODINGS_BY_CLASS_TYPE.get("Assassin"))
				|| mainHandItemHasTooltipText(client, ITEM_ENCODINGS_BY_WEAPON_TYPE.get("Assassin"));
	}

	public static boolean isShaman(Minecraft client) {
		return mainHandItemHasTooltipText(client, ITEM_ENCODINGS_BY_CLASS_TYPE.get("Shaman"))
				|| mainHandItemHasTooltipText(client, ITEM_ENCODINGS_BY_WEAPON_TYPE.get("Shaman"));
	}

	public static boolean isWeapon(Minecraft client) {
		return isArcher(client) || isWarrior(client) || isMage(client) || isAssassin(client) || isShaman(client);
	}

	public static void sendNotification(Component description, Boolean shouldSend) {
		if (!shouldSend) {
			return;
		}

		Minecraft.getInstance().getToastManager().addToast(
				new SystemToast(SystemToastId.WORLD_BACKUP, Component.literal(WynnSpells.MOD_NAME), description));
	}

	// public static long getAutoDelay() {
	// WynnSpellsClient client = WynnSpellsClient.getInstance();
	// long rtt = client.getPingTracker().getLastPing();
	// long oneWay = rtt / 2;
	// long jitter = MS_PER_TICK / 2;
	// long tolerance = client.getConfig().getAutoDelayTolerance();
	// long margin = tolerance + (tolerance * (oneWay / MS_PER_TICK));
	// long delay = MS_PER_TICK + jitter + margin;
	// WynnSpells.LOGGER.debug("Auto Delay: {}", delay);
	// return delay;
	// }

	public static void refreshKeyBindings() {
		KeyMapping.resetMapping();
		WynnSpells.LOGGER.debug("Refreshed keybinds.");
	}

	public static void saveKeyBindings() {
		Minecraft.getInstance().options.save();
		WynnSpells.LOGGER.debug("Saved keybinds.");
	}

	public static void refreshAndSaveKeyBindings() {
		refreshKeyBindings();
		saveKeyBindings();
	}

	public static boolean[] keyToClicks(KeyMapping key, boolean isArcher) {
		if (key.equals(WynnSpellsClient.MELEE_KEY)) {
			return isArcher ? new boolean[] { true } : new boolean[] { false };
		} else if (key.equals(WynnSpellsClient.FIRST_SPELL_KEY)) {
			return isArcher ? new boolean[] { false, true, false } : new boolean[] { true, false, true };
		} else if (key.equals(WynnSpellsClient.SECOND_SPELL_KEY)) {
			return isArcher ? new boolean[] { false, false, false } : new boolean[] { true, true, true };
		} else if (key.equals(WynnSpellsClient.THIRD_SPELL_KEY)) {
			return isArcher ? new boolean[] { false, true, true } : new boolean[] { true, false, false };
		} else if (key.equals(WynnSpellsClient.FOURTH_SPELL_KEY)) {
			return isArcher ? new boolean[] { false, false, true } : new boolean[] { true, true, false };
		}

		return new boolean[0];
	}
}