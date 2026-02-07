package dev.zenix.wynnspells.client;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.c2s.play.HandSwingC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerInteractItemC2SPacket;
import net.minecraft.util.Hand;

import dev.zenix.wynnspells.client.WynnSpellsClient.Intent;

public class WynnSpellsRunnable implements Runnable {

    private final BlockingQueue<Intent> intentQueue;
    private final AtomicBoolean running;

    public WynnSpellsRunnable(BlockingQueue<Intent> intentQueue, AtomicBoolean running) {
        this.intentQueue = intentQueue;
        this.running = running;
    }

    @Override
    public void run() {
        while (running.get()) {
            try {
                Intent intent = intentQueue.take();
                MinecraftClient client = MinecraftClient.getInstance();
                if (client == null || client.player == null) {
                    continue;
                }

                long delayMs = 100;
                // ClientPlayNetworkHandler networkHandler = client.getNetworkHandler();
                // if (networkHandler != null) {
                // // delayMs = networkHandler.getServerInfo().ping;
                // client.player.sendMessage(Text.of(delayMs + "ms"), false);
                // }

                switch (intent) {
                    case MELEE:
                        sendAttackPacket(client);
                        break;
                    case FIRST_SPELL:
                        sendAttackPacket(client);
                        Thread.sleep(delayMs);
                        sendInteractPacket(client);
                        Thread.sleep(delayMs);
                        sendAttackPacket(client);
                        Thread.sleep(delayMs);
                        break;
                    case SECOND_SPELL:
                        sendAttackPacket(client);
                        Thread.sleep(delayMs);
                        sendAttackPacket(client);
                        Thread.sleep(delayMs);
                        sendAttackPacket(client);
                        Thread.sleep(delayMs);
                        break;
                    case THIRD_SPELL:
                        sendAttackPacket(client);
                        Thread.sleep(delayMs);
                        sendInteractPacket(client);
                        Thread.sleep(delayMs);
                        sendInteractPacket(client);
                        Thread.sleep(delayMs);
                        break;
                    case FOURTH_SPELL:
                        sendAttackPacket(client);
                        Thread.sleep(delayMs);
                        sendAttackPacket(client);
                        Thread.sleep(delayMs);
                        sendInteractPacket(client);
                        Thread.sleep(delayMs);
                        break;
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }

    private void sendPacket(MinecraftClient client, Packet<?> packet) {
        ClientPlayNetworkHandler networkHandler = client.getNetworkHandler();
        if (networkHandler == null)
            return;
        else
            networkHandler.sendPacket(packet);
    }

    private void sendAttackPacket(MinecraftClient client) {
        sendPacket(client, new HandSwingC2SPacket(Hand.MAIN_HAND));
    }

    private void sendInteractPacket(MinecraftClient client) {
        sendPacket(
                client,
                new PlayerInteractItemC2SPacket(
                        Hand.MAIN_HAND,
                        0,
                        client.player.getYaw(),
                        client.player.getPitch()));
    }
}
