package me.artuto.endless.commands.serverconfig;

import com.jagrosh.jdautilities.menu.ButtonMenu;
import me.artuto.endless.Bot;
import me.artuto.endless.Const;
import me.artuto.endless.commands.EndlessCommand;
import me.artuto.endless.commands.EndlessCommandEvent;
import me.artuto.endless.commands.cmddata.Categories;
import me.artuto.endless.utils.ChecksUtil;
import me.artuto.endless.utils.GuildUtils;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.*;

import java.awt.*;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class SetupCmd extends EndlessCommand
{
    private final Bot bot;

    public SetupCmd(Bot bot)
    {
        this.bot = bot;
        this.name = "setup";
        this.children = new EndlessCommand[]{new MutedRoleCmd(), new DisableAtEveryone()};
        this.help = "Server setup";
        this.category = Categories.SERVER_CONFIG;
        this.userPerms = new Permission[]{Permission.MANAGE_SERVER};
        this.needsArguments = false;
    }

    @Override
    protected void executeCommand(EndlessCommandEvent event)
    {
        event.reply("command.setup", Const.INFO, Const.LINE_START);
    }

    private class MutedRoleCmd extends EndlessCommand
    {
        MutedRoleCmd()
        {
            this.name = "mutedrole";
            this.help = "Setup the muted role";
            this.category = Categories.SERVER_CONFIG;
            this.botPerms = new Permission[]{Permission.ADMINISTRATOR};
            this.userPerms = new Permission[]{Permission.MANAGE_SERVER};
            this.needsArguments = false;
            this.parent = SetupCmd.this;
        }

        @Override
        protected void executeCommand(EndlessCommandEvent event)
        {
            Guild guild = event.getGuild();
            Role mutedRole = GuildUtils.getMutedRole(guild);
            String confirm;

            if(!(mutedRole==null))
            {
                if(!(ChecksUtil.canMemberInteract(event.getSelfMember(), mutedRole)))
                {
                    event.replyError("command.setup.muted.cantInteract.bot", mutedRole.getName());
                    return;
                }
                if(!(ChecksUtil.canMemberInteract(event.getMember(), mutedRole)))
                {
                    event.replyError("command.setup.muted.cantInteract.user", mutedRole.getName());
                    return;
                }

                confirm = event.localize("command.setup.muted.confirm.nonExistant", mutedRole.getName());
            }
            else
                confirm = event.localize("command.setup.muted.confirm.create");

            waitForConfirm(event, confirm, () -> setupMutedRole(event, mutedRole));
        }
    }

    private class DisableAtEveryone extends EndlessCommand
    {
        DisableAtEveryone()
        {
            this.name = "disableateveryone";
            this.help = "Disables the everyone permission for every role that isn't Admin";
            this.category = Categories.SERVER_CONFIG;
            this.aliases = new String[]{"disableveryone", "disablevaronbros", "disableale0hio"};
            this.botPerms = new Permission[]{Permission.ADMINISTRATOR};
            this.userPerms = new Permission[]{Permission.MANAGE_SERVER};
            this.needsArguments = false;
            this.parent = SetupCmd.this;
        }

        @Override
        protected void executeCommand(EndlessCommandEvent event)
        {
            long count = event.getGuild().getRoles().stream().filter(r -> r.getPermissions().contains(Permission.MESSAGE_MENTION_EVERYONE) &&
                    !(r.getPermissions().contains(Permission.MANAGE_SERVER))).count();

            if(count==0)
            {
                event.replySuccess("command.setup.everyone.noChanges");
                return;
            }

            String confirm = event.localize("command.setup.everyone.confirm");
            waitForConfirm(event, confirm, () -> disableEveryone(event));
        }
    }

    private void setupMutedRole(EndlessCommandEvent event, Role role)
    {
        StringBuilder sb = new StringBuilder(event.getClient().getSuccess()+" "+event.localize("core.setup.starting")+"\n");
        event.reply(sb+Const.LOADING+" "+event.localize("core.setup.muted.creating"), m -> event.async(() -> {
            try
            {
                Role mutedRole;
                if(role==null)
                    mutedRole = event.getGuild().getController().createRole().setName("Muted").setColor(Color.RED).complete();
                else
                    mutedRole = role;
                sb.append(event.getClient().getSuccess()).append(" ").append(event.localize("core.setup.muted.created")).append("\n");
                m.editMessage(sb+Const.LOADING+" "+event.localize("core.setup.muted.creating.categories")).complete();

                PermissionOverride po;
                for(net.dv8tion.jda.core.entities.Category cat : event.getGuild().getCategories())
                {
                    po = cat.getPermissionOverride(mutedRole);
                    if(po==null)
                        cat.createPermissionOverride(mutedRole).setDeny(Permission.MESSAGE_ADD_REACTION, Permission.MESSAGE_WRITE,
                                Permission.VOICE_CONNECT, Permission.VOICE_SPEAK).complete();
                    else
                        po.getManager().deny(Permission.MESSAGE_ADD_REACTION, Permission.MESSAGE_WRITE,
                                Permission.VOICE_CONNECT, Permission.VOICE_SPEAK).complete();
                }
                sb.append(event.getClient().getSuccess()).append(" ").append(event.localize("core.setup.muted.created.categories")).append("\n");
                m.editMessage(sb+Const.LOADING+" "+event.localize("core.setup.muted.creating.text")).complete();
                for(TextChannel tc : event.getGuild().getTextChannels())
                {
                    po = tc.getPermissionOverride(mutedRole);
                    if(po==null)
                        tc.createPermissionOverride(mutedRole).setDeny(Permission.MESSAGE_ADD_REACTION, Permission.MESSAGE_WRITE).complete();
                    else
                        po.getManager().deny(Permission.MESSAGE_ADD_REACTION, Permission.MESSAGE_WRITE).complete();
                }
                sb.append(event.getClient().getSuccess()).append(" ").append(event.localize("core.setup.muted.created.text")).append("\n");
                m.editMessage(sb+Const.LOADING+" "+event.localize("core.setup.muted.creating.voice")).complete();
                for(VoiceChannel vc : event.getGuild().getVoiceChannels())
                {
                    po = vc.getPermissionOverride(mutedRole);
                    if(po==null)
                        vc.createPermissionOverride(mutedRole).setDeny(Permission.VOICE_CONNECT, Permission.VOICE_SPEAK).complete();
                    else
                        po.getManager().deny(Permission.VOICE_CONNECT, Permission.VOICE_SPEAK).complete();
                }
                m.editMessage(sb+event.getClient().getSuccess()+" "+event.localize("core.setup.muted.created.voice")+"\n\n" +
                        event.getClient().getSuccess()+" "+event.localize("core.setup.finished")).queue();
            }
            catch(Exception e)
            {
                m.editMessage(sb+event.getClient().getError()+" "+event.localize("core.setup.muted.error")).queue();
            }
        }));
    }

    private void disableEveryone(EndlessCommandEvent event)
    {
        StringBuilder sb = new StringBuilder(event.getClient().getSuccess()+" "+event.localize("core.setup.starting")+"\n");
        event.reply(sb+Const.LOADING+" "+event.localize("core.setup.everyone.filtering"), m -> event.async(() -> {
            try
            {
                List<Role> roles = event.getGuild().getRoles().stream().filter(r -> r.getPermissions().contains(Permission.MESSAGE_MENTION_EVERYONE) &&
                        !(r.getPermissions().contains(Permission.MANAGE_SERVER)) && ChecksUtil.canMemberInteract(event.getSelfMember(), r))
                        .collect(Collectors.toList());

                for(Role role : roles)
                    role.getManager().revokePermissions(Permission.MESSAGE_MENTION_EVERYONE).complete();

                sb.append(event.getClient().getSuccess()).append(" ").append(event.localize("core.setup.everyone.revoked"));
                m.editMessage(sb).queue();
            }
            catch(Exception e)
            {
                m.editMessage(sb+event.getClient().getError()+" "+event.localize("core.setup.everyone.error")).queue();
            }
        }));
    }

    private void waitForConfirm(EndlessCommandEvent event, String confirm, Runnable action)
    {
        new ButtonMenu.Builder()
                .setChoices("444226239683624962", "444226355555729428")
                .setEventWaiter(bot.waiter)
                .setTimeout(1, TimeUnit.MINUTES)
                .setText(event.getClient().getWarning()+" "+confirm+"\n\n"+event.getClient().getSuccess()+" "+event.localize("misc.continue")+"\n" +
                        event.getClient().getError()+" "+event.localize("misc.cancel"))
                .setFinalAction(m -> m.delete().queue(s -> {}, e -> {}))
                .setUsers(event.getAuthor())
                .setAction(re -> {
                    if(re.isEmote() && re.getEmote().getAsMention().equals(event.getClient().getSuccess()))
                        action.run();
                }).build().display(event.getTextChannel());
    }
}
