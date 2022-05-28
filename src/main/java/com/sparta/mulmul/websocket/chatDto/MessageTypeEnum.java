package com.sparta.mulmul.websocket.chatDto;

public enum MessageTypeEnum {

    IN(Type.IN),
    OUT(Type.OUT),
    STATUS(Type.STATUS),
    TALK(Type.TALK),
    FULL(Type.FULL),
    NORMAL(Type.NORMAL);

    private final String messageType;

    MessageTypeEnum(String messageType) { this.messageType = messageType; }

    public static class Type {
        public static final String IN = "IN";
        public static final String OUT = "OUT";
        public static final String STATUS = "STATUS";
        public static final String TALK = "TALK";
        public static final String FULL = "FULL";
        public static final String NORMAL = "NORMAL";
    }
}