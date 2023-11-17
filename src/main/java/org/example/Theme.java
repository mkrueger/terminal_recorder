package org.example;

import org.json.simple.JSONObject;

import java.util.ArrayList;

class Theme {
    String fg;
    String bg;
    String[] palette;

    Theme() {
    }

    public String getFg() {
        return this.fg;
    }
    public void setFg(String fg) {
        this.fg = fg;
    }

    public String getBg() {
        return this.bg;
    }
    public void setBg(String bg) {
        this.bg = bg;
    }

    public String[] getPalette() {
        return this.palette;
    }
    public void setBg(String[] palette) {
        this.palette = palette;
    }

    public static Theme parse(JSONObject jsonObject) {
        var result = new Theme();
        result.fg = (String)jsonObject.get("fg");
        result.bg = (String)jsonObject.get("bg");

        var palette = (String) jsonObject.get("palette");
        var palettes = new ArrayList<>();
        if (palette != null) {
            for (String color: palette.split(":")) {
                palettes.add(color);
            }
        }
        result.palette = palettes.toArray(new String[palettes.size()]);
        return result;
    }

    public String toJSON() {
        var builder = new StringBuilder();
        builder.append("{ \"fg\": \"");
        builder.append(this.fg);
        builder.append("\", \"bg\": \"");
        builder.append(this.bg);
        builder.append("\", \"palette\": \"");
        var first = true;
        for (String pal: this.palette) {
            if (!first) {
                builder.append(':');
            }
            first = false;
            builder.append(pal);
        }
        builder.append("\" }");
        return builder.toString();
    }

    @Override
    public String toString() {
        return "Theme{" +
                "fg='" + fg + '\'' +
                ", bg='" + bg + '\'' +
                ", palette=" + palette +
                '}';
    }
}
