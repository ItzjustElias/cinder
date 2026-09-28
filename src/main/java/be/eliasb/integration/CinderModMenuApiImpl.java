package be.eliasb.integration;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import be.eliasb.config.CinderConfigScreen;

public class CinderModMenuApiImpl implements ModMenuApi {
    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return CinderConfigScreen::createScreen;
    }
}