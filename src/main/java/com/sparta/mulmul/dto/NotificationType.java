package com.sparta.mulmul.dto;

public enum NotificationType {

    CHAT(NotificationType.Type.CHAT),
    BARTER(NotificationType.Type.BARTER),
    FINISH(NotificationType.Type.FINISH),
    SCORE(NotificationType.Type.SCORE),
    ETC(NotificationType.Type.ETC);

    private final String notificationType;

    NotificationType(String notificationType) { this.notificationType = notificationType; }

    public static class Type {
        public static final String CHAT = "CHAT";
        public static final String BARTER = "BARTER";
        public static final String FINISH = "FINISH";
        public static final String SCORE = "SCORE";
        public static final String ETC = "ETC";
    }
}
