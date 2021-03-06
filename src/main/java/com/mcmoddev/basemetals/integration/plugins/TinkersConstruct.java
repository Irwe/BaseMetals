package com.mcmoddev.basemetals.integration.plugins;

import com.mcmoddev.basemetals.init.Materials;
import com.mcmoddev.basemetals.integration.BaseMetalsPlugin;
import com.mcmoddev.basemetals.util.Config.Options;
import com.mcmoddev.lib.integration.IIntegration;
import com.mcmoddev.lib.integration.plugins.tinkers.ModifierRegistry;
import com.mcmoddev.lib.integration.plugins.tinkers.TCMetalMaterial;
import com.mcmoddev.lib.integration.plugins.tinkers.TraitRegistry;

import net.minecraft.item.Item;

import com.mcmoddev.lib.integration.plugins.tinkers.TraitLocations;

/**
 *
 * @author Jasmine Iwanek
 *
 */
@BaseMetalsPlugin(TinkersConstruct.PLUGIN_MODID)
public class TinkersConstruct extends com.mcmoddev.lib.integration.plugins.TinkersConstruct implements IIntegration {

	private static boolean initDone = false;

	@Override
	public void init() {
		if (initDone || !com.mcmoddev.basemetals.util.Config.Options.enableTinkersConstruct) {
			return;
		}
		
		TraitRegistry.initTiCTraits();
		TraitRegistry.initMetalsTraits();
		ModifierRegistry.initModifiers();
		
		if (Options.enableAdamantine) {
			/*
			 * Newly Suggested:
			 * Savage (ignore up to 10 points of damage)
			 * Petrarmor
			 */
			TCMetalMaterial adamantTC = new TCMetalMaterial(Materials.adamantine);
			adamantTC.craftable = false;
			adamantTC.addTrait("coldblooded");
			adamantTC.addTrait("insatiable", TraitLocations.HEAD);

			registerMaterial(adamantTC);
		}

		if (Options.enableAntimony) {
			TCMetalMaterial antimonyTC = new TCMetalMaterial(Materials.antimony);
			antimonyTC.craftable = false;

			registerMaterial(antimonyTC);
		}

		if (Options.enableAquarium) {
			TCMetalMaterial aquaTC = new TCMetalMaterial(Materials.aquarium);
			aquaTC.craftable = false;
			aquaTC.addTrait("aquadynamic");
			aquaTC.addTrait("jagged", TraitLocations.HEAD);
			aquaTC.addTrait("aquadynamic", TraitLocations.HEAD);

			registerMaterial(aquaTC);
			// When FMe is out we can probably do the alloy.
			registerAlloy(Materials.aquarium.getName(), 3,
					new String[] { "copper", "zinc", "prismarine" },
					new int[] { 2, 1, 3}); // Not possible currently
		}

		if (Options.enableBismuth) {
			TCMetalMaterial bismuthTC = new TCMetalMaterial(Materials.bismuth);
			bismuthTC.craftable = false;

			registerMaterial(bismuthTC);
		}

		if (Options.enableBrass) {
			TCMetalMaterial brassTC = new TCMetalMaterial(Materials.brass);
			brassTC.craftable = false;
			brassTC.addTrait("dense");

			registerMaterial(brassTC);
		}

		if (Options.enableCoal) {
			registerFluid(Materials.vanilla_coal, 144);
		}

		if (Options.enableColdIron) {
			TCMetalMaterial coldironTC = new TCMetalMaterial(Materials.coldiron);
			coldironTC.craftable = false;
			coldironTC.addTrait("freezing");

			registerMaterial(coldironTC);
		}

		if (Options.enableCupronickel) {
			TCMetalMaterial cuproTC = new TCMetalMaterial(Materials.cupronickel);
			cuproTC.craftable = false;

			registerMaterial(cuproTC);
			registerAlloy(Materials.cupronickel.getName(), 4,
					new String[] { "copper", "nickel" },
					new int[] { 3, 1 });
		}

		if (Options.enableInvar) {
			TCMetalMaterial invarTC = new TCMetalMaterial(Materials.invar);
			invarTC.craftable = false;

			registerMaterial(invarTC);
			registerAlloy(Materials.invar.fluid.getName(), 3,
					new String[] { "iron", "nickel" },
					new int[] { 2, 1 });
		}

		// As much as we'd like to, we cannot do this like this.
		// At some point between this code running and it getting
		// up to the point of registration, the state changes and
		// lead is suddenly registered.
		// There will have to be another means of making this work...
		// There is an event TiC has that covers material registration
		// we could hook that...
		if (Options.enableLead) {
			// Lead itself is added by TiC
			if( Options.enablePlate ) {
				registerModifierItem("plated", Item.getItemFromBlock(Materials.lead.plate));
			}
		} 

		if (Options.enableMercury) {
			registerFluid(Materials.mercury, 144);
			if( Options.enableBasics ) {
				registerModifierItem("toxic", Materials.mercury.powder);
			}
		}

		if (Options.enableMithril) {
			TCMetalMaterial mithrilTC = new TCMetalMaterial(Materials.mithril);
			mithrilTC.craftable = false;
			mithrilTC.addTrait("holy");

			registerMaterial(mithrilTC);
			
				registerAlloy(Materials.mithril.getName(), 3,
						new String[] { "silver", "coldiron", "mercury" },
						new int[] { 2, 1, 1});
		}

		if (Options.enableNickel) {
			TCMetalMaterial nickelTC = new TCMetalMaterial(Materials.nickel);
			nickelTC.craftable = false;

			registerMaterial(nickelTC);
		}

		// Maybe give this 'soft' as well ?
		if (Options.enablePewter) {
			TCMetalMaterial pewterTC = new TCMetalMaterial(Materials.pewter);
			pewterTC.craftable = false;
			pewterTC.addTrait("soft");

			registerMaterial(pewterTC);
			// this makes what the "Worshipful Company of Pewterers" called "trifle"
			registerAlloy(Materials.pewter.getName(), 144,
					new String[] { "tin", "copper", "lead" },
					new int[] { 137, 2, 5 } );
		}

		if (Options.enablePlatinum) {
			TCMetalMaterial platinumTC = new TCMetalMaterial(Materials.platinum);
			platinumTC.craftable = false;

			registerMaterial(platinumTC);
		}

		if (Options.enableSilver) {
			// Anything needed?
		}

		// Steel has 'Sharp' and 'Stiff'
		if (Options.enableSteel) {
			registerAlloy(Materials.steel.fluid.getName(), 8, 
					new String[] { "iron", "coal" },
					new int[]{ 8, 1 });
		}

		if (Options.enableStarSteel) {
			TCMetalMaterial starsteelTC = new TCMetalMaterial(Materials.starsteel);
			starsteelTC.craftable = false;
			starsteelTC.addTrait("enderference", TraitLocations.HEAD);
			starsteelTC.addTrait("sparkly");

			registerMaterial(starsteelTC);
		}

		if (Options.enableTin) {
			TCMetalMaterial tinTC = new TCMetalMaterial(Materials.tin);
			tinTC.craftable = false;

			registerMaterial(tinTC);
		}

		if (Options.enableZinc) {
			TCMetalMaterial zincTC = new TCMetalMaterial(Materials.zinc);
			zincTC.craftable = false;

			registerMaterial(zincTC);
		}

		initDone = true;
	}
}
