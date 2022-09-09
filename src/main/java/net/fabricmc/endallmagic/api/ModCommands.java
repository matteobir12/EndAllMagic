package net.fabricmc.endallmagic.api;

import java.util.concurrent.CompletableFuture;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;

import net.fabricmc.endallmagic.EndAllMagic;
import net.fabricmc.endallmagic.common.MagicUser;
import net.fabricmc.endallmagic.common.spells.Spell;
import net.fabricmc.endallmagic.common.spells.SpellConfig;
import net.fabricmc.fabric.api.command.v2.ArgumentTypeRegistry;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.CommandSource;
import net.minecraft.command.argument.EntityArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import net.minecraft.command.argument.serialize.ConstantArgumentSerializer;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import net.fabricmc.endallmagic.common.spells.Pattern;

public class ModCommands {
	public static void register() {
		ArgumentTypeRegistry.registerArgumentType(new Identifier(EndAllMagic.MOD_ID, "spells"), SpellArgumentType.class, ConstantArgumentSerializer.of(SpellArgumentType::new));
	}

	public static void init(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess access, CommandManager.RegistrationEnvironment environment) {
		dispatcher.register(CommandManager.literal("eom")
				.then(CommandManager.literal("list").requires(source -> source.hasPermissionLevel(0))
						.executes(context -> SpellsCommand.listPlayerSpells(context, context.getSource().getPlayer()))
						.then(CommandManager.argument("player", EntityArgumentType.player()).requires(source -> source.hasPermissionLevel(3))
								.executes(context -> SpellsCommand.listPlayerSpells(context, EntityArgumentType.getPlayer(context, "player")))))
				.then(CommandManager.literal("add").requires(source -> source.hasPermissionLevel(3))
						.then(CommandManager.argument("all", StringArgumentType.word())
								.executes(SpellsCommand::addAllSpellsToSelf))
						.then(CommandManager.argument("spell", SpellArgumentType.spell())
								.executes(SpellsCommand::addSpellToSelf))
						.then(CommandManager.argument("player", EntityArgumentType.player())
								.then(CommandManager.argument("all", StringArgumentType.word())
										.executes(SpellsCommand::addAllSpellsToPlayer))
								.then(CommandManager.argument("spell", SpellArgumentType.spell())
										.executes(SpellsCommand::addSpellToPlayer))))
				.then(CommandManager.literal("remove").requires(source -> source.hasPermissionLevel(3))
						.then(CommandManager.argument("all", StringArgumentType.word())
								.executes(SpellsCommand::removeAllSpellsFromSelf))
						.then(CommandManager.argument("spell", SpellArgumentType.spell())
								.executes(SpellsCommand::removeSpellFromSelf))
						.then(CommandManager.argument("player", EntityArgumentType.player())
								.then(CommandManager.argument("all", StringArgumentType.word())
										.executes(SpellsCommand::removeAllSpellsFromPlayer))
								.then(CommandManager.argument("spell", SpellArgumentType.spell())
										.executes(SpellsCommand::removeSpellFromPlayer))))
				.then(CommandManager.literal("level").requires(source -> source.hasPermissionLevel(3))
						.then(CommandManager.argument("set", IntegerArgumentType.integer())
								.executes(context -> SpellsCommand.setPlayerLevel(context, context.getSource().getPlayer(),IntegerArgumentType.getInteger(context, "set")))
								.then(CommandManager.argument("player", EntityArgumentType.player())
										.executes(context -> SpellsCommand.setPlayerLevel(context, EntityArgumentType.getPlayer(context, "player"),IntegerArgumentType.getInteger(context, "set"))))))
				.then(CommandManager.literal("affinity").requires(source -> source.hasPermissionLevel(3))
						.then(CommandManager.argument("set", IntegerArgumentType.integer())
								.executes(context -> SpellsCommand.setPlayerAffinity(context, context.getSource().getPlayer(),IntegerArgumentType.getInteger(context, "set")))
								.then(CommandManager.argument("player", EntityArgumentType.player())
										.executes(context -> SpellsCommand.setPlayerAffinity(context, EntityArgumentType.getPlayer(context, "player"),IntegerArgumentType.getInteger(context, "set")))))
						.then(CommandManager.literal("show")
								.executes(SpellsCommand::showAffinity))));
	}

	public static class SpellArgumentType implements ArgumentType<Spell> {
		public static final DynamicCommandExceptionType INVALID_SPELL_EXCEPTION = new DynamicCommandExceptionType(object -> Text.translatable("commands." + EndAllMagic.MOD_ID + ".spells.not_found", object));

		public static SpellArgumentType spell() {
			return new SpellArgumentType();
		}

		public static Spell getSpell(CommandContext<ServerCommandSource> commandContext, String string) {
			return commandContext.getArgument(string, Spell.class);
		}

		@Override
		public Spell parse(StringReader reader) throws CommandSyntaxException {
			Identifier identifier = Identifier.fromCommandInput(reader);
			return EndAllMagic.SPELL.getOrEmpty(identifier).orElseThrow(() -> INVALID_SPELL_EXCEPTION.create(identifier));
		}

		@Override
		public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
			return CommandSource.suggestIdentifiers(EndAllMagic.SPELL.getIds(), builder);
		}
	}

	private static class SpellsCommand {
		public static int listPlayerSpells(CommandContext<ServerCommandSource> context, PlayerEntity player) throws CommandSyntaxException {
			if(((MagicUser) player).getKnownSpells().isEmpty()) {
				context.getSource().sendError(Text.translatable("commands." + EndAllMagic.MOD_ID + ".spells.no_known_spells", player.getEntityName()));
				return 0;
			}

			MutableText knownSpells = Text.literal("");

			for(Spell spell : ((MagicUser) player).getKnownSpells().asList()) {
				knownSpells = knownSpells.append("\n    - ").append(Text.translatable(spell.getTranslationKey())).append(" (");
				for (Pattern p : spell.pattern)
					knownSpells = knownSpells.append(p.toString() + "-");

				knownSpells = knownSpells.append(")");
			}
			context.getSource().sendFeedback(knownSpells,false);
			context.getSource().sendFeedback(Text.translatable("commands." + EndAllMagic.MOD_ID + ".spells.list", player.getEntityName(), knownSpells), false);

			return ((MagicUser) player).getKnownSpells().asList().size();
		}

		public static int addAllSpellsToSelf(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
			PlayerEntity player = context.getSource().getPlayer();
			MagicUser user = (MagicUser) player;

			if(StringArgumentType.getString(context, "all").equals("all")) {
				EndAllMagic.SPELL.forEach(spell -> user.setKnownSpell(spell));
				context.getSource().sendFeedback(Text.translatable("commands." + EndAllMagic.MOD_ID + ".spells.added_all", player.getEntityName()), false);
			}
			else
				context.getSource().sendError(Text.translatable("commands." + EndAllMagic.MOD_ID + ".spells.not_valid_spell"));

			return Command.SINGLE_SUCCESS;
		}

		public static int addSpellToSelf(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
			PlayerEntity player = context.getSource().getPlayer();
			MagicUser user = (MagicUser) player;
			Spell spell = ModCommands.SpellArgumentType.getSpell(context, "spell");

			if(!user.getKnownSpells().contains(spell)) {
				user.setKnownSpell(spell);
				context.getSource().sendFeedback(Text.translatable("commands." + EndAllMagic.MOD_ID + ".spells.added", EndAllMagic.SPELL.getId(spell), player.getEntityName()), false);
			}
			else
				context.getSource().sendError(Text.translatable("commands." + EndAllMagic.MOD_ID + ".spells.already_known", player.getEntityName(), EndAllMagic.SPELL.getId(spell)));

			return Command.SINGLE_SUCCESS;
		}

		public static int addAllSpellsToPlayer(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
			PlayerEntity player = EntityArgumentType.getPlayer(context, "player");
			MagicUser user = (MagicUser) player;

			if(StringArgumentType.getString(context, "all").equals("all")) {
				EndAllMagic.SPELL.forEach(spell -> user.setKnownSpell(spell));
				context.getSource().sendFeedback(Text.translatable("commands." + EndAllMagic.MOD_ID + ".spells.added_all", player.getEntityName()), false);
			}
			else
				context.getSource().sendError(Text.translatable("commands." + EndAllMagic.MOD_ID + ".spells.not_valid_spell"));

			return Command.SINGLE_SUCCESS;
		}

		public static int addSpellToPlayer(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
			PlayerEntity player = EntityArgumentType.getPlayer(context, "player");
			MagicUser user = (MagicUser) player;
			Spell spell = ModCommands.SpellArgumentType.getSpell(context, "spell");

			if(!user.getKnownSpells().contains(spell)) {
				user.setKnownSpell(spell);
				context.getSource().sendFeedback(Text.translatable("commands." + EndAllMagic.MOD_ID + ".spells.added", EndAllMagic.SPELL.getId(spell), player.getEntityName()), false);
			}
			else
				context.getSource().sendError(Text.translatable("commands." + EndAllMagic.MOD_ID + ".spells.already_known", player.getEntityName(), EndAllMagic.SPELL.getId(spell)));

			return Command.SINGLE_SUCCESS;
		}

		public static int removeAllSpellsFromSelf(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
			PlayerEntity player = context.getSource().getPlayer();

			((MagicUser) player).getKnownSpells().clear();
			context.getSource().sendFeedback(Text.translatable("commands." + EndAllMagic.MOD_ID + ".spells.cleared", player.getEntityName()), false);

			return Command.SINGLE_SUCCESS;
		}

		public static int removeSpellFromSelf(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
			PlayerEntity player = context.getSource().getPlayer();
			MagicUser user = (MagicUser) player;
			Spell spell = ModCommands.SpellArgumentType.getSpell(context, "spell");

			if(user.getKnownSpells().contains(spell)) {
				user.getKnownSpells().remove(spell);
				context.getSource().sendFeedback(Text.translatable("commands." + EndAllMagic.MOD_ID + ".spells.removed", EndAllMagic.SPELL.getId(spell), player.getEntityName()), false);
			}
			else
				context.getSource().sendError(Text.translatable("commands." + EndAllMagic.MOD_ID + ".spells.does_not_have", player.getEntityName(), EndAllMagic.SPELL.getId(spell)));

			return Command.SINGLE_SUCCESS;
		}

		public static int removeAllSpellsFromPlayer(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
			PlayerEntity player = EntityArgumentType.getPlayer(context, "player");

			((MagicUser) player).getKnownSpells().clear();
			context.getSource().sendFeedback(Text.translatable("commands." + EndAllMagic.MOD_ID + ".spells.cleared", player.getEntityName()), false);

			return Command.SINGLE_SUCCESS;
		}
		public static int showAffinity(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
			PlayerEntity player = context.getSource().getPlayer();
			Text t = Text.literal(((MagicUser) player).getAffinity().toString());
			context.getSource().sendFeedback(t, false);

			return Command.SINGLE_SUCCESS;
		}
		public static int removeSpellFromPlayer(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
			PlayerEntity player = context.getSource().getPlayer();
			MagicUser user = (MagicUser) player;
			Spell spell = ModCommands.SpellArgumentType.getSpell(context, "spell");

			if(user.getKnownSpells().contains(spell)) {
				user.getKnownSpells().remove(spell);
				context.getSource().sendFeedback(Text.translatable("commands." + EndAllMagic.MOD_ID + ".spells.removed", EndAllMagic.SPELL.getId(spell), player.getEntityName()), false);
			}
			else
				context.getSource().sendError(Text.translatable("commands." + EndAllMagic.MOD_ID + ".spells.does_not_have", player.getEntityName(), EndAllMagic.SPELL.getId(spell)));

			return Command.SINGLE_SUCCESS;
		}
		public static int setPlayerLevel(CommandContext<ServerCommandSource> context, PlayerEntity player, int level) throws CommandSyntaxException {
			MagicUser user = (MagicUser) player;
			if(level > 6 || level < 1){
				context.getSource().sendError(Text.translatable("commands." + EndAllMagic.MOD_ID + ".int_not_in_range"));
				return Command.SINGLE_SUCCESS;
			}
			user.setLevel(level);
			return Command.SINGLE_SUCCESS;
		}
		public static int setPlayerAffinity(CommandContext<ServerCommandSource> context, PlayerEntity player, int affinity) throws CommandSyntaxException {
			MagicUser user = (MagicUser) player;
			if(affinity > 4 || affinity < 0){
				context.getSource().sendError(Text.translatable("commands." + EndAllMagic.MOD_ID + ".int_not_in_range"));
				return Command.SINGLE_SUCCESS;
			}
			user.setAffinity(SpellConfig.Affinity.values()[affinity]);
			return Command.SINGLE_SUCCESS;
		}

	}
}
