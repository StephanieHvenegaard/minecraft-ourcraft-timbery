package com.mr208.ourcraft.timbery.common.handler;

import com.mr208.ourcraft.timbery.common.network.ServerSettingsMessage;

import com.mr208.ourcraft.timbery.core.Main;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.network.NetworkDirection;


@EventBusSubscriber
public class EventHandler
{
  @SubscribeEvent
    public static void OnServerConnect(PlayerEvent.PlayerLoggedInEvent loggedInEvent) { Main.channel.sendTo(new ServerSettingsMessage(Main.reverseShift, Main.disableShift), ((ServerPlayerEntity)loggedInEvent.getPlayer()).connection.netManager, NetworkDirection.PLAY_TO_CLIENT); }
}
