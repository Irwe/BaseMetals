/**
 * 
 */
package com.mcmoddev.lib.integration.plugins.tinkers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import com.mcmoddev.lib.material.MetalMaterial;
import com.mcmoddev.basemetals.BaseMetals;
import com.mcmoddev.lib.integration.plugins.tinkers.TraitRegistry;

import slimeknights.tconstruct.library.traits.AbstractTrait;
import slimeknights.tconstruct.library.traits.ITrait;

/**
 * @author Daniel Hazelton &lt;dshadowwolf@gmail.com&gt;
 * @since 8-FEB-2017
 * 
 * Wrapper for all the data that the TiC plugin needs to generate a TiC material
 * and tell TiC how the various parts are to act.
 */

public class TCMetalMaterial {

	// integer values
	// durabilities...
	public int headDurability;
	public int bodyDurability;
	public int extraDurability;
	// Other ints...
	public int miningLevel;
	public int shaftBonusAmmo;

	// float values
	public float miningSpeed;
	public float headAttackDamage;
	public float bodyModifier;
	public float bowDrawingSpeed;
	public float bowRange;
	public float bowDamage;
	public float bowstringModifier;
	public float shaftModifier;
	public float fletchingAccuracy;
	public float fletchingModifier;

	// Because some *metals need to change this
	// this is in mB
	public int amountPerOre = 288;
	
	// for reference and simplifying the API
	public final MetalMaterial metalmaterial;

	// craftable, castable, toolforge - does this allow it ?
	public boolean craftable;
	public boolean castable;
	public boolean toolforge;

	public boolean hasTraits;

	private HashMap<String,List<AbstractTrait>> traits = new HashMap<>();

	/**
	 * @param material MetalMaterial this represents
	 */
	public TCMetalMaterial(MetalMaterial material) {
		headDurability = material.getToolDurability();
		miningSpeed = material.magicAffinity * 3 / 2;
		miningLevel = material.getToolHarvestLevel();
		headAttackDamage = material.getBaseAttackDamage() * 2;
		bodyDurability = material.getToolDurability() / 7;
		bodyModifier = (material.hardness + material.magicAffinity * 2) / 9;
		extraDurability = material.getToolDurability() / 10;
		bowDrawingSpeed = calcDrawSpeed(material.getToolDurability());
		bowDamage = material.getBaseAttackDamage() + 3;

		// From here on its fixed values as defaults
		bowRange = 15.0f;
		bowstringModifier = 1.0f;
		shaftModifier = 1.0f;
		fletchingAccuracy = 1.0f;
		fletchingModifier = 1.0f;
		shaftBonusAmmo = 1;

		metalmaterial = material;

		castable = true;
		craftable = true;
		toolforge = true;
		hasTraits = false;
	}

	private float calcDrawSpeed(int durability) {
		float val;
		if (durability < 204) {
			val = 1.0f;
		} else {
			val = ((durability - 200) + 1) / 10.0f;
			val -= Math.floor(val);
		}

		return val;
	}

	/**
	 * Add a TiC default or custom trait to the material in general
	 * @param trait the AbstractTrait to add
	 */
	public void addTrait(String traitName) {
		addTrait(traitName, "general");
	}

	/**
	 * Add a TiC default or custom trait to the material when used as a specific tool part
	 * @param trait the AbstractTrait to add
	 * @param loc the MaterialType for the tool part {@link slimeknights.tconstruct.library.material.MaterialType}
	 * @throws
	 */
	public void addTrait(String traitName, String loc) {
		ITrait trait = TraitRegistry.get(traitName);
		if( trait == null ) {
			String s = String.format("BaseMetals-TCon: Asked for trait %s that does not exist", traitName);
			BaseMetals.logger.error(s);
			return;
		}
		if (traits.keySet().contains(loc)) {
			if (!traits.get(loc).add((AbstractTrait)trait)) { 
				throw new RuntimeException("Unable to add trait!");
			}
		} else {
			List<AbstractTrait> t = new ArrayList<>();
			t.add((AbstractTrait)trait);
			traits.put(loc, t);
		}

		hasTraits = true;
	}

	/**
	 * Get the trait for a given location
	 * @param loc the MaterialType for the tool part of the trait, null for the overall one
	 * @return the instance of the trait that was originally stored
	 * @throws
	 */
	public List<AbstractTrait> getTraits(String loc) {
		if (!hasTraits) {
			return new ArrayList<>();
		}

		if( loc == null ) {
			return traits.get("general");
		} else if (traits.keySet().contains(loc)) {
			return traits.get(loc);
		} else {
			throw new RuntimeException("Unkown trait location " + loc);
		}
	}
	
	/**
	 * Get the names of the keys in the traits HashMap
	 * @return Set<String>
	 */
	public Set<String> getTraitLocs() {
		return traits.keySet();
	}
}
