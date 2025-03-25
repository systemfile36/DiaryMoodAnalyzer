package org.diarymoodanalyzer.controller;

import lombok.RequiredArgsConstructor;
import org.diarymoodanalyzer.dto.request.NotificationRequest;
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

    @GetMapping
    public ResponseEntity<List<NotificationResponse>> getNotifications() {
        return ResponseEntity.ok().body(notificationService.getNotificationsForTarget());
    }

    @PostMapping
    public ResponseEntity<Void> sendNotification(
            @RequestBody NotificationRequest req
    ) {
        notificationService.sendNotification(req);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/{id}/read")
    public ResponseEntity<Void> updateAsRead(@PathVariable Long id) {
        notificationService.updateAsRead(id);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteNotification(@PathVariable Long id) {
        notificationService.deleteNotification(id);
        return ResponseEntity.ok().build();
    }
}
