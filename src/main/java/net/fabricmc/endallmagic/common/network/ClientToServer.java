package net.fabricmc.endallmagic.common.network;


import io.netty.buffer.Unpooled;
import net.fabricmc.endallmagic.EndAllMagic;
import net.fabricmc.endallmagic.client.gui.MagicScreenFactory;
import net.fabricmc.endallmagic.common.MagicUser;
import net.fabricmc.endallmagic.common.spells.Spell;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;

public class ClientToServer {
    public static final Identifier ID = new Identifier(EndAllMagic.MOD_ID, "cast_spell");
	public static final Identifier SCREEN = new Identifier(EndAllMagic.MOD_ID, "screen");

	public static void send(int spellId) {
		PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
		buf.writeVarInt(spellId);
		EndAllMagic.LOGGER.info("sending id " + spellId);
		ClientPlayNetworking.send(ID, buf);
	}

	public static void handle(MinecraftServer server, ServerPlayerEntity player, ServerPlayNetworkHandler network, PacketByteBuf buf, PacketSender sender) {
		int spellId = buf.readVarInt();

		server.execute(() -> {
			MagicUser user = (MagicUser) player;
			EndAllMagic.LOGGER.info("recived id " + spellId);
			Spell spell = EndAllMagic.SPELL.get(spellId);
			if(user.getKnownSpells().getSpell(spell.pattern).getA() != null) {
				int realManaCost = (spell.getManaCost()); // add some mana math here
				if(player.isCreative() || (user.getCurrentMana() > 0) || (user.getCurrentMana() >= realManaCost)) {
					player.sendMessage(Text.translatable(spell.getTranslationKey()).formatted(Formatting.GREEN), true);
					user.setActiveSpell(spell, 0);

					if(!player.isCreative()) {
						user.setLastCastTime(player.world.getTime());

						if(user.getCurrentMana() < realManaCost) {
							// player.damage(ModDamageSource.MAGIC_BURNOUT, burnoutAmount); // damage if over
							EndAllMagic.LOGGER.info("not enough mana");
						}

						user.addMana(-realManaCost);
					}
				}
				else {
					player.sendMessage(Text.translatable("error." + EndAllMagic.MOD_ID + ".not_enough_mana").formatted(Formatting.RED), false);
				}
			}
			else {
				player.sendMessage(Text.translatable("error." + EndAllMagic.MOD_ID + ".unknown_spell").formatted(Formatting.RED), true);
			}
		});
	}

	public static void openAttributesScreen(final int pageId) {
		PacketByteBuf buf = PacketByteBufs.create();
		buf.writeInt(pageId);
		
		ClientPlayNetworking.send(SCREEN, buf);
	}

	public static void switchScreen(MinecraftServer server, ServerPlayerEntity player, ServerPlayNetworkHandler handler, PacketByteBuf buf, PacketSender responseSender) {
		int pageId = buf.readInt();
		
		server.execute(() -> {
			if(player != null) {
				if(pageId < 0) {
					player.closeScreenHandler();
				} else 
					player.openHandledScreen(new MagicScreenFactory(pageId));
			}
		});
	}

	public static void openInventoryScreen(ButtonWidget button) {
		PacketByteBuf buf = PacketByteBufs.create();
		buf.writeInt(-1);
		
		ClientPlayNetworking.send(SCREEN, buf);
		MinecraftClient client = MinecraftClient.getInstance();
		client.setScreen(new InventoryScreen(client.player));
	}




}
