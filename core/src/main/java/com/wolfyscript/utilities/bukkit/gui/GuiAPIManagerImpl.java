package com.wolfyscript.utilities.bukkit.gui;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.InjectableValues;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.wolfyscript.jackson.dataformat.hocon.HoconMapper;
import com.wolfyscript.utilities.common.WolfyUtils;
import com.wolfyscript.utilities.common.gui.GuiAPIManagerCommonImpl;
import com.wolfyscript.utilities.common.gui.GuiViewManager;
import com.wolfyscript.utilities.common.gui.RouterBuilder;
import java.io.File;
import java.io.IOException;
import java.util.Set;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.regex.Pattern;

public class GuiAPIManagerImpl extends GuiAPIManagerCommonImpl {

    private static final Pattern GUI_FILE_PATTERN = Pattern.compile(".*\\.(conf|json)");

    private File guiDataSubFolder;
    private String guiResourceDir;

    public GuiAPIManagerImpl(WolfyUtils wolfyUtils) {
        super(wolfyUtils);
        this.guiDataSubFolder = new File(wolfyUtils.getDataFolder(), "gui");
        this.guiResourceDir = "com/wolfyscript/utilities/common/gui";
    }

    @Override
    public void registerGui(String id, Consumer<RouterBuilder> consumer) {
        RouterBuilder builder = new RouterBuilderImpl(id, wolfyUtils);
        consumer.accept(builder);
        registerGui(id, builder.create(null));
    }

    @Override
    public  GuiViewManager createView(String clusterID, UUID... uuids) {
        return getGui(clusterID).map(cluster -> new GuiViewManagerImpl(wolfyUtils, cluster, Set.of(uuids))).orElse(null);
    }

    @Override
    public void registerGuiFromFiles(String id, Consumer<RouterBuilder> consumer) {
        HoconMapper mapper = wolfyUtils.getJacksonMapperUtil().getGlobalMapper(HoconMapper.class);
        try {
            wolfyUtils.exportResources(guiResourceDir + "/" + id, new File(guiDataSubFolder, "/includes/" + id), true, GUI_FILE_PATTERN);

            File file = new File(guiDataSubFolder, id + "/index.conf"); // Look for user-override
            if (!file.exists()) {
                file = new File(guiDataSubFolder, "includes/" + id + "/index.conf"); // Fall back to includes version
                if (!file.exists() || !file.isFile()) throw new IllegalArgumentException("Cannot find file to gui: " + file.getPath());
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
