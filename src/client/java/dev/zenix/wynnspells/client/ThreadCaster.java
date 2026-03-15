package dev.zenix.wynnspells.client;

import dev.zenix.wynnspells.client.event.DoAttackEvent;
import dev.zenix.wynnspells.client.event.InteractItemEvent;
import java.util.Deque;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.TimeUnit;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;

public class ThreadCaster {

	private final MinecraftClient mc;
	private final Deque<Boolean> clicks = new ConcurrentLinkedDeque<>();
	private final Deque<Intent> buffer = new ConcurrentLinkedDeque<>();
	private volatile boolean isRunning = true;
	private ItemStack previousItem = ItemStack.EMPTY;
	private long lastTime = System.nanoTime();

	public ThreadCaster(MinecraftClient mc) {
		this.mc = mc;
		DoAttackEvent.EVENT.register(this::processVanillaMelee);
		InteractItemEvent.EVENT.register(this::processVanillaInteract);
	}

	public void start() {
		Thread thread = new Thread(this::run);
		thread.setDaemon(true);
		thread.start();
	}

	public void stop() {
		isRunning = false;
	}

	private void run() {
		while (isRunning) {
			processClicks();
			processIntents();
			processKeys();
		}
	}

	private boolean processVanillaMelee(ClientPlayerEntity player, Hand hand) {
		if (clicks.isEmpty()) {
			return false;
		}

		buffer.add(Intent.MELEE);
		return true;
	}

	private boolean processVanillaInteract(PlayerEntity player, Hand hand) {
		return !clicks.isEmpty();
	}

	private void processClicks() {
		if (clicks.isEmpty()) {
			return;
		}

		ClothConfig config = WynnSpellsClient.getInstance().getConfig();
		long delay = TimeUnit.MILLISECONDS.toNanos(config.getManualDelay());
		if (config.shouldUseAutoDelay()) {
			delay = TimeUnit.MILLISECONDS.toNanos(Utils.getAutoDelay());
		}

		long now = System.nanoTime();
		if (now < lastTime + delay) {
			return;
		}

		boolean click = clicks.poll();
		if (click) {
			Utils.sendInteractPacket(mc); // right click
		} else {
			Utils.sendAttackPacket(mc); // left click
		}

		lastTime = now;
	}

	private void processIntents() {
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

	private void processKey(KeyBinding key, Intent intent) {
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

	private void processKeys() {
		processKey(WynnSpellsClient.MELEE_KEY, Intent.MELEE);
		processKey(WynnSpellsClient.FIRST_SPELL_KEY, Intent.FIRST_SPELL);
		processKey(WynnSpellsClient.SECOND_SPELL_KEY, Intent.SECOND_SPELL);
		processKey(WynnSpellsClient.THIRD_SPELL_KEY, Intent.THIRD_SPELL);
		processKey(WynnSpellsClient.FOURTH_SPELL_KEY, Intent.FOURTH_SPELL);
	}
}
