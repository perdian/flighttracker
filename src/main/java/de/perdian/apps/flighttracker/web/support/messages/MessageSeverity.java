package de.perdian.apps.flighttracker.web.support.messages;

public enum MessageSeverity {

    INFO("green"),
    WARNING("yellow"),
    ERROR("red");

    private String color = null;

    private MessageSeverity(String color) {
        this.setColor(color);
    }

    public String getColor() {
        return this.color;
    }
    private void setColor(String color) {
        this.color = color;
    }

}
