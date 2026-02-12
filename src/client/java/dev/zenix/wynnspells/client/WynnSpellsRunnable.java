package dev.zenix.wynnspells.client;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

import net.minecraft.client.MinecraftClient;
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

                int delay = WynnSpellsClient.getInstance().getConfig().getDelayMillis();
                // ClientPlayNetworkHandler networkHandler = client.getNetworkHandler();
                // if (networkHandler != null) {
                // // delayMs = networkHandler.getServerInfo().ping;
                // client.player.sendMessage(Text.of(delayMs + "ms"), false);
                // }

                switch (intent) {
                    case MELEE:
                        if (WynnSpellsUtils.isArcher(client)) {
                            sendInteractPacket(client);
                            Thread.sleep(delay);
                        } else {
                            sendAttackPacket(client);
                            Thread.sleep(delay);
                        }
                        break;
                    case FIRST_SPELL:
                        if (WynnSpellsUtils.isArcher(client)) {
                            // L-R-L
                            sendAttackPacket(client);
                            Thread.sleep(delay);
                            sendInteractPacket(client);
                            Thread.sleep(delay);
                            sendAttackPacket(client);
                            Thread.sleep(delay);
                        } else {
                            // R-L-R
                            sendInteractPacket(client);
                            Thread.sleep(delay);
                            sendAttackPacket(client);
                            Thread.sleep(delay);
                            sendInteractPacket(client);
                            Thread.sleep(delay);
                        }
                        break;
                    case SECOND_SPELL:
                        if (WynnSpellsUtils.isArcher(client)) {
                            // L-L-L
                            sendAttackPacket(client);
                            Thread.sleep(delay);
                            sendAttackPacket(client);
                            Thread.sleep(delay);
                            sendAttackPacket(client);
                            Thread.sleep(delay);
                        } else {
                            // R-R-R
                            sendInteractPacket(client);
                            Thread.sleep(delay);
                            sendInteractPacket(client);
                            Thread.sleep(delay);
                            sendInteractPacket(client);
                            Thread.sleep(delay);
                        }
                        break;
                    case THIRD_SPELL:
                        if (WynnSpellsUtils.isArcher(client)) {
                            // L-R-R
                            sendAttackPacket(client);
                            Thread.sleep(delay);
                            sendInteractPacket(client);
                            Thread.sleep(delay);
                            sendInteractPacket(client);
                            Thread.sleep(delay);
                        } else {
                            // R-L-L
                            sendInteractPacket(client);
                            Thread.sleep(delay);
                            sendAttackPacket(client);
                            Thread.sleep(delay);
                            sendAttackPacket(client);
                            Thread.sleep(delay);
                        }
                        break;
                    case FOURTH_SPELL:
                        if (WynnSpellsUtils.isArcher(client)) {
                            // L-L-R
                            sendAttackPacket(client);
                            Thread.sleep(delay);
                            sendAttackPacket(client);
                            Thread.sleep(delay);
                            sendInteractPacket(client);
                            Thread.sleep(delay);
                        } else {
                            // R-R-L
                            sendInteractPacket(client);
                            Thread.sleep(delay);
                            sendInteractPacket(client);
                            Thread.sleep(delay);
                            sendAttackPacket(client);
                            Thread.sleep(delay);
                        }
                        break;
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }

    }

    private void sendAttackPacket(MinecraftClient client) {
        WynnSpellsUtils.sendPacket(client, new HandSwingC2SPacket(Hand.MAIN_HAND));
    }

    private void sendInteractPacket(MinecraftClient client) {
        WynnSpellsUtils.sendPacket(client, new PlayerInteractItemC2SPacket(Hand.MAIN_HAND, 0,
                client.player.getYaw(), client.player.getPitch()));
    }
}
