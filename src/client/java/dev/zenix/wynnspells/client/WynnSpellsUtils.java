package dev.zenix.wynnspells.client;

import java.util.List;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.network.packet.Packet;
import net.minecraft.text.Text;

public class WynnSpellsUtils {

    public static void sendPacket(MinecraftClient client, Packet<?> packet) {
        ClientPlayNetworkHandler networkHandler = client.getNetworkHandler();
        if (networkHandler == null)
            return;
        else
            networkHandler.sendPacket(packet);
    }

    public static boolean isArcher(MinecraftClient client) {
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

}
