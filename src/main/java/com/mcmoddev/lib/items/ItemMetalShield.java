package com.mcmoddev.lib.items;

import java.util.List;

import com.mcmoddev.basemetals.registry.IOreDictionaryEntry;
import com.mcmoddev.lib.material.IMetalObject;
import com.mcmoddev.lib.material.MetalMaterial;
import com.mcmoddev.lib.util.Oredicts;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemShield;
import net.minecraft.item.ItemStack;
import net.minecraft.client.resources.I18n;
import net.minecraftforge.oredict.OreDictionary;

/**
 *
 * @author Jasmine Iwanek
 *
 */
public class ItemMetalShield extends ItemShield implements IOreDictionaryEntry, IMetalObject {

	final MetalMaterial material;
	private final String oreDict;
	protected final String repairOreDictName;
	protected final boolean regenerates;
	protected static final long REGEN_INTERVAL = 200;

	/**
	 *
	 * @param material The material to make the shield from
	 */
	public ItemMetalShield(MetalMaterial material) {
		this.material = material;
		this.setMaxDamage((int) (this.material.strength * 168));
		this.setCreativeTab(CreativeTabs.TOOLS);
		this.oreDict = Oredicts.SHIELD + this.material.getCapitalizedName();
		this.repairOreDictName = Oredicts.INGOT + this.material.getCapitalizedName();
		this.regenerates = this.material.regenerates;
	}

	@Override
	public MetalMaterial getMaterial() {
		return this.material;
	}

	/**
	 * @deprecated
	 */
	@Override
	@Deprecated
	public MetalMaterial getMetalMaterial() {
		return this.material;
	}

	@Override
	public String getOreDictionaryName() {
		return this.oreDict;
	}

	/**
	 * Return whether this item is repairable in an anvil.
	 */
	@Override
	public boolean getIsRepairable(ItemStack toRepair, ItemStack repair) {
		final List<ItemStack> acceptableItems = OreDictionary.getOres(this.repairOreDictName);
		for (final ItemStack i : acceptableItems)
			if (ItemStack.areItemsEqual(i, repair))
				return true;
		return false;
	}

	@Override
	public String getItemStackDisplayName(ItemStack stack) {
		return I18n.format(String.format("%s.name", this.getUnlocalizedName()));
	}

}
