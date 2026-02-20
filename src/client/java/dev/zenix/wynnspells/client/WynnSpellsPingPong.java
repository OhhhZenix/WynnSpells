package dev.zenix.wynnspells.client;

import net.minecraft.client.MinecraftClient;
import net.minecraft.network.packet.c2s.query.QueryPingC2SPacket;
import net.minecraft.util.Util;

public final class WynnSpellsPingPong {

    private static final long PING_INTERVAL_TICKS = 20;
    private static long lastPing = 0;
    private static int tickCounter = 0;

    private WynnSpellsPingPong() {}

    public static void tick() {
        // increment tick
        tickCounter++;

        // should send again?
        if (tickCounter < PING_INTERVAL_TICKS) {
            return;
        }

        // send packet
        MinecraftClient client = MinecraftClient.getInstance();
        WynnSpellsUtils.sendPacket(client, new QueryPingC2SPacket(Util.getMeasuringTimeMs()));

        // reset tick
        tickCounter = 0;
    }

    public static long getPing() {
        return lastPing;
    }

    public static void onCallback(long startTime) {
        long currentTime = Util.getMeasuringTimeMs();
        lastPing = currentTime - startTime;
    }
}
