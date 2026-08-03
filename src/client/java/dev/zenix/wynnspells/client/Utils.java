package dev.zenix.wynnspells.client;

import java.util.Map;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ServerboundSwingPacket;
import net.minecraft.network.protocol.game.ServerboundUseItemPacket;
import net.minecraft.world.InteractionHand;

public class Utils {

    enum Spell {
        FIRST,
        SECOND,
        THIRD,
        FOURTH,
        MELEE,
    };

    enum Class {
        WARRIOR,
        MAGE,
        ARCHER,
        ASSASSIN,
        SHAMAN,
    }

    public static final Map<Class, String> CLASS_ENCODINGS = Map.of(
            Class.WARRIOR, "Warrior/Knight",
            Class.MAGE, "Mage/Dark Wizard",
            Class.ARCHER, "Archer/Hunter",
            Class.ASSASSIN, "Assassin/Ninja",
            Class.SHAMAN, "Shaman/Skyseer");

    public static final Map<Class, String> WEAPON_ENCODINGS = Map.of(
            Class.WARRIOR, "󐀂󐀁󏿿󏿿󏿿󏿿󏿿󏿿󏿠",
            Class.MAGE, "󐀂󐀁󏿿󏿿󏿿󏿿󏿿󏿦",
            Class.ARCHER, "󐀂󐀁󏿿󏿿󏿿󏿿󏿬",
            Class.ASSASSIN, "󐀂󐀁󏿿󏿿󏿿󏿿󏿿󏿿󏿿󏿚",
            Class.SHAMAN, "󐀂󐀁󏿿󏿿󏿿󏿿󏿿󏿿󏿢");

    public static void sendPacket(Minecraft client, Packet<?> packet) {
        if (client == null)
            return;

        ClientPacketListener networkHandler = client.getConnection();
        if (networkHandler == null)
            return;

        networkHandler.send(packet);
    }

    public static void sendAttackPacket(Minecraft client) {
        Utils.sendPacket(client, new ServerboundSwingPacket(InteractionHand.MAIN_HAND));
    }

    public static void sendInteractPacket(Minecraft client) {
        float yrot = 0;
        float xrot = 0;
        if (client.player != null) {
            yrot = client.player.getYRot();
            xrot = client.player.getXRot();
        }
        Utils.sendPacket(client, new ServerboundUseItemPacket(InteractionHand.MAIN_HAND, 0, yrot, xrot));
    }
}
