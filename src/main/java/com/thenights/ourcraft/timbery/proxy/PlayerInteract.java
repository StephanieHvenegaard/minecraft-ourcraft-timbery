package com.thenights.ourcraft.timbery.proxy;

import net.minecraft.util.math.BlockPos;

class PlayerInteract
{
  public BlockPos m_BlockPos;
  public float m_LogCount;
  public int m_AxeDurability;

  public PlayerInteract(BlockPos blockPos, float logCount, int axeDurability) {
    this.m_BlockPos = blockPos;
    this.m_LogCount = logCount;
    this.m_AxeDurability = axeDurability;
  }
}
