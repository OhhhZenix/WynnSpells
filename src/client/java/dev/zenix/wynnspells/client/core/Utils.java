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
import net.minecraft.network.protocol.game.ServerboundPlayerInputPacket;
import net.minecraft.network.protocol.game.ServerboundPunchPacket;
import net.minecraft.network.protocol.game.ServerboundUseItemPacket;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Input;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

public class Utils {

  public static long MS_PER_TICK = 1000L / 20L;
  public static int KEY_LIMIT = 1;

  public static void sendPacket(Minecraft client, Packet<?> packet) {
    if (client == null) return;

    ClientPacketListener networkHandler = client.getConnection();
    if (networkHandler == null) return;

    networkHandler.send(packet);
  }

  public static void sendAttackPacket(Minecraft mc) {
    Utils.sendPacket(mc, new ServerboundPunchPacket());
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

  public static void sendSneakingPacket(Minecraft client, boolean isSneaking) {
    Input playerInput =
        new Input(
            client.options.keyUp.isDown(),
            client.options.keyDown.isDown(),
            client.options.keyLeft.isDown(),
            client.options.keyRight.isDown(),
            client.options.keyJump.isDown(),
            isSneaking,
            client.options.keySprint.isDown());

    Utils.sendPacket(client, new ServerboundPlayerInputPacket(playerInput));
  }

  public static boolean mainHandItemHasTooltipText(Minecraft client, String searchText) {
    if (client == null || client.player == null || searchText == null || searchText.isEmpty())
      return false;

    ItemStack heldItem = client.player.getMainHandItem();
    if (heldItem.isEmpty()) return false;

    List<Component> tooltip =
        heldItem.getTooltipLines(Item.TooltipContext.EMPTY, client.player, TooltipFlag.NORMAL);
    if (tooltip.isEmpty()) return false;

    for (Component line : tooltip) {
      if (line.getString().contains(searchText)) return true;
    }

    return false;
  }

  public static boolean isArcher(Minecraft client) {
    return mainHandItemHasTooltipText(client, Classes.ARCHER.getClassEncoding())
        || mainHandItemHasTooltipText(client, Classes.ARCHER.getItemEncoding());
  }

  public static boolean isWarrior(Minecraft client) {
    return mainHandItemHasTooltipText(client, Classes.WARRIOR.getClassEncoding())
        || mainHandItemHasTooltipText(client, Classes.WARRIOR.getItemEncoding());
  }

  public static boolean isAssassin(Minecraft client) {
    return mainHandItemHasTooltipText(client, Classes.ASSASSIN.getClassEncoding())
        || mainHandItemHasTooltipText(client, Classes.ASSASSIN.getItemEncoding());
  }

  public static boolean isMage(Minecraft client) {
    return mainHandItemHasTooltipText(client, Classes.MAGE.getClassEncoding())
        || mainHandItemHasTooltipText(client, Classes.MAGE.getItemEncoding());
  }

  public static boolean isShaman(Minecraft client) {
    return mainHandItemHasTooltipText(client, Classes.SHAMAN.getClassEncoding())
        || mainHandItemHasTooltipText(client, Classes.SHAMAN.getItemEncoding());
  }

  public static boolean isWeapon(Minecraft client) {
    return isArcher(client)
        || isWarrior(client)
        || isMage(client)
        || isAssassin(client)
        || isShaman(client);
  }

  public static void sendNotification(Component description, Boolean shouldSend) {
    if (!shouldSend) {
      return;
    }

    SystemToast.add(
        Minecraft.getInstance().gui.toastManager(),
        SystemToast.SystemToastId.WORLD_BACKUP,
        Component.nullToEmpty(WynnSpells.MOD_NAME),
        description);
  }

  public static long getAutoDelay() {
    WynnSpellsClient client = WynnSpellsClient.getInstance();
    long rtt = client.getPingTracker().getLastPing();
    long oneWay = rtt / 2;
    long jitter = MS_PER_TICK / 2;
    long tolerance = client.getConfig().getAutoDelayTolerance();
    long margin = tolerance + (tolerance * (oneWay / MS_PER_TICK));
    long delay = MS_PER_TICK + jitter + margin;
    WynnSpells.LOGGER.debug("Auto Delay: {}", delay);
    return delay;
  }

  // returns the delay in nanoseconds
  public static long getClickDelay() {
    ClothConfig config = WynnSpellsClient.getInstance().getConfig();
    if (config.shouldUseAutoDelay()) {
      return TimeUnit.MILLISECONDS.toNanos(Utils.getAutoDelay());
    }
    return TimeUnit.MILLISECONDS.toNanos(config.getManualDelay());
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
