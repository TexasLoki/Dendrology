package com.scottkillen.mod.dendrology.block;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.scottkillen.mod.dendrology.TheMod;
import com.scottkillen.mod.dendrology.config.Settings;
import com.scottkillen.mod.dendrology.content.overworld.OverworldTreeBlockFactory;
import com.scottkillen.mod.dendrology.content.overworld.OverworldTreeTaxonomy;
import com.scottkillen.mod.dendrology.item.ModLogItem;
import com.scottkillen.mod.dendrology.item.ModSaplingItem;
import com.scottkillen.mod.dendrology.item.ModSlabItem;
import com.scottkillen.mod.dendrology.item.ModWoodItem;
import com.scottkillen.mod.koresample.common.util.log.Logger;
import com.scottkillen.mod.koresample.tree.DefinesLog;
import com.scottkillen.mod.koresample.tree.DefinesSlab;
import com.scottkillen.mod.koresample.tree.DefinesStairs;
import com.scottkillen.mod.koresample.tree.block.LeavesBlock;
import com.scottkillen.mod.koresample.tree.block.LogBlock;
import com.scottkillen.mod.koresample.tree.block.SaplingBlock;
import com.scottkillen.mod.koresample.tree.block.SlabBlock;
import com.scottkillen.mod.koresample.tree.block.StairsBlock;
import com.scottkillen.mod.koresample.tree.block.WoodBlock;
import com.scottkillen.mod.koresample.tree.item.LeavesItem;
import com.scottkillen.mod.koresample.tree.loader.TreeSpeciesLoader;
import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.WeightedRandomChestContent;
import net.minecraftforge.common.ChestGenHooks;
import java.util.List;

import static net.minecraftforge.common.ChestGenHooks.BONUS_CHEST;
import static net.minecraftforge.common.ChestGenHooks.DUNGEON_CHEST;
import static net.minecraftforge.common.ChestGenHooks.MINESHAFT_CORRIDOR;
import static net.minecraftforge.common.ChestGenHooks.PYRAMID_DESERT_CHEST;
import static net.minecraftforge.common.ChestGenHooks.PYRAMID_JUNGLE_CHEST;
import static net.minecraftforge.common.ChestGenHooks.PYRAMID_JUNGLE_DISPENSER;
import static net.minecraftforge.common.ChestGenHooks.STRONGHOLD_CORRIDOR;
import static net.minecraftforge.common.ChestGenHooks.STRONGHOLD_CROSSING;
import static net.minecraftforge.common.ChestGenHooks.STRONGHOLD_LIBRARY;
import static net.minecraftforge.common.ChestGenHooks.VILLAGE_BLACKSMITH;

public final class ModBlocks
{
    private static final int DEFAULT_LEAVES_FIRE_ENCOURAGEMENT = 30;
    private static final int DEFAULT_LOG_FIRE_ENCOURAGEMENT = 5;
    private static final int DEFAULT_PLANKS_FIRE_ENCOURAGEMENT = 5;
    private static final int DEFAULT_STAIRS_FIRE_ENCOURAGEMENT = DEFAULT_PLANKS_FIRE_ENCOURAGEMENT;
    private static final int DEFAULT_LEAVES_FLAMMABILITY = 60;
    private static final int DEFAULT_LOG_FLAMMABILITY = 5;
    private static final int DEFAULT_PLANKS_FLAMMABILITY = 20;
    private static final int DEFAULT_STAIRS_FLAMMABILITY = DEFAULT_PLANKS_FLAMMABILITY;
    private static final List<LogBlock> logBlocks = Lists.newArrayList();
    private static final List<WoodBlock> woodBlocks = Lists.newArrayList();
    private static final List<SlabBlock> singleSlabBlocks = Lists.newArrayList();
    private static final List<SlabBlock> doubleSlabBlocks = Lists.newArrayList();
    private static final List<StairsBlock> stairsBlocks = Lists.newArrayList();
    private static final List<SaplingBlock> saplingBlocks = Lists.newArrayList();
    private static final List<LeavesBlock> leavesBlocks = Lists.newArrayList();
    private static final OverworldTreeTaxonomy overworldTaxonomy = new OverworldTreeTaxonomy();

    private static void addAllSaplingsToChests()
    {
        Logger.forMod(TheMod.INSTANCE.modID()).info("Hiding species in chests.");
        final Settings settings = Settings.INSTANCE;
        for (final SaplingBlock sapling : saplingBlocks)
        {
            addSaplingToChest(sapling, VILLAGE_BLACKSMITH, settings.blacksmithRarity());
            addSaplingToChest(sapling, BONUS_CHEST, settings.bonusChestRarity());
            addSaplingToChest(sapling, PYRAMID_DESERT_CHEST, settings.desertTempleRarity());
            addSaplingToChest(sapling, DUNGEON_CHEST, settings.dungeonRarity());
            addSaplingToChest(sapling, PYRAMID_JUNGLE_CHEST, settings.jungleTempleRarity());
            addSaplingToChest(sapling, PYRAMID_JUNGLE_DISPENSER, settings.jungleTempleDispenserRarity());
            addSaplingToChest(sapling, MINESHAFT_CORRIDOR, settings.mineshaftCorridorRarity());
            addSaplingToChest(sapling, STRONGHOLD_CORRIDOR, settings.strongholdCorridorRarity());
            addSaplingToChest(sapling, STRONGHOLD_CROSSING, settings.strongholdCrossingRarity());
            addSaplingToChest(sapling, STRONGHOLD_LIBRARY, settings.strongholdLibraryRarity());
        }
    }

    private static void addSaplingToChest(SaplingBlock sapling, String chestType, int rarity)
    {
        if (rarity > 0) ChestGenHooks.getInfo(chestType)
                .addItem(new WeightedRandomChestContent(new ItemStack(sapling), 1, 2, rarity));
    }

    public static Iterable<? extends Block> leavesBlocks() { return ImmutableList.copyOf(leavesBlocks); }

    private static void loadOverWorldContent()
    {
        Logger.forMod(TheMod.INSTANCE.modID()).info("Loading overworld species.");
        final TreeSpeciesLoader overworldContent = new TreeSpeciesLoader(overworldTaxonomy);
        overworldContent.load(new OverworldTreeBlockFactory());
    }

    public static Iterable<? extends Block> logBlocks() { return ImmutableList.copyOf(logBlocks); }

    public static Iterable<? extends DefinesLog> logDefinitions() { return overworldTaxonomy.logDefinitions(); }

    private static void registerAllBlocks()
    {
        registerAllLogBlocks();
        registerAllLeavesBlock();
        registerAllSaplingBlocks();
        registerAllWoodBlocks();
        registerAllStairsBlocks();
        registerAllSingleSlabBlocks();
        registerAllDoubleSlabBlocks();
    }

    private static void registerAllDoubleSlabBlocks()
    {
        int slabCount = 0;
        for (final SlabBlock slab : doubleSlabBlocks)
        {
            registerSlabBlock(slab, String.format("dslab%d", slabCount), singleSlabBlocks.get(slabCount), slab, true);
            slabCount++;
        }
    }

    private static void registerAllLeavesBlock()
    {
        int leavesCount = 0;
        for (final Block block : leavesBlocks)
        {
            registerLeavesBlock(block, String.format("leaves%d", leavesCount));
            leavesCount++;
        }
    }

    private static void registerAllLogBlocks()
    {
        int logCount = 0;
        for (final LogBlock block : logBlocks)
        {
            registerLogBlock(block, String.format("logs%d", logCount), block.getSubBlockNames());
            logCount++;
        }
    }

    private static void registerAllSaplingBlocks()
    {
        int saplingCount = 0;

        for (final SaplingBlock sapling : saplingBlocks)
        {
            registerSaplingBlock(sapling, String.format("sapling%d", saplingCount), sapling.subBlockNames());
            saplingCount++;
        }
    }

    private static void registerAllSingleSlabBlocks()
    {
        int slabCount = 0;
        for (final SlabBlock slab : singleSlabBlocks)
        {
            registerSlabBlock(slab, String.format("sslab%d", slabCount), slab, doubleSlabBlocks.get(slabCount), false);
            slabCount++;
        }
    }

    private static void registerAllStairsBlocks()
    {
        int stairsCount = 0;
        for (final StairsBlock stairs : stairsBlocks)
        {
            registerStairsBlock(stairs, String.format("stairs%d", stairsCount));
            stairsCount++;
        }
    }

    private static void registerAllWoodBlocks()
    {
        int woodBlockCount = 0;
        for (final WoodBlock wood : woodBlocks)
        {
            registerWoodBlock(wood, String.format("wood%d", woodBlockCount), wood.getSubBlockNames());
            woodBlockCount++;
        }
    }

    public static void registerBlock(LeavesBlock leavesBlock) { leavesBlocks.add(leavesBlock); }

    public static void registerBlock(LogBlock logBlock) { logBlocks.add(logBlock); }

    public static void registerBlock(SaplingBlock saplingBlock) { saplingBlocks.add(saplingBlock); }

    public static void registerBlock(SlabBlock singleSlabBlock, SlabBlock doubleSlabBlock)
    {
        singleSlabBlocks.add(singleSlabBlock);
        doubleSlabBlocks.add(doubleSlabBlock);
    }

    public static void registerBlock(StairsBlock stairsBlock) { stairsBlocks.add(stairsBlock); }

    public static void registerBlock(WoodBlock woodBlock) { woodBlocks.add(woodBlock); }

    private static void registerLeavesBlock(Block block, String name)
    {
        GameRegistry.registerBlock(block, LeavesItem.class, name);
        Blocks.fire.setFireInfo(block, DEFAULT_LEAVES_FIRE_ENCOURAGEMENT, DEFAULT_LEAVES_FLAMMABILITY);
    }

    private static void registerLogBlock(Block block, String name, ImmutableList<String> subblockNames)
    {
        GameRegistry.registerBlock(block, ModLogItem.class, name, block,
                subblockNames.toArray(new String[subblockNames.size()]));
        Blocks.fire.setFireInfo(block, DEFAULT_LOG_FIRE_ENCOURAGEMENT, DEFAULT_LOG_FLAMMABILITY);
    }

    private static void registerSaplingBlock(Block block, String name, List<String> subblockNames)
    {
        GameRegistry.registerBlock(block, ModSaplingItem.class, name, block,
                subblockNames.toArray(new String[subblockNames.size()]));
    }

    private static void registerSlabBlock(Block block, String name, SlabBlock singleSlab, SlabBlock doubleSlab,
                                          boolean isDouble)
    {
        GameRegistry.registerBlock(block, ModSlabItem.class, name, singleSlab, doubleSlab, isDouble);
    }

    private static void registerStairsBlock(Block block, String name)
    {
        GameRegistry.registerBlock(block, name);
        Blocks.fire.setFireInfo(block, DEFAULT_STAIRS_FIRE_ENCOURAGEMENT, DEFAULT_STAIRS_FLAMMABILITY);
    }

    private static void registerWoodBlock(Block block, String name, ImmutableList<String> subblockNames)
    {
        GameRegistry.registerBlock(block, ModWoodItem.class, name, block,
                subblockNames.toArray(new String[subblockNames.size()]));
        Blocks.fire.setFireInfo(block, DEFAULT_PLANKS_FIRE_ENCOURAGEMENT, DEFAULT_PLANKS_FLAMMABILITY);
    }

    public static Iterable<? extends Block> saplingBlocks() { return ImmutableList.copyOf(saplingBlocks); }

    public static Iterable<? extends Block> singleSlabBlocks() { return ImmutableList.copyOf(singleSlabBlocks); }

    public static Iterable<? extends DefinesSlab> slabDefinitions() { return overworldTaxonomy.slabDefinitions(); }

    public static Iterable<? extends Block> stairsBlocks() { return ImmutableList.copyOf(stairsBlocks); }

    public static Iterable<? extends DefinesStairs> stairsDefinitions()
    {
        return overworldTaxonomy.stairsDefinitions();
    }

    public static Iterable<? extends Block> woodBlocks() { return ImmutableList.copyOf(woodBlocks); }

    @SuppressWarnings("MethodMayBeStatic")
    public void loadContent()
    {
        loadOverWorldContent();
        registerAllBlocks();
        addAllSaplingsToChests();
    }
}
