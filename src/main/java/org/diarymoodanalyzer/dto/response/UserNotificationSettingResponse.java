package org.diarymoodanalyzer.dto.response;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.diarymoodanalyzer.domain.UserNotificationSetting;

/**
 * 사용자 알림 설정 응답 DTO
 * 오버헤드를 막기 위해 필요한 정보만 포함하였다.
 * User 정보는 프론트에서 불필요하기에 생략하였다.(성능 문제)
 */
@NoArgsConstructor
@Setter
@Getter
public class UserNotificationSettingResponse {
    private String notificationTypeName;
    private boolean isNotifyEnabled;
    private boolean isWebEnabled;
    private boolean isEmailEnabled;

    public UserNotificationSettingResponse(UserNotificationSetting setting) {
        this.notificationTypeName = setting.getNotificationType() != null ? setting.getNotificationType().getName() : "";
        this.isNotifyEnabled = setting.isNotifyEnabled();
        this.isWebEnabled = setting.isWebEnabled();
        this.isEmailEnabled = setting.isEmailEnabled();
    }
}
