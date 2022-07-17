package io.github.sirsalad.friendlyfarmer;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.event.player.AttackBlockCallback;
import net.minecraft.block.BeetrootsBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.CocoaBlock;
import net.minecraft.block.CropBlock;
import net.minecraft.block.NetherWartBlock;
import net.minecraft.block.SweetBerryBushBlock;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.Map;
import java.util.function.BiPredicate;
import java.util.function.Predicate;

public final class FriendlyFarmerMod implements ClientModInitializer {

    private static final Predicate<BlockState> CROP_BLOCK = state -> state.get(CropBlock.AGE) != 7;
    private static final Predicate<BlockState> STEM = state -> true;
    private static final Map<Block, Predicate<BlockState>> PLANTS = Map.ofEntries(
            Map.entry(Blocks.WHEAT, CROP_BLOCK),
            Map.entry(Blocks.CARROTS, CROP_BLOCK),
            Map.entry(Blocks.POTATOES, CROP_BLOCK),
            Map.entry(Blocks.MELON_STEM, STEM),
            Map.entry(Blocks.PUMPKIN_STEM, STEM),
            Map.entry(Blocks.ATTACHED_MELON_STEM, STEM),
            Map.entry(Blocks.ATTACHED_PUMPKIN_STEM, STEM),
            Map.entry(Blocks.BEETROOTS, state -> state.get(BeetrootsBlock.AGE) != 3),
            Map.entry(Blocks.NETHER_WART, state -> state.get(NetherWartBlock.AGE) != 3),
            Map.entry(Blocks.SWEET_BERRY_BUSH, state -> state.get(SweetBerryBushBlock.AGE) != 3),
            Map.entry(Blocks.COCOA, state -> state.get(CocoaBlock.AGE) != 2),
            Map.entry(Blocks.OAK_SAPLING, STEM),
            Map.entry(Blocks.SPRUCE_SAPLING, STEM),
            Map.entry(Blocks.BIRCH_SAPLING, STEM),
            Map.entry(Blocks.JUNGLE_SAPLING, STEM),
            Map.entry(Blocks.ACACIA_SAPLING, STEM),
            Map.entry(Blocks.DARK_OAK_SAPLING, STEM),
            Map.entry(Blocks.CRIMSON_FUNGUS, STEM),
            Map.entry(Blocks.WARPED_FUNGUS, STEM)
    );

    private static final BiPredicate<BlockPos, World> TALL_PLANT = (pos, world) ->
            world.getBlockState(pos.down(1)).getBlock() != world.getBlockState(pos).getBlock();

    private static final Map<Block, BiPredicate<BlockPos, World>> TALL_PLANTS = Map.of(
            Blocks.SUGAR_CANE, TALL_PLANT,
            Blocks.BAMBOO, TALL_PLANT,
            Blocks.CACTUS, TALL_PLANT,
            Blocks.KELP_PLANT, TALL_PLANT,
            Blocks.KELP, (pos, world) -> world.getBlockState(pos.down(1)).getBlock() != Blocks.KELP_PLANT
    );

    @Override
    public void onInitializeClient() {
        AttackBlockCallback.EVENT.register((player, world, hand, pos, direction) -> {
            final BlockState state = world.getBlockState(pos);
            final Block block = state.getBlock();

            final Predicate<BlockState> plant = PLANTS.get(block);
            if (plant != null && plant.test(state) && !player.isSneaking()) {
                return ActionResult.FAIL;
            }

            final BiPredicate<BlockPos, World> tallPlant = TALL_PLANTS.get(block);
            if (tallPlant != null && tallPlant.test(pos, world) && !player.isSneaking()) {
                return ActionResult.FAIL;
            }

            return ActionResult.PASS;
        });
    }
}
