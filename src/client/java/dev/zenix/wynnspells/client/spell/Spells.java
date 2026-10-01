package dev.zenix.wynnspells.client.spell;

import com.mojang.blaze3d.platform.InputConstants;
import dev.zenix.wynnspells.client.WynnSpellsClient;
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;
import net.minecraft.client.KeyMapping;

public enum Spells {
  FIRST(
      KeyMappingHelper.registerKeyMapping(
          new KeyMapping(
              "key.wynnspells.first",
              InputConstants.Type.KEYBOARD,
              InputConstants.UNKNOWN.getValue(),
              WynnSpellsClient.KEY_CATEGORY))),
  SECOND(
      KeyMappingHelper.registerKeyMapping(
          new KeyMapping(
              "key.wynnspells.second",
              InputConstants.Type.KEYBOARD,
              InputConstants.UNKNOWN.getValue(),
              WynnSpellsClient.KEY_CATEGORY))),
  THIRD(
      KeyMappingHelper.registerKeyMapping(
          new KeyMapping(
              "key.wynnspells.third",
              InputConstants.Type.KEYBOARD,
              InputConstants.UNKNOWN.getValue(),
              WynnSpellsClient.KEY_CATEGORY))),
  FOURTH(
      KeyMappingHelper.registerKeyMapping(
          new KeyMapping(
              "key.wynnspells.fourth",
              InputConstants.Type.KEYBOARD,
              InputConstants.UNKNOWN.getValue(),
              WynnSpellsClient.KEY_CATEGORY))),
  MELEE(
      KeyMappingHelper.registerKeyMapping(
          new KeyMapping(
              "key.wynnspells.melee",
              InputConstants.Type.KEYBOARD,
              InputConstants.UNKNOWN.getValue(),
              WynnSpellsClient.KEY_CATEGORY)));

  private final KeyMapping keyMapping;

  Spells(KeyMapping keyMapping) {
    this.keyMapping = keyMapping;
  }

  public KeyMapping getKeyMapping() {
    return keyMapping;
  }

  public boolean[] getSpellCombo(boolean isArcher) {
    return switch (this) {
      case FIRST ->
          isArcher ? new boolean[] {false, true, false} : new boolean[] {true, false, true};
      case SECOND ->
          isArcher ? new boolean[] {false, false, false} : new boolean[] {true, true, true};
      case THIRD ->
          isArcher ? new boolean[] {false, true, true} : new boolean[] {true, false, false};
      case FOURTH ->
          isArcher ? new boolean[] {false, false, true} : new boolean[] {true, true, false};
      case MELEE -> isArcher ? new boolean[] {true} : new boolean[] {false};
    };
  }
}
