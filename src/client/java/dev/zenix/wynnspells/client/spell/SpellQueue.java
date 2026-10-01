package dev.zenix.wynnspells.client.spell;

import java.util.Collection;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import net.minecraft.client.Minecraft;

public class SpellQueue {

  private static final AtomicBoolean running = new AtomicBoolean(true);
  private static final BlockingQueue<Boolean> queue = new LinkedBlockingQueue<>();
  private final Thread spellcaster;

  public SpellQueue(Minecraft mc) {
    this.spellcaster = new Thread(new SpellCaster(mc));
  }

  public void start() {
    spellcaster.start();
  }

  public void stop() {
    running.set(false);
  }

  public boolean isEmpty() {
    return queue.isEmpty();
  }

  public void addClicks(Collection<Boolean> clicks) {
    queue.addAll(clicks);
  }
}
