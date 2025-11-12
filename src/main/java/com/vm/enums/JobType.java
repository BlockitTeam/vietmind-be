package com.vm.enums;

public enum JobType {
    APPOINTMENT_MAIL_24H_REMINDER("appointment_mail_24h_reminder"),
    APPOINTMENT_NOTIFICATION_1H("appointment_notification_1h");
    
    private final String value;
    
    JobType(String value) {
        this.value = value;
    }
    
    public String getValue() {
        return value;
    }
    
    @Override
    public String toString() {
        return value;
    }
}
