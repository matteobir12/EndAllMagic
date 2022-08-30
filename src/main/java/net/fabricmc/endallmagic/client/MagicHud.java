package net.fabricmc.endallmagic.client; 

import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.endallmagic.EndAllMagic;
import net.fabricmc.endallmagic.common.MagicUser;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;

public class MagicHud {
	private static final Identifier HUD_ELEMENTS = new Identifier(EndAllMagic.MOD_ID, "textures/gui/hud_elements.png");

	@Environment(EnvType.CLIENT)
	public static void clientEvents() {
		final MinecraftClient client = MinecraftClient.getInstance();

		//-----HUD Render Callback-----//
		HudRenderCallback.EVENT.register((matrices, tickDelta) -> {
			if(client.cameraEntity instanceof PlayerEntity player && !player.isSpectator() && !player.isCreative()) {
				MagicUser user = (MagicUser) player;
				int mana = Math.min(user.getCurrentMana(), user.getMaxMana());
				int scaledWidth = client.getWindow().getScaledWidth();
				int scaledHeight = client.getWindow().getScaledHeight();
				int x = scaledWidth / 2 + 82;
				int y = scaledHeight - (player.isCreative() ? 34 : 49);

				RenderSystem.enableBlend();
				RenderSystem.setShaderTexture(0, HUD_ELEMENTS);
				RenderSystem.setShaderColor(1F, 1F, 1F, 1F);

				// Draw background
				for(int i = 0; i < 10; i++)
					DrawableHelper.drawTexture(matrices, x - (i * 8), y, 0, 15, 9, 9, 256, 256);

				// Draw full mana orb
				for(int i = 0; i < mana / (2* user.getLevel()); i++)
					DrawableHelper.drawTexture(matrices, x - (i * 8), y,(16 * (user.getLevel()-1)), 0 , 8, 8, 256, 256);

				// Draw half mana orb
				if(mana % (2*user.getLevel()) != 0)
					DrawableHelper.drawTexture(matrices, x - (mana / (2*user.getLevel()) * 8), y, 8F + (16 * (user.getLevel()-1)), 0, 8, 8, 256, 256);
			}
		});
	}
}
