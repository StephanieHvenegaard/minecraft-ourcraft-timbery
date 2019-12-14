package com.mr208.ourcraft.timbery.common.network;

import java.util.function.Supplier;

import com.mr208.ourcraft.timbery.core.Main;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;



public class ServerSettingsMessage
{
  private boolean m_ReverseShift;
  private boolean m_DisableShift;

  public ServerSettingsMessage(boolean reverseShift, boolean disableShift) {
    this.m_ReverseShift = reverseShift;
    this.m_DisableShift = disableShift;
  }


  public static void encode(ServerSettingsMessage msg, PacketBuffer buf) {
    buf.writeBoolean(msg.m_ReverseShift);
    buf.writeBoolean(msg.m_DisableShift);
  }



  public static ServerSettingsMessage decode(PacketBuffer buf) { return new ServerSettingsMessage(buf.readBoolean(), buf.readBoolean()); }



  public static class Handler
  {
    public static void handle(ServerSettingsMessage msg, Supplier<NetworkEvent.Context> ctx) {
      ((NetworkEvent.Context)ctx.get()).enqueueWork(() -> {
        Main.reverseShift = msg.m_ReverseShift;
        Main.disableShift = msg.m_DisableShift;
      });

      ((NetworkEvent.Context)ctx.get()).setPacketHandled(true);
    }
  }
}
