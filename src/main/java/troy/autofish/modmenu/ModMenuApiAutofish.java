package troy.autofish.modmenu;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import troy.autofish.FabricModAutofish;
import troy.autofish.gui.AutofishScreenBuilder;

public class ModMenuApiAutofish implements ModMenuApi {
    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return parent -> AutofishScreenBuilder.buildScreen(FabricModAutofish.getInstance(), parent);
    }
}
