package com.wolfyscript.utilities.bukkit.nms.item.crafting;

import com.wolfyscript.utilities.NamespacedKey;
import com.wolfyscript.utilities.bukkit.WolfyCoreBukkit;
import com.wolfyscript.utilities.bukkit.WolfyCoreImpl;
import com.wolfyscript.utilities.bukkit.nms.Reflection;
import com.wolfyscript.utilities.versioning.MinecraftVersion;
import com.wolfyscript.utilities.versioning.ServerVersion;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtConstructor;
import javassist.CtField;
import javassist.CtNewConstructor;
import javassist.CtNewMethod;
import javassist.LoaderClassPath;
import javassist.Modifier;
import javassist.NotFoundException;
import javassist.bytecode.SignatureAttribute;
import org.apache.commons.lang3.text.StrSubstitutor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice;

import static java.util.Map.entry;

public class FunctionalRecipeGenerator {

    private static final String GENERATOR_PACKAGE = FunctionalRecipeGenerator.class.getPackageName() + "";

    /* ******************
     * Minecraft classes
     * ******************/
    private static final Class<?> CONTAINER_CLASS;
    private static final Class<?> CRAFTING_CONTAINER_CLASS;
    private static final Class<?> WORLD_CLASS;
    private static final Class<?> ITEMSTACK_CLASS;
    private static final Class<?> RECIPE_CLASS;
    private static final Class<?> RECIPE_ITEMSTACK_CLASS;
    private static final Class<?> RESOURCE_KEY_CLASS;
    private static final Class<?> NON_NULL_LIST_CLASS;
    private static final Class<?> RECIPE_MANAGER_CLASS;
    private static final Class<?> MINECRAFT_SERVER_CLASS;
    private static final Class<?> COOKING_BOOK_CATEGORY;
    private static final Class<?> CRAFTING_BOOK_CATEGORY;
    private static final Class<?> CUSTOM_REGISTRY_CLASS;

    // Recipe classes
    private static final Class<?> RECIPE_CAMPFIRE_CLASS;
    private static final Class<?> RECIPE_FURNACE_CLASS;
    private static final Class<?> RECIPE_SMOKING_CLASS;
    private static final Class<?> RECIPE_BLASTING_CLASS;
    private static final Class<?> RECIPE_CRAFTING_SHAPED_CLASS;
    private static final Class<?> RECIPE_CRAFTING_SHAPELESS_CLASS;

    // Fields
    private static final Field ITEMSTACK_EMPTY_CONST;
    private static final Field RECIPE_ITEMSTACK_EMPTY_CONST;
    private static final Field COOKING_BOOK_CATEGORY_CODEC;
    private static final Field CRAFTING_BOOK_CATEGORY_CODEC;

    // Methods
    private static final Method MINECRAFT_SERVER_GET_RECIPE_MANAGER_METHOD;
    private static final Method MINECRAFT_SERVER_STATIC_GETTER_METHOD;
    private static final Method RECIPE_MATCHES_METHOD;
    private static final Method RECIPE_ASSEMBLE_METHOD;
    private static final Method RECIPE_GET_REMAINING_ITEMS_METHOD;
    private static final Method RECIPE_MANAGER_ADD_RECIPE_METHOD;
    private static final Method NONNULLLIST_WITH_SIZE_METHOD;

    /* ******************
     * CraftBukkit classes
     * ******************/
    private static final Class<?> CRAFT_ITEMSTACK_CLASS;
    private static final Class<?> CRAFT_INVENTORY_CLASS;
    private static final Class<?> CRAFT_INVENTORY_CRAFTING_CLASS;

    // Fields

    // Methods
    private static final Method CRAFT_ITEMSTACK_TO_NMS;

    /* ******************
     * Static Objects used to access internal values
     * ******************/
    private static final Object MINECRAFT_SERVER;

    /* ******************
     * Caching and other fields
     * ******************/
    private static final Map<FunctionalRecipeType, Class<?>> GENERATED_RECIPES = new HashMap<>();

    static {
        CONTAINER_CLASS = Reflection.getNMS("world", "IInventory");
        CRAFTING_CONTAINER_CLASS = Reflection.getNMS("world.inventory", "InventoryCrafting");
        WORLD_CLASS = Reflection.getNMS("world.level", "World");
        ITEMSTACK_CLASS = Reflection.getNMS("world.item", "ItemStack");
        RECIPE_CLASS = Reflection.getNMS("world.item.crafting", "IRecipe");
        RECIPE_ITEMSTACK_CLASS = Reflection.getNMS("world.item.crafting", "RecipeItemStack");
        RESOURCE_KEY_CLASS = Reflection.getNMS("resources", "MinecraftKey");
        NON_NULL_LIST_CLASS = Reflection.getNMS("core", "NonNullList");
        RECIPE_MANAGER_CLASS = Reflection.getNMS("world.item.crafting", "CraftingManager");
        MINECRAFT_SERVER_CLASS = Reflection.getNMS("server", "MinecraftServer");

        if (ServerVersion.isAfterOrEq(MinecraftVersion.of(1, 19, 3))) {
            COOKING_BOOK_CATEGORY = Reflection.getNMS("world.item.crafting", "CookingBookCategory");
            CRAFTING_BOOK_CATEGORY = Reflection.getNMS("world.item.crafting", "CraftingBookCategory");
        } else {
            COOKING_BOOK_CATEGORY = null;
            CRAFTING_BOOK_CATEGORY = null;
        }
        if (ServerVersion.isAfterOrEq(MinecraftVersion.of(1, 19, 4))) {
            CUSTOM_REGISTRY_CLASS = Reflection.getNMS("core", "IRegistryCustom");
        } else {
            CUSTOM_REGISTRY_CLASS = null;
        }

        RECIPE_CAMPFIRE_CLASS = Reflection.getNMS("world.item.crafting", "RecipeCampfire");
        RECIPE_FURNACE_CLASS = Reflection.getNMS("world.item.crafting", "FurnaceRecipe");
        RECIPE_SMOKING_CLASS = Reflection.getNMS("world.item.crafting", "RecipeSmoking");
        RECIPE_BLASTING_CLASS = Reflection.getNMS("world.item.crafting", "RecipeBlasting");
        RECIPE_CRAFTING_SHAPED_CLASS = Reflection.getNMS("world.item.crafting", "ShapedRecipes");
        RECIPE_CRAFTING_SHAPELESS_CLASS = Reflection.getNMS("world.item.crafting", "ShapelessRecipes");

        try {
            ITEMSTACK_EMPTY_CONST = ITEMSTACK_CLASS.getDeclaredField("b");
            RECIPE_ITEMSTACK_EMPTY_CONST = RECIPE_ITEMSTACK_CLASS.getDeclaredField("a");

            if (ServerVersion.isAfterOrEq(MinecraftVersion.of(1, 19, 3))) {
                COOKING_BOOK_CATEGORY_CODEC = COOKING_BOOK_CATEGORY.getDeclaredField("d");
                CRAFTING_BOOK_CATEGORY_CODEC = CRAFTING_BOOK_CATEGORY.getDeclaredField("e");
            } else {
                COOKING_BOOK_CATEGORY_CODEC = null;
                CRAFTING_BOOK_CATEGORY_CODEC = null;
            }
        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        }

        MINECRAFT_SERVER_STATIC_GETTER_METHOD = Reflection.getMethod(MINECRAFT_SERVER_CLASS, "getServer");
        MINECRAFT_SERVER_GET_RECIPE_MANAGER_METHOD = Arrays.stream(MINECRAFT_SERVER_CLASS.getMethods()).filter(method -> method.getReturnType().equals(RECIPE_MANAGER_CLASS)).findFirst().orElseGet(() -> Reflection.getMethod(MINECRAFT_SERVER_CLASS, "getCraftingManager"));
        NONNULLLIST_WITH_SIZE_METHOD = Reflection.getMethod(NON_NULL_LIST_CLASS, "a", Integer.TYPE, Object.class);
        RECIPE_MATCHES_METHOD = Reflection.getMethod(RECIPE_CLASS, "a", CONTAINER_CLASS, WORLD_CLASS);

        RECIPE_ASSEMBLE_METHOD = ServerVersion.isAfterOrEq(MinecraftVersion.of(1, 19, 4)) ?
                Reflection.getMethod(RECIPE_CLASS, "a", CONTAINER_CLASS, CUSTOM_REGISTRY_CLASS) :
                Reflection.getMethod(RECIPE_CLASS, "a", CONTAINER_CLASS);

        RECIPE_GET_REMAINING_ITEMS_METHOD = Reflection.getMethod(RECIPE_CLASS, Reflection.NMSMapping.of(
                        MinecraftVersion.of(1, 19, 4), "a")
                .orElse("b"), CONTAINER_CLASS);
        RECIPE_MANAGER_ADD_RECIPE_METHOD = Reflection.getMethod(RECIPE_MANAGER_CLASS, "addRecipe", RECIPE_CLASS);

        /* ******************
         * CraftBukkit classes
         * ******************/
        CRAFT_ITEMSTACK_CLASS = Reflection.getOBC("inventory.CraftItemStack");
        CRAFT_INVENTORY_CLASS = Reflection.getOBC("inventory.CraftInventory");
        CRAFT_INVENTORY_CRAFTING_CLASS = Reflection.getOBC("inventory.CraftInventoryCrafting");

        // Methods
        CRAFT_ITEMSTACK_TO_NMS = Reflection.getDeclaredMethod(CRAFT_ITEMSTACK_CLASS, "asNMSCopy", ItemStack.class);

        /* ******************
         * Static Objects used to access internal values
         * ******************/
        try {
            MINECRAFT_SERVER = MINECRAFT_SERVER_GET_RECIPE_MANAGER_METHOD.invoke(MINECRAFT_SERVER_STATIC_GETTER_METHOD.invoke(null));
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    private final WolfyCoreImpl core;
    private final Map<String, String> mappings;

    public FunctionalRecipeGenerator(WolfyCoreImpl core) {
        this.core = core;

        this.mappings = Map.ofEntries(
                // Java classes
                entry("Optional", Optional.class.getName()),
                entry("List", List.class.getName()),
                entry("ArrayList", ArrayList.class.getName()),

                // NMS
                entry("NMS_ItemStack", ITEMSTACK_CLASS.getName()),
                entry("NMS_ItemStack_Empty", ITEMSTACK_EMPTY_CONST.getName()),
                entry("Level", WORLD_CLASS.getName()),
                entry("ItemStack", ITEMSTACK_CLASS.getName()),
                entry("Container", CONTAINER_CLASS.getName()),
                entry("CraftingContainer", CRAFTING_CONTAINER_CLASS.getName()),
                entry("NonNullList", NON_NULL_LIST_CLASS.getName()),
                entry("NonNullList_Create", NONNULLLIST_WITH_SIZE_METHOD.getName()),
                entry("RecipeMatchesMethod", RECIPE_MATCHES_METHOD.getName()),
                entry("RecipeAssembleMethod", RECIPE_ASSEMBLE_METHOD.getName()),
                entry("RemainingItemsMethod", RECIPE_GET_REMAINING_ITEMS_METHOD.getName()),
                entry("Ingredient_Empty", RECIPE_ITEMSTACK_EMPTY_CONST.getName()),
                entry("Ingredient", RECIPE_ITEMSTACK_CLASS.getName()),
                entry("Ingredient_Choices",
                        // Minecraft 1.17+
                        Arrays.stream(RECIPE_ITEMSTACK_CLASS.getMethods()).filter(field -> field.getReturnType().equals(ITEMSTACK_CLASS.arrayType())).findFirst().map(method -> method.getName() + "()")
                                // Minecraft 1.16
                                .or(() -> Arrays.stream(RECIPE_ITEMSTACK_CLASS.getFields()).filter(field -> field.getType().equals(ITEMSTACK_CLASS.arrayType())).map(Field::getName).findFirst())
                                // Fallback
                                .orElse("choices")
                ),
                //// 1.19.4
                entry("1_19_4_AssembleRegistry", ServerVersion.isAfterOrEq(MinecraftVersion.of(1, 19, 4)) ? (", " + CUSTOM_REGISTRY_CLASS.getName() + " registry") : ""),
                entry("1_19_4_AssembleRegistrySuper", ServerVersion.isAfterOrEq(MinecraftVersion.of(1, 19, 4)) ? ", registry" : ""),

                // CraftBukkit
                entry("Material", Material.class.getName()),
                entry("RecipeChoice", RecipeChoice.class.getName()),
                entry("BukkitItemStack", ItemStack.class.getName()),
                entry("CraftItemStack", CRAFT_ITEMSTACK_CLASS.getName()),
                entry("CraftInventory", CRAFT_INVENTORY_CLASS.getName()),
                entry("CraftInventoryCrafting", CRAFT_INVENTORY_CRAFTING_CLASS.getName()),
                entry("resultInventory", "resultInventory")
        );
    }

    private String applyMappings(String value) {
        return StrSubstitutor.replace(value, mappings);
    }

    /**
     * Generates the internal FunctionalRecipe classes and caches them.<br>
     * Get them using {@link #getFunctionalRecipeClass(FunctionalRecipeType)}.<br>
     *
     * <b>Only the first invocation generates the classes! Any invocation afterwards does nothing.</b>
     */
    public void generateRecipeClasses() {
        if (!GENERATED_RECIPES.isEmpty()) return;
        try {
            ClassPool classPool = ClassPool.getDefault();
            classPool.appendClassPath(new LoaderClassPath(ClassLoader.getPlatformClassLoader()));
            classPool.appendClassPath(new LoaderClassPath(ClassLoader.getSystemClassLoader()));
            classPool.appendClassPath(new LoaderClassPath(WolfyCoreImpl.class.getClassLoader()));
            classPool.appendClassPath(new LoaderClassPath(MINECRAFT_SERVER_CLASS.getClassLoader()));
            classPool.importPackage("java.util");
            classPool.importPackage("org.bukkit");
            classPool.importPackage("net.minecraft");


            generateUtils(classPool);
            GENERATED_RECIPES.put(FunctionalRecipeType.CAMPFIRE, inject(classPool, RECIPE_CAMPFIRE_CLASS));
            GENERATED_RECIPES.put(FunctionalRecipeType.SMELTING, inject(classPool, RECIPE_FURNACE_CLASS));
            GENERATED_RECIPES.put(FunctionalRecipeType.SMOKING, inject(classPool, RECIPE_SMOKING_CLASS));
            GENERATED_RECIPES.put(FunctionalRecipeType.BLASTING, inject(classPool, RECIPE_BLASTING_CLASS));
            GENERATED_RECIPES.put(FunctionalRecipeType.CRAFTING_SHAPED, inject(classPool, RECIPE_CRAFTING_SHAPED_CLASS));
            GENERATED_RECIPES.put(FunctionalRecipeType.CRAFTING_SHAPELESS, inject(classPool, RECIPE_CRAFTING_SHAPELESS_CLASS));
        } catch (NotFoundException | CannotCompileException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Gets the class of the internal FunctionalRecipe for the specified {@link FunctionalRecipeType}
     *
     * @param type The type of the recipe
     * @return The class associated with the type; Null if not available
     */
    public Class<?> getFunctionalRecipeClass(FunctionalRecipeType type) {
        return GENERATED_RECIPES.get(type);
    }

    /**
     * Adds the FunctionalRecipe to the Minecraft RecipeManager.
     *
     * @param recipe The FunctionalRecipe to add to Minecraft.
     * @return True when the recipe was added successfully; False if an error occurred.
     */
    public boolean addRecipeToRecipeManager(FunctionalRecipe<?> recipe) {
        try {
            RECIPE_MANAGER_ADD_RECIPE_METHOD.invoke(MINECRAFT_SERVER, recipe);
            return true;
        } catch (IllegalAccessException | InvocationTargetException e) {
            return false;
        }
    }

    /**
     * Injects FunctionalRecipe into the specified recipe class.
     *
     * @param classPool
     * @param originalClass
     * @throws NotFoundException
     * @throws CannotCompileException
     */
    private Class<?> inject(ClassPool classPool, Class<?> originalClass) throws NotFoundException, CannotCompileException, IOException {
        final String funcRecipeClass = "Functional" + originalClass.getSimpleName();
        final CtClass generatedRecipeClass = classPool.makeClass(GENERATOR_PACKAGE + "." + funcRecipeClass);

        classPool.importPackage("net.minecraft.server." + Reflection.getVersion());
        classPool.importPackage(GENERATOR_PACKAGE);

        CtClass wrapperFuncRecipeInterface = classPool.get(FunctionalRecipe.class.getName());
        generatedRecipeClass.setInterfaces(new CtClass[]{wrapperFuncRecipeInterface});

        CtClass originalRecipeClass = classPool.getCtClass(originalClass.getName());
        generatedRecipeClass.setSuperclass(originalRecipeClass);

        // private final NamespacedKey recipeID;
        CtField recipeIDField = new CtField(classPool.get(NamespacedKey.class.getName()), "recipeID", generatedRecipeClass);
        recipeIDField.setModifiers(Modifier.setPrivate(Modifier.FINAL)); // private final
        generatedRecipeClass.addField(recipeIDField);
        // private final RecipeMatcher matcher;
        CtField matcherField = new CtField(classPool.get(RecipeMatcher.class.getName()), "matcher", generatedRecipeClass);
        matcherField.setModifiers(Modifier.setPrivate(Modifier.FINAL)); // private final
        generatedRecipeClass.addField(matcherField);
        // private final RecipeAssembler assembler;
        CtField assemblerField = new CtField(classPool.get(RecipeAssembler.class.getName()), "assembler", generatedRecipeClass);
        assemblerField.setModifiers(Modifier.setPrivate(Modifier.FINAL)); // private final
        generatedRecipeClass.addField(assemblerField);
        // private final RecipeAssembler assembler;
        CtField remainingItemsFunctionField = new CtField(classPool.get(RecipeRemainingItemsFunction.class.getName()), "remainingItems", generatedRecipeClass);
        remainingItemsFunctionField.setModifiers(Modifier.setPrivate(Modifier.FINAL)); // private final
        generatedRecipeClass.addField(remainingItemsFunctionField);

        generatedRecipeClass.addMethod(CtNewMethod.make(applyMappings("""
                public ${Optional} getMatcher() {
                    return ${Optional}.ofNullable(this.matcher);
                }
                """), generatedRecipeClass));
        generatedRecipeClass.addMethod(CtNewMethod.getter("getNamespacedKey", recipeIDField));
        generatedRecipeClass.addMethod(CtNewMethod.getter("getAssembler", assemblerField));
        generatedRecipeClass.addMethod(CtNewMethod.getter("getRemainingItemsFunction", remainingItemsFunctionField));

        // Matches the Recipe with either the custom or default check
        String matchesMethod = applyMappings("""
                public boolean ${RecipeMatchesMethod}(${Container} container, ${Level} level) {
                    if (getMatcher().isPresent()) {
                        if (level != null) {
                            return matches(ConversionUtils.containerToBukkit(container), level.getWorld());
                        }
                        return matches(ConversionUtils.containerToBukkit(container), null);
                    }
                    return super.${RecipeMatchesMethod}(container, level);
                }
                """);
        generatedRecipeClass.addMethod(CtNewMethod.make(matchesMethod, generatedRecipeClass));

        // Assemble method injection
        // In 1.19.4 we need to add the registry parameter. However, it is just ignored and not used. Seems like they are used in MC for the new Smithing recipes, but these recipes do not require it!
        String assembleMethod = applyMappings("""
                public ${NMS_ItemStack} ${RecipeAssembleMethod}(${Container} container${1_19_4_AssembleRegistry}) {
                    ${Optional} item = assemble(ConversionUtils.containerToBukkit(container)).map(new ConvertCraftItemStackToNMS());
                    if (item.isPresent()) {
                        return (${NMS_ItemStack}) item.get();
                    }
                    return super.${RecipeAssembleMethod}(container${1_19_4_AssembleRegistrySuper});
                }
                """);
        generatedRecipeClass.addMethod(CtNewMethod.make(assembleMethod, generatedRecipeClass));

        // Remaining Items method injection
        String remainingItemsMethod = applyMappings("""
                public ${NonNullList} ${RemainingItemsMethod}(${Container} container) {
                    ${Optional} nmsItems = getRemainingItems(ConversionUtils.containerToBukkit(container)).map(new ConvertRemainingItemsToNMS());
                    if (nmsItems.isPresent()) {
                        return (${NonNullList}) nmsItems.get();
                    }
                    return super.${RemainingItemsMethod}(container);
                }
                """);
        generatedRecipeClass.addMethod(CtNewMethod.make(remainingItemsMethod, generatedRecipeClass));

        // Create constructor
        // This adds the custom parameters and after that the super parameters.
        // Except the ResourceLocation parameter, which is replaced by the NamespacedKey parameter.
        for (Constructor<?> constructor : originalClass.getConstructors()) {
            Class<?>[] parameters = constructor.getParameterTypes();
            StringBuilder bodyBuilder = new StringBuilder("{\n    super(");
            StringBuilder signatureBuilder = new StringBuilder("public ");
            signatureBuilder.append(funcRecipeClass).append('(');

            // NamespacedKey var0,
            signatureBuilder.append(NamespacedKey.class.getName()).append(" var0").append(", ");
            // RecipeMatcher var1
            signatureBuilder.append(RecipeMatcher.class.getName()).append(" var1").append(", ");
            // RecipeAssembler var2
            signatureBuilder.append(RecipeAssembler.class.getName()).append(" var2").append(", ");
            // RecipeRemainingItemsFunction var3
            signatureBuilder.append(RecipeRemainingItemsFunction.class.getName()).append(" var3");

            for (int i = 0; i < parameters.length; i++) {
                String name = "var" + (i + 4);
                // , var<i>
                if (i != 0) {
                    bodyBuilder.append(", ");
                }
                if (parameters[i].equals(RESOURCE_KEY_CLASS)) {
                    // new ResourceLocation(var0.getNamespace(), var0.getKey())
                    bodyBuilder.append("new ").append(RESOURCE_KEY_CLASS.getName()).append("(");
                    bodyBuilder.append("var0.getNamespace(), var0.getKey())");
                    // Do not add it to the constructor parameters, since we use the NamespacedKey as the first parameter already!
                } else if (parameters[i].equals(RECIPE_ITEMSTACK_CLASS)) {
                    // Use Bukkit RecipeChoice in Constructor and convert to NMS
                    // ConversionUtils.recipeChoiceToNMS(var<i>, true)
                    bodyBuilder.append("ConversionUtils.recipeChoiceToNMS(").append(name).append(", true)");
                    // RecipeChoice var<i>
                    signatureBuilder.append(", ");
                    signatureBuilder.append(RecipeChoice.class.getName()).append(' ').append(name);
                } else if (parameters[i].equals(ITEMSTACK_CLASS)) {
                    // Use Bukkit ItemStack in the Constructor and convert to NMS
                    bodyBuilder.append(CRAFT_ITEMSTACK_CLASS.getName()).append(".").append(CRAFT_ITEMSTACK_TO_NMS.getName()).append("(").append(name).append(")");
                    // ItemStack var<i>
                    signatureBuilder.append(", ");
                    signatureBuilder.append(ItemStack.class.getName()).append(' ').append(name);
                } else if (parameters[i].equals(NON_NULL_LIST_CLASS)) {
                    // Use Bukkit RecipeChoices in Constructor and convert them to NMS
                    bodyBuilder.append("ConversionUtils.recipeChoicesToIngredients(").append(name).append(")");
                    // List var<i>
                    signatureBuilder.append(", ");
                    signatureBuilder.append(List.class.getName()).append(' ').append(name);
                } else if (ServerVersion.isAfterOrEq(MinecraftVersion.of(1, 19, 3)) && parameters[i].equals(COOKING_BOOK_CATEGORY)) {
                    // Use String in the constructor and find the category for it.
                    bodyBuilder.append("(").append(COOKING_BOOK_CATEGORY.getName()).append(")").append(COOKING_BOOK_CATEGORY.getName()).append(".").append(COOKING_BOOK_CATEGORY_CODEC.getName()).append(".a(").append(name).append(")");
                    // String var<i>
                    signatureBuilder.append(", ");
                    signatureBuilder.append("String ").append(name);
                } else if (ServerVersion.isAfterOrEq(MinecraftVersion.of(1, 19, 3)) && parameters[i].equals(CRAFTING_BOOK_CATEGORY)) {
                    // Use String in the constructor and find the category for it.
                    bodyBuilder.append("(").append(CRAFTING_BOOK_CATEGORY.getName()).append(")").append(CRAFTING_BOOK_CATEGORY.getName()).append(".").append(CRAFTING_BOOK_CATEGORY_CODEC.getName()).append(".a(").append(name).append(")");
                    // String var<i>
                    signatureBuilder.append(", ");
                    signatureBuilder.append("String ").append(name);
                } else {
                    bodyBuilder.append(name);
                    // <Class Name> var<i>
                    signatureBuilder.append(", ");
                    signatureBuilder.append(parameters[i].getName()).append(' ').append(name);
                }
            }
            signatureBuilder.append(") ");
            bodyBuilder.append(");\n");
            bodyBuilder.append("    this.recipeID = var0;\n");
            bodyBuilder.append("    this.matcher = var1;\n");
            bodyBuilder.append("    this.assembler = var2;\n");
            bodyBuilder.append("    this.remainingItems = var3;\n");
            bodyBuilder.append('}');

            CtConstructor generatedConstructor = CtNewConstructor.make(signatureBuilder.toString() + bodyBuilder.toString(), generatedRecipeClass);
            generatedRecipeClass.addConstructor(generatedConstructor);
        }

        generatedRecipeClass.writeFile(WolfyCoreImpl.getInstance().getWolfyUtils().getDataFolder().getPath() + "/generated_classes");
        return generatedRecipeClass.toClass(FunctionalRecipe.class);
    }

    private void generateUtils(ClassPool classPool) throws CannotCompileException, IOException, NotFoundException {
        generateConverterFunctions(classPool);
        generateConversionUtils(classPool);
    }

    /**
     * We need to create separate Classes, as we cannot compile inner classes using javassist.
     * This means that lambda functions are not possible!
     *
     * @param classPool
     * @throws NotFoundException
     * @throws CannotCompileException
     * @throws IOException
     */
    private void generateConverterFunctions(ClassPool classPool) throws NotFoundException, CannotCompileException, IOException {
        // Functional Class to convert a Bukkit ItemStack to a NMS ItemStack
        final CtClass convertCraftItemStack = classPool.makeClass(GENERATOR_PACKAGE + ".ConvertCraftItemStackToNMS");
        classPool.importPackage(GENERATOR_PACKAGE);
        convertCraftItemStack.addInterface(classPool.get(Function.class.getName()));
        SignatureAttribute.ClassSignature classSignature = new SignatureAttribute.ClassSignature(null, null,
                // Function<org.bukkit.inventory.ItemStack, ItemStack>
                new SignatureAttribute.ClassType[]{
                        new SignatureAttribute.ClassType(Function.class.getName(), new SignatureAttribute.TypeArgument[]{
                                new SignatureAttribute.TypeArgument(new SignatureAttribute.ClassType(ItemStack.class.getName())),
                                new SignatureAttribute.TypeArgument(new SignatureAttribute.ClassType(ITEMSTACK_CLASS.getName()))
                        })
                }
        );
        convertCraftItemStack.setGenericSignature(classSignature.encode());
        String convertItemStackMethod = applyMappings("""
                public Object apply(Object itemStack) {
                    return ${CraftItemStack}.asNMSCopy((${BukkitItemStack}) itemStack);
                }
                """);
        convertCraftItemStack.addMethod(CtNewMethod.make(convertItemStackMethod, convertCraftItemStack));
        convertCraftItemStack.writeFile(WolfyCoreImpl.getInstance().getWolfyUtils().getDataFolder().getPath() + "/generated_classes");
        convertCraftItemStack.toClass(FunctionalRecipe.class);

        // Functional Class to convert a list of Bukkit ItemStacks to NMS ItemStacks
        final CtClass convertRemainingItems = classPool.makeClass(GENERATOR_PACKAGE + ".ConvertRemainingItemsToNMS");
        classPool.importPackage(GENERATOR_PACKAGE);
        convertRemainingItems.addInterface(classPool.get(Function.class.getName()));
        String convertRemainingItemsMethod = applyMappings("""
                public Object apply(Object itemStacks) {
                    ${NonNullList} items = ${NonNullList}.${NonNullList_Create}(((${List}) itemStacks).size(), ${NMS_ItemStack}.${NMS_ItemStack_Empty});
                    for(int i = 0; i < ((${List}) itemStacks).size(); i++) {
                        items.set(i, ${CraftItemStack}.asNMSCopy((${BukkitItemStack}) ((${List}) itemStacks).get(i)));
                    }
                    return items;
                }
                """);
        convertRemainingItems.addMethod(CtNewMethod.make(convertRemainingItemsMethod, convertRemainingItems));
        convertRemainingItems.writeFile(WolfyCoreImpl.getInstance().getWolfyUtils().getDataFolder().getPath() + "/generated_classes");
        convertRemainingItems.toClass(FunctionalRecipe.class);
    }

    private void generateConversionUtils(ClassPool classPool) throws CannotCompileException, IOException {
        // Other conversion utils
        final CtClass conversionUtils = classPool.makeClass(GENERATOR_PACKAGE + ".ConversionUtils");
        classPool.importPackage(GENERATOR_PACKAGE);
        // Find the Ingredient.EMPTY field
        Field emptyIngredientField = Arrays.stream(RECIPE_ITEMSTACK_CLASS.getDeclaredFields()).filter(field -> field.getType().equals(RECIPE_ITEMSTACK_CLASS)).findFirst().orElse(null);

        // Converts the Bukkit RecipeChoice to the NMS Ingredient
        String recipeChoiceConverterMethod = applyMappings("""
                public static ${Ingredient} recipeChoiceToNMS(${RecipeChoice} bukkit, boolean requireNotEmpty) {
                    ${Ingredient} stack;
                    if (bukkit == null) {
                        stack = ${Ingredient}.${Ingredient_Empty};
                    } else if (bukkit instanceof ${RecipeChoice}.MaterialChoice) {
                        ${List} valueList = new ${ArrayList}();
                        ${List} materials = ((${RecipeChoice}.MaterialChoice) bukkit).getChoices();
                        for (int i = 0; i < materials.size(); i++) {
                            valueList.add(new ${Ingredient}.StackProvider(${CraftItemStack}.asNMSCopy(new ${BukkitItemStack}((${Material}) materials.get(i)))));
                        }
                        stack = new ${Ingredient}(valueList.stream());
                    } else {
                        if (!(bukkit instanceof ${RecipeChoice}.ExactChoice)) {
                            throw new java.lang.IllegalArgumentException("Unknown recipe stack instance " + bukkit);
                        }
                        ${List} valueList = new ${ArrayList}();
                        ${List} itemStacks = ((${RecipeChoice}.ExactChoice) bukkit).getChoices();
                        for (int i = 0; i < itemStacks.size(); i++) {
                            valueList.add(new ${Ingredient}.StackProvider(${CraftItemStack}.asNMSCopy((${BukkitItemStack}) itemStacks.get(i))));
                        }
                        stack = new ${Ingredient}(valueList.stream());
                        stack.exact = true;
                    }
                            
                    if (requireNotEmpty && stack.${Ingredient_Choices}.length == 0) {
                        throw new java.lang.IllegalArgumentException("Recipe requires at least one non-air choice!");
                    } else {
                        return stack;
                    }
                }
                """);
        conversionUtils.addMethod(CtNewMethod.make(recipeChoiceConverterMethod, conversionUtils));

        // Converts Lists of Bukkit RecipeChoices to Lists of NMS Ingredients
        String convertRecipeChoicesToIngredientsMethod = applyMappings("""
                public static ${NonNullList} recipeChoicesToIngredients(${List} choices) {
                    ${NonNullList} ingredients = ${NonNullList}.${NonNullList_Create}(choices.size(), ${Ingredient}.${Ingredient_Empty});
                    for(int i = 0; i < choices.size(); i++) {
                        ingredients.set(i, recipeChoiceToNMS((${RecipeChoice}) choices.get(i), false));
                    }
                    return ingredients;
                }
                """);
        conversionUtils.addMethod(CtNewMethod.make(convertRecipeChoicesToIngredientsMethod, conversionUtils));

        // Creates CraftInventories based on the type of the Container.
        String convertContainerToCraftBukkitMethod = applyMappings("""
                public static ${CraftInventory} containerToBukkit(${Container} container) {
                    if (container instanceof ${CraftingContainer}) {
                        ${CraftingContainer} containerCrafting = (${CraftingContainer}) container;
                        return new ${CraftInventoryCrafting}(containerCrafting, containerCrafting.${resultInventory});
                    }
                    return new ${CraftInventory}(container);
                }
                """);
        conversionUtils.addMethod(CtNewMethod.make(convertContainerToCraftBukkitMethod, conversionUtils));

        conversionUtils.writeFile(WolfyCoreImpl.getInstance().getWolfyUtils().getDataFolder().getPath() + "/generated_classes");
        conversionUtils.toClass(FunctionalRecipe.class);
    }

}
