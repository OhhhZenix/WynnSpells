package dev.zenix.wynnspells.client.core;

import dev.zenix.wynnspells.WynnSpells;
import dev.zenix.wynnspells.client.WynnSpellsClient;
import dev.zenix.wynnspells.client.config.ClothConfig;
import dev.zenix.wynnspells.client.spell.Classes;
import java.util.List;
import java.util.concurrent.TimeUnit;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.toasts.SystemToast;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ServerboundSwingPacket;
import net.minecraft.network.protocol.game.ServerboundUseItemPacket;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

public class Utils {

  public static long MS_PER_TICK = 1000L / 20L;
  public static int KEY_LIMIT = 1;
  public static int CLICK_LIMIT = 3;

  public static void sendPacket(Minecraft mc, Packet<?> packet) {
    if (mc == null) return;

    ClientPacketListener networkHandler = mc.getConnection();
    if (networkHandler == null) return;

    networkHandler.send(packet);
  }

  public static void sendAttackPacket(Minecraft mc) {
    Utils.sendPacket(mc, new ServerboundSwingPacket(InteractionHand.MAIN_HAND));
  }

  public static void sendInteractPacket(Minecraft mc) {
    float yRot = 0;
    float xRot = 0;

    LocalPlayer player = mc.player;
    if (player != null) {
      yRot = player.getYRot();
      xRot = player.getXRot();
    }

    Utils.sendPacket(mc, new ServerboundUseItemPacket(InteractionHand.MAIN_HAND, 0, yRot, xRot));
  }

  public static void sendClick(Minecraft mc, boolean click) {
    if (click ^ Utils.isArcher(mc)) {
      Utils.sendInteractPacket(mc);
    } else {
      Utils.sendAttackPacket(mc);
    }
  }

  public static boolean mainHandItemHasTooltipText(Minecraft mc, String searchText) {
    LocalPlayer player = mc.player;

    if (player == null || searchText == null || searchText.isEmpty()) return false;

    ItemStack heldItem = player.getMainHandItem();
    if (heldItem.isEmpty()) return false;

    List<Component> tooltip =
        heldItem.getTooltipLines(Item.TooltipContext.EMPTY, player, TooltipFlag.NORMAL);
    if (tooltip.isEmpty()) return false;

    for (Component line : tooltip) {
      if (line.getString().contains(searchText)) return true;
    }

    return false;
  }

  public static boolean isArcher(Minecraft mc) {
    return mainHandItemHasTooltipText(mc, Classes.ARCHER.getClassEncoding())
        || mainHandItemHasTooltipText(mc, Classes.ARCHER.getItemEncoding());
  }

  public static boolean isWarrior(Minecraft mc) {
    return mainHandItemHasTooltipText(mc, Classes.WARRIOR.getClassEncoding())
        || mainHandItemHasTooltipText(mc, Classes.WARRIOR.getItemEncoding());
  }

  public static boolean isAssassin(Minecraft mc) {
    return mainHandItemHasTooltipText(mc, Classes.ASSASSIN.getClassEncoding())
        || mainHandItemHasTooltipText(mc, Classes.ASSASSIN.getItemEncoding());
  }

  public static boolean isMage(Minecraft mc) {
    return mainHandItemHasTooltipText(mc, Classes.MAGE.getClassEncoding())
        || mainHandItemHasTooltipText(mc, Classes.MAGE.getItemEncoding());
  }

  public static boolean isShaman(Minecraft mc) {
    return mainHandItemHasTooltipText(mc, Classes.SHAMAN.getClassEncoding())
        || mainHandItemHasTooltipText(mc, Classes.SHAMAN.getItemEncoding());
  }

  public static boolean isWeapon(Minecraft mc) {
    return isArcher(mc) || isWarrior(mc) || isAssassin(mc) || isMage(mc) || isShaman(mc);
  }

  public static void sendNotification(Component description, Boolean shouldSend) {
    if (!shouldSend) {
      return;
    }

    SystemToast.add(
        Minecraft.getInstance().getToastManager(),
        SystemToast.SystemToastId.WORLD_BACKUP,
        Component.nullToEmpty(WynnSpells.MOD_NAME),
        description);
  }

  public static long getAutoDelay() {
    WynnSpellsClient client = WynnSpellsClient.getInstance();
    long roundTripTime = client.getPingTracker().getAvgPing();
    long oneWay = roundTripTime / 2;
    long jitter = MS_PER_TICK / 2;
    long tolerance = client.getConfig().getAutoDelayTolerance();
    long margin = tolerance + (tolerance * (oneWay / MS_PER_TICK));
    long delay = MS_PER_TICK + jitter + margin;
    WynnSpells.LOGGER.debug("Auto Delay: {}", delay);
    return delay;
  }

  public static long getClickDelay() {
    ClothConfig config = WynnSpellsClient.getInstance().getConfig();
    if (config.isUseAutoDelay()) {
      return TimeUnit.MILLISECONDS.toNanos(Utils.getAutoDelay());
    }
    return TimeUnit.MILLISECONDS.toNanos(config.getManualDelay());
  }

  public static List<Boolean> getClicks(KeyMapping keyMapping) {
    if (keyMapping == WynnSpellsClient.FIRST_SPELL_KEY) {
      return List.of(true, false, true);
    } else if (keyMapping == WynnSpellsClient.SECOND_SPELL_KEY) {
      return List.of(true, true, true);
    } else if (keyMapping == WynnSpellsClient.THIRD_SPELL_KEY) {
      return List.of(true, false, false);
    } else if (keyMapping == WynnSpellsClient.FOURTH_SPELL_KEY) {
      return List.of(true, true, false);
    } else if (keyMapping == WynnSpellsClient.MELEE_KEY) {
      return List.of(false);
    }
    return List.of();
  }

  public static void refreshKeyBindings() {
    KeyMapping.resetMapping();
    WynnSpells.LOGGER.debug("Refreshed keybinds.");
  }

  public static void saveKeyBindings() {
    Minecraft.getInstance().options.save();
    WynnSpells.LOGGER.debug("Saved keybinds.");
  }

  public static void refreshAndSaveKeyBindings() {
    refreshKeyBindings();
    saveKeyBindings();
  }
}
