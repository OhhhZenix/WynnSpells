package dev.zenix.wynnspells.client;

import java.util.concurrent.atomic.AtomicBoolean;
import net.minecraft.client.MinecraftClient;
import net.minecraft.network.packet.c2s.query.QueryPingC2SPacket;

public class WynnSpellsPingPong implements Runnable {

    private static final long MS_PER_PING = 1000;
    private static volatile long lastPing = 0;

    private final AtomicBoolean running;

    public WynnSpellsPingPong(AtomicBoolean running) {
        this.running = running;
    }

    @Override
    public void run() {
        while (running.get()) {
            MinecraftClient client = MinecraftClient.getInstance();
            WynnSpellsUtils.sendPacket(client, new QueryPingC2SPacket(System.currentTimeMillis()));

            try {
                Thread.sleep(MS_PER_PING);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return;
            }
        }
    }

    public static long getPing() {
        return lastPing;
    }

    public static void onCallback(long startTime) {
        lastPing = Math.max(0, System.currentTimeMillis() - startTime);
    }
}
