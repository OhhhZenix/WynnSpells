package dev.zenix.wynnspells.client.core;

import dev.zenix.wynnspells.WynnSpells;
import dev.zenix.wynnspells.client.WynnSpellsClient;
import dev.zenix.wynnspells.client.event.ClientPacketListenerEvents;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import net.minecraft.client.Minecraft;
import net.minecraft.network.protocol.ping.ClientboundPongResponsePacket;
import net.minecraft.util.Util;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

public class PingTracker extends Thread {

  private final Queue<Long> pings = new LinkedBlockingQueue<>();
  private final Minecraft mc;

  public PingTracker(Minecraft mc) {
    this.mc = mc;
    ClientPacketListenerEvents.HANDLE_PONG_RESPONSE.register(this::onPongReceivedEvent);
  }

  @Override
  public void run() {
    while (WynnSpellsClient.isRunning()) {
      try {
        Utils.sendPingPacket(mc);
        Thread.sleep(TimeUnit.SECONDS.toMillis(1));
      } catch (InterruptedException e) {
        throw new RuntimeException(e);
      }
    }
  }

  private void onPongReceivedEvent(ClientboundPongResponsePacket packet, CallbackInfo ci) {
    long currentTime = Util.getMillis();
    long packetTime = packet.time();
    long ping = currentTime - packetTime;

    WynnSpells.LOGGER.debug("Current Time: {}", currentTime);
    WynnSpells.LOGGER.debug("Packet Time: {}", packetTime);
    WynnSpells.LOGGER.debug("Ping: {}", ping);

    pings.add(ping);

    int lookBack = WynnSpellsClient.getInstance().getConfig().getPingLookBack();
    if (pings.size() > lookBack) {
      pings.remove();
    }
  }

  public long getAvgPing() {
    long totalPing = 0;
    int count = 0;

    for (long ping : pings) {
      totalPing += ping;
      count++;
    }

    return count == 0 ? 0 : totalPing / count;
  }
}
