package com.thenights.ourcraft.timbery.core;

import com.electronwill.nightconfig.core.CommentedConfig;
import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import com.electronwill.nightconfig.core.io.WritingMode;
import com.thenights.ourcraft.timbery.common.network.ClientSettingsMessage;
import com.thenights.ourcraft.timbery.common.network.ServerSettingsMessage;
import java.util.HashSet;
import java.util.Set;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLLoadCompleteEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLPaths;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.simple.SimpleChannel;
import net.minecraftforge.registries.ForgeRegistries;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


@Mod(OurcraftTimbery.MODID)
public class OurcraftTimbery
{
  public static final String MODID = "ourcraft-timbery";
  public static final Logger LOGGER = LogManager.getLogger(MODID);                    // Logger
  private static final String PROTOCOL_VERSION = Integer.toString(1);
  public static SimpleChannel channel = NetworkRegistry.ChannelBuilder.named(new ResourceLocation("ourcraft-timbery", "main_channel"))
          .clientAcceptedVersions(PROTOCOL_VERSION::equals)
          .serverAcceptedVersions(PROTOCOL_VERSION::equals)
          .networkProtocolVersion(() -> PROTOCOL_VERSION)
          .simpleChannel();

  public static Set<Item> blacklistAxes = new HashSet<>();
  public static Set<Block> registeredLogs = new HashSet<>();
  public static Set<Block> registeredLeaves = new HashSet<>();

  public static boolean decayLeaves;
  public static boolean disableShift;
  public static boolean reverseShift;
  public static boolean useTagLog;
  public static boolean useTagLeaves;

  public OurcraftTimbery() {
    CommentedFileConfig configData = (CommentedFileConfig)CommentedFileConfig.builder(FMLPaths.CONFIGDIR.get().resolve("ourcraft-timbery.toml")).sync().autosave().writingMode(WritingMode.REPLACE).build();

    configData.load();

    TimberyConfig.SPEC.setConfig((CommentedConfig)configData);

    FMLJavaModLoadingContext.get().getModEventBus().addListener(this::loadComplete);
    FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
  }


  public void setup(FMLCommonSetupEvent event) {
    int pktID = 0;

    channel.registerMessage(pktID++, ServerSettingsMessage.class, ServerSettingsMessage::encode, ServerSettingsMessage::decode, ServerSettingsMessage.Handler::handle);
    channel.registerMessage(pktID++, ClientSettingsMessage.class, ClientSettingsMessage::encode, ClientSettingsMessage::decode, ClientSettingsMessage.Handler::handle);
  }

  public void loadComplete(FMLLoadCompleteEvent event) {
    reverseShift = ((Boolean) TimberyConfig.options.reverseShift.get()).booleanValue();
    disableShift = ((Boolean) TimberyConfig.options.disableShift.get()).booleanValue();
    decayLeaves = ((Boolean) TimberyConfig.options.decayLeaves.get()).booleanValue();
    useTagLeaves = ((Boolean) TimberyConfig.logs.logTag.get()).booleanValue();
    useTagLeaves = ((Boolean) TimberyConfig.leaves.leavesTag.get()).booleanValue();

    for (String axe : TimberyConfig.axes.blacklistAxe.get()) {

      Item temp = (Item)ForgeRegistries.ITEMS.getValue(new ResourceLocation(axe));
      if (temp != Items.AIR) {
        blacklistAxes.add(temp);
      }
    }
    if (useTagLog) {
      registeredLogs.addAll(BlockTags.LOGS.getAllElements());
    }
    for (String log : TimberyConfig.logs.logBlocks.get()) {

      Block temp = (Block)ForgeRegistries.BLOCKS.getValue(new ResourceLocation(log));
      if (temp != Blocks.AIR && !registeredLogs.contains(temp)) {
        registeredLogs.add(temp);
      }
    }
    if (useTagLeaves) {
      registeredLeaves.addAll(BlockTags.LEAVES.getAllElements());
    }
    for (String log : TimberyConfig.leaves.leaves.get()) {

      Block temp = (Block)ForgeRegistries.BLOCKS.getValue(new ResourceLocation(log));
      if (temp != Blocks.AIR && !registeredLeaves.contains(temp))
        registeredLeaves.add(temp);
    }
  }
}
