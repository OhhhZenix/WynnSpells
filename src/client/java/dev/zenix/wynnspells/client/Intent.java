package dev.zenix.wynnspells.client;

public enum Intent {
	MELEE, FIRST_SPELL, SECOND_SPELL, THIRD_SPELL, FOURTH_SPELL;

	public boolean[] convert(boolean isArcher) {
		return switch (this) {
			case MELEE -> isArcher ? new boolean[]{true} : new boolean[]{false};

			case FIRST_SPELL -> isArcher ? new boolean[]{false, true, false} : new boolean[]{true, false, true};

			case SECOND_SPELL -> isArcher ? new boolean[]{false, false, false} : new boolean[]{true, true, true};

			case THIRD_SPELL -> isArcher ? new boolean[]{false, true, true} : new boolean[]{true, false, false};

			case FOURTH_SPELL -> isArcher ? new boolean[]{false, false, true} : new boolean[]{true, true, false};
		};
	}
}
