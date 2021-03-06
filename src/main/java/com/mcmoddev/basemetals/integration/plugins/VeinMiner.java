package com.mcmoddev.basemetals.integration.plugins;

import com.mcmoddev.basemetals.integration.BaseMetalsPlugin;
import com.mcmoddev.lib.integration.IIntegration;

/**
 * VeinMiner Integration Plugin
 *
 * @author Jasmine Iwanek
 *
 */
@BaseMetalsPlugin(VeinMiner.PLUGIN_MODID)
public class VeinMiner extends com.mcmoddev.lib.integration.plugins.VeinMiner implements IIntegration {

	private static boolean initDone = false;

	@Override
	public void init() {
		if (initDone || !com.mcmoddev.basemetals.util.Config.Options.enableVeinminer) {
			return;
		}

		addToolsForMaterial("aluminum");
		addToolsForMaterial("aluminumbrass");
		addToolsForMaterial("cadmium");
		addToolsForMaterial("chromium");
		addToolsForMaterial("galvanizedsteel");
		addToolsForMaterial("iridium");
		addToolsForMaterial("magnesium");
		addToolsForMaterial("manganese");
		addToolsForMaterial("nichrome");
		addToolsForMaterial("osmium");
		addToolsForMaterial("plutonium");
		addToolsForMaterial("rutile");
		addToolsForMaterial("stainlesssteel");
		addToolsForMaterial("tantalum");
		addToolsForMaterial("titanium");
		addToolsForMaterial("tungsten");
		addToolsForMaterial("uranium");
		addToolsForMaterial("zirconium");

		initDone = true;
	}
}
