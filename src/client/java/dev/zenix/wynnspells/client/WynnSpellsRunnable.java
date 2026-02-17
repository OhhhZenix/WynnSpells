package dev.zenix.wynnspells.client;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

import net.minecraft.client.MinecraftClient;
import net.minecraft.network.packet.c2s.play.HandSwingC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerInputC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerInteractItemC2SPacket;
import net.minecraft.util.Hand;
import net.minecraft.util.PlayerInput;

public class WynnSpellsRunnable implements Runnable {

    private final BlockingQueue<WynnSpellsQueue> queueList;
    private final AtomicBoolean running;

    public WynnSpellsRunnable(BlockingQueue<WynnSpellsQueue> queueList, AtomicBoolean running) {
        this.queueList = queueList;
        this.running = running;
    }

    @Override
    public void run() {
        while (running.get()) {
            try {
                WynnSpellsQueue queue = queueList.take();
                MinecraftClient client = MinecraftClient.getInstance();
                if (client == null || client.player == null) {
                    continue;
                }

                int delay = WynnSpellsClient.getInstance().getConfig().getDelayMillis();
                // TODO: calculate delay based on ping

                WynnSpellsIntent intent = queue.getIntent();
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

                boolean isSneaking = queue.isSneaking();
                if (isSneaking != client.options.sneakKey.isPressed()) {
                    sendSneakingPacket(client, isSneaking);
                    Thread.sleep(delay);
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

    private void sendSneakingPacket(MinecraftClient client, boolean isSneaking) {
        PlayerInput playerInput = new PlayerInput(client.options.forwardKey.isPressed(),
                client.options.backKey.isPressed(), client.options.leftKey.isPressed(),
                client.options.rightKey.isPressed(), client.options.jumpKey.isPressed(), isSneaking,
                client.options.sprintKey.isPressed());

        WynnSpellsUtils.sendPacket(client, new PlayerInputC2SPacket(playerInput));
    }
}
