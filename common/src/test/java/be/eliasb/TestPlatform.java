package be.eliasb;

import be.eliasb.platform.Platform;
import java.nio.file.Path;

public record TestPlatform(Path configDir) implements Platform {
  @Override
  public String loaderName() {
    return "Test";
  }
}
