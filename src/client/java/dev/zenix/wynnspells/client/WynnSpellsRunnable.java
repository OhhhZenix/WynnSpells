package dev.zenix.wynnspells.client;

import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.c2s.play.HandSwingC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerInteractItemC2SPacket;
import net.minecraft.text.Text;
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
                        if (isArcher(client)) {
                            sendInteractPacket(client);
                            Thread.sleep(delayMs);
                        } else {
                            sendAttackPacket(client);
                            Thread.sleep(delayMs);
                        }
                        break;
                    case FIRST_SPELL:
                        if (isArcher(client)) {
                            // L-R-L
                            sendAttackPacket(client);
                            Thread.sleep(delayMs);
                            sendInteractPacket(client);
                            Thread.sleep(delayMs);
                            sendAttackPacket(client);
                            Thread.sleep(delayMs);
                        } else {
                            // R-L-R
                            sendInteractPacket(client);
                            Thread.sleep(delayMs);
                            sendAttackPacket(client);
                            Thread.sleep(delayMs);
                            sendInteractPacket(client);
                            Thread.sleep(delayMs);
                        }
                        break;
                    case SECOND_SPELL:
                        if (isArcher(client)) {
                            // L-L-L
                            sendAttackPacket(client);
                            Thread.sleep(delayMs);
                            sendAttackPacket(client);
                            Thread.sleep(delayMs);
                            sendAttackPacket(client);
                            Thread.sleep(delayMs);
                        } else {
                            // R-R-R
                            sendInteractPacket(client);
                            Thread.sleep(delayMs);
                            sendInteractPacket(client);
                            Thread.sleep(delayMs);
                            sendInteractPacket(client);
                            Thread.sleep(delayMs);
                        }
                        break;
                    case THIRD_SPELL:
                        if (isArcher(client)) {
                            // L-R-R
                            sendAttackPacket(client);
                            Thread.sleep(delayMs);
                            sendInteractPacket(client);
                            Thread.sleep(delayMs);
                            sendInteractPacket(client);
                            Thread.sleep(delayMs);
                        } else {
                            // R-L-L
                            sendInteractPacket(client);
                            Thread.sleep(delayMs);
                            sendAttackPacket(client);
                            Thread.sleep(delayMs);
                            sendAttackPacket(client);
                            Thread.sleep(delayMs);
                        }
                        break;
                    case FOURTH_SPELL:
                        if (isArcher(client)) {
                            // L-L-R
                            sendAttackPacket(client);
                            Thread.sleep(delayMs);
                            sendAttackPacket(client);
                            Thread.sleep(delayMs);
                            sendInteractPacket(client);
                            Thread.sleep(delayMs);
                        } else {
                            // R-R-L
                            sendInteractPacket(client);
                            Thread.sleep(delayMs);
                            sendInteractPacket(client);
                            Thread.sleep(delayMs);
                            sendAttackPacket(client);
                            Thread.sleep(delayMs);
                        }
                        break;
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }

    }

    private boolean isArcher(MinecraftClient client) {
        if (client == null || client.player == null)
            return false;

        ItemStack heldItem = client.player.getMainHandStack();
        if (heldItem == null)
            return false;

        List<Text> tooltip = heldItem.getTooltip(Item.TooltipContext.DEFAULT, client.player, TooltipType.BASIC);
        if (tooltip == null || tooltip.isEmpty())
            return false;

        for (Text line : tooltip) {
            if (line.getString().contains("Archer/Hunter"))
                return true;
        }

        return false;
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
