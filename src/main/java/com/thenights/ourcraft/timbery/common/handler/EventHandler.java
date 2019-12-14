package com.thenights.ourcraft.timbery.common.handler;

import com.thenights.ourcraft.timbery.common.network.ServerSettingsMessage;

import com.thenights.ourcraft.timbery.core.OurcraftTimbery;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.network.NetworkDirection;


@EventBusSubscriber
public class EventHandler
{
  @SubscribeEvent
    public static void OnServerConnect(PlayerEvent.PlayerLoggedInEvent loggedInEvent) { OurcraftTimbery.channel.sendTo(new ServerSettingsMessage(OurcraftTimbery.reverseShift, OurcraftTimbery.disableShift), ((ServerPlayerEntity)loggedInEvent.getPlayer()).connection.netManager, NetworkDirection.PLAY_TO_CLIENT); }
}
