package com.lion.graveyard.init;

import com.lion.graveyard.platform.RegistryHelper;
import com.lion.graveyard.world.processors.RemoveWaterloggedCryptProcessor;
import com.lion.graveyard.world.processors.RemoveWaterloggedProcessor;
import com.lion.graveyard.world.processors.SwitchSpawnerProcessor;


public class TGProcessors {

    static {
        RegistryHelper.registerStructureProcessor("remove_waterlogged_processor", RemoveWaterloggedProcessor.CODEC);
        RegistryHelper.registerStructureProcessor("switch_spawner_processor", SwitchSpawnerProcessor.CODEC);
        RegistryHelper.registerStructureProcessor("waterlogged_crypt_processor", RemoveWaterloggedCryptProcessor.CODEC);
    }

    public static void init() {}
}
