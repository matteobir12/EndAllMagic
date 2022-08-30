package net.fabricmc.endallmagic.api.client;

import java.text.DecimalFormat;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;


@Environment(EnvType.CLIENT)
public final class ClientUtil {
	/** Format for two decimal places. */
	public static final DecimalFormat FORMATTING_2 = new DecimalFormat("###.##");
	/** Format for three decimal places. */
	public static final DecimalFormat FORMATTING_3 = new DecimalFormat("###.###");

}
