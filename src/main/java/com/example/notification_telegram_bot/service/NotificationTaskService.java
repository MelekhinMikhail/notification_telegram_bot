package com.example.notification_telegram_bot.service;

import com.example.notification_telegram_bot.entity.NotificationTask;
import com.example.notification_telegram_bot.repository.NotificationTaskRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class NotificationTaskService {

    private final NotificationTaskRepository notificationTaskRepository;

    public NotificationTaskService(NotificationTaskRepository notificationTaskRepository) {
        this.notificationTaskRepository = notificationTaskRepository;
    }

    public void save(NotificationTask notificationTask) {
        notificationTaskRepository.save(notificationTask);
    }

    public List<NotificationTask> getAllNotificationTasksByDateTime(LocalDateTime localDateTime) {

        return notificationTaskRepository.findAllByNotificationDateTime(localDateTime);
    }

    public void delete(NotificationTask notificationTask) {
        notificationTaskRepository.delete(notificationTask);
    }
}
