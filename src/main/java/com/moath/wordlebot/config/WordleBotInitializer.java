package com.moath.wordlebot.config;

import com.moath.wordlebot.service.WordleTrackerBotService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.updates.DeleteWebhook;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

@Slf4j
@Component
@RequiredArgsConstructor
public class WordleBotInitializer {

    private final WordleTrackerBotService wordleBot;

    @EventListener({ContextRefreshedEvent.class})
    public void init() {
        try {
            // First, try to delete any existing webhook
            DeleteWebhook deleteWebhook = new DeleteWebhook();
            wordleBot.execute(deleteWebhook);

            // Then initialize the bot with long polling
            TelegramBotsApi telegramBotsApi = new TelegramBotsApi(DefaultBotSession.class);
            telegramBotsApi.registerBot(wordleBot);

            log.info("Bot successfully initialized and registered");
        } catch (TelegramApiException e) {
            log.error("Failed to initialize bot: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to initialize Telegram bot: " + e.getMessage(), e);
        }
    }
}