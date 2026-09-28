package be.eliasb.integration;

import be.eliasb.config.CinderConfigScreen;
import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;

public class CinderModMenuApiImpl implements ModMenuApi {
  @Override
  public ConfigScreenFactory<?> getModConfigScreenFactory() {
    return CinderConfigScreen::createScreen;
  }
}
