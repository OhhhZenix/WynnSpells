package dev.zenix.wynnspells.client;

import dev.zenix.wynnspells.WynnSpells;
import dev.zenix.wynnspells.client.event.PingResultEvent;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import net.minecraft.client.MinecraftClient;
import net.minecraft.network.packet.c2s.query.QueryPingC2SPacket;
import net.minecraft.network.packet.s2c.query.PingResultS2CPacket;
import net.minecraft.util.Util;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

public final class PingTracker {

	private final MinecraftClient mc;
	private final ScheduledExecutorService scheduler;
	private volatile long lastPing = 0;

	public PingTracker(MinecraftClient mc) {
		this.mc = mc;
		this.scheduler = Executors.newSingleThreadScheduledExecutor();
	}

	public void start() {
		scheduler.scheduleAtFixedRate(this::sendPing, 0, 1, TimeUnit.SECONDS);
		PingResultEvent.EVENT.register(this::onPingResult);
	}

	public void stop() {
		scheduler.shutdown();
	}

	private void sendPing() {
		Utils.sendPacket(mc, new QueryPingC2SPacket(Util.getMeasuringTimeMs()));
	}

	private void onPingResult(PingResultS2CPacket packet, CallbackInfo callbackInfo) {
		long currentTime = Util.getMeasuringTimeMs();
		long startTime = packet.startTime();
		WynnSpells.LOGGER.debug("Current Time: {}", currentTime);
		WynnSpells.LOGGER.debug("Start Time: {}", startTime);
		WynnSpells.LOGGER.debug("Ping: {}", currentTime - startTime);
		lastPing = currentTime - startTime;
	}

	public long getLastPing() {
		return lastPing;
	}
}
