package com.thenights.ourcraft.timbery.common.tree;

import java.util.HashSet;
import java.util.Set;
import net.minecraft.util.math.BlockPos;







public class Tree
{
  private Set<BlockPos> m_Wood = new HashSet<>();
  private Set<BlockPos> m_Leaves = new HashSet<>();

  private BlockPos m_Position;

  public void InsertWood(BlockPos blockPos) { this.m_Wood.add(blockPos); }



  public void InsertLeaf(BlockPos blockPos) { this.m_Leaves.add(blockPos); }



  public int GetLogCount() { return this.m_Wood.size(); }



  public int GetLeavesCount() { return this.m_Leaves.size(); }



  public Set<BlockPos> GetM_Wood() { return this.m_Wood; }



  public Set<BlockPos> GetM_Leaves() { return this.m_Leaves; }



  public BlockPos getM_Position() { return this.m_Position; }



  public void setM_Position(BlockPos m_Position) { this.m_Position = m_Position; }
}
