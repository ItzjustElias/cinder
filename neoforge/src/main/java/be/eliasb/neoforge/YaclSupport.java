package be.eliasb.neoforge;

import be.eliasb.config.CinderConfigScreen;
import net.neoforged.fml.ModContainer;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;

final class YaclSupport {
  private YaclSupport() {}

  static void registerConfigScreen(ModContainer container) {
    container.registerExtensionPoint(
        IConfigScreenFactory.class,
        (IConfigScreenFactory) (modContainer, parent) -> CinderConfigScreen.createScreen(parent));
  }
}
