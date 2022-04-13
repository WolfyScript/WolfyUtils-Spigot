package com.wolfyscript.utilities.bukkit;

import me.wolfyscript.utilities.api.WolfyUtilCore;
import me.wolfyscript.utilities.compatibility.plugins.PlaceholderAPIIntegration;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.Tag;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.bukkit.entity.Player;

public class TagResolverUtil {

    public static TagResolver papi(Player player) {
        return TagResolver.resolver("papi", (args, context) -> {
            String text = args.popOr("The <papi> tag requires exactly one argument, text with papi placeholders!").value();
            PlaceholderAPIIntegration integration = WolfyUtilCore.getInstance().getCompatibilityManager().getPlugins().getIntegration("PlaceholderAPI", PlaceholderAPIIntegration.class);
            if (integration != null) {
                text = integration.setPlaceholders(player, text);
            }
            return Tag.inserting(Component.text(text));
        });
    }

}
