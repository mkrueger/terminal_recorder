package org.example;


import org.json.simple.JSONObject;

class Header {
    String id;

    int width;
    int height;
    long timeStamp;
    String title;
    String term;
    String shell;
    String userId;
    Theme theme;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getWidth() {
        return this.width;
    }
    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return this.height;
    }
    public void setHeight(int height) {
        this.height = height;
    }

    public long getTimestamp() {
        return this.timeStamp;
    }
    public void setTimeStamp(long timeStamp) {
        this.timeStamp = timeStamp;
    }

    public String getTitle() {
        return this.title;
    }
    public void setTitle(String title) {
        this.title = title;
    }

    public String getTerm() {
        return this.term;
    }
    public void setTerm(String term) {
        this.term = term;
    }

    public String getShell() {
        return this.shell;
    }
    public void setShell(String shell) {
        this.shell= shell;
    }

    public Theme getTheme() {
        return this.theme;
    }
    public void setTheme(Theme theme) {
        this.theme = theme;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public static Header parse(JSONObject jsonObject) {
        var result = new Header();
        result.width = ((Long)jsonObject.get("width")).intValue();
        result.height = ((Long)jsonObject.get("height")).intValue();
        result.title = (String)jsonObject.get("title");
        result.timeStamp = ((Long)jsonObject.get("timestamp")).longValue();

        var env = (JSONObject) jsonObject.get("env");
        if (env != null) {
            result.term = (String)env.get("TERM");
            result.shell = (String)env.get("SHELL");
        }
        var theme = (JSONObject) jsonObject.get("theme");
        if (theme != null) {
            result.theme = Theme.parse(theme);
        }
        return result;
    }

    public String toJSON() {
        StringBuilder builder = new StringBuilder();
        builder.append("{\"version\": 2");
        builder.append(", \"width\": ");
        builder.append(this.width);
        builder.append(", \"height\": ");
        builder.append(this.height);
        builder.append(", \"timestamp\": ");
        builder.append(this.timeStamp);
        builder.append(", \"title\": \"");
        builder.append(this.title);
        builder.append("\"");
        builder.append(", \"env\": {\"TERM\": \"");
        builder.append(this.term);
        builder.append("\", \"SHELL\": \"");
        builder.append(this.shell);
        builder.append("\"}");
        if (this.theme != null) {
            builder.append(", \"theme\": ");
            builder.append(this.theme.toJSON());
        }
        builder.append(" }");
        return builder.toString();
    }

    @Override
    public String toString() {
        return "FileHeader{" +
                "width=" + width +
                ", height=" + height +
                ", title='" + title + '\'' +
                ", term='" + term + '\'' +
                ", shell='" + shell + '\'' +
                ", theme=" + theme +
                '}';
    }
}
