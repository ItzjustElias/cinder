package be.eliasb;

import be.eliasb.config.CinderConfig;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Environment(EnvType.CLIENT)
public class Cinder implements ClientModInitializer {
  public static final String MOD_ID = "cinder";
  public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

  @Override
  public void onInitializeClient() {
    LOGGER.info("[Cinder] loaded!");
    CinderConfig.load();
  }
}
