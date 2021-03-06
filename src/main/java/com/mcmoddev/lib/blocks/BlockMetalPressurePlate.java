package com.mcmoddev.lib.blocks;

import com.mcmoddev.lib.material.IMetalObject;
import com.mcmoddev.lib.material.MetalMaterial;

import net.minecraft.block.BlockPressurePlate;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;

/**
 *
 * @author Jasmine Iwanek
 *
 */
public class BlockMetalPressurePlate extends BlockPressurePlate implements IMetalObject {

	final MetalMaterial material;

	/**
	 *
	 * @param material The material the pressure plate is made from
	 */
	public BlockMetalPressurePlate(MetalMaterial material) {
		super(Material.IRON, BlockPressurePlate.Sensitivity.MOBS);
		this.setSoundType(SoundType.METAL);
		this.material = material;
		this.blockHardness = material.getMetalBlockHardness();
		this.blockResistance = material.getBlastResistance();
		this.setHarvestLevel("pickaxe", material.getRequiredHarvestLevel());
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
