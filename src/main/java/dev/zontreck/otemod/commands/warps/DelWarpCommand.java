package dev.zontreck.otemod.commands.warps;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;

import dev.zontreck.libzontreck.chat.ChatColor;
import dev.zontreck.libzontreck.vectors.Vector2;
import dev.zontreck.libzontreck.vectors.Vector3;
import dev.zontreck.otemod.OTEMod;
import dev.zontreck.otemod.chat.ChatServerOverride;
import dev.zontreck.otemod.database.TeleportDestination;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;

public class DelWarpCommand {
    
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher)
    {
        dispatcher.register(Commands.literal("delwarp").then(Commands.argument("nickname", StringArgumentType.string()).executes(c -> setWarp(c.getSource(), StringArgumentType.getString(c, "nickname")))));
        
        //dispatcher.register(Commands.literal("sethome").then(Commands.argument("nickname", StringArgumentType.string())).executes(command -> {
            //String arg = StringArgumentType.getString(command, "nickname");
            //return setHome(command.getSource(), arg);
        //}));
    }

    private static int setWarp(CommandSourceStack source, String string) {
        
        ServerPlayer p = (ServerPlayer)source.getEntity();
        Connection con = OTEMod.DB.getConnection();
        try {
            con.beginRequest();
            PreparedStatement pstat;
            Vec3 position = p.position();
            Vec2 rot = p.getRotationVector();

            TeleportDestination dest = new TeleportDestination(new Vector3(position), new Vector2(rot), p.getLevel().dimension().location().getNamespace() + ":" + p.getLevel().dimension().location().getPath());

            String SQL = "DELETE FROM `warps` WHERE `warpname`=? AND `owner`=?;";
            pstat = con.prepareStatement(SQL);
            pstat.setString(1, string);
            pstat.setString(2, p.getStringUUID());
            pstat.execute();
            

            ChatServerOverride.broadcastTo(p.getUUID(), new TextComponent(ChatColor.GREEN).append(new TranslatableComponent("dev.zontreck.otemod.msgs.warps.del.success")), source.getServer());

            con.endRequest();
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            ChatServerOverride.broadcastTo(p.getUUID(), new TextComponent(ChatColor.DARK_RED).append(new TranslatableComponent("dev.zontreck.otemod.msgs.warps.del.fail")), source.getServer());
        }

        return 0;
    }
}
