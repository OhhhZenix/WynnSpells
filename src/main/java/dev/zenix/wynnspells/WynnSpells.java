package dev.zenix.wynnspells;

import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WynnSpells implements ModInitializer {

	public static final String MOD_NAME = "WynnSpells";
	public static final String MOD_ID = "wynnspells";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		LOGGER.info("Hello Fabric world!");
	}
}