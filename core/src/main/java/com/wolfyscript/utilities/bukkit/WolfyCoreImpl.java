package com.wolfyscript.utilities.bukkit;

import com.fasterxml.jackson.databind.InjectableValues;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Stage;
import com.wolfyscript.jackson.dataformat.hocon.HoconMapper;
import com.wolfyscript.utilities.bukkit.chat.BukkitChat;
import com.wolfyscript.utilities.bukkit.commands.ChatActionCommand;
import com.wolfyscript.utilities.bukkit.commands.GuiExampleCommand;
import com.wolfyscript.utilities.bukkit.commands.InfoCommand;
import com.wolfyscript.utilities.bukkit.commands.InputCommand;
import com.wolfyscript.utilities.bukkit.commands.QueryDebugCommand;
import com.wolfyscript.utilities.bukkit.commands.SpawnParticleAnimationCommand;
import com.wolfyscript.utilities.bukkit.commands.SpawnParticleEffectCommand;
import com.wolfyscript.utilities.bukkit.compatibility.CompatibilityManager;
import com.wolfyscript.utilities.bukkit.compatibility.CompatibilityManagerBukkit;
import com.wolfyscript.utilities.bukkit.config.WUConfig;
import com.wolfyscript.utilities.bukkit.console.Console;
import com.wolfyscript.utilities.bukkit.gui.interaction.GUIInventoryListener;
import com.wolfyscript.utilities.bukkit.gui.example.TestGUI;
import com.wolfyscript.utilities.bukkit.json.serialization.ColorSerialization;
import com.wolfyscript.utilities.bukkit.json.serialization.DustOptionsSerialization;
import com.wolfyscript.utilities.bukkit.json.serialization.ItemStackSerialization;
import com.wolfyscript.utilities.bukkit.json.serialization.LocationSerialization;
import com.wolfyscript.utilities.bukkit.json.serialization.PotionEffectSerialization;
import com.wolfyscript.utilities.bukkit.json.serialization.PotionEffectTypeSerialization;
import com.wolfyscript.utilities.bukkit.json.serialization.VectorSerialization;
import com.wolfyscript.utilities.bukkit.listeners.EquipListener;
import com.wolfyscript.utilities.bukkit.listeners.PersistentStorageListener;
import com.wolfyscript.utilities.bukkit.listeners.PlayerListener;
import com.wolfyscript.utilities.bukkit.listeners.custom_item.CustomDurabilityListener;
import com.wolfyscript.utilities.bukkit.listeners.custom_item.CustomItemDataListener;
import com.wolfyscript.utilities.bukkit.listeners.custom_item.CustomItemPlayerListener;
import com.wolfyscript.utilities.bukkit.listeners.custom_item.CustomParticleListener;
import com.wolfyscript.utilities.bukkit.nbt.QueryNode;
import com.wolfyscript.utilities.bukkit.nbt.QueryNodeBoolean;
import com.wolfyscript.utilities.bukkit.nbt.QueryNodeByte;
import com.wolfyscript.utilities.bukkit.nbt.QueryNodeByteArray;
import com.wolfyscript.utilities.bukkit.nbt.QueryNodeCompound;
import com.wolfyscript.utilities.bukkit.nbt.QueryNodeDouble;
import com.wolfyscript.utilities.bukkit.nbt.QueryNodeFloat;
import com.wolfyscript.utilities.bukkit.nbt.QueryNodeInt;
import com.wolfyscript.utilities.bukkit.nbt.QueryNodeIntArray;
import com.wolfyscript.utilities.bukkit.nbt.QueryNodeListCompound;
import com.wolfyscript.utilities.bukkit.nbt.QueryNodeListDouble;
import com.wolfyscript.utilities.bukkit.nbt.QueryNodeListFloat;
import com.wolfyscript.utilities.bukkit.nbt.QueryNodeListInt;
import com.wolfyscript.utilities.bukkit.nbt.QueryNodeListLong;
import com.wolfyscript.utilities.bukkit.nbt.QueryNodeListString;
import com.wolfyscript.utilities.bukkit.nbt.QueryNodeLong;
import com.wolfyscript.utilities.bukkit.nbt.QueryNodeShort;
import com.wolfyscript.utilities.bukkit.nbt.QueryNodeString;
import com.wolfyscript.utilities.bukkit.network.messages.MessageFactory;
import com.wolfyscript.utilities.bukkit.network.messages.MessageHandler;
import com.wolfyscript.utilities.bukkit.persistent.PersistentStorage;
import com.wolfyscript.utilities.bukkit.persistent.player.CustomPlayerData;
import com.wolfyscript.utilities.bukkit.persistent.player.PlayerParticleEffectData;
import com.wolfyscript.utilities.bukkit.persistent.world.CustomBlockData;
import com.wolfyscript.utilities.bukkit.registry.BukkitRegistries;
import com.wolfyscript.utilities.bukkit.world.inventory.CreativeModeTab;
import com.wolfyscript.utilities.bukkit.world.items.BukkitItemStackConfig;
import com.wolfyscript.utilities.bukkit.world.items.CustomItemBlockData;
import com.wolfyscript.utilities.bukkit.world.items.CustomItemData;
import com.wolfyscript.utilities.bukkit.world.items.actions.Action;
import com.wolfyscript.utilities.bukkit.world.items.actions.ActionCommand;
import com.wolfyscript.utilities.bukkit.world.items.actions.ActionParticleAnimation;
import com.wolfyscript.utilities.bukkit.world.items.actions.ActionSound;
import com.wolfyscript.utilities.bukkit.world.items.actions.Event;
import com.wolfyscript.utilities.bukkit.world.items.actions.EventPlayerConsumeItem;
import com.wolfyscript.utilities.bukkit.world.items.actions.EventPlayerInteract;
import com.wolfyscript.utilities.bukkit.world.items.actions.EventPlayerInteractAtEntity;
import com.wolfyscript.utilities.bukkit.world.items.actions.EventPlayerInteractEntity;
import com.wolfyscript.utilities.bukkit.world.items.actions.EventPlayerItemBreak;
import com.wolfyscript.utilities.bukkit.world.items.actions.EventPlayerItemDamage;
import com.wolfyscript.utilities.bukkit.world.items.actions.EventPlayerItemDrop;
import com.wolfyscript.utilities.bukkit.world.items.actions.EventPlayerItemHandSwap;
import com.wolfyscript.utilities.bukkit.world.items.actions.EventPlayerItemHeld;
import com.wolfyscript.utilities.bukkit.world.items.meta.AttributesModifiersMeta;
import com.wolfyscript.utilities.bukkit.world.items.meta.CustomDamageMeta;
import com.wolfyscript.utilities.bukkit.world.items.meta.CustomDurabilityMeta;
import com.wolfyscript.utilities.bukkit.world.items.meta.CustomItemTagMeta;
import com.wolfyscript.utilities.bukkit.world.items.meta.CustomModelDataMeta;
import com.wolfyscript.utilities.bukkit.world.items.meta.DamageMeta;
import com.wolfyscript.utilities.bukkit.world.items.meta.EnchantMeta;
import com.wolfyscript.utilities.bukkit.world.items.meta.FlagsMeta;
import com.wolfyscript.utilities.bukkit.world.items.meta.LoreMeta;
import com.wolfyscript.utilities.bukkit.world.items.meta.Meta;
import com.wolfyscript.utilities.bukkit.world.items.meta.NameMeta;
import com.wolfyscript.utilities.bukkit.world.items.meta.PlayerHeadMeta;
import com.wolfyscript.utilities.bukkit.world.items.meta.PotionMeta;
import com.wolfyscript.utilities.bukkit.world.items.meta.RepairCostMeta;
import com.wolfyscript.utilities.bukkit.world.items.meta.UnbreakableMeta;
import com.wolfyscript.utilities.bukkit.world.particles.animators.Animator;
import com.wolfyscript.utilities.bukkit.world.particles.animators.AnimatorBasic;
import com.wolfyscript.utilities.bukkit.world.particles.animators.AnimatorCircle;
import com.wolfyscript.utilities.bukkit.world.particles.animators.AnimatorShape;
import com.wolfyscript.utilities.bukkit.world.particles.animators.AnimatorSphere;
import com.wolfyscript.utilities.bukkit.world.particles.animators.AnimatorVectorPath;
import com.wolfyscript.utilities.bukkit.world.particles.shapes.Shape;
import com.wolfyscript.utilities.bukkit.world.particles.shapes.ShapeCircle;
import com.wolfyscript.utilities.bukkit.world.particles.shapes.ShapeComplexCompound;
import com.wolfyscript.utilities.bukkit.world.particles.shapes.ShapeComplexRotation;
import com.wolfyscript.utilities.bukkit.world.particles.shapes.ShapeCube;
import com.wolfyscript.utilities.bukkit.world.particles.shapes.ShapeIcosahedron;
import com.wolfyscript.utilities.bukkit.world.particles.shapes.ShapeSphere;
import com.wolfyscript.utilities.bukkit.world.particles.shapes.ShapeSquare;
import com.wolfyscript.utilities.bukkit.world.particles.timer.Timer;
import com.wolfyscript.utilities.bukkit.world.particles.timer.TimerLinear;
import com.wolfyscript.utilities.bukkit.world.particles.timer.TimerPi;
import com.wolfyscript.utilities.bukkit.world.particles.timer.TimerRandom;
import com.wolfyscript.utilities.WolfyCore;
import com.wolfyscript.utilities.WolfyUtils;
import com.wolfyscript.utilities.gui.ComponentBuilder;
import com.wolfyscript.utilities.eval.operator.BoolOperatorConst;
import com.wolfyscript.utilities.eval.operator.ComparisonOperatorEqual;
import com.wolfyscript.utilities.eval.operator.ComparisonOperatorGreater;
import com.wolfyscript.utilities.eval.operator.ComparisonOperatorGreaterEqual;
import com.wolfyscript.utilities.eval.operator.ComparisonOperatorLess;
import com.wolfyscript.utilities.eval.operator.ComparisonOperatorLessEqual;
import com.wolfyscript.utilities.eval.operator.ComparisonOperatorNotEqual;
import com.wolfyscript.utilities.eval.operator.LogicalOperatorAnd;
import com.wolfyscript.utilities.eval.operator.LogicalOperatorNot;
import com.wolfyscript.utilities.eval.operator.LogicalOperatorOr;
import com.wolfyscript.utilities.eval.operator.Operator;
import com.wolfyscript.utilities.eval.value_provider.ValueProvider;
import com.wolfyscript.utilities.eval.value_provider.ValueProviderByteArrayConst;
import com.wolfyscript.utilities.eval.value_provider.ValueProviderConditioned;
import com.wolfyscript.utilities.eval.value_provider.ValueProviderDoubleConst;
import com.wolfyscript.utilities.eval.value_provider.ValueProviderDoubleVar;
import com.wolfyscript.utilities.eval.value_provider.ValueProviderFloatConst;
import com.wolfyscript.utilities.eval.value_provider.ValueProviderFloatVar;
import com.wolfyscript.utilities.eval.value_provider.ValueProviderIntArrayConst;
import com.wolfyscript.utilities.eval.value_provider.ValueProviderIntegerConst;
import com.wolfyscript.utilities.eval.value_provider.ValueProviderIntegerVar;
import com.wolfyscript.utilities.eval.value_provider.ValueProviderLongConst;
import com.wolfyscript.utilities.eval.value_provider.ValueProviderLongVar;
import com.wolfyscript.utilities.eval.value_provider.ValueProviderShortConst;
import com.wolfyscript.utilities.eval.value_provider.ValueProviderShortVar;
import com.wolfyscript.utilities.eval.value_provider.ValueProviderStringConst;
import com.wolfyscript.utilities.eval.value_provider.ValueProviderStringVar;
import com.wolfyscript.utilities.config.jackson.KeyedTypeIdResolver;
import com.wolfyscript.utilities.config.jackson.OptionalKeyReference;
import com.wolfyscript.utilities.config.jackson.OptionalValueDeserializer;
import com.wolfyscript.utilities.config.jackson.OptionalValueSerializer;
import com.wolfyscript.utilities.config.jackson.JacksonUtil;
import com.wolfyscript.utilities.gui.components.*;
import com.wolfyscript.utilities.nbt.NBTTagConfigBoolean;
import com.wolfyscript.utilities.nbt.NBTTagConfigByte;
import com.wolfyscript.utilities.nbt.NBTTagConfigByteArray;
import com.wolfyscript.utilities.nbt.NBTTagConfigDouble;
import com.wolfyscript.utilities.nbt.NBTTagConfigFloat;
import com.wolfyscript.utilities.nbt.NBTTagConfigInt;
import com.wolfyscript.utilities.nbt.NBTTagConfigIntArray;
import com.wolfyscript.utilities.nbt.NBTTagConfigListCompound;
import com.wolfyscript.utilities.nbt.NBTTagConfigListDouble;
import com.wolfyscript.utilities.nbt.NBTTagConfigListFloat;
import com.wolfyscript.utilities.nbt.NBTTagConfigListInt;
import com.wolfyscript.utilities.nbt.NBTTagConfigListIntArray;
import com.wolfyscript.utilities.nbt.NBTTagConfigListLong;
import com.wolfyscript.utilities.nbt.NBTTagConfigListString;
import com.wolfyscript.utilities.nbt.NBTTagConfigLong;
import com.wolfyscript.utilities.nbt.NBTTagConfigShort;
import com.wolfyscript.utilities.nbt.NBTTagConfigString;
import com.wolfyscript.utilities.platform.Platform;
import com.wolfyscript.utilities.versioning.ServerVersion;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import com.wolfyscript.utilities.world.items.ItemStackConfig;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandMap;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.reflections.Reflections;

/**
 * The core implementation of WolfyUtils.<br>
 * It manages the core plugin of WolfyUtils and there is only one instance of it.<br>
 * <p>
 * If you want to use the plugin specific API, see {@link com.wolfyscript.utilities.WolfyUtils} & {@link WolfyUtilsBukkit}
 */
public abstract class WolfyCoreImpl implements WolfyCore {

    private static final Map<String, Boolean> classes = new HashMap<>();
    private final Console console;
    private WUConfig config;
    private final MessageHandler messageHandler;
    private final MessageFactory messageFactory;
    private final CompatibilityManager compatibilityManager;
    private final PersistentStorage persistentStorage;
    private final List<SimpleModule> jsonMapperModules = new ArrayList<>();
    private final Map<String, WolfyUtilsBukkit> wolfyUtilsInstances = new HashMap<>();
    private final WolfyUtilsBukkit api;
    private final BukkitRegistries registries;
    private final Logger logger;
    private final WolfyCoreBootstrap plugin;
    protected Platform platform;

    /**
     * Constructor invoked by Spigot when the plugin is loaded.
     */
    public WolfyCoreImpl(WolfyCoreBootstrap plugin) {
        this.plugin = plugin;
        this.logger = plugin.getLogger();
        this.api = getOrCreate(plugin);
        api.getChat().setChatPrefix(Component.text("[", NamedTextColor.GRAY).append(Component.text("WU", NamedTextColor.AQUA)).append(Component.text("] ", NamedTextColor.DARK_GRAY)));
        this.console = api.getConsole();
        this.messageHandler = new MessageHandler(this);
        this.messageFactory = new MessageFactory(this);
        this.compatibilityManager = new CompatibilityManagerBukkit(this);
        this.persistentStorage = new PersistentStorage(this);
        this.registries = new BukkitRegistries(this);
    }

    /**
     * Gets an instance of the core plugin.
     * <strong>Only use this if necessary! First try to get the instance via your {@link WolfyUtilsBukkit} instance!</strong>
     *
     * @return The instance of the core.
     */
    @Deprecated
    public static WolfyCoreImpl getInstance() {
        return WolfyCoreBukkitBootstrap.getInstance().getCore();
    }

    /**
     * Gets the {@link CompatibilityManagerBukkit}, that manages the plugins compatibility features.
     *
     * @return The {@link CompatibilityManagerBukkit}.
     */
    public abstract CompatibilityManager getCompatibilityManager();

    /**
     * Gets the {@link Reflections} instance of the plugins' package.
     *
     * @return The Reflection of the plugins' package.
     */
    @Override
    public WolfyUtilsBukkit getWolfyUtils() {
        return api;
    }

    @Override
    public Reflections getReflections() {
        return plugin.getReflections();
    }

    /**
     * Gets the {@link BukkitRegistries} object, that contains all info about available registries.
     *
     * @return The {@link BukkitRegistries} object, to access registries.
     */
    @Override
    public BukkitRegistries getRegistries() {
        return registries;
    }

    @Override
    public Platform platform() {
        return platform;
    }

    /**
     * Gets or create the {@link WolfyUtilsBukkit} instance for the specified plugin.<br>
     * In case init is enabled it will directly initialize the event listeners and possibly other things.<br>
     * <b>This will call {@link WolfyUtilsBukkit#initialize()} directly, so only use it inside your onEnable()!</b>
     *
     * @param plugin The plugin to get the instance for.
     * @return The WolfyUtilities instance for the plugin.
     */
    public WolfyUtilsBukkit getOrCreate(Plugin plugin) {
        return wolfyUtilsInstances.computeIfAbsent(plugin.getName(), s -> new WolfyUtilsBukkit(this, plugin));
    }


    /**
     * Checks if the specified plugin has an API instance associated with it.
     *
     * @param plugin The plugin to check.
     * @return True in case the API is available; false otherwise.
     */
    public boolean has(Plugin plugin) {
        return wolfyUtilsInstances.containsKey(plugin.getName());
    }

    /**
     * Returns an unmodifiable List of all available {@link WolfyUtilsBukkit} instances.
     *
     * @return A list containing all the created API instances.
     */
    public List<WolfyUtilsBukkit> getAPIList() {
        return List.copyOf(wolfyUtilsInstances.values());
    }

    @NotNull
    public Logger getLogger() {
        return logger;
    }

    public void load() {
        Injector injector = Guice.createInjector(Stage.DEVELOPMENT, binder -> {
            binder.bind(WolfyCore.class).toInstance(this);
            binder.requestStaticInjection(KeyedTypeIdResolver.class);
        });

        // Jackson Serializer
        getLogger().info("Register JSON de-/serializers");
        var module = new SimpleModule();
        ItemStackSerialization.create(module);
        ColorSerialization.create(module);
        DustOptionsSerialization.create(module);
        LocationSerialization.create(module);
        // ParticleContentSerialization.create(module);
        PotionEffectTypeSerialization.create(module);
        PotionEffectSerialization.create(module);
        VectorSerialization.create(module);

        // Register implementation types to use for de/serialization
        module.addAbstractTypeMapping(ItemStackConfig.class, BukkitItemStackConfig.class);

        // Add module to WU Modules and register it to the old JacksonUtil.
        jsonMapperModules.add(module);
        JacksonUtil.registerModule(module);

        // De-/Serializer Modifiers that handle type references in JSON
        var keyReferenceModule = new SimpleModule();
        keyReferenceModule.setSerializerModifier(new OptionalKeyReference.SerializerModifier());
        keyReferenceModule.setDeserializerModifier(new OptionalKeyReference.DeserializerModifier(this));
        jsonMapperModules.add(keyReferenceModule);
        JacksonUtil.registerModule(keyReferenceModule);

        var valueReferenceModule = new SimpleModule();
        valueReferenceModule.setSerializerModifier(new OptionalValueSerializer.SerializerModifier());
        valueReferenceModule.setDeserializerModifier(new OptionalValueDeserializer.DeserializerModifier());
        jsonMapperModules.add(valueReferenceModule);
        JacksonUtil.registerModule(valueReferenceModule);

        // Create Global WUCore Mapper and apply modules
        HoconMapper mapper = applyWolfyUtilsJsonMapperModules(new HoconMapper());
        api.getJacksonMapperUtil().applyWolfyUtilsInjectableValues(mapper, new InjectableValues.Std());
        api.getJacksonMapperUtil().setGlobalMapper(mapper);

        // Initialise all the Registers
        getLogger().info("Register JSON Operators");
        var operators = getRegistries().getOperators();
        operators.register(BoolOperatorConst.class);
        // Compare operators
        operators.register(ComparisonOperatorEqual.class);
        operators.register(ComparisonOperatorNotEqual.class);
        operators.register(ComparisonOperatorGreater.class);
        operators.register(ComparisonOperatorGreaterEqual.class);
        operators.register(ComparisonOperatorLess.class);
        operators.register(ComparisonOperatorLessEqual.class);
        // Logical
        operators.register(LogicalOperatorAnd.class);
        operators.register(LogicalOperatorOr.class);
        operators.register(LogicalOperatorNot.class);

        getLogger().info("Register JSON Value Providers");
        var valueProviders = getRegistries().getValueProviders();
        // Custom
        valueProviders.register((Class<ValueProviderConditioned<?>>) (Object) ValueProviderConditioned.class);
        // Primitive
        valueProviders.register(ValueProviderShortConst.class);
        valueProviders.register(ValueProviderShortVar.class);
        valueProviders.register(ValueProviderIntegerConst.class);
        valueProviders.register(ValueProviderIntegerVar.class);
        valueProviders.register(ValueProviderLongConst.class);
        valueProviders.register(ValueProviderLongVar.class);
        valueProviders.register(ValueProviderFloatConst.class);
        valueProviders.register(ValueProviderFloatVar.class);
        valueProviders.register(ValueProviderDoubleConst.class);
        valueProviders.register(ValueProviderDoubleVar.class);
        valueProviders.register(ValueProviderStringConst.class);
        valueProviders.register(ValueProviderStringVar.class);
        // Arrays
        valueProviders.register(ValueProviderByteArrayConst.class);
        valueProviders.register(ValueProviderIntArrayConst.class);

        getLogger().info("Register CustomItem NBT Checks");
        var nbtChecks = getRegistries().getCustomItemNbtChecks();
        nbtChecks.register(AttributesModifiersMeta.KEY, AttributesModifiersMeta.class);
        nbtChecks.register(CustomDamageMeta.KEY, CustomDamageMeta.class);
        nbtChecks.register(CustomDurabilityMeta.KEY, CustomDurabilityMeta.class);
        nbtChecks.register(CustomItemTagMeta.KEY, CustomItemTagMeta.class);
        nbtChecks.register(CustomModelDataMeta.KEY, CustomModelDataMeta.class);
        nbtChecks.register(DamageMeta.KEY, DamageMeta.class);
        nbtChecks.register(EnchantMeta.KEY, EnchantMeta.class);
        nbtChecks.register(FlagsMeta.KEY, FlagsMeta.class);
        nbtChecks.register(LoreMeta.KEY, LoreMeta.class);
        nbtChecks.register(NameMeta.KEY, NameMeta.class);
        nbtChecks.register(PlayerHeadMeta.KEY, PlayerHeadMeta.class);
        nbtChecks.register(PotionMeta.KEY, PotionMeta.class);
        nbtChecks.register(RepairCostMeta.KEY, RepairCostMeta.class);
        nbtChecks.register(UnbreakableMeta.KEY, UnbreakableMeta.class);

        getLogger().info("Register CustomItem Actions");
        var customItemActions = getRegistries().getCustomItemActions();
        customItemActions.register(ActionCommand.KEY, ActionCommand.class);
        customItemActions.register(ActionParticleAnimation.KEY, ActionParticleAnimation.class);
        customItemActions.register(ActionSound.KEY, ActionSound.class);

        getLogger().info("Register CustomItem Events");
        var customItemEvents = getRegistries().getCustomItemEvents();
        customItemEvents.register(EventPlayerInteract.KEY, EventPlayerInteract.class);
        customItemEvents.register(EventPlayerConsumeItem.KEY, EventPlayerConsumeItem.class);
        customItemEvents.register(EventPlayerInteractEntity.KEY, EventPlayerInteractEntity.class);
        customItemEvents.register(EventPlayerInteractAtEntity.KEY, EventPlayerInteractAtEntity.class);
        customItemEvents.register(EventPlayerItemBreak.KEY, EventPlayerItemBreak.class);
        customItemEvents.register(EventPlayerItemDamage.KEY, EventPlayerItemDamage.class);
        customItemEvents.register(EventPlayerItemDrop.KEY, EventPlayerItemDrop.class);
        customItemEvents.register(EventPlayerItemHandSwap.KEY, EventPlayerItemHandSwap.class);
        customItemEvents.register(EventPlayerItemHeld.KEY, EventPlayerItemHeld.class);

        getLogger().info("Register Particle Animators");
        var particleAnimators = getRegistries().getParticleAnimators();
        particleAnimators.register(AnimatorBasic.KEY, AnimatorBasic.class);
        particleAnimators.register(AnimatorSphere.KEY, AnimatorSphere.class);
        particleAnimators.register(AnimatorCircle.KEY, AnimatorCircle.class);
        particleAnimators.register(AnimatorVectorPath.KEY, AnimatorVectorPath.class);
        particleAnimators.register(AnimatorShape.KEY, AnimatorShape.class);

        getLogger().info("Register Particle Shapes");
        var particleShapes = getRegistries().getParticleShapes();
        particleShapes.register(ShapeSquare.KEY, ShapeSquare.class);
        particleShapes.register(ShapeCircle.KEY, ShapeCircle.class);
        particleShapes.register(ShapeSphere.KEY, ShapeSphere.class);
        particleShapes.register(ShapeCube.KEY, ShapeCube.class);
        particleShapes.register(ShapeIcosahedron.KEY, ShapeIcosahedron.class);
        particleShapes.register(ShapeComplexRotation.KEY, ShapeComplexRotation.class);
        particleShapes.register(ShapeComplexCompound.KEY, ShapeComplexCompound.class);

        getLogger().info("Register Particle Timers");
        var particleTimers = getRegistries().getParticleTimer();
        particleTimers.register(TimerLinear.KEY, TimerLinear.class);
        particleTimers.register(TimerRandom.KEY, TimerRandom.class);
        particleTimers.register(TimerPi.KEY, TimerPi.class);

        getLogger().info("Register Custom Block Data");
        var customBlockData = getRegistries().getCustomBlockData();
        customBlockData.register(CustomItemBlockData.ID, CustomItemBlockData.class);

        getLogger().info("Register Custom Player Data");
        var customPlayerDataReg = getRegistries().getCustomPlayerData();
        customPlayerDataReg.register(PlayerParticleEffectData.class);

        getLogger().info("Register NBT Tag Configs");
        var nbtTagConfigs = getRegistries().getNbtTagConfigs();
        // Primitives
        nbtTagConfigs.register(NBTTagConfigBoolean.class);
        nbtTagConfigs.register(NBTTagConfigString.class);
        // Primitive Numerals
        nbtTagConfigs.register(NBTTagConfigByte.class);
        nbtTagConfigs.register(NBTTagConfigByteArray.class);
        nbtTagConfigs.register(NBTTagConfigShort.class);
        nbtTagConfigs.register(NBTTagConfigInt.class);
        nbtTagConfigs.register(NBTTagConfigIntArray.class);
        nbtTagConfigs.register(NBTTagConfigLong.class);
        nbtTagConfigs.register(NBTTagConfigFloat.class);
        nbtTagConfigs.register(NBTTagConfigDouble.class);
        // Lists
        nbtTagConfigs.register(NBTTagConfigListCompound.class);
        nbtTagConfigs.register(NBTTagConfigListInt.class);
        nbtTagConfigs.register(NBTTagConfigListIntArray.class);
        nbtTagConfigs.register(NBTTagConfigListLong.class);
        nbtTagConfigs.register(NBTTagConfigListFloat.class);
        nbtTagConfigs.register(NBTTagConfigListDouble.class);
        nbtTagConfigs.register(NBTTagConfigListString.class);

        getLogger().info("Register NBT Query Nodes");
        var nbtQueryNodes = getRegistries().getNbtQueryNodes();
        nbtQueryNodes.register(QueryNodeCompound.class);
        nbtQueryNodes.register(QueryNodeBoolean.class);
        //Primitives
        nbtQueryNodes.register(QueryNodeByte.class);
        nbtQueryNodes.register(QueryNodeShort.class);
        nbtQueryNodes.register(QueryNodeInt.class);
        nbtQueryNodes.register(QueryNodeLong.class);
        nbtQueryNodes.register(QueryNodeDouble.class);
        nbtQueryNodes.register(QueryNodeFloat.class);
        nbtQueryNodes.register(QueryNodeString.class);
        //Arrays
        nbtQueryNodes.register(QueryNodeByteArray.class);
        nbtQueryNodes.register(QueryNodeIntArray.class);
        //Lists
        nbtQueryNodes.register(QueryNodeListInt.class);
        nbtQueryNodes.register(QueryNodeListLong.class);
        nbtQueryNodes.register(QueryNodeListDouble.class);
        nbtQueryNodes.register(QueryNodeListFloat.class);
        nbtQueryNodes.register(QueryNodeListString.class);
        nbtQueryNodes.register(QueryNodeListCompound.class);

        // Register GUI things
        var guiComponents = getRegistries().getGuiComponents();
        guiComponents.register(ButtonImpl.class);
        guiComponents.register(StackInputSlotImpl.class);
        guiComponents.register(ComponentClusterImpl.class);

        var guiComponentBuilders = getRegistries().getGuiComponentBuilders();
        guiComponentBuilders.register(ButtonBuilderImpl.class);
        guiComponentBuilders.register(StackInputSlotBuilderImpl.class);
        guiComponentBuilders.register(ComponentClusterBuilderImpl.class);

        // Register the Registries to resolve type references in JSON
        KeyedTypeIdResolver.registerTypeRegistry(CustomItemData.class, registries.getCustomItemDataTypeRegistry());
        KeyedTypeIdResolver.registerTypeRegistry(Meta.class, nbtChecks);
        KeyedTypeIdResolver.registerTypeRegistry(Animator.class, particleAnimators);
        KeyedTypeIdResolver.registerTypeRegistry(Shape.class, particleShapes);
        KeyedTypeIdResolver.registerTypeRegistry(Timer.class, particleTimers);
        KeyedTypeIdResolver.registerTypeRegistry((Class<Action<?>>) (Object) Action.class, customItemActions);
        KeyedTypeIdResolver.registerTypeRegistry((Class<Event<?>>) (Object) Event.class, customItemEvents);
        KeyedTypeIdResolver.registerTypeRegistry(Operator.class, operators);
        KeyedTypeIdResolver.registerTypeRegistry((Class<ValueProvider<?>>) (Object) ValueProvider.class, valueProviders);
        KeyedTypeIdResolver.registerTypeRegistry((Class<QueryNode<?>>) (Object) QueryNode.class, nbtQueryNodes);
        KeyedTypeIdResolver.registerTypeRegistry(CustomBlockData.class, customBlockData);
        KeyedTypeIdResolver.registerTypeRegistry(CustomPlayerData.class, registries.getCustomPlayerData());
        KeyedTypeIdResolver.registerTypeRegistry((Class<ComponentBuilder<?, ?>>) (Object) ComponentBuilder.class, guiComponentBuilders);
    }

    public void enable() {
        this.api.initialize();
        console.info("Minecraft version: " + ServerVersion.getVersion().getVersion());
        console.info("WolfyUtils version: " + ServerVersion.getWUVersion().getVersion());
        console.info("Environment: " + WolfyUtils.getENVIRONMENT());
        this.config = new WUConfig(api.getConfigAPI(), plugin);
        compatibilityManager.init();

        //Load Language
        api.getLanguageAPI().loadLangFile("en_US");

        registerListeners();
        registerCommands();

        TestGUI testGUI = new TestGUI(this);
        testGUI.initWithConfig();

        CreativeModeTab.init();
    }

    public void disable() {
        api.getConfigAPI().saveConfigs();
        console.info("Save stored Custom Items");
    }

    protected void registerCommands() {
        registerDynamicCommands(
                new ChatActionCommand(this),
                new InputCommand(this),
                new InfoCommand(this),
                new SpawnParticleAnimationCommand(this),
                new SpawnParticleEffectCommand(this),
                new QueryDebugCommand(this),
                new GuiExampleCommand(this)
        );
    }

    protected void registerDynamicCommands(Command... cmds) {
        CommandMap commandMap = null;
        try {
            Field commandMapField = Bukkit.getServer().getClass().getDeclaredField("commandMap");
            commandMapField.setAccessible(true);
            commandMap = (CommandMap) commandMapField.get(Bukkit.getServer());
        } catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e) {
            e.printStackTrace();
        }
        if (commandMap == null) {
            getLogger().severe("Failed to register Commands: Failed to access CommandMap!");
            return;
        }
        for (Command cmd : cmds) {
            commandMap.register("wolfyutils", cmd);
        }
    }

    private void registerListeners() {
        Bukkit.getPluginManager().registerEvents(new BukkitChat.ChatListener(), plugin);
        Bukkit.getPluginManager().registerEvents(new CustomDurabilityListener(this), plugin);
        Bukkit.getPluginManager().registerEvents(new CustomParticleListener(this), plugin);
        Bukkit.getPluginManager().registerEvents(new CustomItemPlayerListener(this), plugin);
        Bukkit.getPluginManager().registerEvents(new EquipListener(this), plugin);
        Bukkit.getPluginManager().registerEvents(new PlayerListener(), plugin);
        Bukkit.getPluginManager().registerEvents(new GUIInventoryListener(), plugin);
        Bukkit.getPluginManager().registerEvents(new PersistentStorageListener(this), plugin);
        Bukkit.getPluginManager().registerEvents(new CustomItemDataListener(this), plugin);
    }

    /**
     * Handles JUnit test startup
     */
    private void onJUnitTests() {
        registerCommands();
    }

    public MessageHandler getMessageHandler() {
        return messageHandler;
    }

    public MessageFactory getMessageFactory() {
        return messageFactory;
    }

    @Override
    public BukkitChat getChat() {
        return api.getChat();
    }

    @Override
    public <M extends ObjectMapper> M applyWolfyUtilsJsonMapperModules(M mapper) {
        mapper.registerModules(jsonMapperModules);
        return mapper;
    }

    public PersistentStorage getPersistentStorage() {
        return persistentStorage;
    }

    /**
     * Check if the specific class exists.
     *
     * @param path The path to the class to check for.
     * @return If the class exists.
     */
    public static boolean hasClass(String path) {
        if (classes.containsKey(path)) {
            return classes.get(path);
        }
        try {
            Class.forName(path);
            classes.put(path, true);
            return true;
        } catch (Exception e) {
            classes.put(path, false);
            return false;
        }
    }

    public WolfyCoreBootstrap getPlugin() {
        return plugin;
    }
}


