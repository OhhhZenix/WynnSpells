package dev.zenix.wynnspells.client;

import dev.zenix.wynnspells.client.event.SwingHandEvent;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;

import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.TimeUnit;

public class Caster {

    private final MinecraftClient mc;
    private final LinkedBlockingDeque<Intent> buffer = new LinkedBlockingDeque<>();
    private final LinkedBlockingDeque<Boolean> clicks = new LinkedBlockingDeque<>();
    private volatile ItemStack previousItem = null;
    private volatile boolean isRunning = true;
    private volatile long lastTime = System.nanoTime();

    public Caster(MinecraftClient mc) {
        this.mc = mc;
    }

    public void start() {
        Thread thread = new Thread(this::process);
        thread.start();

        SwingHandEvent.EVENT.register(this::processVanillaMelee);
    }

    public void stop() {
        isRunning = false;
    }

    private void process() {
        while (isRunning) {
            processClicks();
            processBuffer();
        }
    }

    private void processClicks() {
        if (clicks.isEmpty())
            return;


        ClothConfig config = WynnSpellsClient.getInstance().getConfig();
        long delay = TimeUnit.MILLISECONDS.toNanos(config.getManualDelay());
        if (config.shouldUseAutoDelay()) {
            delay = TimeUnit.MILLISECONDS.toNanos(Utils.getAutoDelay());
        }

        long now = System.nanoTime();
        if (now < lastTime + delay)
            return;

        try {
            boolean click = clicks.take();

            if (click) {
                Utils.sendInteractPacket(mc); // right click
            } else {
                Utils.sendAttackPacket(mc); // left click
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        lastTime = now;
    }

    private void processBuffer() {
        if (!clicks.isEmpty())
            return;

        if (buffer.isEmpty())
            return;

        try {
            Intent intent = buffer.take();
            boolean isArcher = Utils.isArcher(mc);
            for (boolean click : intent.convert(isArcher)) {
                clicks.add(click);
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private void processIntentKey(KeyBinding key, Intent intent) {
        if (key == null)
            return;

        if (!key.isPressed())
            return;

        ClothConfig config = WynnSpellsClient.getInstance().getConfig();
        if (buffer.size() >= config.getBufferLimit()) {
            Utils.sendNotification(Text.of("Cast ignored: spell queue is busy."), config.shouldNotifyBusyCast());
            return;
        }

        if (mc == null || mc.player == null) {
            return;
        }

        ItemStack itemInMainHand = mc.player.getMainHandStack();
        if (itemInMainHand != previousItem) {
            previousItem = itemInMainHand;
            buffer.clear();
        }

        key.setPressed(false);
        buffer.add(intent);
    }

    private boolean processVanillaMelee(ClientPlayerEntity player, Hand hand) {
        if (clicks.isEmpty()) {
            return false;
        }

        buffer.add(Intent.MELEE);
        return true;
    }

    public void processIntentKeys() {
        processIntentKey(WynnSpellsClient.MELEE_KEY, Intent.MELEE);
        processIntentKey(WynnSpellsClient.FIRST_SPELL_KEY, Intent.FIRST_SPELL);
        processIntentKey(WynnSpellsClient.SECOND_SPELL_KEY, Intent.SECOND_SPELL);
        processIntentKey(WynnSpellsClient.THIRD_SPELL_KEY, Intent.THIRD_SPELL);
        processIntentKey(WynnSpellsClient.FOURTH_SPELL_KEY, Intent.FOURTH_SPELL);
    }
}
