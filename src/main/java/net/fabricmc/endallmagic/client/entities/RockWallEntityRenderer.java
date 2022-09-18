package net.fabricmc.endallmagic.client.entities;

import net.fabricmc.endallmagic.common.entities.RockWallEntity;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.util.Identifier;

public class RockWallEntityRenderer extends EntityRenderer<RockWallEntity> {
	public RockWallEntityRenderer(EntityRendererFactory.Context context) {
		super(context);
	}

	@Override
	public Identifier getTexture(RockWallEntity entity) {
		// todo add break textures
		return new Identifier("minecraft:stone");
	}
}
