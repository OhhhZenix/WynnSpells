package dev.zenix.wynnspells.client;

import dev.zenix.wynnspells.WynnSpells;
import dev.zenix.wynnspells.client.event.PongReceivedEvent;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import net.minecraft.client.Minecraft;
import net.minecraft.network.protocol.ping.ServerboundPingRequestPacket;
import net.minecraft.util.Util;

public class PingTracker {

	private final Minecraft mc;
	private final ScheduledExecutorService scheduler;
	private volatile long lastPing = 0;
	private volatile long smoothedPing = 0;

	public PingTracker(Minecraft mc) {
		this.mc = mc;
		this.scheduler = Executors.newSingleThreadScheduledExecutor();
	}

	public void start() {
		scheduler.scheduleAtFixedRate(this::sendPing, 0, 1, TimeUnit.SECONDS);
		PongReceivedEvent.HANDLER.register(this::onPongReceivedEvent);
	}

	public void stop() {
		scheduler.shutdown();
	}

	private void sendPing() {
		Utils.sendPacket(mc, new ServerboundPingRequestPacket(Util.getMillis()));
	}

	private void onPongReceivedEvent(long time) {
		long currentTime = Util.getMillis();
		long sample = Math.max(0, currentTime - time);
		lastPing = sample;
		smoothedPing = smoothedPing == 0 ? sample : (smoothedPing * 3 + sample) / 4;
		WynnSpells.LOGGER.debug("Ping sample: {} ms, smoothed: {} ms", sample, smoothedPing);
	}

	public long getLastPing() {
		return lastPing;
	}

	public long getSmoothedPing() {
		return smoothedPing;
	}
}
