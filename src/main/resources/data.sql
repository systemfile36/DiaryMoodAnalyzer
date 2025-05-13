-- Insert default notification type records
INSERT INTO notification_types(name, description, default_template, default_notify_enabled, default_email_enabled, default_web_enabled)
VALUES ('NEW_COMMENT', 'New comment has added to own diary', '{expert}님의 새 코멘트가 작성되었습니다.', true, true, true),
    ('DANGER_DEPRESSION', 'There is User managed by you depression level is too low', '{user}님의 우울 수치가 위험 단계입니다.', true, true, true),
    ('NEW_DIARY', 'User managed by you write new diary.', '{user}님의 새 Diary가 작성되었습니다.', true, false, true),
    ('WEB_TEST', 'For test of web notification', '{Test} Test', true, false, true),
    ('EMAIL_TEST', 'For test of email notification', '{Test} Test', true, true, false);

