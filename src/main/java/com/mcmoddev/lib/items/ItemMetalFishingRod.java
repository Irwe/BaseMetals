package com.mcmoddev.lib.items;

import java.util.List;

import com.mcmoddev.basemetals.items.MetalToolEffects;
import com.mcmoddev.lib.material.IMetalObject;
import com.mcmoddev.lib.material.MetalMaterial;
import com.mcmoddev.lib.util.Oredicts;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.oredict.OreDictionary;

/**
 *
 * @author Jasmine Iwanek
 *
 */
public class ItemMetalFishingRod extends ItemFishingRod implements IMetalObject {

	private final MetalMaterial material;
	protected final String repairOreDictName;
	protected final boolean regenerates;
	protected static final long REGEN_INTERVAL = 200;

	/**
	 *
	 * @param material The material to make the fishing rod from
	 */
	public ItemMetalFishingRod(MetalMaterial material) {
		super();
		this.material = material;
//		this.setMaxDamage(64);
//		this.setMaxStackSize(1);
//		this.setCreativeTab(CreativeTabs.TOOLS);
		this.repairOreDictName = Oredicts.INGOT + this.material.getCapitalizedName();
//		this.addPropertyOverride(new ResourceLocation("cast"), new IItemPropertyGetter() {
//			@SideOnly(Side.CLIENT)
//			@Override
//			public float apply(ItemStack stack, World worldIn, EntityLivingBase entityIn) {
//				return entityIn == null ? 0.0F : ((entityIn.getHeldItemMainhand() == stack) && (entityIn instanceof EntityPlayer) && (((EntityPlayer) entityIn).fishEntity != null) ? 1.0F : 0.0F);
//			}
//		});
		this.regenerates = this.material.regenerates;
	}

	/*
	@Override
	public ActionResult<ItemStack> onItemRightClick(ItemStack itemStackIn, World worldIn, EntityPlayer playerIn, EnumHand hand) {
		if (playerIn.fishEntity != null) {
			final int i = playerIn.fishEntity.handleHookRetraction();
			itemStackIn.damageItem(i, playerIn);
			playerIn.swingArm(hand);
		} else {
			worldIn.playSound((EntityPlayer) null, playerIn.posX, playerIn.posY, playerIn.posZ, SoundEvents.ENTITY_BOBBER_THROW, SoundCategory.NEUTRAL, 0.5F, 0.4F / ((itemRand.nextFloat() * 0.4F) + 0.8F));

			if (!worldIn.isRemote)
				worldIn.spawnEntity(new EntityMetalFishHook(worldIn, playerIn));

			playerIn.swingArm(hand);
			playerIn.addStat(StatList.getObjectUseStats(this));
		}

		return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, itemStackIn);
	}
	*/

	@Override
	public boolean getIsRepairable(final ItemStack intputItem, final ItemStack repairMaterial) {
		final List<ItemStack> acceptableItems = OreDictionary.getOres(this.repairOreDictName);
		for (final ItemStack i : acceptableItems)
			if (ItemStack.areItemsEqual(i, repairMaterial))
				return true;
		return false;
	}

	@Override
	public void onUpdate(final ItemStack item, final World world, final Entity player, final int inventoryIndex, final boolean isHeld) {
		super.onUpdate(item, world, player, inventoryIndex, isHeld);

		if (this.regenerates && !world.isRemote && isHeld && (item.getItemDamage() > 0) && ((world.getTotalWorldTime() % REGEN_INTERVAL) == 0))
			item.setItemDamage(item.getItemDamage() - 1);
	}
/*
	public String getMaterialName() {
		return this.material.getName();
	}
*/
	@Override
	public void addInformation(ItemStack stack, EntityPlayer player, List<String> list, boolean b) {
		super.addInformation(stack, player, list, b);
		MetalToolEffects.addToolSpecialPropertiesToolTip(this.material, list);
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
}
