package com.thenights.ourcraft.timbery.common.network;
import java.util.function.Supplier;

import com.thenights.ourcraft.timbery.core.OurcraftTimbery;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

public class ClientSettingsMessage
{
  private boolean m_ReverseShift;
  private boolean m_DisableShift;

  public ClientSettingsMessage(boolean reverseShift, boolean disableShift) {
    this.m_ReverseShift = reverseShift;
    this.m_DisableShift = disableShift;
  }

  public static void encode(ClientSettingsMessage msg, PacketBuffer buf) {
    buf.writeBoolean(msg.m_ReverseShift);
    buf.writeBoolean(msg.m_DisableShift);
  }

  public static ClientSettingsMessage decode(PacketBuffer buf) { return new ClientSettingsMessage(buf.readBoolean(), buf.readBoolean()); }

  public static class Handler
  {
    public static void handle(ClientSettingsMessage msg, Supplier<NetworkEvent.Context> ctx) {
      ((NetworkEvent.Context)ctx.get()).enqueueWork(() -> {
        OurcraftTimbery.reverseShift = msg.m_ReverseShift;
        OurcraftTimbery.disableShift = msg.m_DisableShift;
      });

      ((NetworkEvent.Context)ctx.get()).setPacketHandled(true);
    }
  }
}
