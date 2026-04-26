package dev.zenix.wynnspells.client;

import dev.zenix.wynnspells.client.event.PlayerStartAttackEvent;
import dev.zenix.wynnspells.client.event.UseItemEvent;
import java.util.Deque;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.TimeUnit;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;

public class Caster {

	private final Minecraft mc;
	private final Deque<Boolean> clicks = new ConcurrentLinkedDeque<>();
	private final Deque<KeyMapping> keys = new ConcurrentLinkedDeque<>();
	private final Set<KeyMapping> previousPressedKeys = ConcurrentHashMap.newKeySet();
	private final Map<KeyMapping, Long> keysTimer = new ConcurrentHashMap<>();
	private volatile boolean isRunning = true;
	private int previousSlot = -1;
	private long lastTime = System.nanoTime();

	public Caster(Minecraft mc) {
		this.mc = mc;
		PlayerStartAttackEvent.HANDLER.register(this::onPlayerStartAttackEvent);
		UseItemEvent.HANDLER.register(this::onUseItemEvent);
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

	private boolean onPlayerStartAttackEvent(LocalPlayer localPlayer, InteractionHand hand) {
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

	boolean onUseItemEvent(Player player, InteractionHand hand) {
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

		KeyMapping key = keys.poll();
		boolean isArcher = Utils.isArcher(mc);

		for (boolean click : Utils.keyToClicks(key, isArcher)) {
			clicks.add(click);
		}
	}

	private void addKey(KeyMapping key) {
		ClothConfig config = WynnSpellsClient.getInstance().getConfig();

		if (keys.size() >= Utils.KEY_LIMIT) {
			Utils.sendNotification(Component.literal("Cast ignored: try slowing down a bit."),
					config.shouldNotifyBusyCast());
			return;
		}

		if (config.isWeaponOnlyCasting() && !Utils.isWeapon(mc)) {
			return;
		}

		keys.add(key);
	}

	private void processKey(KeyMapping key) {
		if (key == null) {
			return;
		}

		boolean shouldRepeatHeldKeys = WynnSpellsClient.getInstance().getConfig().getRepeatHeldKeys();

		if (shouldRepeatHeldKeys) {
			boolean isPressed = key.isDown();
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
			if (!key.consumeClick()) {
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