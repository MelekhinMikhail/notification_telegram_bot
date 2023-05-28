package com.example.notification_telegram_bot.listener;

import com.example.notification_telegram_bot.entity.NotificationTask;
import com.example.notification_telegram_bot.service.NotificationTaskService;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.response.SendResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.lang.Nullable;

import javax.annotation.PostConstruct;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Configuration
public class TelegramBotUpdatesListener implements UpdatesListener {

    private final Logger logger = LoggerFactory.getLogger(TelegramBotUpdatesListener.class);
    private final Pattern pattern = Pattern.compile(
            "(\\d{1,2}\\.\\d{1,2}\\.\\d{4} \\d{1,2}:\\d{2})\\s+([A-я\\d\\s.,!&]+)"
    );
    private final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(
            "dd.MM.yyyy HH:mm"
    );
    private final TelegramBot telegramBot;
    private final NotificationTaskService notificationTaskService;

    public TelegramBotUpdatesListener(TelegramBot telegramBot, NotificationTaskService notificationTaskService) {
        this.telegramBot = telegramBot;
        this.notificationTaskService = notificationTaskService;
    }

    @PostConstruct
    public void init() {
        telegramBot.setUpdatesListener(this);
    }

    @Override
    public int process(List<Update> list) {
        try {
            list.stream()
                    .filter(update -> update.message() != null)
                    .forEach(update -> {

                        logger.info("Handles update: {}", update);

                        Message message = update.message();
                        Long chatId = message.chat().id();
                        String text = message.text();

                        if ("/start".equals(text)) {
                            sendMessage(chatId,
                                    "Привет! Я помогу тебе запланировать задачу. Отправь ее в формате 01.01.2024 21:00 Текст задачи"
                            );
                        } else if (text != null) {
                            Matcher matcher = pattern.matcher(text);

                            if (matcher.find()) {
                                LocalDateTime dateTime = parse(matcher.group(1));

                                if (Objects.isNull(dateTime)) {
                                    sendMessage(chatId, "Неккоректный формат даты и/или времени!");
                                } else {
                                    LocalDateTime currentDateTime = LocalDateTime.now();

                                    if (dateTime.isBefore(currentDateTime)) {
                                        sendMessage(chatId,
                                                "Я не могу напоминать то, что уже прошло :)"
                                        );

                                    } else {
                                        String txt = matcher.group(2);

                                        NotificationTask notificationTask = new NotificationTask();
                                        notificationTask.setChatId(chatId);
                                        notificationTask.setMessage(txt);
                                        notificationTask.setNotificationDateTime(dateTime);

                                        notificationTaskService.save(notificationTask);

                                        sendMessage(chatId,
                                                "Задача успешно запланирована!"
                                        );
                                    }
                                }
                            } else {
                                sendMessage(chatId,
                                        "Некорректный формат сообщения!"
                                );
                            }
                        }
                    });
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }

        return UpdatesListener.CONFIRMED_UPDATES_ALL;
    }

    private void sendMessage(Long chatId, String message) {
        SendMessage sendMessage = new SendMessage(chatId, message);

        SendResponse sendResponse = telegramBot.execute(sendMessage);
        if (!sendResponse.isOk()) {
            logger.error("Error during sending message: {}", sendResponse.description());
        }
    }

    @Nullable
    private LocalDateTime parse(String dateTime) {
        try {
            return LocalDateTime.parse(dateTime, dateTimeFormatter);
        } catch (DateTimeParseException e) {
            return null;
        }
    }
}
