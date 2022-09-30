package com.jsorrell.carpetskyadditions.settings;

import carpet.api.settings.CarpetRule;
import carpet.api.settings.Rule;
import carpet.api.settings.Validator;
import carpet.api.settings.Validators;
import com.jsorrell.carpetskyadditions.Build;
import net.minecraft.server.command.ServerCommandSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;

import static carpet.api.settings.RuleCategory.FEATURE;

public class SkyAdditionsSettings {
  public static final Logger LOG = LoggerFactory.getLogger(Build.NAME);
  public static final String GENERATION = "generation";
  public static final String WANDERING_TRADER = "wandering_trader";

  /* Generation -- Only obeyed with SkyBlock world generation */
  @Rule(categories = GENERATION)
  public static boolean generateEndPortals = true;

  @Rule(categories = GENERATION)
  public static boolean generateSilverfishSpawners = true;

  @Rule(categories = GENERATION)
  public static boolean generateLavaSource = true;

  @Rule(categories = GENERATION)
  public static boolean generateAncientCityPortals = true;

  @Rule(categories = GENERATION)
  public static boolean generateMagmaCubeSpawners = false;

  /* Features -- can be used in any generation, SkyBlock or not.
   *  These all default to false so this mod is impotent by default like Carpet.
   *  When a SkyBlock world is created, some are enabled by default using the SkyBlockSetting annotation. */

  /* Wandering Trader */
  private static class TallFlowersTradesNameFix extends SettingFixer {
    @Override
    public String[] alternateNames() {
      return new String[]{"wanderingTraderSkyBlockTrades"};
    }

    @Override
    public FieldPair fix(FieldPair fieldPair) {
      fieldPair.setName("tallFlowersFromWanderingTrader");
      return fieldPair;
    }
  }

  @Rule(categories = {FEATURE, WANDERING_TRADER})
  @SkyAdditionsSetting(value = "true", fixer = TallFlowersTradesNameFix.class)
  public static boolean tallFlowersFromWanderingTrader = false;

  /* Wandering Trader Lava */
  @Rule(categories = {FEATURE, WANDERING_TRADER})
  public static boolean lavaFromWanderingTrader = false;

  /* Lightning Electrifies Vines */
  @Rule(categories = FEATURE)
  @SkyAdditionsSetting("true")
  public static boolean lightningElectrifiesVines = false;

  /* Renewable Budding Amethysts */
  @Rule(categories = FEATURE)
  @SkyAdditionsSetting("true")
  public static boolean renewableBuddingAmethysts = false;

  /* Dolphins Find Hearts of the Sea */
  @Rule(categories = FEATURE)
  @SkyAdditionsSetting("true")
  public static boolean renewableHeartsOfTheSea = false;

  /* Anvils Compact Coal into Diamonds */
  @Rule(categories = FEATURE)
  @SkyAdditionsSetting("true")
  public static boolean renewableDiamonds = false;

  /* Goats Ramming Break Nether Wart Blocks */
  @Rule(categories = FEATURE)
  @SkyAdditionsSetting("true")
  public static boolean rammingWart = false;

  /* Foxes Spawn With Berries */
  private static class SweetBerriesFixer extends SettingFixer {
    @Override
    public String[] alternateNames() {
      return new String[]{"foxesSpawnWithBerries"};
    }

    @Override
    public FieldPair fix(FieldPair fieldPair) {
      if (fieldPair.getName().equals("foxesSpawnWithBerries")) {
        fieldPair.setName("foxesSpawnWithSweetBerriesChance");

        if ("true".equalsIgnoreCase(fieldPair.getValue())) {
          Field settingField;
          try {
            settingField = SkyAdditionsSettings.class.getDeclaredField("foxesSpawnWithSweetBerriesChance");
          } catch (Exception e) {
            return null;
          }
          fieldPair.setValue(settingField.getAnnotation(SkyAdditionsSetting.class).value());
        } else if ("false".equalsIgnoreCase(fieldPair.getValue())) {
          fieldPair.setValue("0");
        }
      }

      return fieldPair;
    }
  }

  @Rule(
    categories = FEATURE,
    options = {"0", "0.2", "1"},
    strict = false,
    validators = Validators.Probablity.class
  )
  @SkyAdditionsSetting(value = "0.2", fixer = SweetBerriesFixer.class)
  public static double foxesSpawnWithSweetBerriesChance = 0d;

  /* Poisonous Potatoes Convert Spiders into Cave Spiders */
  @Rule(categories = FEATURE)
  @SkyAdditionsSetting("true")
  public static boolean poisonousPotatoesConvertSpiders = false;

  /* Saplings Placed on Sand Turn into Dead Bushes */
  @Rule(categories = FEATURE)
  @SkyAdditionsSetting("true")
  public static boolean saplingsDieOnSand = false;

  @Rule(categories = FEATURE)
  @SkyAdditionsSetting("true")
  public static boolean renewableEchoShards = false;

  private static class AllayableVexesFixer extends SettingFixer {
    @Override
    public String[] alternateNames() {
      return new String[]{"renewableAllays"};
    }

    @Override
    public FieldPair fix(FieldPair fieldPair) {
      fieldPair.setName("allayableVexes");
      return fieldPair;
    }
  }

  @Rule(categories = FEATURE)
  @SkyAdditionsSetting(value = "true", fixer = AllayableVexesFixer.class)
  public static boolean allayableVexes = false;

  @Rule(categories = FEATURE)
  @SkyAdditionsSetting("true")
  public static boolean coralErosion = false;

  @Rule(categories = FEATURE)
  @SkyAdditionsSetting("true")
  public static boolean hugeMushroomsSpreadMycelium = false;

  @Rule(categories = FEATURE)
  @SkyAdditionsSetting("true")
  public static boolean renewableNetherrack = false;

  private static class RenewableDeepslateSetting extends Validator<String> {
    @Override
    public String validate(ServerCommandSource source, CarpetRule<String> currentRule, String newValue, String string) {
      SkyAdditionsSettings.doRenewableDeepslate = !"false".equalsIgnoreCase(newValue);
      SkyAdditionsSettings.renewableDeepslateFromSplash = "true".equalsIgnoreCase(newValue);

      return newValue;
    }
  }

  @Rule(
    categories = FEATURE,
    options = {"true", "false", "no_splash"},
    validators = RenewableDeepslateSetting.class
  )
  @SkyAdditionsSetting("true")
  @SuppressWarnings("unused")
  public static String renewableDeepslate = "false";
  public static boolean doRenewableDeepslate = false;
  public static boolean renewableDeepslateFromSplash = false;

  @Rule(categories = FEATURE)
  @SkyAdditionsSetting("true")
  public static boolean renewableSwiftSneak = false;

  /* Wandering Trader Spawn Chance */
  public static class WanderingTraderSpawnChanceValidator extends Validator<Double> {
    @Override
    public Double validate(ServerCommandSource source, CarpetRule<Double> currentRule, Double newValue, String string) {
      return (0.025 <= newValue && newValue <= 1) ? newValue : null;
    }

    @Override
    public String description() {
      return "Must be between 0.025 and 1";
    }
  }

  @Rule(
    categories = WANDERING_TRADER,
    options = {"0.075", "0.2", "1"},
    strict = false,
    validators = WanderingTraderSpawnChanceValidator.class
  )
  public static double maxWanderingTraderSpawnChance = 0.075;

  /* Wandering Trader Spawn Rate */
  public static class POSITIVE_NUMBER<T extends Number> extends Validator<T> {
    @Override
    public T validate(ServerCommandSource source, CarpetRule<T> currentRule, T newValue, String string) {
      return 0 < newValue.doubleValue() ? newValue : null;
    }

    @Override
    public String description() {
      return "Must be a positive number";
    }
  }

  @Rule(
    categories = WANDERING_TRADER,
    options = {"6000", "24000", "72000"},
    strict = false,
    validators = POSITIVE_NUMBER.class
  )
  public static int wanderingTraderSpawnRate = 24000;
}
