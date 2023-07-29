package com.wolfyscript.utilities.bukkit.gui;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.InjectableValues;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.google.common.collect.Multimap;
import com.google.common.collect.MultimapBuilder;
import com.google.common.collect.Sets;
import com.wolfyscript.jackson.dataformat.hocon.HoconMapper;
import com.wolfyscript.utilities.common.WolfyUtils;
import com.wolfyscript.utilities.common.gui.GuiAPIManagerCommonImpl;
import com.wolfyscript.utilities.common.gui.GuiViewManager;
import com.wolfyscript.utilities.common.gui.RouterBuilder;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenCustomHashMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;

import java.io.File;
import java.io.IOException;
import java.sql.ClientInfoStatus;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.regex.Pattern;
import java.util.stream.Stream;

public class GuiAPIManagerImpl extends GuiAPIManagerCommonImpl {

    private static final Pattern GUI_FILE_PATTERN = Pattern.compile(".*\\.(conf|json)");

    private File guiDataSubFolder;
    private String guiResourceDir;

    private final Long2ObjectMap<GuiViewManager> VIEW_MANAGERS = new Long2ObjectOpenHashMap<>();
    private final Multimap<String, Long> CACHED_VIEW_MANAGERS = MultimapBuilder.hashKeys().hashSetValues().build();
    private final Multimap<UUID, Long> VIEW_MANAGERS_PER_PLAYER = MultimapBuilder.hashKeys().hashSetValues().build();

    public GuiAPIManagerImpl(WolfyUtils wolfyUtils) {
        super(wolfyUtils);
        this.guiDataSubFolder = new File(wolfyUtils.getDataFolder(), "gui");
        this.guiResourceDir = "com/wolfyscript/utilities/common/gui";
    }

    public void setGuiResourceDir(String path) {
        this.guiResourceDir = path;
    }

    public void setGuiDataDir(File dir) {
        this.guiDataSubFolder = dir;
    }

    @Override
    public Stream<GuiViewManager> getViewManagersFor(UUID uuid) {
        return VIEW_MANAGERS_PER_PLAYER.get(uuid).stream().map(id -> VIEW_MANAGERS.get(id));
    }

    @Override
    public Stream<GuiViewManager> getViewManagersFor(UUID uuid, String guiID) {
        Collection<Long> ids = CACHED_VIEW_MANAGERS.get(guiID);
        return VIEW_MANAGERS_PER_PLAYER.get(uuid).stream()
                .filter(ids::contains)
                .map(id -> VIEW_MANAGERS.get(id));
    }

    @Override
    public void registerGui(String id, Consumer<RouterBuilder> consumer) {
        RouterBuilder builder = new RouterBuilderImpl(id, wolfyUtils);
        consumer.accept(builder);
        registerGui(id, builder.create(null));
    }

    @Override
    public GuiViewManager createView(String clusterID, UUID... uuids) {
        return getGui(clusterID).map(cluster -> {
            Set<UUID> viewers = Set.of(uuids);
            Collection<Long> viewManagers = CACHED_VIEW_MANAGERS.get(clusterID);
            return viewManagers.stream()
                    .map(id -> VIEW_MANAGERS.get(id))
                    .filter(viewManager -> viewManager.getViewers().equals(viewers))
                    .findFirst()
                    .orElseGet(() -> {
                        GuiViewManagerImpl viewManager = new GuiViewManagerImpl(wolfyUtils, cluster, viewers);
                        viewManagers.add(viewManager.getId());
                        VIEW_MANAGERS.put(viewManager.getId(), viewManager);
                        for (UUID viewer : viewers) {
                            VIEW_MANAGERS_PER_PLAYER.put(viewer, viewManager.getId());
                        }
                        return viewManager;
                    });
        }).orElse(null);
    }

    @Override
    public void registerGuiFromFiles(String id, Consumer<RouterBuilder> consumer) {
        HoconMapper mapper = wolfyUtils.getJacksonMapperUtil().getGlobalMapper(HoconMapper.class);
        try {
            wolfyUtils.exportResources(guiResourceDir + "/" + id, new File(guiDataSubFolder, "/includes/" + id), true, GUI_FILE_PATTERN);

            File file = new File(guiDataSubFolder, id + "/entry.conf"); // Look for user-override
            if (!file.exists()) {
                file = new File(guiDataSubFolder, "includes/" + id + "/entry.conf"); // Fall back to includes version
                if (!file.exists() || !file.isFile()) throw new IllegalArgumentException("Cannot find gui index file! Expected: " + file.getPath());
            }

            CustomInjectableValues injectableValues = new CustomInjectableValues();
            injectableValues.addValue("parent", null);
            injectableValues.addValue(WolfyUtils.class, wolfyUtils);
            injectableValues.addValue("wolfyUtils", wolfyUtils);

            RouterBuilder builder = mapper.readerFor(new TypeReference<RouterBuilderImpl>() {}).with(injectableValues).readValue(file);
            consumer.accept(builder);
            registerGui(id, builder.create(null));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static class CustomInjectableValues extends InjectableValues.Std {

        @Override
        public Object findInjectableValue(Object valueId, DeserializationContext ctxt, BeanProperty forProperty, Object beanInstance) throws JsonMappingException {
            return super.findInjectableValue(valueId, ctxt, forProperty, beanInstance);
        }
    }

}
