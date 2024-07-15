package me.wolfyscript.utilities.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.wolfyscript.jackson.dataformat.hocon.HoconMapper;
import com.wolfyscript.utilities.bukkit.WolfyUtilsBukkit;
import com.wolfyscript.utilities.bukkit.chat.ChatImpl;
import com.wolfyscript.utilities.bukkit.items.CustomItemBlockData;
import com.wolfyscript.utilities.bukkit.items.CustomItemData;
import com.wolfyscript.utilities.bukkit.listeners.EquipListener;
import com.wolfyscript.utilities.bukkit.listeners.GUIInventoryListener;
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
import com.wolfyscript.utilities.bukkit.nms.item.crafting.FunctionalRecipeGenerator;
import com.wolfyscript.utilities.bukkit.persistent.PersistentStorage;
import com.wolfyscript.utilities.bukkit.persistent.player.CustomPlayerData;
import com.wolfyscript.utilities.bukkit.persistent.player.PlayerParticleEffectData;
import com.wolfyscript.utilities.bukkit.persistent.world.CustomBlockData;
import com.wolfyscript.utilities.bukkit.world.items.reference.BukkitStackIdentifier;
import com.wolfyscript.utilities.bukkit.world.items.reference.StackIdentifier;
import com.wolfyscript.utilities.bukkit.world.items.reference.WolfyUtilsStackIdentifier;
import com.wolfyscript.utilities.common.WolfyCore;
import com.wolfyscript.utilities.bukkit.commands.ChatActionCommand;
import com.wolfyscript.utilities.bukkit.commands.InfoCommand;
import com.wolfyscript.utilities.bukkit.commands.InputCommand;
import com.wolfyscript.utilities.bukkit.commands.QueryDebugCommand;
import com.wolfyscript.utilities.bukkit.commands.SpawnParticleAnimationCommand;
import com.wolfyscript.utilities.bukkit.commands.SpawnParticleEffectCommand;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.logging.Level;
import me.wolfyscript.utilities.api.console.Console;
import me.wolfyscript.utilities.api.inventory.custom_items.CustomData;
import me.wolfyscript.utilities.api.inventory.custom_items.CustomItem;
import me.wolfyscript.utilities.api.inventory.custom_items.actions.Action;
import me.wolfyscript.utilities.api.inventory.custom_items.actions.ActionCommand;
import me.wolfyscript.utilities.api.inventory.custom_items.actions.ActionParticleAnimation;
import me.wolfyscript.utilities.api.inventory.custom_items.actions.ActionSound;
import me.wolfyscript.utilities.api.inventory.custom_items.actions.Event;
import me.wolfyscript.utilities.api.inventory.custom_items.actions.EventPlayerConsumeItem;
import me.wolfyscript.utilities.api.inventory.custom_items.actions.EventPlayerInteract;
import me.wolfyscript.utilities.api.inventory.custom_items.actions.EventPlayerInteractAtEntity;
import me.wolfyscript.utilities.api.inventory.custom_items.actions.EventPlayerInteractEntity;
import me.wolfyscript.utilities.api.inventory.custom_items.actions.EventPlayerItemBreak;
import me.wolfyscript.utilities.api.inventory.custom_items.actions.EventPlayerItemDamage;
import me.wolfyscript.utilities.api.inventory.custom_items.actions.EventPlayerItemDrop;
import me.wolfyscript.utilities.api.inventory.custom_items.actions.EventPlayerItemHandSwap;
import me.wolfyscript.utilities.api.inventory.custom_items.actions.EventPlayerItemHeld;
import me.wolfyscript.utilities.api.inventory.custom_items.meta.AttributesModifiersMeta;
import me.wolfyscript.utilities.api.inventory.custom_items.meta.CustomDamageMeta;
import me.wolfyscript.utilities.api.inventory.custom_items.meta.CustomDurabilityMeta;
import me.wolfyscript.utilities.api.inventory.custom_items.meta.CustomItemTagMeta;
import me.wolfyscript.utilities.api.inventory.custom_items.meta.CustomModelDataMeta;
import me.wolfyscript.utilities.api.inventory.custom_items.meta.DamageMeta;
import me.wolfyscript.utilities.api.inventory.custom_items.meta.EnchantMeta;
import me.wolfyscript.utilities.api.inventory.custom_items.meta.FlagsMeta;
import me.wolfyscript.utilities.api.inventory.custom_items.meta.LoreMeta;
import me.wolfyscript.utilities.api.inventory.custom_items.meta.Meta;
import me.wolfyscript.utilities.api.inventory.custom_items.meta.NameMeta;
import me.wolfyscript.utilities.api.inventory.custom_items.meta.PlayerHeadMeta;
import me.wolfyscript.utilities.api.inventory.custom_items.meta.PotionMeta;
import me.wolfyscript.utilities.api.inventory.custom_items.meta.RepairCostMeta;
import me.wolfyscript.utilities.api.inventory.custom_items.meta.UnbreakableMeta;
import me.wolfyscript.utilities.api.inventory.custom_items.references.APIReference;
import me.wolfyscript.utilities.api.inventory.custom_items.references.VanillaRef;
import me.wolfyscript.utilities.api.inventory.custom_items.references.WolfyUtilitiesRef;
import me.wolfyscript.utilities.api.inventory.gui.cache.CustomCache;
import me.wolfyscript.utilities.compatibility.CompatibilityManager;
import me.wolfyscript.utilities.compatibility.CompatibilityManagerBukkit;
import me.wolfyscript.utilities.main.configs.WUConfig;
import me.wolfyscript.utilities.messages.MessageFactory;
import me.wolfyscript.utilities.messages.MessageHandler;
import me.wolfyscript.utilities.registry.Registries;
import me.wolfyscript.utilities.util.eval.operators.BoolOperatorConst;
import me.wolfyscript.utilities.util.eval.operators.ComparisonOperatorEqual;
import me.wolfyscript.utilities.util.eval.operators.ComparisonOperatorGreater;
import me.wolfyscript.utilities.util.eval.operators.ComparisonOperatorGreaterEqual;
import me.wolfyscript.utilities.util.eval.operators.ComparisonOperatorLess;
import me.wolfyscript.utilities.util.eval.operators.ComparisonOperatorLessEqual;
import me.wolfyscript.utilities.util.eval.operators.ComparisonOperatorNotEqual;
import me.wolfyscript.utilities.util.eval.operators.LogicalOperatorAnd;
import me.wolfyscript.utilities.util.eval.operators.LogicalOperatorNot;
import me.wolfyscript.utilities.util.eval.operators.LogicalOperatorOr;
import me.wolfyscript.utilities.util.eval.operators.Operator;
import me.wolfyscript.utilities.util.eval.value_providers.ValueProvider;
import me.wolfyscript.utilities.util.eval.value_providers.ValueProviderConditioned;
import me.wolfyscript.utilities.util.eval.value_providers.ValueProviderFloatConst;
import me.wolfyscript.utilities.util.eval.value_providers.ValueProviderFloatVar;
import me.wolfyscript.utilities.util.eval.value_providers.ValueProviderIntegerConst;
import me.wolfyscript.utilities.util.eval.value_providers.ValueProviderIntegerVar;
import me.wolfyscript.utilities.util.eval.value_providers.ValueProviderStringConst;
import me.wolfyscript.utilities.util.eval.value_providers.ValueProviderStringVar;
import me.wolfyscript.utilities.util.inventory.CreativeModeTab;
import me.wolfyscript.utilities.util.json.jackson.JacksonUtil;
import me.wolfyscript.utilities.util.json.jackson.KeyedTypeIdResolver;
import me.wolfyscript.utilities.util.json.jackson.annotations.OptionalKeyReference;
import me.wolfyscript.utilities.util.json.jackson.annotations.OptionalValueDeserializer;
import me.wolfyscript.utilities.util.json.jackson.annotations.OptionalValueSerializer;
import me.wolfyscript.utilities.util.json.jackson.serialization.APIReferenceSerialization;
import me.wolfyscript.utilities.util.json.jackson.serialization.ColorSerialization;
import me.wolfyscript.utilities.util.json.jackson.serialization.DustOptionsSerialization;
import me.wolfyscript.utilities.util.json.jackson.serialization.ItemStackSerialization;
import me.wolfyscript.utilities.util.json.jackson.serialization.LocationSerialization;
import me.wolfyscript.utilities.util.json.jackson.serialization.PotionEffectSerialization;
import me.wolfyscript.utilities.util.json.jackson.serialization.PotionEffectTypeSerialization;
import me.wolfyscript.utilities.util.json.jackson.serialization.VectorSerialization;
import me.wolfyscript.utilities.util.particles.animators.Animator;
import me.wolfyscript.utilities.util.particles.animators.AnimatorBasic;
import me.wolfyscript.utilities.util.particles.animators.AnimatorCircle;
import me.wolfyscript.utilities.util.particles.animators.AnimatorShape;
import me.wolfyscript.utilities.util.particles.animators.AnimatorSphere;
import me.wolfyscript.utilities.util.particles.animators.AnimatorVectorPath;
import me.wolfyscript.utilities.util.particles.shapes.Shape;
import me.wolfyscript.utilities.util.particles.shapes.ShapeCircle;
import me.wolfyscript.utilities.util.particles.shapes.ShapeComplexCompound;
import me.wolfyscript.utilities.util.particles.shapes.ShapeComplexRotation;
import me.wolfyscript.utilities.util.particles.shapes.ShapeCube;
import me.wolfyscript.utilities.util.particles.shapes.ShapeIcosahedron;
import me.wolfyscript.utilities.util.particles.shapes.ShapeSphere;
import me.wolfyscript.utilities.util.particles.shapes.ShapeSquare;
import me.wolfyscript.utilities.util.particles.timer.Timer;
import me.wolfyscript.utilities.util.particles.timer.TimerLinear;
import me.wolfyscript.utilities.util.particles.timer.TimerPi;
import me.wolfyscript.utilities.util.particles.timer.TimerRandom;
import me.wolfyscript.utilities.util.version.ServerVersion;
import me.wolfyscript.utilities.util.world.WorldUtils;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import org.bstats.bukkit.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandMap;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.reflections.Reflections;
import org.reflections.scanners.Scanners;
import org.reflections.util.ConfigurationBuilder;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This abstract class is the actual core of the plugin (This class is being extended by the plugin instance).<br>
 * <p>
 * It provides access to internal functionality like {@link Registries}, {@link CompatibilityManagerBukkit}, and of course the creation of the API instance.<br>
 * <p>
 * To get an instance of the API ({@link WolfyUtilities}) for your plugin you need one of the following methods. <br>
 * <ul>
 *     <li>{@link #getAPI(Plugin)} - Simple method to get your instance. Only use this in your <strong>onEnable()</strong></li>
 *     <li>{@link #getAPI(Plugin, boolean)} - Specify if it should init Event Listeners. Can be used inside the onLoad(), or plugin constructor, if set to false; Else only use this in your <strong>onEnable()</strong></li>
 *     <li>{@link #getAPI(Plugin, Class)} - Specify the type of your {@link CustomCache}. Can be used inside the onLoad(), or plugin constructor.</li>
 * </ul>
 * </p>
 */
public abstract class WolfyUtilCore extends JavaPlugin implements WolfyCore {

    //Static reference to the instance of this class.
    private static WolfyUtilCore instance;
    public static final String NAME = "wolfyutils";

    private final CompatibilityManager compatibilityManager;
    private Metrics metrics;
    private final Console console;
    protected Reflections reflections;
    protected final Map<String, WolfyUtilities> wolfyUtilsInstances = new HashMap<>();
    protected final WolfyUtilities api;
    protected final Registries registries;
    private final List<SimpleModule> jsonMapperModules = new ArrayList<>();
    private WUConfig config;

    private final MessageHandler messageHandler;
    private final MessageFactory messageFactory;
    private final PersistentStorage persistentStorage;
    private final FunctionalRecipeGenerator functionalRecipeGenerator;

    protected WolfyUtilCore() {
        super();
        if (instance == null && getClass().getPackageName().startsWith("com.wolfyscript.utilities")) {
            instance = this;
        } else {
            throw new IllegalArgumentException("This constructor can only be called by WolfyUtilities itself!");
        }
        this.api = getAPI(this);
        ServerVersion.setWUVersion(getDescription().getVersion());
        this.registries = new Registries(this);
        this.reflections = initReflections();
        this.console = api.getConsole();
        this.compatibilityManager = createCompatibilityManager();
        this.messageHandler = new MessageHandler(this);
        this.messageFactory = new MessageFactory(this);
        this.persistentStorage = new PersistentStorage(this);
        this.functionalRecipeGenerator = FunctionalRecipeGenerator.create(this);
        this.config = new WUConfig(api.getConfigAPI(), this);

        // Data that needs to be registered
        getLogger().info("Register Default StackIdentifiers");
        var stackIdentifierParsers = getRegistries().getStackIdentifierParsers();
        stackIdentifierParsers.register(new BukkitStackIdentifier.Parser());
        stackIdentifierParsers.register(new WolfyUtilsStackIdentifier.Parser());

    }

    protected abstract CompatibilityManager createCompatibilityManager();

    private Reflections initReflections() {
        return new Reflections(new ConfigurationBuilder().forPackages("me.wolfyscript", "com.wolfyscript").addClassLoaders(getClassLoader()).addScanners(Scanners.TypesAnnotated, Scanners.SubTypes, Scanners.Resources));
    }

    @Override
    public void onLoad() {
        getLogger().setLevel(Level.ALL);

        functionalRecipeGenerator.generateRecipeClasses();

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
        // APIReference Deserializer
        APIReferenceSerialization.create(module);
        // Serializer for the old CustomData
        module.addSerializer(CustomData.DeprecatedCustomDataWrapper.class, new CustomData.Serializer());

        // Add module to WU Modules and register it to the old JacksonUtil.
        jsonMapperModules.add(module);
        JacksonUtil.registerModule(module);

        // De-/Serializer Modifiers that handle type references in JSON
        var keyReferenceModule = new SimpleModule();
        keyReferenceModule.setSerializerModifier(new OptionalKeyReference.SerializerModifier());
        keyReferenceModule.setDeserializerModifier(new OptionalKeyReference.DeserializerModifier());
        jsonMapperModules.add(keyReferenceModule);
        JacksonUtil.registerModule(keyReferenceModule);

        var valueReferenceModule = new SimpleModule();
        valueReferenceModule.setSerializerModifier(new OptionalValueSerializer.SerializerModifier());
        valueReferenceModule.setDeserializerModifier(new OptionalValueDeserializer.DeserializerModifier());
        jsonMapperModules.add(valueReferenceModule);
        JacksonUtil.registerModule(valueReferenceModule);

        // Create Global WUCore Mapper and apply modules
        api.getJacksonMapperUtil().setGlobalMapper(applyWolfyUtilsJsonMapperModules(new HoconMapper()));

        var stackIdentifiers = getRegistries().getStackIdentifierTypeRegistry();
        stackIdentifiers.register(BukkitStackIdentifier.class);
        stackIdentifiers.register(WolfyUtilsStackIdentifier.class);

        // Initialise all the Registers
        getLogger().info("Register JSON Operators");
        var operators = getRegistries().getOperators();
        operators.register(BoolOperatorConst.KEY, BoolOperatorConst.class);
        operators.register(ComparisonOperatorEqual.KEY, ComparisonOperatorEqual.class);
        operators.register(ComparisonOperatorNotEqual.KEY, ComparisonOperatorNotEqual.class);
        operators.register(ComparisonOperatorGreater.KEY, ComparisonOperatorGreater.class);
        operators.register(ComparisonOperatorGreaterEqual.KEY, ComparisonOperatorGreaterEqual.class);
        operators.register(ComparisonOperatorLess.KEY, ComparisonOperatorLess.class);
        operators.register(ComparisonOperatorLessEqual.KEY, ComparisonOperatorLessEqual.class);
        operators.register(LogicalOperatorAnd.KEY, LogicalOperatorAnd.class);
        operators.register(LogicalOperatorOr.KEY, LogicalOperatorOr.class);
        operators.register(LogicalOperatorNot.KEY, LogicalOperatorNot.class);

        getLogger().info("Register JSON Value Providers");
        var valueProviders = getRegistries().getValueProviders();
        valueProviders.register(ValueProviderConditioned.KEY, (Class<ValueProviderConditioned<?>>)(Object) ValueProviderConditioned.class);
        valueProviders.register(ValueProviderIntegerConst.KEY, ValueProviderIntegerConst.class);
        valueProviders.register(ValueProviderIntegerVar.KEY, ValueProviderIntegerVar.class);
        valueProviders.register(ValueProviderFloatConst.KEY, ValueProviderFloatConst.class);
        valueProviders.register(ValueProviderFloatVar.KEY, ValueProviderFloatVar.class);
        valueProviders.register(ValueProviderStringConst.KEY, ValueProviderStringConst.class);
        valueProviders.register(ValueProviderStringVar.KEY, ValueProviderStringVar.class);

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

        getLogger().info("Register NBT Query Nodes");
        var nbtQueryNodes = getRegistries().getNbtQueryNodes();
        nbtQueryNodes.register(QueryNodeCompound.TYPE, QueryNodeCompound.class);
        nbtQueryNodes.register(QueryNodeBoolean.TYPE, QueryNodeBoolean.class);
        //Primitives
        nbtQueryNodes.register(QueryNodeByte.TYPE, QueryNodeByte.class);
        nbtQueryNodes.register(QueryNodeShort.TYPE, QueryNodeShort.class);
        nbtQueryNodes.register(QueryNodeInt.TYPE, QueryNodeInt.class);
        nbtQueryNodes.register(QueryNodeLong.TYPE, QueryNodeLong.class);
        nbtQueryNodes.register(QueryNodeDouble.TYPE, QueryNodeDouble.class);
        nbtQueryNodes.register(QueryNodeFloat.TYPE, QueryNodeFloat.class);
        nbtQueryNodes.register(QueryNodeString.TYPE, QueryNodeString.class);
        //Arrays
        nbtQueryNodes.register(QueryNodeByteArray.TYPE, QueryNodeByteArray.class);
        nbtQueryNodes.register(QueryNodeIntArray.TYPE, QueryNodeIntArray.class);
        //Lists
        nbtQueryNodes.register(QueryNodeListInt.TYPE, QueryNodeListInt.class);
        nbtQueryNodes.register(QueryNodeListLong.TYPE, QueryNodeListLong.class);
        nbtQueryNodes.register(QueryNodeListDouble.TYPE, QueryNodeListDouble.class);
        nbtQueryNodes.register(QueryNodeListFloat.TYPE, QueryNodeListFloat.class);
        nbtQueryNodes.register(QueryNodeListString.TYPE, QueryNodeListString.class);
        nbtQueryNodes.register(QueryNodeListCompound.TYPE, QueryNodeListCompound.class);

        // Register the Registries to resolve type references in JSON
        KeyedTypeIdResolver.registerTypeRegistry(StackIdentifier.class, registries.getStackIdentifierTypeRegistry());
        KeyedTypeIdResolver.registerTypeRegistry(CustomItemData.class, registries.getCustomItemDataTypeRegistry());
        KeyedTypeIdResolver.registerTypeRegistry(Meta.class, nbtChecks);
        KeyedTypeIdResolver.registerTypeRegistry(Animator.class, particleAnimators);
        KeyedTypeIdResolver.registerTypeRegistry(Shape.class, particleShapes);
        KeyedTypeIdResolver.registerTypeRegistry(Timer.class, particleTimers);
        KeyedTypeIdResolver.registerTypeRegistry((Class<Action<?>>)(Object) Action.class, customItemActions);
        KeyedTypeIdResolver.registerTypeRegistry((Class<Event<?>>)(Object) Event.class, customItemEvents);
        KeyedTypeIdResolver.registerTypeRegistry(Operator.class, operators);
        KeyedTypeIdResolver.registerTypeRegistry((Class<ValueProvider<?>>) (Object)ValueProvider.class, valueProviders);
        KeyedTypeIdResolver.registerTypeRegistry((Class<QueryNode<?>>) (Object)QueryNode.class, nbtQueryNodes);
        KeyedTypeIdResolver.registerTypeRegistry(CustomBlockData.class, customBlockData);
        KeyedTypeIdResolver.registerTypeRegistry(CustomPlayerData.class, registries.getCustomPlayerData());

    }

    @Override
    public void onEnable() {
        this.api.initialize();
        console.info("Minecraft version: " + ServerVersion.getVersion().getVersion());
        console.info("WolfyUtils version: " + ServerVersion.getWUVersion().getVersion());
        console.info("Environment: " + WolfyUtilities.getENVIRONMENT());
        compatibilityManager.init();

        // Register ReferenceParser
        console.info("Register API references");
        registerAPIReference(new VanillaRef.Parser());
        registerAPIReference(new WolfyUtilitiesRef.Parser());

        //Load Language
        api.getLanguageAPI().loadLangFile("en_US");

        if (!ServerVersion.isIsJUnitTest()) {
            this.metrics = new Metrics(this, 5114);

            WorldUtils.load();
            registerListeners();
            registerCommands();

            CreativeModeTab.init();
        }
    }

    @Override
    public void onDisable() {
        api.getConfigAPI().saveConfigs();
        console.info("Save stored Custom Items");
    }

    private void registerListeners() {
        Bukkit.getPluginManager().registerEvents(new ChatImpl.ChatListener(), this);
        Bukkit.getPluginManager().registerEvents(new CustomDurabilityListener(this), this);
        Bukkit.getPluginManager().registerEvents(new CustomParticleListener(this), this);
        Bukkit.getPluginManager().registerEvents(new CustomItemPlayerListener(this), this);
        Bukkit.getPluginManager().registerEvents(new EquipListener(this), this);
        Bukkit.getPluginManager().registerEvents(new PlayerListener(), this);
        Bukkit.getPluginManager().registerEvents(new GUIInventoryListener(), this);
        Bukkit.getPluginManager().registerEvents(new PersistentStorageListener(this), this);
        Bukkit.getPluginManager().registerEvents(new CustomItemDataListener(this), this);
    }

    protected void registerCommands() {
        registerDynamicCommands(
                new ChatActionCommand(this),
                new InputCommand(this),
                new InfoCommand(this),
                new QueryDebugCommand(this),
                new SpawnParticleAnimationCommand(this),
                new SpawnParticleEffectCommand(this)
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
            commandMap.register(NAME, cmd);
        }
    }

    /**
     * Gets an instance of the core plugin.
     * <strong>Only use this if necessary! First try to get the instance via your {@link WolfyUtilities} instance!</strong>
     *
     * @return The instance of the core.
     */
    @Deprecated
    public static WolfyUtilCore getInstance() {
        return instance;
    }

    @Override
    public WolfyUtilsBukkit getWolfyUtils() {
        return api;
    }

    /**
     * Gets the {@link Registries} object, that contains all info about available registries.
     *
     * @return The {@link Registries} object, to access registries.
     */
    public Registries getRegistries() {
        return registries;
    }

    /**
     * Gets the {@link CompatibilityManagerBukkit}, that manages the plugins compatibility features.
     *
     * @return The {@link CompatibilityManagerBukkit}.
     */
    public CompatibilityManager getCompatibilityManager() {
        return compatibilityManager;
    }

    public abstract BukkitAudiences getAdventure();

    /**
     * Gets the {@link Reflections} instance of the plugins' package.
     *
     * @return The Reflection of the plugins' package.
     */
    public Reflections getReflections() {
        return reflections;
    }

    /**
     * Gets or create the {@link WolfyUtilities} instance for the specified plugin.
     *
     * @param plugin The plugin to get the instance for.
     * @return The WolfyUtilities instance for the plugin.
     */
    public WolfyUtilities getAPI(Plugin plugin) {
        return getAPI(plugin, false);
    }

    /**
     * Gets or create the {@link WolfyUtilities} instance for the specified plugin.<br>
     * In case init is enabled it will directly initialize the event listeners and possibly other things.<br>
     * <b>In case you disable init you need to run {@link WolfyUtilities#initialize()} inside your onEnable()!</b>
     *
     * @param plugin The plugin to get the instance for.
     * @param init   If it should directly initialize the APIs' events, etc. (They must be initialized later via {@link WolfyUtilities#initialize()})
     * @return The WolfyUtilities instance for the plugin.
     */
    public WolfyUtilities getAPI(Plugin plugin, boolean init) {
        return wolfyUtilsInstances.computeIfAbsent(plugin.getName(), s -> new WolfyUtilities(this, plugin, init));
    }

    /**
     * Gets or create the {@link WolfyUtilities} instance for the specified plugin.
     * This method also creates the InventoryAPI with the specified custom class of the {@link CustomCache}.<br>
     * <b>You need to run {@link WolfyUtilities#initialize()} inside your onEnable() </b> to register required events!
     *
     * @param plugin           The plugin to get the instance from.
     * @param customCacheClass The class of the custom cache you created. Must extend {@link CustomCache}
     * @return The WolfyUtilities instance for the plugin.
     */
    public WolfyUtilities getAPI(Plugin plugin, Class<? extends CustomCache> customCacheClass) {
        return wolfyUtilsInstances.computeIfAbsent(plugin.getName(), s -> new WolfyUtilities(this, plugin, customCacheClass));
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
     * Returns an unmodifiable List of all available {@link WolfyUtilities} instances.
     *
     * @return A list containing all the created API instances.
     */
    public List<WolfyUtilities> getAPIList() {
        return List.copyOf(wolfyUtilsInstances.values());
    }

    /**
     * Register a new {@link APIReference.Parser} that can parse ItemStacks and keys from another plugin to a usable {@link APIReference}
     *
     * @param parser an {@link APIReference.Parser} instance.
     * @see me.wolfyscript.utilities.api.inventory.custom_items.CustomItem#registerAPIReferenceParser(APIReference.Parser)
     */
    public void registerAPIReference(APIReference.Parser<?> parser) {
        if (parser instanceof VanillaRef.Parser || parser instanceof WolfyUtilitiesRef.Parser || config.isAPIReferenceEnabled(parser)) {
            CustomItem.registerAPIReferenceParser(parser);
        }
    }

    @NotNull
    @Override
    public WUConfig getConfig() {
        return config;
    }

    public MessageHandler getMessageHandler() {
        return messageHandler;
    }

    public MessageFactory getMessageFactory() {
        return messageFactory;
    }

    public PersistentStorage getPersistentStorage() {
        return persistentStorage;
    }

    public FunctionalRecipeGenerator getFunctionalRecipeGenerator() {
        return functionalRecipeGenerator;
    }

    @Override
    public <M extends ObjectMapper> M applyWolfyUtilsJsonMapperModules(M mapper) {
        mapper.registerModules(jsonMapperModules);
        return mapper;
    }

    public Console getConsole() {
        return console;
    }
}
