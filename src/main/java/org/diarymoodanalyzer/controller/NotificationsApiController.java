package org.diarymoodanalyzer.controller;

import lombok.RequiredArgsConstructor;
import org.diarymoodanalyzer.domain.UserNotificationSetting;
import org.diarymoodanalyzer.dto.request.NotificationRequest;
import org.diarymoodanalyzer.dto.request.NotificationSettingRequest;
import org.diarymoodanalyzer.dto.response.NotificationResponse;
import org.diarymoodanalyzer.service.NotificationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/notifications")
public class NotificationsApiController {

    private final NotificationService notificationService;

    // Notification Endpoints

    @GetMapping
    public ResponseEntity<List<NotificationResponse>> getNotifications() {
        return ResponseEntity.ok().body(notificationService.getNotificationsForTarget());
    }

    @PostMapping
    public ResponseEntity<Void> sendNotification(
            @RequestBody NotificationRequest req
    ) {
        notificationService.sendNotificationWithAuth(req);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/{id}/read")
    public ResponseEntity<Void> updateAsRead(@PathVariable Long id) {
        notificationService.updateAsRead(id);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/read")
    public ResponseEntity<Void> updateAsRead(@RequestBody List<Long> ids) {
        notificationService.updateAsRead(ids);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/read/all")
    public ResponseEntity<Void> updateAllAsRead() {
        notificationService.updateAllAsRead();
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteNotification(@PathVariable Long id) {
        notificationService.deleteNotification(id);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping
    public ResponseEntity<Void> deleteNotification(@RequestBody List<Long> ids) {
        notificationService.deleteNotification(ids);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/all")
    public ResponseEntity<Void> deleteNotification() {
        notificationService.deleteAllNotification();
        return ResponseEntity.ok().build();
    }

    // UserNotificationSetting Endpoints

    @GetMapping("/settings")
    public ResponseEntity<List<UserNotificationSetting>> getUserNotificationSettings() {
        return ResponseEntity.ok().body(
                notificationService.getUserNotificationSettings()
        );
    }

    @PatchMapping("/settings")
    public ResponseEntity<Void> updateUserNotificationSetting(
            @RequestBody NotificationSettingRequest req
    ) {
        notificationService.updateUserNotificationSetting(req);
        return ResponseEntity.ok().build();
    }

}
