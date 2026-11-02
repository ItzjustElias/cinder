package be.eliasb.fabric;

import be.eliasb.config.CinderConfigScreen;
import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;

public final class CinderModMenuApiImpl implements ModMenuApi {
  @Override
  public ConfigScreenFactory<?> getModConfigScreenFactory() {
    return CinderConfigScreen::createScreen;
  }
}
