package com.mcmoddev.lib.init;

import java.util.*;

import com.mcmoddev.basemetals.BaseMetals;
import com.mcmoddev.basemetals.util.VillagerTradeHelper;
import com.mcmoddev.lib.items.ItemMetalCrackHammer;
import com.mcmoddev.lib.items.ItemMetalIngot;
import com.mcmoddev.lib.material.MetalMaterial;

import net.minecraft.entity.passive.EntityVillager.*;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemAxe;
import net.minecraft.item.ItemHoe;
import net.minecraft.item.ItemPickaxe;
import net.minecraft.item.ItemSpade;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.village.MerchantRecipe;
import net.minecraft.village.MerchantRecipeList;
import net.minecraftforge.fml.common.Loader;

/**
 *
 * @author DrCyano
 *
 */
public abstract class VillagerTrades {

	private static boolean initDone = false;

	protected static final int TRADES_PER_LEVEL = 4;

	protected VillagerTrades() {
		throw new IllegalAccessError("Not a instantiable class");
	}

	/**
	 *
	 */
	public static void init() {
		if (initDone) {
			return;
		}

		Materials.init();
		Items.init();

		registerCommonTrades();

		initDone = true;
	}

	protected static void registerCommonTrades() {
		final Map<Integer, List<ITradeList>> tradesTable = new HashMap<>(); // integer is used as byte data: (unused) (profession) (career) (level)

		// Minecraft stores trades in a 4D array:
		// [Profession ID][Sub-profession ID][villager level - 1][trades]

		final int size = Materials.getAllMaterials().size();
		final Map<MetalMaterial, List<Item>> allArmors = new HashMap<>(size);
		final Map<MetalMaterial, Item> allHammers = new HashMap<>(size);
		final Map<MetalMaterial, Item> allSwords = new HashMap<>(size);
		final Map<MetalMaterial, Item> allHoes = new HashMap<>(size);
		final Map<MetalMaterial, Item> allAxes = new HashMap<>(size);
		final Map<MetalMaterial, Item> allPickAxes = new HashMap<>(size);
		final Map<MetalMaterial, Item> allShovels = new HashMap<>(size);
		final Map<MetalMaterial, Item> allIngots = new HashMap<>(size);

//		@SuppressWarnings("unused")
//		final Map<Item, Integer> tradeLevelMap = new HashMap<>();

		String modid = Loader.instance().activeModContainer().getModId();
		Items.getItemsByMaterial().entrySet().stream().forEach((Map.Entry<MetalMaterial, List<Item>> e) -> {
			final MetalMaterial m = e.getKey();
			if (m == null) {
				return;
			}

			for (final Item i : e.getValue()) {
				if (i.getRegistryName().getResourceDomain().equals(modid)) {
					if (i instanceof ItemArmor) {
						allArmors.computeIfAbsent(m, (MetalMaterial g) -> new ArrayList<>()).add(i);
					} else if (i instanceof ItemMetalCrackHammer) {
						allHammers.put(m, i);
					} else if (i instanceof ItemSword) {
						allSwords.put(m, i);
					} else if (i instanceof ItemHoe) {
						allHoes.put(m, i);
					} else if (i instanceof ItemAxe) {
						allAxes.put(m, i);
					} else if (i instanceof ItemPickaxe) {
						allPickAxes.put(m, i);
					} else if (i instanceof ItemSpade) {
						allShovels.put(m, i);
					} else if (i instanceof ItemMetalIngot) {
						allIngots.put(m, i);
					}
				}
			}
		});

		for (final MetalMaterial m : Materials.getAllMaterials()) {
			final float value = m.hardness + m.strength + m.magicAffinity + m.getToolHarvestLevel();
			if (m.isRare) {
				continue;
			}

			// for reference, iron has a value of 21.5, gold would be 14, copper
			// is 14, and diamond is 30
			final int emeraldPurch = emeraldPurchaseValue(value);
			final int emeraldSale = emeraldSaleValue(value);
			final int tradeLevel = tradeLevel(value);

			if ((emeraldPurch > 64) || (emeraldSale > 64)) {
				continue; // too expensive
			}

			final int armorsmith = (3 << 16) | (1 << 8) | (tradeLevel);
			final int weaponsmith = (3 << 16) | (2 << 8) | (tradeLevel);
			final int toolsmith = (3 << 16) | (3 << 8) | (tradeLevel);

			if (allIngots.containsKey(m)) {
				final ITradeList[] ingotTrades = makeTradePalette(makePurchasePalette(emeraldPurch, 12, allIngots.get(m)), makeSalePalette(emeraldSale, 12, allIngots.get(m)));
				tradesTable.computeIfAbsent(armorsmith, (Integer key) -> new ArrayList<>()).addAll(Arrays.asList(ingotTrades));
				tradesTable.computeIfAbsent(weaponsmith, (Integer key) -> new ArrayList<>()).addAll(Arrays.asList(ingotTrades));
				tradesTable.computeIfAbsent(toolsmith, (Integer key) -> new ArrayList<>()).addAll(Arrays.asList(ingotTrades));
			}
			if (allHammers.containsKey(m) && allPickAxes.containsKey(m) && allAxes.containsKey(m) && allShovels.containsKey(m) && allHoes.containsKey(m)) {
				tradesTable.computeIfAbsent(toolsmith, (Integer key) -> new ArrayList<>()).addAll(Arrays.asList(makeTradePalette(makePurchasePalette(emeraldPurch, 1, allPickAxes.get(m), allAxes.get(m), allShovels.get(m), allHoes.get(m)))));
				tradesTable.computeIfAbsent((3 << 16) | (3 << 8) | (tradeLevel + 1), (Integer key) -> new ArrayList<>()).addAll(Arrays.asList(makeTradePalette(makePurchasePalette(emeraldPurch, 1, allHammers.get(m)))));
			}
			if (allSwords.containsKey(m)) {
				tradesTable.computeIfAbsent(weaponsmith, (Integer key) -> new ArrayList<>()).addAll(Arrays.asList(makeTradePalette(makePurchasePalette((emeraldPurch + (int) (m.getBaseAttackDamage() / 2)) - 1, 1, allSwords.get(m)))));
			}
			if (allArmors.containsKey(m)) {
				tradesTable.computeIfAbsent(armorsmith, (Integer key) -> new ArrayList<>()).addAll(Arrays.asList(makeTradePalette(makePurchasePalette(emeraldPurch + (int) (m.hardness / 2), 1, allArmors.get(m).toArray(new Item[0])))));
			}

			if (m.magicAffinity > 5) {
				if (allHammers.containsKey(m)) {
					tradesTable.computeIfAbsent((3 << 16) | (3 << 8) | (tradeLevel + 2), (Integer key) -> new ArrayList<>()).addAll(Collections.singletonList(new ListEnchantedItemForEmeralds(allHammers.get(m), new PriceInfo(emeraldPurch + 7, emeraldPurch + 12))));
				}
				if (allPickAxes.containsKey(m)) {
					tradesTable.computeIfAbsent((3 << 16) | (3 << 8) | (tradeLevel + 1), (Integer key) -> new ArrayList<>()).addAll(Collections.singletonList(new ListEnchantedItemForEmeralds(allPickAxes.get(m), new PriceInfo(emeraldPurch + 7, emeraldPurch + 12))));
				}
				if (allArmors.containsKey(m)) {
					for (int i = 0; i < allArmors.get(m).size(); i++) {
						tradesTable.computeIfAbsent((3 << 16) | (1 << 8) | (tradeLevel + 1), (Integer key) -> new ArrayList<>()).addAll(Collections.singletonList(new ListEnchantedItemForEmeralds(allArmors.get(m).get(i), new PriceInfo(emeraldPurch + 7 + (int) (m.hardness / 2), emeraldPurch + 12 + (int) (m.hardness / 2)))));
					}
				}
				if (allSwords.containsKey(m)) {
					tradesTable.computeIfAbsent((3 << 16) | (2 << 8) | (tradeLevel + 1), (Integer key) -> new ArrayList<>()).addAll(Collections.singletonList(new ListEnchantedItemForEmeralds(allSwords.get(m), new PriceInfo(emeraldPurch + 7 + (int) (m.getBaseAttackDamage() / 2) - 1, emeraldPurch + 12 + (int) (m.getBaseAttackDamage() / 2) - 1))));
				}
			}
		}

		/*
		if (Loader.instance().activeModContainer().getModId().equals("basemetals")) {
			if (Options.ENABLE_CHARCOAL) {
				tradesTable.computeIfAbsent((3 << 16) | (1 << 8) | (1), (Integer key) -> new ArrayList<>()).addAll(Arrays.asList(makePurchasePalette(1, 10, Items.charcoal_powder)));
				tradesTable.computeIfAbsent((3 << 16) | (2 << 8) | (1), (Integer key) -> new ArrayList<>()).addAll(Arrays.asList(makePurchasePalette(1, 10, Items.charcoal_powder)));
				tradesTable.computeIfAbsent((3 << 16) | (3 << 8) | (1), (Integer key) -> new ArrayList<>()).addAll(Arrays.asList(makePurchasePalette(1, 10, Items.charcoal_powder)));
			}

			if (Options.ENABLE_COAL) {
				tradesTable.computeIfAbsent((3 << 16) | (1 << 8) | (1), (Integer key) -> new ArrayList<>()).addAll(Arrays.asList(makePurchasePalette(1, 10, Items.coal_powder)));
				tradesTable.computeIfAbsent((3 << 16) | (2 << 8) | (1), (Integer key) -> new ArrayList<>()).addAll(Arrays.asList(makePurchasePalette(1, 10, Items.coal_powder)));
				tradesTable.computeIfAbsent((3 << 16) | (3 << 8) | (1), (Integer key) -> new ArrayList<>()).addAll(Arrays.asList(makePurchasePalette(1, 10, Items.coal_powder)));
			}
		}
		*/

		for (final Integer k : tradesTable.keySet()) {
			final List<ITradeList> trades = tradesTable.get(k);
			final int profession = (k >> 16) & 0xFF;
			final int career = (k >> 8) & 0xFF;
			final int level = k & 0xFF;

			try {
				VillagerTradeHelper.insertTrades(profession, career, level, new MultiTradeGenerator(TRADES_PER_LEVEL, trades));
			} catch (NoSuchFieldException | IllegalAccessException ex) {
				BaseMetals.logger.error("Java Reflection Exception", ex);
//				FMLLog.log(Level.ERROR, ex, "Java Reflection Exception");
			}
		}
	}

	protected static int emeraldPurchaseValue(float value) {
		return Math.max(1, (int) (value * 0.2F));
	}

	protected static int emeraldSaleValue(float value) {
		return Math.max(1, emeraldPurchaseValue(value) / 3);
	}

	protected static int tradeLevel(float value) {
		return Math.max(1, Math.min(4, (int) (value * 0.1F)));
	}

	protected static int fluctuation(int baseValue) {
		if (baseValue <= 1) {
			return 0;
		}
		return Math.max(2, baseValue / 4);
	}

	protected static ITradeList[] makePurchasePalette(int emeraldPrice, int stackSize, Item... items) {
		final ITradeList[] trades = new ITradeList[items.length];
		for (int i = 0; i < items.length; i++) {
			final Item item = items[i];
			trades[i] = new SimpleTrade(new ItemStack(net.minecraft.init.Items.EMERALD, emeraldPrice, 0), fluctuation(emeraldPrice), null, 0, new ItemStack(item, stackSize, 0), 0);
		}
		return trades;
	}

	protected static ITradeList[] makeSalePalette(int emeraldValue, int stackSize, Item... items) {
		final ITradeList[] trades = new ITradeList[items.length];
		for (int i = 0; i < items.length; i++) {
			final Item item = items[i];
			trades[i] = new SimpleTrade(new ItemStack(item, stackSize, 0), fluctuation(stackSize), null, 0, new ItemStack(net.minecraft.init.Items.EMERALD, emeraldValue, 0), 0);
		}
		return trades;
	}

	protected static ITradeList[] makeTradePalette(ITradeList[]... list) {
		if (list.length == 1) {
			return list[0];
		}
		int totalsize = 0;
		for (final ITradeList[] e : list) {
			totalsize += e.length;
		}
		final ITradeList[] concat = new ITradeList[totalsize];
		int index = 0;
		int element = 0;
		while (index < totalsize) {
			System.arraycopy(list[element], 0, concat, index, list[element].length);
			index += list[element].length;
			element++;
		}
		return concat;
	}

	/**
	 * This ITradeList object holds a list of ITradeLists and picks a few at random to place in a merchant's trade menu.
	 */
	public static class MultiTradeGenerator implements ITradeList {
		private final int numberOfTrades;
		private final ITradeList[] trades;

		/**
		 * Creates an ITradeList instanec that randomly adds multiple trades at a time
		 * @param tradeCount Number of trades to add to the merchant's trade menu
		 * @param tradePalette The trades to randomly choose from
		 */
		public MultiTradeGenerator(int tradeCount, List<ITradeList> tradePalette) {
			numberOfTrades = Math.min(tradeCount, tradePalette.size());
			trades = tradePalette.toArray(new ITradeList[tradePalette.size()]);
		}

		/**
		 * Invoked when the merchant generates its trade menu
		 * @param recipeList existing trade menu
		 * @param random a psuedorandom number generator instance
		 */
		@Override
		public void modifyMerchantRecipeList(MerchantRecipeList recipeList, Random random) {
			for (int n = 0; n < numberOfTrades; n++) {
				trades[random.nextInt(trades.length)].modifyMerchantRecipeList(recipeList, random);
			}
		}

		/**
		 * For debugging purposes only
		 * @return String representation
		 */
		@Override
		public String toString() {
			return MultiTradeGenerator.class.getSimpleName() + ": " + numberOfTrades + " trades chosen from " + Arrays.toString(trades);
		}
	}

	/**
	 * A simple, easy to use ITradeList class that holds a single trade recipe
	 */
	public static class SimpleTrade implements ITradeList {

		private final ItemStack input1;
		private final int maxInputMarkup1;
		private final ItemStack input2;
		private final int maxInputMarkup2;
		private final ItemStack output;
		private final int maxOutputMarkup;
		private final int maxTrades;
		private final int maxTradeVariation;

		/**
		 * Full constructor for making a trade recipe
		 * @param in1 Item for the left purchase price trade slot
		 * @param variation1 range of variation in quantity of <code>in1</code>
		 * @param in2 Item for the right purchase price trade slot. Can be <code>null</code> (and usually is)
		 * @param variation2 range of variation in quantity of <code>in2</code>
		 * @param out The item to be purchased (trade recipe output slot)
		 * @param variationOut range of variation in quantity of <code>out</code>
		 * @param numberTrades Max number of trades before this recipe is invalidated (-1 for infinite trading)
		 * @param tradeNumberVariation  range of variation in value of <code>numberTrades</code> (-1 to disable)
		 */
		public SimpleTrade(ItemStack in1, int variation1, ItemStack in2, int variation2, ItemStack out, int variationOut, int numberTrades, int tradeNumberVariation) {
			input1 = in1;
			maxInputMarkup1 = variation1;
			input2 = in2;
			maxInputMarkup2 = variation2;
			output = out;
			maxOutputMarkup = variationOut;
			maxTrades = numberTrades;
			maxTradeVariation = tradeNumberVariation;
		}

		/**
		 * Constructor for making a simple two-for-one trade recipe with price variation
		 * @param in1 Item for the left purchase price trade slot
		 * @param v1 range of variation in quantity of <code>in1</code>
		 * @param in2 Item for the right purchase price trade slot. Can be <code>null</code> (and usually is)
		 * @param v2 range of variation in quantity of <code>in2</code>
		 * @param out The item to be purchased (trade recipe output slot)
		 * @param vout range of variation in quantity of <code>out</code>
		 */
		public SimpleTrade(ItemStack in1, int v1, ItemStack in2, int v2, ItemStack out, int vout) {
			this(in1, v1, in2, v2, out, vout, -1, -1);
		}

		/**
		 * Constructor for making a simple one-for-one trade with price variation
		 * @param in1 Item for the left purchase price trade slot
		 * @param v1 range of variation in quantity of <code>in1</code>
		 * @param out The item to be purchased (trade recipe output slot)
		 * @param vout range of variation in quantity of <code>out</code>
		 */
		public SimpleTrade(ItemStack in1, int v1, ItemStack out, int vout) {
			this(in1, v1, null, 0, out, vout, -1, -1);
		}

		/**
		 * Constructor for making a simple one-for-one trade
		 * @param in1 Item for the left purchase price trade slot
		 * @param out The item to be purchased (trade recipe output slot)
		 */
		public SimpleTrade(ItemStack in1, ItemStack out) {
			this(in1, 0, null, 0, out, 0, -1, -1);
		}

		@Override
		public String toString() {
			return input1 + " + " + input2 + " => " + output;
		}

		/**
		 * Invoked when the merchant generates its trade menu
		 * @param recipeList existing trade menu
		 * @param random a psuedorandom number generator instance
		 */
		@Override
		public void modifyMerchantRecipeList(MerchantRecipeList recipeList, Random random) {
			int numTrades = -1;
			if (maxTrades > 0) {
				if (maxTradeVariation > 0) {
					numTrades = Math.max(1, maxTrades + random.nextInt(maxTradeVariation) - maxTradeVariation / 2);
				}
				else {
					numTrades = maxTrades;
				}
			}
			ItemStack in1 = input1.copy();
			if (maxInputMarkup1 > 0) {
				in1.stackSize = in1.stackSize + random.nextInt(maxInputMarkup1);
			}
			ItemStack in2 = null;
			if (input2 != null && input2.getItem() != null) {
				in2 = input2.copy();
				if (maxInputMarkup2 > 0) {
					in2.stackSize = in2.stackSize + random.nextInt(maxInputMarkup2);
				}
			}
			ItemStack out = output.copy();
			if (maxOutputMarkup > 0) {
				out.stackSize = out.stackSize + random.nextInt(maxOutputMarkup);
			}

			if (numTrades > 0) {
				recipeList.add(new MerchantRecipe(in1, in2, out, 0, numTrades));
			}
			else {
				recipeList.add(new MerchantRecipe(in1, in2, out));
			}
		}
	}
}
