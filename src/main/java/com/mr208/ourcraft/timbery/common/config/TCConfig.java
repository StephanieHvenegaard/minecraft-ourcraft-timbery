package com.mr208.ourcraft.timbery.common.config;

import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.List;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;


@EventBusSubscriber
public class TCConfig
{
  protected static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
  public static final ForgeConfigSpec SPEC = BUILDER.build();
  public static final Logs logs = new Logs();
  public static final Leaves leaves = new Leaves();
  public static final Axes axes = new Axes();
  public static final Options options = new Options();


  public static class Logs
  {
    public final ForgeConfigSpec.ConfigValue<List<? extends String>> logBlocks;
    public final ForgeConfigSpec.ConfigValue<Boolean> logTag;
    private ArrayList<String> logsDef = Lists.newArrayList();


    Logs() {
      TCConfig.BUILDER.push("logs");
      this
              .logTag = (ForgeConfigSpec.ConfigValue<Boolean>)TCConfig.BUILDER.comment("Add the contents of the Tag minecraft:logs as valid Logs").define("Use Logs Tag", true);
      this
              .logBlocks = TCConfig.BUILDER.comment("Add the registry name of blocks here that should count as Logs").defineList("Log Blocks", this.logsDef, entry -> entry instanceof String);
      TCConfig.BUILDER.pop();
    }
  }


  public static class Leaves
  {
    public final ForgeConfigSpec.ConfigValue<List<? extends String>> leaves;
    public final ForgeConfigSpec.ConfigValue<Boolean> leavesTag;
    private ArrayList<String> leavesDef = Lists.newArrayList();


    Leaves() {
      TCConfig.BUILDER.push("leaves");
      this
              .leavesTag = (ForgeConfigSpec.ConfigValue<Boolean>)TCConfig.BUILDER.comment("Add the content of the Tag minecraft:leaves as valid Leaves").define("Use Leaves Tag", true);
      this
              .leaves = TCConfig.BUILDER.comment("Add the registry name of blocks here that should count as Leaves").defineList("Leaves Blocks", this.leavesDef, entry -> entry instanceof String);
      TCConfig.BUILDER.pop();
    }
  }


  public static class Axes
  {
    public final ForgeConfigSpec.ConfigValue<List<? extends String>> blacklistAxe;
    private ArrayList<String> blacklistAxeDef = Lists.newArrayList();


    Axes() {
      TCConfig.BUILDER.push("axes");
      this
              .blacklistAxe = TCConfig.BUILDER.comment(new String[] { "Any Axe added to this list will not work with Tree Choppin", "One Entry Per line, no Commas" }).defineList("Axe Blacklist", this.blacklistAxeDef, entry -> entry instanceof String);
      TCConfig.BUILDER.pop();
    }
  }


  public static class Options
  {
    public final ForgeConfigSpec.BooleanValue disableShift;
    public final ForgeConfigSpec.BooleanValue reverseShift;
    public final ForgeConfigSpec.BooleanValue plantSapling;
    public final ForgeConfigSpec.BooleanValue decayLeaves;

    Options() {
      TCConfig.BUILDER.push("options");
      this

              .disableShift = TCConfig.BUILDER.comment("Ignore Sneaking when chopping trees").define("disableShift", false);
      this

              .reverseShift = TCConfig.BUILDER.comment("Only chop down trees when sneaking").define("reverseShift", false);
      this

              .plantSapling = TCConfig.BUILDER.comment("Automaticly plant sapling on tree chop").define("plantSapling", true);
      this

              .decayLeaves = TCConfig.BUILDER.comment("Cut down leaves and logs").define("decayLeaves", true);
      TCConfig.BUILDER.pop();
    }
  }
}
