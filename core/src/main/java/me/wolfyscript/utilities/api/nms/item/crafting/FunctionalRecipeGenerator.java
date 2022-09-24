package me.wolfyscript.utilities.api.nms.item.crafting;

import com.wolfyscript.utilities.bukkit.WolfyCoreBukkit;
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
import me.wolfyscript.utilities.api.nms.inventory.InjectGUIInventory;
import me.wolfyscript.utilities.util.NamespacedKey;
import me.wolfyscript.utilities.util.Reflection;
import me.wolfyscript.utilities.util.version.MinecraftVersions;
import org.apache.commons.lang3.text.StrSubstitutor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice;

public class FunctionalRecipeGenerator {

    private static final String GENERATOR_PACKAGE = FunctionalRecipeGenerator.class.getPackageName() + "";

    private static final Class<?> CONTAINER_CLASS;
    private static final Class<?> WORLD_CLASS;
    private static final Class<?> ITEMSTACK_CLASS;
    private static final Class<?> RECIPE_CLASS;
    private static final Class<?> RECIPE_ITEMSTACK_CLASS;
    private static final Class<?> RESOURCE_KEY_CLASS;
    private static final Class<?> NON_NULL_LIST_CLASS;
    private static final Class<?> RECIPE_MANAGER_CLASS;
    private static final Class<?> MINECRAFT_SERVER_CLASS;

    private static final Class<?> RECIPE_CAMPFIRE_CLASS;
    private static final Class<?> RECIPE_FURNACE_CLASS;
    private static final Class<?> RECIPE_SMOKING_CLASS;
    private static final Class<?> RECIPE_BLASTING_CLASS;

    private static final Method MINECRAFT_SERVER_GET_RECIPE_MANAGER_METHOD;
    private static final Method MINECRAFT_SERVER_STATIC_GETTER_METHOD;
    private static final Method RECIPE_MATCHES_METHOD;
    private static final Method RECIPE_ASSEMBLE_METHOD;
    private static final Method RECIPE_GET_REMAINING_ITEMS_METHOD;
    private static final Method RECIPE_MANAGER_ADD_RECIPE_METHOD;

    private static final Method WORLD_GET_CRAFT_WORLD_METHOD;
    private static final Method NONNULLLIST_CREATE_METHOD;

    private static final Class<?> CRAFT_ITEMSTACK_CLASS;
    private static final Class<?> CRAFT_INVENTORY_CLASS;
    private static final Class<?> CRAFT_RECIPE_CLASS;

    private static final Method CRAFT_RECIPE_RECIPE_CHOICE_TO_NMS;
    private static final Method CRAFT_ITEMSTACK_TO_NMS;

    private static final Object ACCESS_RECIPE;
    private static final Object MINECRAFT_SERVER;

    private static final Map<FunctionalRecipeType, Class<?>> GENERATED_RECIPES = new HashMap<>();

    static {
        MINECRAFT_SERVER_CLASS = Reflection.getNMS("server", "MinecraftServer");
        CONTAINER_CLASS = Reflection.getNMS("world", "IInventory");
        WORLD_CLASS = Reflection.getNMS("world.level", "World");
        ITEMSTACK_CLASS = Reflection.getNMS("world.item", "ItemStack");
        RESOURCE_KEY_CLASS = Reflection.getNMS("resources", "MinecraftKey");
        RECIPE_CLASS = Reflection.getNMS("world.item.crafting", "IRecipe");
        RECIPE_ITEMSTACK_CLASS = Reflection.getNMS("world.item.crafting", "RecipeItemStack");

        RECIPE_MANAGER_CLASS = Reflection.getNMS("world.item.crafting", "CraftingManager");
        NON_NULL_LIST_CLASS = Reflection.getNMS("core", "NonNullList");

        RECIPE_CAMPFIRE_CLASS = Reflection.getNMS("world.item.crafting", "RecipeCampfire");
        RECIPE_FURNACE_CLASS = Reflection.getNMS("world.item.crafting", "FurnaceRecipe");
        RECIPE_SMOKING_CLASS = Reflection.getNMS("world.item.crafting", "RecipeSmoking");
        RECIPE_BLASTING_CLASS = Reflection.getNMS("world.item.crafting", "RecipeBlasting");

        MINECRAFT_SERVER_STATIC_GETTER_METHOD = Reflection.getMethod(MINECRAFT_SERVER_CLASS, "getServer");

        MINECRAFT_SERVER_GET_RECIPE_MANAGER_METHOD = Arrays.stream(MINECRAFT_SERVER_CLASS.getMethods()).filter(method -> method.getReturnType().equals(RECIPE_MANAGER_CLASS)).findFirst().orElseGet(() -> Reflection.getMethod(MINECRAFT_SERVER_CLASS, "getCraftingManager"));

        NONNULLLIST_CREATE_METHOD = Reflection.getMethod(NON_NULL_LIST_CLASS, "a", Integer.TYPE);
        RECIPE_MATCHES_METHOD = Reflection.getMethod(RECIPE_CLASS, "a", CONTAINER_CLASS, WORLD_CLASS);
        RECIPE_ASSEMBLE_METHOD = Reflection.getMethod(RECIPE_CLASS, "a", CONTAINER_CLASS);
        RECIPE_GET_REMAINING_ITEMS_METHOD = Reflection.getMethod(RECIPE_CLASS, "b", CONTAINER_CLASS);
        RECIPE_MANAGER_ADD_RECIPE_METHOD = Reflection.getMethod(RECIPE_MANAGER_CLASS, "addRecipe", RECIPE_CLASS);

        WORLD_GET_CRAFT_WORLD_METHOD = Reflection.getMethod(WORLD_CLASS, "getWorld");

        CRAFT_ITEMSTACK_CLASS = Reflection.getOBC("inventory.CraftItemStack");
        CRAFT_INVENTORY_CLASS = Reflection.getOBC("inventory.CraftInventory");
        CRAFT_RECIPE_CLASS = Reflection.getOBC("inventory.CraftRecipe");

        Constructor<?> accessRecipeConstructor = Reflection.getOBC("inventory.CraftFurnaceRecipe").getConstructors()[0];
        try {
            ACCESS_RECIPE = accessRecipeConstructor.newInstance(null, new ItemStack(Material.STRING), new RecipeChoice.MaterialChoice(Material.AIR), 1f, 60);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }

        CRAFT_RECIPE_RECIPE_CHOICE_TO_NMS = Reflection.getMethod(ACCESS_RECIPE.getClass(), "toNMS", RecipeChoice.class, Boolean.TYPE);
        CRAFT_ITEMSTACK_TO_NMS = Reflection.getDeclaredMethod(CRAFT_ITEMSTACK_CLASS, "asNMSCopy", ItemStack.class);
        CRAFT_RECIPE_RECIPE_CHOICE_TO_NMS.setAccessible(true);

        try {
            MINECRAFT_SERVER = MINECRAFT_SERVER_GET_RECIPE_MANAGER_METHOD.invoke(MINECRAFT_SERVER_STATIC_GETTER_METHOD.invoke(null));
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    public static void generateRecipeClasses() {
        try {
            ClassPool classPool = ClassPool.getDefault();
            generateConverterFunctions(classPool);
            GENERATED_RECIPES.put(FunctionalRecipeType.CAMPFIRE, inject(classPool, RECIPE_CAMPFIRE_CLASS));
            GENERATED_RECIPES.put(FunctionalRecipeType.SMELTING, inject(classPool, RECIPE_FURNACE_CLASS));
            GENERATED_RECIPES.put(FunctionalRecipeType.SMOKING, inject(classPool, RECIPE_SMOKING_CLASS));
            GENERATED_RECIPES.put(FunctionalRecipeType.BLASTING, inject(classPool, RECIPE_BLASTING_CLASS));
        } catch (NotFoundException | CannotCompileException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static Class<?> getFunctionalRecipeClass(FunctionalRecipeType type) {
        return GENERATED_RECIPES.get(type);
    }

    public static boolean addRecipeToRecipeManager(FunctionalRecipe recipe) {
        try {
            RECIPE_MANAGER_ADD_RECIPE_METHOD.invoke(MINECRAFT_SERVER, recipe);
            return true;
        } catch (IllegalAccessException | InvocationTargetException e) {
            return false;
        }
    }

    public static void createAndRegisterTestRecipes() throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {

        // Create a campfire with custom checks.
        // Campfire recipes have limitations!
        // The inventory that is used in the check, assembly and remaining items calculation has no reference to the campfire block
        //
        FunctionalRecipeBuilderCampfire builderCampfire = new FunctionalRecipeBuilderCampfire(new NamespacedKey(NamespacedKey.WOLFYUTILITIES, "test_functional_campfire"), new ItemStack(Material.CHEST), new RecipeChoice.MaterialChoice(Material.PLAYER_HEAD));
        builderCampfire.createAndRegister();

        // other types
        FunctionalRecipeBuilderSmelting builderSmelting = new FunctionalRecipeBuilderSmelting(new NamespacedKey(NamespacedKey.WOLFYUTILITIES, "test_functional_smelting"), new ItemStack(Material.CHEST), new RecipeChoice.MaterialChoice(Material.PLAYER_HEAD));
        builderSmelting.createAndRegister();

        FunctionalRecipeBuilderBlasting builderBlasting = new FunctionalRecipeBuilderBlasting(new NamespacedKey(NamespacedKey.WOLFYUTILITIES, "test_functional_blasting"), new ItemStack(Material.CHEST), new RecipeChoice.MaterialChoice(Material.PLAYER_HEAD));
        builderBlasting.createAndRegister();

        FunctionalRecipeBuilderSmoking builderSmoking = new FunctionalRecipeBuilderSmoking(new NamespacedKey(NamespacedKey.WOLFYUTILITIES, "test_functional_smoking"), new ItemStack(Material.CHEST), new RecipeChoice.MaterialChoice(Material.PLAYER_HEAD));
        builderSmoking.createAndRegister();
    }

    /**
     * Injects FunctionalRecipe into the specified recipe class.
     *
     * @param classPool
     * @param originalClass
     * @throws NotFoundException
     * @throws CannotCompileException
     */
    private static Class<?> inject(ClassPool classPool, Class<?> originalClass) throws NotFoundException, CannotCompileException, IOException {
        classPool.insertClassPath(new LoaderClassPath(InjectGUIInventory.class.getClassLoader()));

        final String funcRecipeClass = "Functional" + originalClass.getSimpleName();
        final CtClass generatedRecipeClass = classPool.makeClass(GENERATOR_PACKAGE + "." + funcRecipeClass);

        classPool.importPackage("net.minecraft.server." + Reflection.getVersion());
        classPool.importPackage(GENERATOR_PACKAGE);

        CtClass wrapperFuncRecipeInterface = classPool.get(FunctionalRecipe.class.getName());
        generatedRecipeClass.setInterfaces(new CtClass[]{wrapperFuncRecipeInterface});
        generatedRecipeClass.setSuperclass(classPool.get(originalClass.getName()));

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

        generatedRecipeClass.addMethod(CtNewMethod.getter("getNamespacedKey", recipeIDField));
        generatedRecipeClass.addMethod(CtNewMethod.getter("getMatcher", matcherField));
        generatedRecipeClass.addMethod(CtNewMethod.getter("getAssembler", assemblerField));
        generatedRecipeClass.addMethod(CtNewMethod.getter("getRemainingItemsFunction", remainingItemsFunctionField));

        // Create converters and logic
        String matchesMethod = "public boolean " + RECIPE_MATCHES_METHOD.getName() + "(" + CONTAINER_CLASS.getName() + " container, " + WORLD_CLASS.getName() + " level) {\n" +
                "    return matches(new " + CRAFT_INVENTORY_CLASS.getName() + "(container), level.getWorld());\n" +
                "}";
        generatedRecipeClass.addMethod(CtNewMethod.make(matchesMethod, generatedRecipeClass));

        String assembleMethod = "public " + ITEMSTACK_CLASS.getName() + " " + RECIPE_ASSEMBLE_METHOD.getName() + "(" + CONTAINER_CLASS.getName() + " container) {\n"
                + "    " + Optional.class.getName() + " item = assemble(new " + CRAFT_INVENTORY_CLASS.getName() + "(container)).map(new ConvertCraftItemStackToNMS());\n"
                + "    if (item.isPresent()) {\n"
                + "        return (" + ITEMSTACK_CLASS.getName() + ") item.get();\n"
                + "    }\n"
                + "    return super." + RECIPE_ASSEMBLE_METHOD.getName() + "(container);\n" +
                "}";
        generatedRecipeClass.addMethod(CtNewMethod.make(assembleMethod, generatedRecipeClass));

        String remainingItemsMethod = StrSubstitutor.replace("""
                public ${non_null_list} ${method_name}(${container} container) {
                    ${optional} nmsItems = getRemainingItems(new ${craft_inventory}(container)).map(new ConvertRemainingItemsToNMS());
                    if (nmsItems.isPresent()) {
                        return (${non_null_list}) nmsItems.get();
                    }
                    return super.${method_name}(container);
                }
                """, Map.of(
                "container", CONTAINER_CLASS.getName(),
                "craft_inventory", CRAFT_INVENTORY_CLASS.getName(),
                "non_null_list", NON_NULL_LIST_CLASS.getName(),
                "itemstack", ITEMSTACK_CLASS.getName(),
                "optional", Optional.class.getName(),
                "method_name", RECIPE_GET_REMAINING_ITEMS_METHOD.getName()
        ));
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
            // RecipeAssembler var1
            signatureBuilder.append(RecipeAssembler.class.getName()).append(" var2").append(", ");
            // RecipeRemainingItemsFunction var1
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

        generatedRecipeClass.writeFile(WolfyCoreBukkit.getInstance().getDataFolder().getPath() + "/generated_classes");
        return generatedRecipeClass.toClass(FunctionalRecipe.class);
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
    private static void generateConverterFunctions(ClassPool classPool) throws NotFoundException, CannotCompileException, IOException {
        // Functional Class to convert a Bukkit ItemStack to a NMS ItemStack
        final CtClass convertCraftItemStack = classPool.makeClass(GENERATOR_PACKAGE + ".ConvertCraftItemStackToNMS");
        classPool.importPackage(GENERATOR_PACKAGE);
        convertCraftItemStack.addInterface(classPool.get(Function.class.getName()));
        convertCraftItemStack.addMethod(CtNewMethod.make("public " + ITEMSTACK_CLASS.getName() + " apply(" + ItemStack.class.getName() + " itemStack) { return " + CRAFT_ITEMSTACK_CLASS.getName() + ".asNMSCopy(itemStack); }", convertCraftItemStack));

        convertCraftItemStack.writeFile(WolfyCoreBukkit.getInstance().getDataFolder().getPath() + "/generated_classes");
        convertCraftItemStack.toClass(FunctionalRecipe.class);

        // Functional Class to convert a list of Bukkit ItemStacks to NMS ItemStacks
        final CtClass convertRemainingItems = classPool.makeClass(GENERATOR_PACKAGE + ".ConvertRemainingItemsToNMS");
        classPool.importPackage(GENERATOR_PACKAGE);
        convertRemainingItems.addInterface(classPool.get(Function.class.getName()));
        String convertRemainingItemsMethod = "public " + NON_NULL_LIST_CLASS.getName() + " apply(" + List.class.getName() + " itemStacks) {\n"
                + "    " + NON_NULL_LIST_CLASS.getName() + " items = " + NON_NULL_LIST_CLASS.getName() + "." + NONNULLLIST_CREATE_METHOD.getName() + "(itemStacks.size());\n"
                + "    for(int i = 0; i < itemStacks.size(); i++) {\n"
                + "        items.set(i, " + CRAFT_ITEMSTACK_CLASS.getName() + ".asNMSCopy((" + ItemStack.class.getName() + ") itemStacks.get(i)));\n"
                + "    }\n"
                + "    return items;\n"
                + "}";
        convertRemainingItems.addMethod(CtNewMethod.make(convertRemainingItemsMethod, convertRemainingItems));

        convertRemainingItems.writeFile(WolfyCoreBukkit.getInstance().getDataFolder().getPath() + "/generated_classes");
        convertRemainingItems.toClass(FunctionalRecipe.class);

        // Other conversion utils
        final CtClass conversionUtils = classPool.makeClass(GENERATOR_PACKAGE + ".ConversionUtils");
        classPool.importPackage(GENERATOR_PACKAGE);
        // Find the Ingredient.EMPTY field
        Field emptyIngredientField = Arrays.stream(RECIPE_ITEMSTACK_CLASS.getDeclaredFields()).filter(field -> field.getType().equals(RECIPE_ITEMSTACK_CLASS)).findFirst().orElse(null);

        String recipeChoiceConverterMethod = StrSubstitutor.replace("""
                public static ${ingredient} recipeChoiceToNMS(${recipe_choice} bukkit, boolean requireNotEmpty) {
                    ${ingredient} stack;
                    if (bukkit == null) {
                        stack = ${ingredient}.${empty_ingredient};
                    } else if (bukkit instanceof ${recipe_choice}.MaterialChoice) {
                        ${list} valueList = new ${arraylist}();
                        ${list} materials = ((${recipe_choice}.MaterialChoice) bukkit).getChoices();
                        for (int i = 0; i < materials.size(); i++) {
                            valueList.add(new ${ingredient}.StackProvider(${craftitemstack}.asNMSCopy(new ${itemstack}((${material}) materials.get(i)))));
                        }
                        stack = new ${ingredient}(valueList.stream());
                    } else {
                        if (!(bukkit instanceof ${recipe_choice}.ExactChoice)) {
                            throw new java.lang.IllegalArgumentException("Unknown recipe stack instance " + bukkit);
                        }
                        ${list} valueList = new ${arraylist}();
                        ${list} itemStacks = ((${recipe_choice}.ExactChoice) bukkit).getChoices();
                        for (int i = 0; i < itemStacks.size(); i++) {
                            valueList.add(new ${ingredient}.StackProvider(${craftitemstack}.asNMSCopy((${itemstack}) itemStacks.get(i))));
                        }
                        stack = new ${ingredient}(valueList.stream());
                        stack.exact = true;
                    }
                            
                    stack.${ingredient_dissolve}();
                    if (requireNotEmpty && stack.${ingredient_choices}.length == 0) {
                        throw new java.lang.IllegalArgumentException("Recipe requires at least one non-air choice!");
                    } else {
                        return stack;
                    }
                }
                """, Map.of(
                "list", List.class.getName(),
                "arraylist", ArrayList.class.getName(),
                "ingredient", RECIPE_ITEMSTACK_CLASS.getName(),
                "recipe_choice", RecipeChoice.class.getName(),
                "material", Material.class.getName(),
                "itemstack", ItemStack.class.getName(),
                "empty_ingredient", emptyIngredientField.getName(),
                "craftitemstack", CRAFT_ITEMSTACK_CLASS.getName(),
                "ingredient_dissolve", Reflection.NMSMapping.of(MinecraftVersions.v1_19, "f").orElse("buildChoices"),
                "ingredient_choices", Arrays.stream(RECIPE_ITEMSTACK_CLASS.getFields()).filter(field -> field.getType().equals(ITEMSTACK_CLASS.arrayType())).findFirst().map(Field::getName).orElse("choices")
        ));
        conversionUtils.addMethod(CtNewMethod.make(recipeChoiceConverterMethod, conversionUtils));

        conversionUtils.writeFile(WolfyCoreBukkit.getInstance().getDataFolder().getPath() + "/generated_classes");
        conversionUtils.toClass(FunctionalRecipe.class);
    }

}
