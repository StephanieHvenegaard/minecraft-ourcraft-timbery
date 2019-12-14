package com.thenights.ourcraft.timbery.proxy;

import com.thenights.ourcraft.timbery.common.handler.TreeHandler;
import com.thenights.ourcraft.timbery.core.OurcraftTimbery;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;



@EventBusSubscriber
public class ServerProxy
        extends CommonProxy
{
  @OnlyIn(Dist.DEDICATED_SERVER)
  public static void InteractWithTree(PlayerInteractEvent interactEvent) {
    boolean shifting = true;

    if (!OurcraftTimbery.disableShift) {
      if (interactEvent.getEntityPlayer().isSneaking() && !OurcraftTimbery.reverseShift) {
        shifting = false;
      }

      if (!interactEvent.getEntityPlayer().isSneaking() && OurcraftTimbery.reverseShift) {
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
}
