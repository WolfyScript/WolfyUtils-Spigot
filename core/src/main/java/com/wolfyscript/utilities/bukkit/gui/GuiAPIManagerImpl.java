package com.wolfyscript.utilities.bukkit.gui;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.InjectableValues;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wolfyscript.utilities.common.WolfyUtils;
import com.wolfyscript.utilities.common.gui.ComponentBuilder;
import com.wolfyscript.utilities.common.gui.GuiAPIManagerCommonImpl;
import com.wolfyscript.utilities.common.gui.GuiViewManager;
import com.wolfyscript.utilities.common.gui.RouterBuilder;
import java.io.File;
import java.io.IOException;
import java.util.Set;
import java.util.UUID;
import java.util.function.Consumer;

public class GuiAPIManagerImpl extends GuiAPIManagerCommonImpl {

    public GuiAPIManagerImpl(WolfyUtils wolfyUtils) {
        super(wolfyUtils);
    }

    @Override
    public void registerRouter(String id, Consumer<RouterBuilder> consumer) {
        RouterBuilder builder = new RouterBuilderImpl(id, wolfyUtils);
        consumer.accept(builder);
        registerCluster(builder.create(null));
    }

    @Override
    public  GuiViewManager createView(String clusterID, UUID... uuids) {
        return getRouter(clusterID).map(cluster -> new GuiViewManagerImpl(wolfyUtils, cluster, Set.of(uuids))).orElse(null);
    }

    @Override
    public void registerRouterFromFile(File file, Consumer<RouterBuilder> consumer) {
        ObjectMapper mapper = wolfyUtils.getJacksonMapperUtil().getGlobalMapper();
        try {
            CustomInjectableValues injectableValues = new CustomInjectableValues();
            injectableValues.addValue("parent", null);
            injectableValues.addValue(WolfyUtils.class, wolfyUtils);
            injectableValues.addValue("wolfyUtils", wolfyUtils);
            ComponentBuilder<?,?> builder = mapper.readerFor(new TypeReference<ComponentBuilder<?,?>>() {}).with(injectableValues).readValue(file);
            System.out.println(builder);
            if (builder instanceof RouterBuilder routerBuilder) {
                consumer.accept(routerBuilder);
                registerCluster(routerBuilder.create(null));
            } else {
                throw new IllegalArgumentException("Loaded builder is not a RouterBuilder! The root must be a Route!");
            }
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
