package net.fabricmc.endallmagic.client.entities;

import net.fabricmc.endallmagic.common.entities.WindBladeEntity;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.util.Identifier;

public class WindBladeEntityRenderer extends EntityRenderer<WindBladeEntity> {
	public WindBladeEntityRenderer(EntityRendererFactory.Context context) {
		super(context);
	}

	@Override
	public Identifier getTexture(WindBladeEntity entity) {
		return null;
	}
}
