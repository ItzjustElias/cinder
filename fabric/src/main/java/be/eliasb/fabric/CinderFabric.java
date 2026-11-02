package be.eliasb.fabric;

import be.eliasb.Cinder;
import net.fabricmc.api.ClientModInitializer;

public final class CinderFabric implements ClientModInitializer {
  @Override
  public void onInitializeClient() {
    Cinder.init(new FabricPlatform());
  }
}
