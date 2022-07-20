package me.wolfyscript.utilities.compatibility.plugins.itemsadder;

import java.util.List;
import java.util.Optional;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class CustomBlockWrapper implements CustomBlock {

    private final CustomBlock iaBlock;

    private CustomBlockWrapper(@NotNull CustomBlock iaBlock) {
        this.iaBlock = iaBlock;
    }

    public static Optional<CustomBlockWrapper> wrapBlock(@Nullable CustomBlock iaBlock) {
        return Optional.ofNullable(wrapNullableBlock(iaBlock));
    }

    private static CustomBlockWrapper wrapNullableBlock(@Nullable CustomBlock iaBlock) {
        return iaBlock != null ? new CustomBlockWrapper(iaBlock) : null;
    }

    @Override
    public Optional<CustomBlock> place(Location location) {
        return Optional.ofNullable(wrapNullableBlock(iaBlock.place(location)));
    }

    @Override
    public boolean remove() {
        return iaBlock.remove();
    }

    @Override
    public BlockData getBaseBlockData() {
        return iaBlock.getBaseBlockData();
    }

    @Override
    public Block getBlock() {
        return iaBlock.getBlock();
    }

    @Override
    public boolean isPlaced() {
        return iaBlock.isPlaced();
    }

    @Override
    public List<ItemStack> getLoot(boolean includeSelfBlock) {
        return iaBlock.getLoot(includeSelfBlock);
    }

    @Override
    public List<ItemStack> getLoot() {
        return iaBlock.getLoot();
    }

    @Override
    public List<ItemStack> getLoot(ItemStack tool, boolean includeSelfBlock) {
        return iaBlock.getLoot(tool, includeSelfBlock);
    }

    @Override
    public int getOriginalLightLevel() {
        return iaBlock.getOriginalLightLevel();
    }

    @Override
    public void setCurrentLightLevel(int level) {
        iaBlock.setCurrentLightLevel(level);
    }

    @Override
    public boolean playBreakEffect() {
        return iaBlock.playBreakEffect();
    }

    @Override
    public boolean playBreakParticles() {
        return iaBlock.playBreakParticles();
    }

    @Override
    public boolean playBreakSound() {
        return iaBlock.playBreakSound();
    }

    @Override
    public boolean playPlaceSound() {
        return iaBlock.playPlaceSound();
    }
}
