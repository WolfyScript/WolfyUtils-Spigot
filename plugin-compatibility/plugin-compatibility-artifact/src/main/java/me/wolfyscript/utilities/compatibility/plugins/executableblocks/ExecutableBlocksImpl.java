package me.wolfyscript.utilities.compatibility.plugins.executableblocks;

import com.ssomar.score.api.executableblocks.ExecutableBlocksAPI;
import com.ssomar.score.api.executableblocks.config.ExecutableBlocksManagerInterface;
import com.wolfyscript.utilities.bukkit.WolfyCoreBukkit;
import com.wolfyscript.utilities.bukkit.WolfyCoreImpl;
import com.wolfyscript.utilities.bukkit.annotations.WUPluginIntegration;
import com.wolfyscript.utilities.bukkit.compatibility.PluginIntegrationAbstract;
import java.util.List;
import java.util.Optional;
import com.wolfyscript.utilities.bukkit.compatibility.plugins.ExecutableBlocksIntegration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;

@WUPluginIntegration(pluginName = ExecutableBlocksIntegration.PLUGIN_NAME)
public class ExecutableBlocksImpl extends PluginIntegrationAbstract implements ExecutableBlocksIntegration {

    private ExecutableBlocksManagerInterface manager;

    /**
     * The main constructor that is called whenever the integration is created.<br>
     *  @param core The WolfyUtilCore.
     *
     * @param pluginName The name of the associated plugin.
     */
    protected ExecutableBlocksImpl(WolfyCoreBukkit core, String pluginName) {
        super(core, pluginName);
    }

    @Override
    public void init(Plugin plugin) {
        this.manager = ExecutableBlocksAPI.getExecutableBlocksManager();
        core.registerAPIReference(new ExecutableBlocksRef.Parser(this, manager));
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
        return manager.getExecutableBlockIdsList();
    }

    @Override
    public Optional<String> getExecutableBlock(ItemStack stack) {
        if (stack == null || stack.getItemMeta() == null || stack.getItemMeta().getPersistentDataContainer().isEmpty()) return Optional.empty();
        return Optional.ofNullable(stack.getItemMeta().getPersistentDataContainer().get(BLOCK_ID, PersistentDataType.STRING)).map(s -> manager.isValidID(s) ? s : null);
    }
}
