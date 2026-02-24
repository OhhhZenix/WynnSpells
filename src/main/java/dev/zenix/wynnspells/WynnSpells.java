package dev.zenix.wynnspells;

import net.fabricmc.api.ModInitializer;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.config.Configurator;

public class WynnSpells implements ModInitializer {

	public static final String MOD_NAME = "WynnSpells";
	public static final String MOD_ID = "wynnspells";
	public static final Logger LOGGER = LogManager.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		setupLogger();
	}

	private void setupLogger() {
		Configurator.setLevel(LOGGER.getName(), Level.INFO);
	}
}
