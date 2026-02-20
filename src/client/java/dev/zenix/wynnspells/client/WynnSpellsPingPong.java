package dev.zenix.wynnspells.client;

import java.util.concurrent.atomic.AtomicBoolean;
import net.minecraft.client.MinecraftClient;
import net.minecraft.network.packet.c2s.query.QueryPingC2SPacket;

public class WynnSpellsPingPong implements Runnable {

    private static final long MS_PER_PING = 1000;

    private AtomicBoolean running;
    private long lastPing;

    public WynnSpellsPingPong(AtomicBoolean running) {
        this.running = running;
        this.lastPing = 0;
    }

    @Override
    public void run() {
        while (running.get()) {
            WynnSpellsUtils.sendPacket(MinecraftClient.getInstance(),
                    new QueryPingC2SPacket(System.currentTimeMillis()));

            try {
                Thread.sleep(MS_PER_PING);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public long getPing() {
        return lastPing;
    }

    public void onCallback(long ping) {
        lastPing = ping;
    }
}
