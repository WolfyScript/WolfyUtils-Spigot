package com.wolfyscript.utilities.bukkit.language;

import com.fasterxml.jackson.databind.InjectableValues;
import com.fasterxml.jackson.databind.JsonNode;
import com.wolfyscript.utilities.WolfyUtils;
import com.wolfyscript.utilities.bukkit.WolfyUtilsBukkit;
import com.wolfyscript.utilities.bukkit.chat.ChatColor;
import com.wolfyscript.utilities.language.LanguageAPI;
import com.wolfyscript.utilities.language.Language;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import net.kyori.adventure.platform.bukkit.BukkitComponentSerializer;
import org.jetbrains.annotations.NotNull;

public class LangAPISpigot extends LanguageAPI {

    private static final Pattern LEGACY_PLACEHOLDER_PATTERN = Pattern.compile("%([^%]+)%");

    public LangAPISpigot(WolfyUtils api) {
        super(api);
    }

    private WolfyUtilsBukkit getAPI() {
        return (WolfyUtilsBukkit) api;
    }

    public Language loadLangFile(String lang) {
        var file = getLangFile(lang);
        if (!file.exists()) {
            try {
                getAPI().getPlugin().saveResource("lang/" + lang + ".json", true);
            } catch (IllegalArgumentException ex) {
                getAPI().getLogger().severe("Couldn't load lang \""+lang+"\"! Language resource doesn't exists!");
                return null;
            }
        }
        var injectableValues = new InjectableValues.Std();
        injectableValues.addValue("file", file);
        injectableValues.addValue("api", api);
        injectableValues.addValue("lang", lang);
        try {
            Language language = api.getJacksonMapperUtil().getGlobalMapper().reader(injectableValues).readValue(file, Language.class);
            registerLanguage(language);
            return language;
        } catch (IOException ex) {
            getAPI().getLogger().log(Level.SEVERE, "Couldn't load language \""+lang+"\"!");
            ex.printStackTrace();
        }
        return null;
    }

    public void saveLangFile(@NotNull Language language) {
        try {
            api.getJacksonMapperUtil().getGlobalMapper().writeValue(getLangFile(language.getName()), language);
        } catch (IOException ex) {
            getAPI().getLogger().severe("Couldn't save language \""+language.getName()+"\"!");
            getAPI().getLogger().throwing("LanguageAPI", "saveLangFile", ex);
        }

    }

    protected File getLangFile(String lang) {
        return new File(api.getDataFolder(), "lang/" + lang + ".json");
    }

    @Deprecated
    public List<String> replaceKeys(List<String> msg) {
        Pattern pattern = Pattern.compile("[$]([a-zA-Z0-9._]*?)[$]");
        List<String> result = new ArrayList<>();
        msg.forEach(s -> {
            List<String> keys = new ArrayList<>();
            Matcher matcher = pattern.matcher(s);
            while (matcher.find()) {
                keys.add(matcher.group(0));
            }
            if (keys.size() > 1) {
                for (String key : keys) {
                    JsonNode node = getNodeAt(key.replace("$", ""));
                    if (node.isTextual()) {
                        result.add(s.replace(key, node.asText()));
                    } else if (node.isArray()) {
                        StringBuilder sB = new StringBuilder();
                        node.elements().forEachRemaining(n -> sB.append(' ').append(n.asText()));
                        result.add(s.replace(key, sB.toString()));
                    }
                }
            } else if (!keys.isEmpty()) {
                String key = keys.get(0);
                JsonNode node = getNodeAt(key.replace("$", ""));
                if (node.isTextual()) {
                    result.add(s.replace(key, node.asText()));
                } else if (node.isArray()) {
                    node.elements().forEachRemaining(n -> result.add(n.asText()));
                }
            } else {
                result.add(s);
            }
        });
        return result;
    }

    @Override
    public String convertLegacyToMiniMessage(String legacyText) {
        String rawLegacy = ChatColor.convert(legacyText);
        Matcher matcher = LEGACY_PLACEHOLDER_PATTERN.matcher(rawLegacy);
        Map<String, String> foundPlaceholders = new HashMap<>();
        while (matcher.find()) {
            //find the old placeholder.
            foundPlaceholders.put(matcher.group(), "<" + api.getChat().convertOldPlaceholder(matcher.group(1)) + ">");
        }
        if (rawLegacy.contains("§")) {
            rawLegacy = api.getChat().getMiniMessage().serialize(BukkitComponentSerializer.legacy().deserialize(rawLegacy));
        }
        //Replace the old placeholders with the new tags after the color conversion, so these tags are not escaped!
        if (!foundPlaceholders.isEmpty()) {
            for (Map.Entry<String, String> entry : foundPlaceholders.entrySet()) {
                rawLegacy = rawLegacy.replace(entry.getKey(), entry.getValue());
            }
        }
        return rawLegacy;
    }

    @Deprecated
    public List<String> replaceKeys(String... msg) {
        return Arrays.stream(msg).map(this::replaceKeys).collect(Collectors.toList());
    }

    @Deprecated
    public String replaceColoredKeys(String msg) {
        return ChatColor.convert(replaceKeys(msg));
    }

    @Deprecated
    public List<String> replaceColoredKeys(List<String> msg) {
        return replaceKeys(msg).stream().map(ChatColor::convert).collect(Collectors.toList());
    }

    @Deprecated
    public List<String> replaceKey(String key) {
        return readKey(key, JsonNode::asText);
    }

    @Deprecated
    public List<String> replaceColoredKey(String key) {
        return readKey(key, node -> ChatColor.convert(node.asText()));
    }

}
