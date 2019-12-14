package com.mr208.treechoppin.common.handler;

import com.mr208.treechoppin.common.network.ServerSettingsMessage;
import com.mr208.treechoppin.core.TreeChoppin;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.network.NetworkDirection;


@EventBusSubscriber
public class EventHandler
{
  @SubscribeEvent
    public static void OnServerConnect(PlayerEvent.PlayerLoggedInEvent loggedInEvent) { TreeChoppin.channel.sendTo(new ServerSettingsMessage(TreeChoppin.reverseShift, TreeChoppin.disableShift), ((ServerPlayerEntity)loggedInEvent.getPlayer()).connection.netManager, NetworkDirection.PLAY_TO_CLIENT); }
}
