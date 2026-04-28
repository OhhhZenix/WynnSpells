package dev.zenix.wynnspells.client;

import dev.zenix.wynnspells.client.event.ContinueDestroyBlockEvent;
import dev.zenix.wynnspells.client.event.PlayerAttackEvent;
import dev.zenix.wynnspells.client.event.PlayerInteractAtEvent;
import dev.zenix.wynnspells.client.event.PlayerInteractEvent;
import dev.zenix.wynnspells.client.event.PlayerStartAttackEvent;
import dev.zenix.wynnspells.client.event.StartDestroyBlockEvent;
import dev.zenix.wynnspells.client.event.UseItemEvent;
import dev.zenix.wynnspells.client.event.UseItemOnEvent;
import java.util.Deque;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.TimeUnit;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;

public class Caster {

	private final Minecraft mc;
	private final Deque<Boolean> clicks = new ConcurrentLinkedDeque<>();
	private final Deque<KeyMapping> keys = new ConcurrentLinkedDeque<>();
	private final Set<KeyMapping> previousPressedKeys = ConcurrentHashMap.newKeySet();
	private final Map<KeyMapping, Long> keysTimer = new ConcurrentHashMap<>();
	private volatile boolean running = true;
	private int previousSlot = -1;
	private long lastClickTime = System.nanoTime();

	public Caster(Minecraft mc) {
		this.mc = mc;
		PlayerStartAttackEvent.HANDLER.register(this::onPlayerStartAttackEvent);
		PlayerAttackEvent.HANDLER.register(this::onPlayerAttackEvent);
		StartDestroyBlockEvent.HANDLER.register(this::onStartDestroyBlockEvent);
		ContinueDestroyBlockEvent.HANDLER.register(this::onContinueDestroyBlockEvent);
		UseItemEvent.HANDLER.register(this::onUseItemEvent);
		UseItemOnEvent.HANDLER.register(this::onUseItemOnEvent);
		PlayerInteractEvent.HANDLER.register(this::onPlayerInteractEvent);
		PlayerInteractAtEvent.HANDLER.register(this::onPlayerInteractAtEvent);
	}

	public void start() {
		Thread thread = new Thread(this::run);
		thread.setDaemon(true);
		thread.start();
	}

	public void stop() {
		running = false;
	}

	private boolean shouldBlock() {
		long now = System.nanoTime();
		long tolerance = 10;
		long delay = Utils.getClickDelay() * tolerance;
		return now < lastClickTime + delay;
	}

	private void run() {
		while (running) {
			resetState();
			processClicks();
			processIntents();
			processKeys();
		}
	}

	private boolean handleVanillaMelee() {
		if (!shouldBlock()) {
			return false;
		}

		if (!Utils.isArcher(mc)) {
			// keys.add(WynnSpellsClient.MELEE_KEY);
		}

		return true;
	}

	private boolean handleVanillaInteract() {
		if (!shouldBlock()) {
			return false;
		}

		if (Utils.isArcher(mc)) {
			// keys.add(WynnSpellsClient.MELEE_KEY);
		}

		return true;
	}

	private boolean onPlayerStartAttackEvent(LocalPlayer localPlayer, InteractionHand hand) {
		return handleVanillaMelee();
	}

	private boolean onPlayerAttackEvent(Player player, Entity target) {
		return handleVanillaMelee();
	}

	private boolean onStartDestroyBlockEvent(BlockPos position, Direction direction) {
		return handleVanillaMelee();
	}

	private boolean onContinueDestroyBlockEvent(BlockPos position, Direction direction) {
		return handleVanillaMelee();
	}

	private boolean onUseItemEvent(Player player, InteractionHand hand) {
		return handleVanillaInteract();
	}

	private boolean onUseItemOnEvent(LocalPlayer player, InteractionHand hand, BlockHitResult result) {
		return handleVanillaInteract();
	}

	private boolean onPlayerInteractEvent(Player player, Entity target, InteractionHand hand) {
		return handleVanillaInteract();
	}

	private boolean onPlayerInteractAtEvent(Player player, Entity target, EntityHitResult ray, InteractionHand hand) {
		return handleVanillaInteract();
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

		long now = System.nanoTime();
		long delay = Utils.getClickDelay();
		if (now < lastClickTime + delay) {
			return;
		}

		boolean click = clicks.poll();
		if (click) {
			Utils.sendInteractPacket(mc); // right click
		} else {
			Utils.sendAttackPacket(mc); // left click
		}

		lastClickTime = now;
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