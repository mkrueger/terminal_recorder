package org.example;

import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.UUID;
import java.util.stream.Stream;

class File {

    Header header;
    Event[] events;

    File() {
        events = new Event[0];
    }

    public Header getHeader() {
        return this.header;
    }
    public void setHeader(Header header) {
        this.header = header;
    }

    public Event[] getEvents() {
        return this.events;
    }
    public void setEvents(Event[] events) {
        this.events = events;
    }

    static File loadFile(String fileName) throws ParseException, IOException {
        var result = new File();
        var stream = Files.lines(Paths.get(fileName));
        var it = stream.iterator();
        if (it.hasNext()) {
            var firstLine = it.next();
            var parser = new JSONParser();
            var jsonObj = parser.parse(firstLine);
            var jsonObject = (JSONObject) jsonObj;
            result.header = Header.parse(jsonObject);
            result.header.setId(UUID.randomUUID().toString());
        }
        var events = new ArrayList<Event>();
        while (it.hasNext()) {
            var line = it.next();
            if (!line.startsWith("[")) {
                continue;
            }
            var comma1 = line.indexOf(',');
            if (comma1 < 0) {
                continue;
            }
            var comma2 = line.indexOf(',', comma1 + 1);
            if (comma2 < 0) {
                continue;
            }
            float timeStamp = Float.parseFloat(line.substring(1, comma1));

            var typeStr = line.substring(comma1 + 1, comma2);
            var type = EventType.Output;
            if (typeStr.contains("i")) {
                type = EventType.Input;
            } else if (typeStr.contains("m")) {
                type = EventType.Marker;
            } else if (typeStr.contains("r")) {
                type = EventType.Resize;
            }
            var data = line.substring(comma2 + 1, line.length() -1).trim();
            data = (String) JSONValue.parse(data);
            events.add(new Event(result.header.getId(), timeStamp, type, data));
        }
        result.events = events.toArray(new Event[events.size()]);
        return result;
    }

    public String toOutput() {
        var builder = new StringBuilder();
        builder.append(this.header.toJSON());
        builder.append("\n");
        for (var e: this.events) {
            builder.append(e.toOutput());
            builder.append("\n");
        }
        return builder.toString();
    }
}
