package dev.zenix.wynnspells.client.core;

import dev.zenix.wynnspells.WynnSpells;
import dev.zenix.wynnspells.client.event.ClientPacketListenerEvents;
import java.util.ArrayDeque;
import java.util.Queue;
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
  private final Queue<Long> pings = new ArrayDeque<>();

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
    long packetTime = packet.time();
    long ping = currentTime - packetTime;

    WynnSpells.LOGGER.debug("Current Time: {}", currentTime);
    WynnSpells.LOGGER.debug("Packet Time: {}", packetTime);
    WynnSpells.LOGGER.debug("Ping: {}", ping);

    pings.add(ping);
    if (pings.size() > 5) {
      pings.remove();
    }
  }

  public long getAvgPing() {
    long totalPing = 0;
    for (long ping : pings) {
      totalPing += ping;
    }
    return totalPing / pings.size();
  }
}
