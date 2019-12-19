package com.thenights.ourcraft.timbery.common.handler;
import com.thenights.ourcraft.timbery.common.tree.Tree;
import java.util.*;

import com.thenights.ourcraft.timbery.core.OurcraftTimbery;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.common.util.FakePlayerFactory;

public class TreeHandler {
  private static Map<UUID, Tree> m_Trees = new HashMap<>();
  private Tree tree;

  private static <T> T getLastElement(Iterable<T> elements) {
    Iterator<T> itr = elements.iterator();
    T lastElement = itr.next();

    while (itr.hasNext()) {
      lastElement = itr.next();
    }

    return lastElement;
  }


  public int AnalyzeTree(World world, BlockPos blockPos, PlayerEntity entityPlayer) {
    // OurcraftTimbery.LOGGER.info("starting tree analyze");
    Queue<BlockPos> queuedBlocks = new LinkedList<>();
    Set<BlockPos> tmpBlocks = new HashSet<>();
    Set<BlockPos> checkedBlocks = new HashSet<>();
    BlockPos baseblock = blockPos;
    Block logBlock = world.getBlockState(blockPos).getBlock();
    this.tree = new Tree();

    queuedBlocks.add(blockPos);
    this.tree.InsertWood(blockPos);

    while (!queuedBlocks.isEmpty()) {

      BlockPos currentPos = queuedBlocks.remove();
      checkedBlocks.add(currentPos);

      tmpBlocks.addAll(LookAroundBlock(logBlock, currentPos, world, checkedBlocks));

      queuedBlocks.addAll(tmpBlocks);
      checkedBlocks.addAll(tmpBlocks);
      tmpBlocks.clear();
    }

    Set<BlockPos> tmpLeaves = new HashSet<>();
    tmpLeaves.addAll(this.tree.GetM_Leaves());

    for (BlockPos blockPos1 : tmpLeaves) {
      checkedBlocks.add(blockPos1);
      LookAroundBlock(null, blockPos1, world, checkedBlocks);
    }
    Set<BlockPos> remBlocks = new HashSet<>();
    for(BlockPos bp : tree.GetM_Wood())
    {
      boolean isBelow = bp.getY() <= baseblock.getY();
      // OurcraftTimbery.LOGGER.info("bp-y: "+ bp.getY() + " <= base-y: " +baseblock.getY() +" = "+isBelow);
      if(isBelow)
        remBlocks.add(bp);
    }
    tree.GetM_Wood().removeAll(remBlocks);


    this.tree.setM_Position(blockPos);
    m_Trees.put(entityPlayer.getUniqueID(), this.tree);

    return this.tree.GetLogCount();
  }


  private Queue<BlockPos> LookAroundBlock(Block logBlock, BlockPos currentPos, World world, Set<BlockPos> checkedBlocks) {
    Queue<BlockPos> queuedBlocks = new LinkedList<>();


    for (int i = -1; i <= 1; i++) {
      BlockPos tmpPos = new BlockPos(currentPos.getX() + 1, currentPos.getY() + i, currentPos.getZ());
      if (CheckBlock(world, tmpPos, checkedBlocks, logBlock)) {
        queuedBlocks.add(tmpPos);
      }

      tmpPos = new BlockPos(currentPos.getX(), currentPos.getY() + i, currentPos.getZ() + 1);
      if (CheckBlock(world, tmpPos, checkedBlocks, logBlock)) {
        queuedBlocks.add(tmpPos);
      }

      tmpPos = new BlockPos(currentPos.getX() - 1, currentPos.getY() + i, currentPos.getZ());
      if (CheckBlock(world, tmpPos, checkedBlocks, logBlock)) {
        queuedBlocks.add(tmpPos);
      }

      tmpPos = new BlockPos(currentPos.getX(), currentPos.getY() + i, currentPos.getZ() - 1);
      if (CheckBlock(world, tmpPos, checkedBlocks, logBlock)) {
        queuedBlocks.add(tmpPos);
      }

      tmpPos = new BlockPos(currentPos.getX() + 1, currentPos.getY() + i, currentPos.getZ() + 1);
      if (CheckBlock(world, tmpPos, checkedBlocks, logBlock)) {
        queuedBlocks.add(tmpPos);
      }

      tmpPos = new BlockPos(currentPos.getX() - 1, currentPos.getY() + i, currentPos.getZ() - 1);
      if (CheckBlock(world, tmpPos, checkedBlocks, logBlock)) {
        queuedBlocks.add(tmpPos);
      }

      tmpPos = new BlockPos(currentPos.getX() - 1, currentPos.getY() + i, currentPos.getZ() + 1);
      if (CheckBlock(world, tmpPos, checkedBlocks, logBlock)) {
        queuedBlocks.add(tmpPos);
      }

      tmpPos = new BlockPos(currentPos.getX() + 1, currentPos.getY() + i, currentPos.getZ() - 1);
      if (CheckBlock(world, tmpPos, checkedBlocks, logBlock)) {
        queuedBlocks.add(tmpPos);
      }

      tmpPos = new BlockPos(currentPos.getX(), currentPos.getY() + i, currentPos.getZ());
      if (CheckBlock(world, tmpPos, checkedBlocks, logBlock)) {
        queuedBlocks.add(tmpPos);
      }
    }

    return queuedBlocks;
  }


  private boolean CheckBlock(World world, BlockPos blockPos, Set<BlockPos> checkedBlocks, Block originBlock) {
    if (checkedBlocks.contains(blockPos)) {
      return false;
    }

    if (world.getBlockState(blockPos).getBlock() != originBlock) {

      if (OurcraftTimbery.plantSapling && world.getBlockState(blockPos).getMaterial() == Material.LEAVES && this.tree.GetM_Leaves().isEmpty()) {
        this.tree.InsertLeaf(blockPos);
      }

      if (OurcraftTimbery.decayLeaves && OurcraftTimbery.registeredLeaves.contains(world.getBlockState(blockPos).getBlock())) {
        this.tree.InsertLeaf(blockPos);

        return false;
      }

      if (OurcraftTimbery.decayLeaves && world.getBlockState(blockPos).getMaterial() == Material.LEAVES) {
        this.tree.InsertLeaf(blockPos);

        return false;
      }
      return false;
    }


    this.tree.InsertWood(blockPos);

    return true;
  }


  public void DestroyTree(IWorld world, PlayerEntity entityPlayer) {
    int soundReduced = 0;

    if (m_Trees.containsKey(entityPlayer.getUniqueID())) {

      Tree tmpTree = m_Trees.get(entityPlayer.getUniqueID());

      for (BlockPos blockPos : tmpTree.GetM_Wood()) {

        if (soundReduced <= 1) {
          world.destroyBlock(blockPos, true);
        } else {
          Block.spawnDrops(world.getBlockState(blockPos), world.getWorld(), blockPos, world.getTileEntity(blockPos), (Entity)entityPlayer, entityPlayer.getHeldItemMainhand());
        }

        world.removeBlock(blockPos, true);

        soundReduced++;
      }

      if (OurcraftTimbery.plantSapling && !tmpTree.GetM_Leaves().isEmpty()) {

        BlockPos tmpPosition = getLastElement(tmpTree.GetM_Leaves());
        PlantSapling(world.getWorld(), tmpPosition, tmpTree.getM_Position());
      }

      soundReduced = 0;

      if (OurcraftTimbery.decayLeaves)
      {
        for (BlockPos blockPos : tmpTree.GetM_Leaves()) {

          if (soundReduced <= 1) {
            world.destroyBlock(blockPos, true);
          } else {
            Block.spawnDrops(world.getBlockState(blockPos), world.getWorld(), blockPos, world.getTileEntity(blockPos), (Entity)entityPlayer, entityPlayer.getHeldItemMainhand());
          }

          world.removeBlock(blockPos, true);

          soundReduced++;
        }
      }
    }
  }


  private void PlantSapling(World world, BlockPos blockPos, BlockPos originPos) {
    Set<ItemStack> leafDrop = new HashSet<>();
    BlockPos plantPos1 = new BlockPos(originPos.getX() - 1, originPos.getY(), originPos.getZ() - 1);
    int counter = 0;

    while (leafDrop.isEmpty() && counter <= 100) {
      NonNullList<ItemStack> tmpList = NonNullList.create();

      tmpList.addAll(Block.getDrops(world.getBlockState(blockPos), (ServerWorld)world.getWorld(), blockPos, null));

      leafDrop.addAll((Collection<? extends ItemStack>)tmpList);

      counter++;
    }

    if (leafDrop.isEmpty()) {
      return;
    }

    FakePlayer fakePlayer = FakePlayerFactory.getMinecraft((ServerWorld)world);
    fakePlayer.setHeldItem(Hand.MAIN_HAND, leafDrop.iterator().next());

    for (ItemStack itemStack : leafDrop) {
      if (ItemTags.LEAVES.contains(itemStack.getItem()))
        itemStack.onItemUse(new ItemUseContext((PlayerEntity)fakePlayer, Hand.MAIN_HAND, new BlockRayTraceResult(new Vec3d(0.0D, 0.0D, 0.0D), Direction.NORTH, plantPos1, false)));
    }
  }
}
