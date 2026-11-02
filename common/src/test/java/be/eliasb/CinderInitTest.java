package be.eliasb;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class CinderInitTest {
  @Test
  void initStoresThePlatformAndCreatesTheConfigFile(@TempDir Path dir) {
    var platform = new TestPlatform(dir);

    Cinder.init(platform);

    assertSame(platform, Cinder.platform());
    assertTrue(Files.exists(dir.resolve("cinder.json")));
  }
}
