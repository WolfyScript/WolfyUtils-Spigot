package com.wolfyscript.utilities.bukkit;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.wolfyscript.jackson.dataformat.hocon.HoconMapper;
import com.wolfyscript.utilities.bukkit.chat.BukkitChat;
import com.wolfyscript.utilities.bukkit.commands.ChatActionCommand;
import com.wolfyscript.utilities.bukkit.commands.InfoCommand;
import com.wolfyscript.utilities.bukkit.commands.InputCommand;
import com.wolfyscript.utilities.bukkit.commands.QueryDebugCommand;
import com.wolfyscript.utilities.bukkit.commands.SpawnParticleAnimationCommand;
import com.wolfyscript.utilities.bukkit.commands.SpawnParticleEffectCommand;
import com.wolfyscript.utilities.bukkit.compatibility.CompatibilityManager;
import com.wolfyscript.utilities.bukkit.compatibility.CompatibilityManagerBukkit;
import com.wolfyscript.utilities.bukkit.config.WUConfig;
import com.wolfyscript.utilities.bukkit.console.Console;
import com.wolfyscript.utilities.bukkit.items.CustomData;
import com.wolfyscript.utilities.bukkit.items.CustomItem;
import com.wolfyscript.utilities.bukkit.items.CustomItemBlockData;
import com.wolfyscript.utilities.bukkit.items.CustomItemData;
import com.wolfyscript.utilities.bukkit.items.actions.Action;
import com.wolfyscript.utilities.bukkit.items.actions.ActionCommand;
import com.wolfyscript.utilities.bukkit.items.actions.ActionParticleAnimation;
import com.wolfyscript.utilities.bukkit.items.actions.ActionSound;
import com.wolfyscript.utilities.bukkit.items.actions.Event;
import com.wolfyscript.utilities.bukkit.items.actions.EventPlayerConsumeItem;
import com.wolfyscript.utilities.bukkit.items.actions.EventPlayerInteract;
import com.wolfyscript.utilities.bukkit.items.actions.EventPlayerInteractAtEntity;
import com.wolfyscript.utilities.bukkit.items.actions.EventPlayerInteractEntity;
import com.wolfyscript.utilities.bukkit.items.actions.EventPlayerItemBreak;
import com.wolfyscript.utilities.bukkit.items.actions.EventPlayerItemDamage;
import com.wolfyscript.utilities.bukkit.items.actions.EventPlayerItemDrop;
import com.wolfyscript.utilities.bukkit.items.actions.EventPlayerItemHandSwap;
import com.wolfyscript.utilities.bukkit.items.actions.EventPlayerItemHeld;
import com.wolfyscript.utilities.bukkit.items.meta.AttributesModifiersMeta;
import com.wolfyscript.utilities.bukkit.items.meta.CustomDamageMeta;
import com.wolfyscript.utilities.bukkit.items.meta.CustomDurabilityMeta;
import com.wolfyscript.utilities.bukkit.items.meta.CustomItemTagMeta;
import com.wolfyscript.utilities.bukkit.items.meta.CustomModelDataMeta;
import com.wolfyscript.utilities.bukkit.items.meta.DamageMeta;
import com.wolfyscript.utilities.bukkit.items.meta.EnchantMeta;
import com.wolfyscript.utilities.bukkit.items.meta.FlagsMeta;
import com.wolfyscript.utilities.bukkit.items.meta.LoreMeta;
import com.wolfyscript.utilities.bukkit.items.meta.Meta;
import com.wolfyscript.utilities.bukkit.items.meta.NameMeta;
import com.wolfyscript.utilities.bukkit.items.meta.PlayerHeadMeta;
import com.wolfyscript.utilities.bukkit.items.meta.PotionMeta;
import com.wolfyscript.utilities.bukkit.items.meta.RepairCostMeta;
import com.wolfyscript.utilities.bukkit.items.meta.UnbreakableMeta;
import com.wolfyscript.utilities.bukkit.items.references.APIReference;
import com.wolfyscript.utilities.bukkit.items.references.VanillaRef;
import com.wolfyscript.utilities.bukkit.items.references.WolfyUtilitiesRef;
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
import com.wolfyscript.utilities.common.WolfyUtils;
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
import com.wolfyscript.utilities.json.KeyedTypeIdResolver;
import com.wolfyscript.utilities.json.annotations.OptionalKeyReference;
import com.wolfyscript.utilities.json.annotations.OptionalValueDeserializer;
import com.wolfyscript.utilities.json.annotations.OptionalValueSerializer;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.wolfyscript.utilities.bukkit.network.messages.MessageFactory;
import com.wolfyscript.utilities.bukkit.network.messages.MessageHandler;
import me.wolfyscript.utilities.util.inventory.CreativeModeTab;
import me.wolfyscript.utilities.util.json.jackson.JacksonUtil;
import me.wolfyscript.utilities.util.json.jackson.serialization.APIReferenceSerialization;
import me.wolfyscript.utilities.util.json.jackson.serialization.ColorSerialization;
import me.wolfyscript.utilities.util.json.jackson.serialization.DustOptionsSerialization;
import me.wolfyscript.utilities.util.json.jackson.serialization.ItemStackSerialization;
import me.wolfyscript.utilities.util.json.jackson.serialization.LocationSerialization;
import me.wolfyscript.utilities.util.json.jackson.serialization.PotionEffectSerialization;
import me.wolfyscript.utilities.util.json.jackson.serialization.PotionEffectTypeSerialization;
import me.wolfyscript.utilities.util.json.jackson.serialization.VectorSerialization;
import com.wolfyscript.utilities.bukkit.particles.animators.Animator;
import com.wolfyscript.utilities.bukkit.particles.animators.AnimatorBasic;
import com.wolfyscript.utilities.bukkit.particles.animators.AnimatorCircle;
import com.wolfyscript.utilities.bukkit.particles.animators.AnimatorShape;
import com.wolfyscript.utilities.bukkit.particles.animators.AnimatorSphere;
import com.wolfyscript.utilities.bukkit.particles.animators.AnimatorVectorPath;
import com.wolfyscript.utilities.bukkit.particles.shapes.Shape;
import com.wolfyscript.utilities.bukkit.particles.shapes.ShapeCircle;
import com.wolfyscript.utilities.bukkit.particles.shapes.ShapeComplexCompound;
import com.wolfyscript.utilities.bukkit.particles.shapes.ShapeComplexRotation;
import com.wolfyscript.utilities.bukkit.particles.shapes.ShapeCube;
import com.wolfyscript.utilities.bukkit.particles.shapes.ShapeIcosahedron;
import com.wolfyscript.utilities.bukkit.particles.shapes.ShapeSphere;
import com.wolfyscript.utilities.bukkit.particles.shapes.ShapeSquare;
import com.wolfyscript.utilities.bukkit.particles.timer.Timer;
import com.wolfyscript.utilities.bukkit.particles.timer.TimerLinear;
import com.wolfyscript.utilities.bukkit.particles.timer.TimerPi;
import com.wolfyscript.utilities.bukkit.particles.timer.TimerRandom;
import me.wolfyscript.utilities.util.version.ServerVersion;
import me.wolfyscript.utilities.util.world.WorldUtils;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bstats.bukkit.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPluginLoader;
import org.jetbrains.annotations.NotNull;

/**
 * The core implementation of WolfyUtils.<br>
 * It manages the core plugin of WolfyUtils and there is only one instance of it.<br>
 *
 * If you want to use the plugin specific API, see {@link com.wolfyscript.utilities.common.WolfyUtils} & {@link WolfyUtilsBukkit}
 */
public final class WolfyCoreBukkit extends WolfyUtilCore {

    private static final Map<String, Boolean> classes = new HashMap<>();
    private final Console console;
    private Metrics metrics;
    private WUConfig config;
    private final MessageHandler messageHandler;
    private final MessageFactory messageFactory;
    private final CompatibilityManager compatibilityManager;
    private BukkitAudiences adventure;
    private PersistentStorage persistentStorage;
    private final List<SimpleModule> jsonMapperModules = new ArrayList<>();

    /**
     * Constructor invoked by Spigot when the plugin is loaded.
     */
    public WolfyCoreBukkit() {
        super();
        this.console = api.getConsole();
        api.getChat().setChatPrefix(Component.text("[", NamedTextColor.GRAY).append(Component.text("WU", NamedTextColor.AQUA)).append(Component.text("] ", NamedTextColor.DARK_GRAY)));
        this.messageHandler = new MessageHandler(this);
        this.messageFactory = new MessageFactory(this);
        this.compatibilityManager = new CompatibilityManagerBukkit(this);
        this.persistentStorage = new PersistentStorage(this);
    }

    /**
     * Constructor invoked by MockBukkit to mock the plugin.
     */
    private WolfyCoreBukkit(JavaPluginLoader loader, PluginDescriptionFile description, File dataFolder, File file) {
        super(loader, description, dataFolder, file);
        this.console = api.getConsole();
        api.getChat().setChatPrefix(Component.text("[", NamedTextColor.GRAY).append(Component.text("WU", NamedTextColor.AQUA)).append(Component.text("] ", NamedTextColor.DARK_GRAY)));
        this.messageHandler = new MessageHandler(this);
        this.messageFactory = new MessageFactory(this);
        this.compatibilityManager = new CompatibilityManagerBukkit(this);
    }

    /**
     * Gets an instance of the core plugin.
     * <strong>Only use this if necessary! First try to get the instance via your {@link WolfyUtilsBukkit} instance!</strong>
     *
     * @return The instance of the core.
     */
    @Deprecated
    public static WolfyCoreBukkit getInstance() {
        return (WolfyCoreBukkit) WolfyUtilCore.getInstance();
    }

    @Override
    public CompatibilityManager getCompatibilityManager() {
        return compatibilityManager;
    }

    @Override
    @NotNull
    public BukkitAudiences getAdventure() {
        if(this.adventure == null) {
            throw new IllegalStateException("Tried to access Adventure when the plugin was disabled!");
        }
        return this.adventure;
    }

    @Override
    public void onLoad() {
        getLogger().info("Generate Functional Recipes");
        FunctionalRecipeGenerator.generateRecipeClasses();

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
        valueProviders.register((Class<ValueProviderConditioned<?>>)(Object) ValueProviderConditioned.class);
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

        // Register the Registries to resolve type references in JSON
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
        console.info("Environment: " + WolfyUtils.getENVIRONMENT());
        this.adventure = BukkitAudiences.create(this);
        this.config = new WUConfig(api.getConfigAPI(), this);
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
        } else {
            onJUnitTests();
        }
    }

    /**
     * Handles JUnit test startup
     */
    private void onJUnitTests() {
        WorldUtils.load();

        registerCommands();
    }

    @Override
    public void registerAPIReference(APIReference.Parser<?> parser) {
        if (parser instanceof VanillaRef.Parser || parser instanceof WolfyUtilitiesRef.Parser || config.isAPIReferenceEnabled(parser)) {
            CustomItem.registerAPIReferenceParser(parser);
        }
    }

    @Override
    public void onDisable() {
        if(this.adventure != null) {
            this.adventure.close();
            this.adventure = null;
        }
        api.getConfigAPI().saveConfigs();
        console.info("Save stored Custom Items");
    }

    private void registerListeners() {
        Bukkit.getPluginManager().registerEvents(new BukkitChat.ChatListener(), this);
        Bukkit.getPluginManager().registerEvents(new CustomDurabilityListener(this), this);
        Bukkit.getPluginManager().registerEvents(new CustomParticleListener(this), this);
        Bukkit.getPluginManager().registerEvents(new CustomItemPlayerListener(this), this);
        Bukkit.getPluginManager().registerEvents(new EquipListener(this), this);
        Bukkit.getPluginManager().registerEvents(new PlayerListener(), this);
        Bukkit.getPluginManager().registerEvents(new GUIInventoryListener(), this);
        Bukkit.getPluginManager().registerEvents(new PersistentStorageListener(this), this);
        Bukkit.getPluginManager().registerEvents(new CustomItemDataListener(this), this);
    }

    private void registerCommands() {
        Bukkit.getServer().getPluginCommand("wolfyutils").setExecutor(new InfoCommand(this));
        Bukkit.getServer().getPluginCommand("particle_effect").setExecutor(new SpawnParticleEffectCommand(api));
        Bukkit.getServer().getPluginCommand("particle_animation").setExecutor(new SpawnParticleAnimationCommand(api));
        Bukkit.getServer().getPluginCommand("wui").setExecutor(new InputCommand(this));
        Bukkit.getServer().getPluginCommand("wui").setTabCompleter(new InputCommand(this));
        Bukkit.getServer().getPluginCommand("wua").setExecutor(new ChatActionCommand());

        Bukkit.getServer().getPluginCommand("query_item").setExecutor(new QueryDebugCommand(this));
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
}
