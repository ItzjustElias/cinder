package be.eliasb.platform;

import java.nio.file.Path;

public interface Platform {
  // path to the games Directory for the config file :)
  Path configDir();

  // display name of the loader used in the client
  String loaderName();
}
