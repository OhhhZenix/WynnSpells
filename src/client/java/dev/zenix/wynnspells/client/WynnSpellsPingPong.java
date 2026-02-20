package dev.zenix.wynnspells.client;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import net.minecraft.client.MinecraftClient;
import net.minecraft.network.packet.c2s.query.QueryPingC2SPacket;
import net.minecraft.util.Util;

public final class WynnSpellsPingPong {

    private static final long PING_INTERVAL_MILLIS = 1000;
    private static ScheduledExecutorService executor = null;
    private static volatile long lastPing = 0;

    private WynnSpellsPingPong() {}

    public static void start() {
        if (executor != null && !executor.isShutdown()) {
            return;
        }

        executor = Executors.newSingleThreadScheduledExecutor(runnable -> {
            Thread thread = new Thread(runnable, "wynnspells-pingpong");
            thread.setDaemon(true);
            return thread;
        });

        executor.scheduleAtFixedRate(
            WynnSpellsPingPong::sendPing,
            0L,
            PING_INTERVAL_MILLIS,
            TimeUnit.MILLISECONDS
        );
    }

    public static void stop() {}

    private static void sendPing() {
        MinecraftClient client = MinecraftClient.getInstance();
        WynnSpellsUtils.sendPacket(
            client,
            new QueryPingC2SPacket(Util.getMeasuringTimeMs())
        );
    }

    public static long getPing() {
        return lastPing;
    }

    public static void onCallback(long startTime) {
        long currentTime = Util.getMeasuringTimeMs();
        lastPing = currentTime - startTime;
    }
}
