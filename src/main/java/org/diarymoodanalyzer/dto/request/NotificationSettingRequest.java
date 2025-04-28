package org.diarymoodanalyzer.dto.request;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.diarymoodanalyzer.domain.UserNotificationSetting;

@NoArgsConstructor
@Getter
@Setter
public class NotificationSettingRequest {
    private String typeName;
    private boolean isNotifyEnabled;
    private boolean isWebEnabled;
    private boolean isEmailEnabled;

    @Builder
    public NotificationSettingRequest(String typeName, boolean isNotifyEnabled, boolean isWebEnabled, boolean isEmailEnabled) {
        this.typeName = typeName; this.isNotifyEnabled = isNotifyEnabled; this.isWebEnabled = isWebEnabled;
        this.isEmailEnabled = isEmailEnabled;
    }

    /**
     * 알림 설정을 DTO에 포함된 값으로 업데이트 한다.
     * @param setting 변경할 알림 설정 객체
     */
    public void updateNotificationSetting(UserNotificationSetting setting) {
        setting.setNotifyEnabled(this.isNotifyEnabled);
        setting.setWebEnabled(this.isWebEnabled);
        setting.setEmailEnabled(this.isEmailEnabled);
    }
}
