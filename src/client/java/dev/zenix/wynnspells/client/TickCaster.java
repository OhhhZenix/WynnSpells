package dev.zenix.wynnspells.client;

import dev.zenix.wynnspells.client.event.DoAttackEvent;
import dev.zenix.wynnspells.client.event.InteractItemEvent;
import java.util.ArrayDeque;
import java.util.Deque;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;

public class TickCaster {

	private long tickCounter = 0;
	private ItemStack previousItem = null;
	private Deque<Boolean> clicks = new ArrayDeque<>();
	private Deque<Intent> buffer = new ArrayDeque<>();

	public TickCaster() {
		DoAttackEvent.EVENT.register(this::processVanillaMelee);
		InteractItemEvent.EVENT.register(this::processVanillaInteract);
	}

	public void run(MinecraftClient mc) {
		tickCounter++;
		processClicks(mc);
		processIntents(mc);
		processKey(mc, WynnSpellsClient.MELEE_KEY, Intent.MELEE);
		processKey(mc, WynnSpellsClient.FIRST_SPELL_KEY, Intent.FIRST_SPELL);
		processKey(mc, WynnSpellsClient.SECOND_SPELL_KEY, Intent.SECOND_SPELL);
		processKey(mc, WynnSpellsClient.THIRD_SPELL_KEY, Intent.THIRD_SPELL);
		processKey(mc, WynnSpellsClient.FOURTH_SPELL_KEY, Intent.FOURTH_SPELL);
	}

	private void processClicks(MinecraftClient mc) {
		if (clicks.isEmpty()) {
			return;
		}

		ClothConfig config = WynnSpellsClient.getInstance().getConfig();
		long delay = config.getManualDelay();
		if (config.shouldUseAutoDelay()) {
			delay = Utils.getAutoDelay();
		}

		long delayTicks = Math.max(1, Math.ceilDiv(delay, Utils.MS_PER_TICK));
		if (tickCounter < delayTicks) {
			return;
		}

		boolean click = clicks.poll();
		if (click) {
			Utils.sendInteractPacket(mc); // right click
		} else {
			Utils.sendAttackPacket(mc); // left click
		}

		tickCounter = 0;
	}

	private void processIntents(MinecraftClient mc) {
		if (!clicks.isEmpty()) {
			return;
		}

		if (buffer.isEmpty()) {
			return;
		}

		Intent intent = buffer.poll();
		boolean isArcher = Utils.isArcher(mc);

		for (boolean click : intent.convert(isArcher)) {
			clicks.add(click);
		}
	}

	private void processKey(MinecraftClient mc, KeyBinding key, Intent intent) {
		if (key == null) {
			return;
		}

		if (!key.wasPressed()) {
			return;
		}

		ClothConfig config = WynnSpellsClient.getInstance().getConfig();
		if (buffer.size() >= config.getBufferLimit()) {
			Utils.sendNotification(Text.of("Cast ignored: spell queue is busy."), config.shouldNotifyBusyCast());
			return;
		}

		if (mc == null || mc.player == null) {
			return;
		}

		ItemStack itemInMainHand = mc.player.getMainHandStack();
		if (previousItem == null || !ItemStack.areEqual(itemInMainHand, previousItem)) {
			previousItem = itemInMainHand;
			buffer.clear();
		}

		if (config.isWeaponOnlyCasting() && !Utils.isWeapon(mc)) {
			return;
		}

		buffer.add(intent);
	}

	private boolean processVanillaMelee(ClientPlayerEntity player, Hand hand) {
		if (clicks.isEmpty()) {
			return false;
		}

		buffer.add(Intent.MELEE);
		return true;
	}

	private boolean processVanillaInteract(PlayerEntity player, Hand hand) {
		player.sendMessage(Text.of("Hello"), false);
		return false;
	}
}
