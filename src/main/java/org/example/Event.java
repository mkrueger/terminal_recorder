package org.example;

import org.json.simple.JSONValue;

import java.util.UUID;

enum EventType {
    Output,
    Input,
    Marker,
    Resize
}
class Event {
    String id;
    String fileId;
    float timeStamp;
    EventType type;
    String data;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
    public String getFileId() {
        return fileId;
    }

    public void setFileId(String fileId) {
        this.fileId = fileId;
    }

    public float getTimeStamp() {
        return this.timeStamp;
    }
    public void setTimeStamp(float timeStamp) {
        this.timeStamp = timeStamp;
    }

    public EventType getEventType() {
        return this.type;
    }
    public void setEventType(EventType type) {
        this.type = type;
    }

    public String getData() {
        return this.data;
    }
    public void setData(String data) {
        this.data = data;
    }

    public Event() {
    }

    public Event(String fileId, float timeStamp, EventType type, String data) {
        this.fileId = fileId;
        this.id = UUID.randomUUID().toString();
        this.timeStamp = timeStamp;
        this.type = type;
        this.data = data;
    }

    public String toOutput() {
        var builder = new StringBuilder();
        builder.append("[");
        builder.append(timeStamp);
        builder.append(", ");
        switch (this.type) {
            case Output:
                builder.append("\"o\"");
                break;
            case Input:
                builder.append("\"i\"");
                break;
            case Marker:
                builder.append("\"m\"");
                break;
            case Resize:
                builder.append("\"r\"");
                break;
        }
        builder.append(", \"");
        builder.append(JSONValue.escape(this.data));
        builder.append("\"]");
        return builder.toString();
    }
}