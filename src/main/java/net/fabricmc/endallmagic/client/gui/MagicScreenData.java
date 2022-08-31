package net.fabricmc.endallmagic.client.gui;

import java.util.List;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.endallmagic.client.gui.pages.Page;

@Environment(EnvType.CLIENT)
public interface MagicScreenData {
	int getX();
	int getY();
	List<Page> pages();
}
