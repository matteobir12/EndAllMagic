package net.fabricmc.endallmagic.common.network;


import io.netty.buffer.Unpooled;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.endallmagic.EndAllMagic;
import net.fabricmc.endallmagic.common.MagicUser;
import net.fabricmc.endallmagic.common.spells.Spell;
import net.fabricmc.endallmagic.common.spells.SpellConfig.Affinity;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.network.PacketByteBuf;

@Environment(EnvType.CLIENT)
public class ClientNetworking {

	public static void castSpellSend(int spellId) {
		PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
		buf.writeVarInt(spellId);
		ClientPlayNetworking.send(ServerNetworking.ID, buf);
	}

    public static void openAttributesScreen(final int pageId) {
		PacketByteBuf buf = PacketByteBufs.create();
		buf.writeInt(pageId);
		
		ClientPlayNetworking.send(ServerNetworking.SCREEN, buf);
	}
    public static void openInventoryScreen(ButtonWidget button) {
		PacketByteBuf buf = PacketByteBufs.create();
		buf.writeInt(-1);
		
		ClientPlayNetworking.send(ServerNetworking.SCREEN, buf);
		MinecraftClient client = MinecraftClient.getInstance();
		client.setScreen(new InventoryScreen(client.player));
	}

	public static void updateAffinitySend(Affinity affinity) {
		PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
		buf.writeVarInt(affinity.ordinal());
		ClientPlayNetworking.send(ServerNetworking.AFFINITY, buf);
	}
	public static void receiveKnownSpell(MinecraftClient client, ClientPlayNetworkHandler handler, PacketByteBuf buf, PacketSender responseSender){
		int spellId = buf.readVarInt();
		client.execute(()->{
			if(client.player != null){
				Spell spell = EndAllMagic.SPELL.get(spellId);
				((MagicUser)client.player).setKnownSpell(spell);
			}
		});

	}
}
