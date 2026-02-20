package dev.zenix.wynnspells.client;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import net.minecraft.client.MinecraftClient;

public class WynnSpellsCaster implements Runnable {

    private final AtomicBoolean running;
    private final BlockingQueue<WynnSpellsIntent> buffer;

    public WynnSpellsCaster(
        BlockingQueue<WynnSpellsIntent> buffer,
        AtomicBoolean running
    ) {
        this.buffer = buffer;
        this.running = running;
    }

    @Override
    public void run() {
        while (running.get()) {
            try {
                MinecraftClient client = MinecraftClient.getInstance();

                WynnSpellsConfig config =
                    WynnSpellsClient.getInstance().getConfig();
                long delay = config.getDelayMillis();
                if (config.shouldUseAutoDelay()) {
                    delay = WynnSpellsUtils.getAutoDelay();
                }

                WynnSpellsIntent intent = buffer.take();
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
