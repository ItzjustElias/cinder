package be.eliasb.fabric;

import be.eliasb.platform.Platform;
import java.nio.file.Path;
import net.fabricmc.loader.api.FabricLoader;

public final class FabricPlatform implements Platform {
  @Override
  public Path configDir() {
    return FabricLoader.getInstance().getConfigDir();
  }

  @Override
  public String loaderName() {
    return "Fabric";
  }
}
