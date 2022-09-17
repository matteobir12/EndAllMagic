package net.fabricmc.endallmagic.client.entities;

import net.fabricmc.endallmagic.common.entities.TornadoEntity;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.util.Identifier;

public class TornadoEntityRenderer extends EntityRenderer<TornadoEntity> {
	public TornadoEntityRenderer(EntityRendererFactory.Context context) {
		super(context);
	}

	@Override
	public Identifier getTexture(TornadoEntity entity) {
		return null;
	}
}
