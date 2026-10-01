package dev.zenix.wynnspells.client.core;

import dev.zenix.wynnspells.WynnSpells;
import dev.zenix.wynnspells.client.event.ClientPacketListenerEvents;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import net.minecraft.client.Minecraft;
import net.minecraft.network.protocol.ping.ClientboundPongResponsePacket;
import net.minecraft.network.protocol.ping.ServerboundPingRequestPacket;
import net.minecraft.util.Util;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

public class PingTracker {

  private final Minecraft mc;
  private final ScheduledExecutorService scheduler;
  private volatile long lastPing = 0;

  public PingTracker(Minecraft mc) {
    this.mc = mc;
    this.scheduler = Executors.newSingleThreadScheduledExecutor();
  }

  public void start() {
    scheduler.scheduleAtFixedRate(this::sendPing, 0, 1, TimeUnit.SECONDS);
    ClientPacketListenerEvents.HANDLE_PONG_RESPONSE.register(this::onPongReceivedEvent);
  }

  public void stop() {
    scheduler.shutdown();
  }

  private void sendPing() {
    Utils.sendPacket(mc, new ServerboundPingRequestPacket(Util.getMillis()));
  }

  private void onPongReceivedEvent(ClientboundPongResponsePacket packet, CallbackInfo ci) {
    long currentTime = Util.getMillis();
    long startTime = packet.time();
    WynnSpells.LOGGER.debug("Current Time: {}", currentTime);
    WynnSpells.LOGGER.debug("Start Time: {}", startTime);
    WynnSpells.LOGGER.debug("Ping: {}", currentTime - startTime);
    lastPing = currentTime - startTime;
  }

  public long getLastPing() {
    return lastPing;
  }
}
