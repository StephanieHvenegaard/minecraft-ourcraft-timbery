package com.mr208.ourcraft.timbery.proxy;

import com.mr208.ourcraft.timbery.common.config.TCConfig;
import com.mr208.ourcraft.timbery.common.handler.TreeHandler;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import com.mr208.ourcraft.timbery.core.Main;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.ToolType;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;



@EventBusSubscriber
public class CommonProxy
{
  public static Map<UUID, Boolean> m_PlayerPrintNames = new HashMap<>();
  protected static Map<UUID, PlayerInteract> m_PlayerData = new HashMap<>();

  protected static TreeHandler treeHandler;


  @SubscribeEvent
  public static void InteractWithTree(PlayerInteractEvent interactEvent) {
    boolean shifting = true;

    if (!((Boolean) TCConfig.options.disableShift.get()).booleanValue()) {
      if (interactEvent.getEntityPlayer().isSneaking() && !Main.reverseShift) {
        shifting = false;
      }

      if (!interactEvent.getEntityPlayer().isSneaking() && Main.reverseShift) {
        shifting = false;
      }
    }

    if (CheckWoodenBlock(interactEvent.getWorld(), interactEvent.getPos()) && CheckItemInHand(interactEvent.getEntityPlayer()) && shifting) {

      int axeDurability = interactEvent.getEntityPlayer().getHeldItemMainhand().getMaxDamage() - interactEvent.getEntityPlayer().getHeldItemMainhand().getDamage();

      if (m_PlayerData.containsKey(interactEvent.getEntityPlayer().getUniqueID()) && ((PlayerInteract)m_PlayerData
              .get(interactEvent.getEntityPlayer().getUniqueID())).m_BlockPos.equals(interactEvent.getPos()) && ((PlayerInteract)m_PlayerData
              .get(interactEvent.getEntityPlayer().getUniqueID())).m_AxeDurability == axeDurability) {
        return;
      }

      treeHandler = new TreeHandler();
      int logCount = treeHandler.AnalyzeTree(interactEvent.getWorld(), interactEvent.getPos(), interactEvent.getEntityPlayer());




      if (interactEvent.getEntityPlayer().getHeldItemMainhand().isDamageable() && axeDurability < logCount) {
        m_PlayerData.remove(interactEvent.getEntityPlayer().getUniqueID());

        return;
      }
      if (logCount > 1) {
        m_PlayerData.put(interactEvent.getEntityPlayer().getUniqueID(), new PlayerInteract(interactEvent.getPos(), logCount, axeDurability));
      }
    } else {
      m_PlayerData.remove(interactEvent.getEntityPlayer().getUniqueID());
    }
  }


  @SubscribeEvent
  public static void BreakingBlock(PlayerEvent.BreakSpeed breakSpeed) {
    if (m_PlayerData.containsKey(breakSpeed.getEntityPlayer().getUniqueID())) {

      BlockPos blockPos = ((PlayerInteract)m_PlayerData.get(breakSpeed.getEntityPlayer().getUniqueID())).m_BlockPos;

      if (blockPos.equals(breakSpeed.getPos())) {
        breakSpeed.setNewSpeed(breakSpeed.getOriginalSpeed() / ((PlayerInteract)m_PlayerData.get(breakSpeed.getEntityPlayer().getUniqueID())).m_LogCount / 2.0F);
      } else {
        breakSpeed.setNewSpeed(breakSpeed.getOriginalSpeed());
      }
    }
  }


  @SubscribeEvent
  public static void DestroyWoodBlock(BlockEvent.BreakEvent breakEvent) {
    if (m_PlayerData.containsKey(breakEvent.getPlayer().getUniqueID())) {

      BlockPos blockPos = ((PlayerInteract)m_PlayerData.get(breakEvent.getPlayer().getUniqueID())).m_BlockPos;

      if (blockPos.equals(breakEvent.getPos())) {
        treeHandler.DestroyTree(breakEvent.getWorld(), breakEvent.getPlayer());

        if (!breakEvent.getPlayer().isCreative()) {
          int extraDamage = (int)((PlayerInteract)m_PlayerData.get(breakEvent.getPlayer().getUniqueID())).m_LogCount;
          breakEvent.getPlayer().getHeldItemMainhand().attemptDamageItem(extraDamage, breakEvent.getWorld().getRandom(), (ServerPlayerEntity)breakEvent.getPlayer());
        }
      }
    }
  }


  protected static boolean CheckWoodenBlock(World world, BlockPos blockPos) {
    if (Main.registeredLogs.contains(world.getBlockState(blockPos).getBlock())) {
      return true;
    }
    return (world.getBlockState(blockPos).getMaterial() == Material.WOOD);
  }


  protected static boolean CheckItemInHand(PlayerEntity entityPlayer) {
    if (entityPlayer.getHeldItemMainhand().isEmpty()) {
      return false;
    }

    if (Main.blacklistAxes.contains(entityPlayer.getHeldItemMainhand().getItem())) {
      return false;
    }
    return entityPlayer.getHeldItemMainhand().getToolTypes().contains(ToolType.AXE);
  }
}
