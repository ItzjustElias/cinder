package be.eliasb.neoforge;

import be.eliasb.Cinder;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.Mod;

@Mod(value = Cinder.MOD_ID, dist = Dist.CLIENT)
public final class CinderNeoForge {
  private static final String YACL_ID = "yet_another_config_lib_v3";

  public CinderNeoForge(ModContainer container) {
    Cinder.init(new NeoForgePlatform());

    if (ModList.get().isLoaded(YACL_ID)) {
      YaclSupport.registerConfigScreen(container);
    }
  }
}
