package com.wolfyscript.utilities.bukkit.persistent.player;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.InjectableValues;
import com.wolfyscript.utilities.bukkit.persistent.world.CustomBlockData;
import java.util.Optional;
import java.util.UUID;
import me.wolfyscript.utilities.api.WolfyUtilCore;
import me.wolfyscript.utilities.util.NamespacedKey;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataHolder;
import org.bukkit.persistence.PersistentDataType;

public class PlayerStorage {

    private static final org.bukkit.NamespacedKey DATA_KEY = new org.bukkit.NamespacedKey("wolfyutils", "data");

    private final WolfyUtilCore core;
    private final UUID playerUUID;

    public PlayerStorage(WolfyUtilCore core, UUID playerUUID) {
        this.core = core;
        this.playerUUID = playerUUID;
    }

    public Optional<Player> getPlayer () {
        return Optional.ofNullable(Bukkit.getPlayer(playerUUID));
    }

    public Optional<PersistentDataContainer> getPersistentDataContainer() {
        return getPlayer().map(PersistentDataHolder::getPersistentDataContainer);
    }

    public void setData(CustomPlayerData data) {
        getPersistentDataContainer().ifPresent(container -> {
            var dataContainer = container.getOrDefault(DATA_KEY, PersistentDataType.TAG_CONTAINER, container.getAdapterContext().newPersistentDataContainer());
            var objectMapper = core.getWolfyUtils().getJacksonMapperUtil().getGlobalMapper();
            try {
                data.onLoad();
                dataContainer.set(data.getNamespacedKey().bukkit(), PersistentDataType.STRING, objectMapper.writeValueAsString(data));
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
            container.set(DATA_KEY, PersistentDataType.TAG_CONTAINER, dataContainer);
        });
    }

    public <T extends CustomPlayerData> Optional<T> getData(Class<T> dataType) {
        NamespacedKey dataID = core.getRegistries().getCustomPlayerData().getKey(dataType);
        return getPersistentDataContainer().map(container -> {
            var dataContainer = container.getOrDefault(DATA_KEY, PersistentDataType.TAG_CONTAINER, container.getAdapterContext().newPersistentDataContainer());
            var objectMapper = core.getWolfyUtils().getJacksonMapperUtil().getGlobalMapper();
            org.bukkit.NamespacedKey key = dataID.bukkit();
            if (dataContainer.has(key, PersistentDataType.STRING)) {
                try {
                    return objectMapper.reader(new InjectableValues.Std().addValue(WolfyUtilCore.class, core)).forType(CustomBlockData.class).readValue(dataContainer.get(key, PersistentDataType.STRING));
                } catch (JsonProcessingException e) {
                    e.printStackTrace();
                }
            }
            return null;
        });
    }

    public void removeData(NamespacedKey dataID) {
        getPersistentDataContainer().ifPresent(container -> {
            var dataContainer = container.getOrDefault(DATA_KEY, PersistentDataType.TAG_CONTAINER, container.getAdapterContext().newPersistentDataContainer());
            dataContainer.remove(dataID.bukkit());
            container.set(DATA_KEY, PersistentDataType.TAG_CONTAINER, dataContainer);
        });
    }

}
