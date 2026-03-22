package dev.zenix.wynnspells.client;

import dev.zenix.wynnspells.client.event.DoAttackEvent;
import dev.zenix.wynnspells.client.event.InteractItemEvent;
import java.util.Deque;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.TimeUnit;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;

public class ThreadCaster {

	private final MinecraftClient mc;
	private final Deque<Boolean> clicks = new ConcurrentLinkedDeque<>();
	private final Deque<KeyBinding> keys = new ConcurrentLinkedDeque<>();
	private final Set<KeyBinding> previousPressedKeys = ConcurrentHashMap.newKeySet();
	private final Map<KeyBinding, Long> keysTimer = new ConcurrentHashMap<>();
	private volatile boolean isRunning = true;
	private int previousSlot = -1;
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
			resetState();
			processClicks();
			processIntents();
			processKeys();
		}
	}

	private boolean processVanillaMelee(ClientPlayerEntity player, Hand hand) {
		boolean shouldBlockClicks = WynnSpellsClient.getInstance().getConfig().getBlockClicks();

		if (!shouldBlockClicks) {
			return false;
		}

		if (clicks.isEmpty()) {
			return false;
		}

		keys.add(WynnSpellsClient.MELEE_KEY);
		return true;
	}

	private boolean processVanillaInteract(PlayerEntity player, Hand hand) {
		boolean shouldBlockClicks = WynnSpellsClient.getInstance().getConfig().getBlockClicks();

		if (!shouldBlockClicks) {
			return false;
		}

		return !clicks.isEmpty();
	}

	private void resetState() {
		if (mc == null || mc.player == null) {
			return;
		}

		int currentSlot = mc.player.getInventory().getSelectedSlot();

		if (previousSlot == currentSlot) {
			return;
		}

		previousSlot = currentSlot;
		clicks.clear();
		keys.clear();
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

		if (keys.isEmpty()) {
			return;
		}

		KeyBinding key = keys.poll();
		boolean isArcher = Utils.isArcher(mc);

		for (boolean click : Utils.keyToClicks(key, isArcher)) {
			clicks.add(click);
		}
	}

	private void addKey(KeyBinding key) {
		ClothConfig config = WynnSpellsClient.getInstance().getConfig();

		int bufferLimit = config.getKeyLimit();
		if (keys.size() >= bufferLimit) {
			Utils.sendNotification(Text.of("Ignored: key limit reached."), config.shouldNotifyBusyCast());
			return;
		}

		if (config.isWeaponOnlyCasting() && !Utils.isWeapon(mc)) {
			return;
		}

		keys.add(key);
	}

	private void processKey(KeyBinding key) {
		if (key == null) {
			return;
		}

		boolean shouldRepeatHeldKeys = WynnSpellsClient.getInstance().getConfig().getRepeatHeldKeys();

		if (shouldRepeatHeldKeys) {
			boolean isPressed = key.isPressed();
			long now = System.nanoTime();

			// Key released → cleanup state
			if (!isPressed) {
				if (previousPressedKeys.remove(key)) {
					keysTimer.remove(key);
				}
				return;
			}

			// First press
			if (!previousPressedKeys.contains(key)) {
				previousPressedKeys.add(key);
				keysTimer.put(key, now);
				addKey(key);
				return;
			}

			// Held key → check repeat threshold
			Long lastPressTime = keysTimer.get(key);
			if (lastPressTime == null) {
				keysTimer.put(key, now);
				return;
			}

			long repeatThreshold = TimeUnit.MILLISECONDS
					.toNanos(WynnSpellsClient.getInstance().getConfig().getRepeatThreshold());
			if (now - lastPressTime >= repeatThreshold) {
				addKey(key);
				keysTimer.put(key, now); // reset repeat timer
			}
		} else {
			if (!key.wasPressed()) {
				return;
			}

			addKey(key);
		}
	}

	private void processKeys() {
		processKey(WynnSpellsClient.MELEE_KEY);
		processKey(WynnSpellsClient.FIRST_SPELL_KEY);
		processKey(WynnSpellsClient.SECOND_SPELL_KEY);
		processKey(WynnSpellsClient.THIRD_SPELL_KEY);
		processKey(WynnSpellsClient.FOURTH_SPELL_KEY);
	}
}
