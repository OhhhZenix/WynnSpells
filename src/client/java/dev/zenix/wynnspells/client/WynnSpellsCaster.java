package dev.zenix.wynnspells.client;

import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.TimeUnit;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;

public class WynnSpellsCaster implements Runnable {

	private final MinecraftClient mc;
	private final LinkedBlockingDeque<WynnSpellsIntent> buffer = new LinkedBlockingDeque<>();
	private final LinkedBlockingDeque<Boolean> clicks = new LinkedBlockingDeque<>();
	private volatile ItemStack previousItem = null;
	private volatile boolean isRunning = true;
	private long lastTime = System.nanoTime();

	public WynnSpellsCaster(MinecraftClient mc) {
		this.mc = mc;
	}

	public void start() {
		Thread thread = new Thread(this);
		thread.start();
	}

	public void stop() {
		isRunning = false;
	}

	@Override
	public void run() {
		while (isRunning) {
			processClicks();
			processBuffer();
		}
	}

	private void processClicks() {
		if (clicks.isEmpty())
			return;

		long now = System.nanoTime();
		long delay = TimeUnit.MILLISECONDS.toNanos(WynnSpellsClient.getInstance().getConfig().getManualDelay());
		if (now < lastTime + delay)
			return;

		try {
			boolean click = clicks.take();

			if (click) {
				WynnSpellsUtils.sendInteractPacket(mc); // right click
			} else {
				WynnSpellsUtils.sendAttackPacket(mc); // left click
			}
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}

		lastTime = now;
	}

	private void processBuffer() {
		if (!clicks.isEmpty())
			return;

		if (buffer.isEmpty())
			return;

		try {
			WynnSpellsIntent intent = buffer.take();
			boolean isArcher = WynnSpellsUtils.isArcher(mc);
			for (boolean click : intent.convert(isArcher)) {
				clicks.add(click);
			}
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}
	}

	private void processIntentKey(KeyBinding key, WynnSpellsIntent intent) {
		if (key == null)
			return;

		if (!key.isPressed())
			return;

		WynnSpellsConfig config = WynnSpellsClient.getInstance().getConfig();
		if (buffer.size() >= config.getBufferLimit()) {
			WynnSpellsUtils.sendNotification(Text.of("Cast ignored: spell queue is busy."),
					config.shouldNotifyBusyCast());
			return;
		}

		if (mc == null || mc.player == null) {
			return;
		}

		ItemStack itemInMainHand = mc.player.getMainHandStack();
		if (itemInMainHand != previousItem) {
			previousItem = itemInMainHand;
			buffer.clear();
		}

		key.setPressed(false);
		buffer.add(intent);
	}

	private void processVanillaMelee() {

	}

	public void processIntentKeys() {
		processVanillaMelee();
		processIntentKey(WynnSpellsClient.MELEE_KEY, WynnSpellsIntent.MELEE);
		processIntentKey(WynnSpellsClient.FIRST_SPELL_KEY, WynnSpellsIntent.FIRST_SPELL);
		processIntentKey(WynnSpellsClient.SECOND_SPELL_KEY, WynnSpellsIntent.SECOND_SPELL);
		processIntentKey(WynnSpellsClient.THIRD_SPELL_KEY, WynnSpellsIntent.THIRD_SPELL);
		processIntentKey(WynnSpellsClient.FOURTH_SPELL_KEY, WynnSpellsIntent.FOURTH_SPELL);
	}
}
