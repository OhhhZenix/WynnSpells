package dev.zenix.wynnspells.client;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

import net.minecraft.client.MinecraftClient;

public class WynnSpellsRunnable implements Runnable {

    private final AtomicBoolean running;
    private final BlockingQueue<WynnSpellsQueue> queueList;

    public WynnSpellsRunnable(BlockingQueue<WynnSpellsQueue> queueList, AtomicBoolean running) {
        this.queueList = queueList;
        this.running = running;
    }

    @Override
    public void run() {
        while (running.get()) {
            try {
                MinecraftClient client = MinecraftClient.getInstance();
                WynnSpellsQueue queue = queueList.take();
                WynnSpellsIntent intent = queue.getIntent();

                // TODO: calculate delay based on ping
                int delay = WynnSpellsClient.getInstance().getConfig().getDelayMillis();

                WynnSpellsClient.LOGGER.debug("Intent: {}", intent.toString());
                switch (intent) {
                    case MELEE:
                        if (WynnSpellsUtils.isArcher(client)) {
                            WynnSpellsUtils.sendInteractPacket(client);
                            Thread.sleep(delay);
                        } else {
                            WynnSpellsUtils.sendAttackPacket(client);
                            Thread.sleep(delay);
                        }
                        break;
                    case FIRST_SPELL:
                        if (WynnSpellsUtils.isArcher(client)) {
                            // L-R-L
                            WynnSpellsUtils.sendAttackPacket(client);
                            Thread.sleep(delay);
                            WynnSpellsUtils.sendInteractPacket(client);
                            Thread.sleep(delay);
                            WynnSpellsUtils.sendAttackPacket(client);
                            Thread.sleep(delay);
                        } else {
                            // R-L-R
                            WynnSpellsUtils.sendInteractPacket(client);
                            Thread.sleep(delay);
                            WynnSpellsUtils.sendAttackPacket(client);
                            Thread.sleep(delay);
                            WynnSpellsUtils.sendInteractPacket(client);
                            Thread.sleep(delay);
                        }
                        break;
                    case SECOND_SPELL:
                        if (WynnSpellsUtils.isArcher(client)) {
                            // L-L-L
                            WynnSpellsUtils.sendAttackPacket(client);
                            Thread.sleep(delay);
                            WynnSpellsUtils.sendAttackPacket(client);
                            Thread.sleep(delay);
                            WynnSpellsUtils.sendAttackPacket(client);
                            Thread.sleep(delay);
                        } else {
                            // R-R-R
                            WynnSpellsUtils.sendInteractPacket(client);
                            Thread.sleep(delay);
                            WynnSpellsUtils.sendInteractPacket(client);
                            Thread.sleep(delay);
                            WynnSpellsUtils.sendInteractPacket(client);
                            Thread.sleep(delay);
                        }
                        break;
                    case THIRD_SPELL:
                        if (WynnSpellsUtils.isArcher(client)) {
                            // L-R-R
                            WynnSpellsUtils.sendAttackPacket(client);
                            Thread.sleep(delay);
                            WynnSpellsUtils.sendInteractPacket(client);
                            Thread.sleep(delay);
                            WynnSpellsUtils.sendInteractPacket(client);
                            Thread.sleep(delay);
                        } else {
                            // R-L-L
                            WynnSpellsUtils.sendInteractPacket(client);
                            Thread.sleep(delay);
                            WynnSpellsUtils.sendAttackPacket(client);
                            Thread.sleep(delay);
                            WynnSpellsUtils.sendAttackPacket(client);
                            Thread.sleep(delay);
                        }
                        break;
                    case FOURTH_SPELL:
                        if (WynnSpellsUtils.isArcher(client)) {
                            // L-L-R
                            WynnSpellsUtils.sendAttackPacket(client);
                            Thread.sleep(delay);
                            WynnSpellsUtils.sendAttackPacket(client);
                            Thread.sleep(delay);
                            WynnSpellsUtils.sendInteractPacket(client);
                            Thread.sleep(delay);
                        } else {
                            // R-R-L
                            WynnSpellsUtils.sendInteractPacket(client);
                            Thread.sleep(delay);
                            WynnSpellsUtils.sendInteractPacket(client);
                            Thread.sleep(delay);
                            WynnSpellsUtils.sendAttackPacket(client);
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
}
