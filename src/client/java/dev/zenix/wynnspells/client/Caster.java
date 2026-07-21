package dev.zenix.wynnspells.client;

import dev.zenix.wynnspells.client.event.*;
import java.util.*;
import java.util.concurrent.TimeUnit;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
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

	private final Deque<Boolean> clicks = new ArrayDeque<>();
	private final Deque<KeyMapping> keys = new ArrayDeque<>();

	private final Set<KeyMapping> previousPressedKeys = new HashSet<>();
	private final Map<KeyMapping, Long> keysTimer = new HashMap<>();

	private int previousSlot = -1;
	private long lastClickTime = 0;

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

		ClientTickEvents.END_CLIENT_TICK.register(client -> tick());
	}

	// =========================
	// Core Loop
	// =========================

	private void tick() {
		if (mc == null || mc.player == null || mc.isPaused()) {
			return;
		}

		resetState();
		processKeys();
		processIntents();
		processClicks();
	}

	// =========================
	// Casting State
	// =========================

	private boolean isCasting() {
		long now = System.nanoTime();
		long delay = Utils.getClickDelay();
		long tolerance = delay * 3;
		return !clicks.isEmpty() || now < lastClickTime + (delay + tolerance);
	}

	private boolean shouldBlockVanillaInput() {
		ClothConfig config = WynnSpellsClient.getInstance().getConfig();
		return config.getBlockClicks() && isCasting();
	}

	private boolean handleVanillaAction() {
		return shouldBlockVanillaInput();
	}

	// =========================
	// Event Hooks
	// =========================

	private boolean onPlayerStartAttackEvent(LocalPlayer player, InteractionHand hand) {
		return handleVanillaAction();
	}

	private boolean onPlayerAttackEvent(Player player, Entity target) {
		return handleVanillaAction();
	}

	private boolean onStartDestroyBlockEvent(BlockPos pos, Direction dir) {
		return handleVanillaAction();
	}

	private boolean onContinueDestroyBlockEvent(BlockPos pos, Direction dir) {
		return handleVanillaAction();
	}

	private boolean onUseItemEvent(Player player, InteractionHand hand) {
		return handleVanillaAction();
	}

	private boolean onUseItemOnEvent(LocalPlayer player, InteractionHand hand, BlockHitResult result) {
		return handleVanillaAction();
	}

	private boolean onPlayerInteractEvent(Player player, Entity target, InteractionHand hand) {
		return handleVanillaAction();
	}

	private boolean onPlayerInteractAtEvent(Player player, Entity target, EntityHitResult ray, InteractionHand hand) {
		return handleVanillaAction();
	}

	// =========================
	// State Reset
	// =========================

	private void resetState() {
		int currentSlot = mc.player.getInventory().getSelectedSlot();

		if (previousSlot == currentSlot) {
			return;
		}

		previousSlot = currentSlot;

		clicks.clear();
		keys.clear();
		lastClickTime = 0;
	}

	// =========================
	// Click Processing
	// =========================

	private void processClicks() {
		if (clicks.isEmpty()) {
			return;
		}

		long now = System.nanoTime();
		long delay = Utils.getClickDelay();

		if (now - lastClickTime < delay) {
			return;
		}

		boolean click = clicks.poll();

		if (click) {
			Utils.sendInteractPacket(mc);
		} else {
			Utils.sendAttackPacket(mc);
		}

		lastClickTime = now;
	}

	// =========================
	// Intent Processing
	// =========================

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

	// =========================
	// Key Handling
	// =========================

	private void addKey(KeyMapping key) {
		ClothConfig config = WynnSpellsClient.getInstance().getConfig();

		if (isCasting()) {
			if (Utils.isSpellKey(key)) {
				Utils.sendNotification(Component.literal("Cast ignored: finish the current spell first."),
						config.shouldNotifyBusyCast());
			}
			return;
		}

		if (keys.size() >= Utils.KEY_LIMIT) {
			Utils.sendNotification(Component.literal("Cast ignored: try slowing down a bit."),
					config.shouldNotifyBusyCast());
			return;
		}

		if (config.isWeaponOnlyCasting() && !Utils.isWeapon(mc)) {
			return;
		}

		keys.offer(key);
	}

	private void processKey(KeyMapping key) {
		if (key == null) {
			return;
		}

		ClothConfig config = WynnSpellsClient.getInstance().getConfig();
		boolean repeat = config.getRepeatHeldKeys() && key.same(WynnSpellsClient.MELEE_KEY);

		long now = System.nanoTime();

		if (repeat) {
			boolean pressed = key.isDown();

			if (!pressed) {
				if (previousPressedKeys.remove(key)) {
					keysTimer.remove(key);
				}
				return;
			}

			if (!previousPressedKeys.contains(key)) {
				previousPressedKeys.add(key);
				keysTimer.put(key, now);
				addKey(key);
				return;
			}

			long threshold = TimeUnit.MILLISECONDS.toNanos(config.getRepeatThreshold());
			long last = keysTimer.getOrDefault(key, 0L);

			if (now - last >= threshold) {
				addKey(key);
				keysTimer.put(key, now);
			}

		} else {
			if (key.consumeClick()) {
				addKey(key);
			}
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
