package dev.zenix.wynnspells.client.spell;

public enum Classes {
  ARCHER("Archer/Hunter", "󐀂󐀁󏿿󏿿󏿿󏿿󏿬"),
  WARRIOR("Warrior/Knight", "󐀂󐀁󏿿󏿿󏿿󏿿󏿿󏿿󏿠"),
  ASSASSIN("Assassin/Ninja", "󐀂󐀁󏿿󏿿󏿿󏿿󏿿󏿿󏿿󏿚"),
  MAGE("Mage/Dark Wizard", "󐀂󐀁󏿿󏿿󏿿󏿿󏿿󏿦"),
  SHAMAN("Shaman/Skyseer", "󐀂󐀁󏿿󏿿󏿿󏿿󏿿󏿿󏿢");

  private final String classEncoding;
  private final String itemEncoding;

  Classes(String classEncoding, String itemEncoding) {
    this.classEncoding = classEncoding;
    this.itemEncoding = itemEncoding;
  }

  public String getClassEncoding() {
    return classEncoding;
  }

  public String getItemEncoding() {
    return itemEncoding;
  }
}
