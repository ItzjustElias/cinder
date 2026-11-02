package be.eliasb.neoforge;

import be.eliasb.platform.Platform;
import java.nio.file.Path;
import net.neoforged.fml.loading.FMLPaths;

public final class NeoForgePlatform implements Platform {
  @Override
  public Path configDir() {
    return FMLPaths.CONFIGDIR.get();
  }

  @Override
  public String loaderName() {
    return "NeoForge";
  }
}
