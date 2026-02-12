package dev.zenix.wynnspells.client;

import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;

public class WynnSpellsDataGenerator implements DataGeneratorEntrypoint {

    @Override
    public void onInitializeDataGenerator(FabricDataGenerator fabricDataGenerator) {
        @SuppressWarnings("unused")
        FabricDataGenerator.Pack pack = fabricDataGenerator.createPack();
    }
}
