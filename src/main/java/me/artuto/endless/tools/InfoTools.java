package me.artuto.endless.tools;

import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.User;

public class InfoTools
{
    public static boolean hasUserRoles(Member member)
    {
        if(member.getRoles().isEmpty())
        {
            return false;
        }
        else
        {
            return true;
        }
    }

    public static boolean hasGuildRoles(Guild guild)
    {
        if(guild.getRoles().toString().replace("@everyone", "").isEmpty())
        {
            return true;
        }
        else
        {
            return false;
        }
    }

    public static String mentionUserRoles(Member member)
    {
        StringBuilder rolesbldr = new StringBuilder();

        if(hasUserRoles(member))
        {
            member.getRoles().forEach(r -> rolesbldr.append(" ").append(r.getAsMention()));
            return rolesbldr.toString();
        }
        else
        {
            return "None";
        }
    }

    public static String mentionGuildRoles(Guild guild)
    {
        StringBuilder rolesbldr = new StringBuilder();

        if(hasGuildRoles(guild))
        {
            guild.getRoles().forEach(r -> rolesbldr.append(" ").append(r.getAsMention()));
            return rolesbldr.toString();
        }
        else
        {
            return "None";
        }
    }

    public static String onlineStatus(Member member)
    {
        String emote;

        if(member.getOnlineStatus().toString().equals("ONLINE"))
        {
            emote = "<:online:334859814410911745>";
        }
        else if(member.getOnlineStatus().toString().equals("IDLE"))
        {
            emote = "<:away:334859813869584384>";
        }
        else if(member.getOnlineStatus().toString().equals("DO_NOT_DISTURB"))
        {
            emote = "<:dnd:334859814029099008>";
        }
        else if(member.getOnlineStatus().toString().equals("INVISIBLE"))
        {
            emote = "<:invisible:334859814410649601>";
        }
        else if(member.getOnlineStatus().toString().equals("OFFLINE"))
        {
            emote = "<:offline:334859814423232514>";
        }
        else
        {
            emote = ":interrobang:";
        }

        return emote;
    }

    public static boolean nitroCheck(User user)
    {
        if(!(user.getAvatarId()==null))
        {
            if(user.getAvatarId().startsWith("a_"))
            {
                return  true;
            }
            else
            {
                return false;
            }
        }
        else
        {
            return false;
        }
    }


}
