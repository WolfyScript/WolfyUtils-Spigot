package me.wolfyscript.utilities.compatibility.plugins.executableblocks;

import com.ssomar.executableblocks.executableblocks.ExecutableBlocksManager;
import java.util.List;
import java.util.Optional;
import me.wolfyscript.utilities.annotations.WUPluginIntegration;
import me.wolfyscript.utilities.api.WolfyUtilCore;
import me.wolfyscript.utilities.compatibility.PluginIntegrationAbstract;
import me.wolfyscript.utilities.compatibility.plugins.ExecutableBlocksIntegration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;

@WUPluginIntegration(pluginName = ExecutableBlocksIntegration.PLUGIN_NAME)
public class ExecutableBlocksImpl extends PluginIntegrationAbstract implements ExecutableBlocksIntegration {

    private ExecutableBlocksManager manager;

    /**
     * The main constructor that is called whenever the integration is created.<br>
     *  @param core The WolfyUtilCore.
     */
    protected ExecutableBlocksImpl(WolfyUtilCore core) {
        super(core, ExecutableBlocksIntegration.PLUGIN_NAME);
    }

    @Override
    public void init(Plugin plugin) {
        this.manager = ExecutableBlocksManager.getInstance();
        core.registerAPIReference(new ExecutableBlocksRef.Parser(this, manager));
        core.getRegistries().getStackIdentifierParsers().register(new ExecutableBlocksStackIdentifier.Parser(this, manager));
    }

    @Override
    public boolean hasAsyncLoading() {
        return false;
    }

    @Override
    public boolean isValidID(String id) {
        return manager.isValidID(id);
    }

    @Override
    public List<String> getExecutableBlockIdsList() {
        return manager.getLoadedObjectsIDs();
    }

    @Override
    public Optional<String> getExecutableBlock(ItemStack stack) {
        if (stack == null || stack.getItemMeta() == null || stack.getItemMeta().getPersistentDataContainer().isEmpty()) return Optional.empty();
        return Optional.ofNullable(stack.getItemMeta().getPersistentDataContainer().get(BLOCK_ID, PersistentDataType.STRING)).map(s -> manager.isValidID(s) ? s : null);
    }
}
