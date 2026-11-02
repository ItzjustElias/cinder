package be.eliasb;

import be.eliasb.config.CinderConfig;
import be.eliasb.platform.Platform;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class Cinder {
  public static final String MOD_ID = "cinder";
  public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

  private static Platform platform;

  private Cinder() {}

  public static void init(Platform loaderPlatform) {
    platform = loaderPlatform;
    LOGGER.info("[Cinder] loaded on {}!", loaderPlatform.loaderName());
    CinderConfig.load();
  }

  public static Platform platform() {
    if (platform == null) {
      throw new IllegalStateException("Cinder.init(Platform) has not been called yet");
    }
    return platform;
  }
}
