package com.jagrosh.jmusicbot.utils;

import com.jagrosh.jmusicbot.Bot;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.exceptions.ErrorResponseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.PrintWriter;
import java.io.StringWriter;

public final class ErrorReporter {
    private static final Logger LOG = LoggerFactory.getLogger(ErrorReporter.class);
    private static final int MAX_TRACE_LENGTH = 1800;

    private ErrorReporter() {}

    public static void reportError(Bot bot, MessageChannel channel, Throwable throwable) {
        if(bot == null || throwable == null)
            return;
        String payload = formatStacktrace(throwable);
        if(channel != null) {
            channel.sendMessage(payload).queue(null, failure -> sendToOwner(bot, payload));
        } else {
            sendToOwner(bot, payload);
        }
    }

    public static void reportError(Bot bot, long channelId, Throwable throwable) {
        MessageChannel channel = null;
        if(bot != null && bot.getJDA() != null && channelId != 0L) {
            TextChannel textChannel = bot.getJDA().getTextChannelById(channelId);
            channel = textChannel;
        }
        reportError(bot, channel, throwable);
    }

    private static void sendToOwner(Bot bot, String payload) {
        if(bot == null || bot.getJDA() == null)
            return;
        long ownerId = bot.getConfig().getOwnerId();
        bot.getJDA().retrieveUserById(ownerId).queue(user ->
                user.openPrivateChannel().queue(pc -> pc.sendMessage(payload).queue(),
                        error -> LOG.error("Failed to DM owner about error", error)),
                error -> handleOwnerLookupFailure(error));
    }

    private static void handleOwnerLookupFailure(Throwable error) {
        if(error instanceof ErrorResponseException)
            LOG.warn("Unable to notify owner about an error: {}", error.getMessage());
        else
            LOG.error("Unable to notify owner about an error", error);
    }

    private static String formatStacktrace(Throwable throwable) {
        StringWriter sw = new StringWriter();
        throwable.printStackTrace(new PrintWriter(sw));
        String trace = sw.toString();
        if(trace.length() > MAX_TRACE_LENGTH)
            trace = trace.substring(0, MAX_TRACE_LENGTH - 3) + "...";
        return "An internal error occurred. Full stack trace:\n```" + trace + "```";
    }
}
