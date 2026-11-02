package shelter.database;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

// A very small JSON reader/writer, so the project needs no external library.
// Objects become Map<String, Object>, arrays become List<Object>,
// numbers become Double, and true/false become Boolean.
public class Json {

    // ===================== Writing =====================

    public static String write(Object value) {
        StringBuilder out = new StringBuilder();
        write(value, out, 0);
        return out.toString();
    }

    private static void write(Object value, StringBuilder out, int indent) {
        if (value == null) {
            out.append("null");
        } else if (value instanceof String) {
            writeString((String) value, out);
        } else if (value instanceof Number || value instanceof Boolean) {
            out.append(value);
        } else if (value instanceof Map) {
            Map<?, ?> map = (Map<?, ?>) value;
            if (map.isEmpty()) {
                out.append("{}");
                return;
            }
            out.append("{\n");
            int i = 0;
            for (Map.Entry<?, ?> entry : map.entrySet()) {
                spaces(out, indent + 1);
                writeString(entry.getKey().toString(), out);
                out.append(": ");
                write(entry.getValue(), out, indent + 1);
                out.append(++i < map.size() ? ",\n" : "\n");
            }
            spaces(out, indent);
            out.append("}");
        } else if (value instanceof List) {
            List<?> list = (List<?>) value;
            if (list.isEmpty()) {
                out.append("[]");
                return;
            }
            out.append("[\n");
            for (int i = 0; i < list.size(); i++) {
                spaces(out, indent + 1);
                write(list.get(i), out, indent + 1);
                out.append(i < list.size() - 1 ? ",\n" : "\n");
            }
            spaces(out, indent);
            out.append("]");
        } else {
            writeString(value.toString(), out);
        }
    }

    private static void writeString(String text, StringBuilder out) {
        out.append('"');
        for (char c : text.toCharArray()) {
            switch (c) {
                case '"': out.append("\\\""); break;
                case '\\': out.append("\\\\"); break;
                case '\n': out.append("\\n"); break;
                case '\r': out.append("\\r"); break;
                case '\t': out.append("\\t"); break;
                default:
                    if (c < 0x20) {
                        out.append(String.format("\\u%04x", (int) c));
                    } else {
                        out.append(c);
                    }
            }
        }
        out.append('"');
    }

    private static void spaces(StringBuilder out, int indent) {
        for (int i = 0; i < indent; i++) {
            out.append("  ");
        }
    }

    // ===================== Reading =====================

    public static Object parse(String text) {
        Json parser = new Json(text);
        Object value = parser.readValue();
        parser.skipSpaces();
        if (parser.pos != text.length()) {
            throw new IllegalArgumentException("Unexpected text at position " + parser.pos);
        }
        return value;
    }

    private final String text;
    private int pos;

    private Json(String text) {
        this.text = text;
    }

    private Object readValue() {
        skipSpaces();
        if (pos >= text.length()) {
            throw new IllegalArgumentException("Unexpected end of JSON");
        }
        char c = text.charAt(pos);
        switch (c) {
            case '{': return readObject();
            case '[': return readArray();
            case '"': return readString();
            case 't': expect("true"); return Boolean.TRUE;
            case 'f': expect("false"); return Boolean.FALSE;
            case 'n': expect("null"); return null;
            default: return readNumber();
        }
    }

    private Map<String, Object> readObject() {
        Map<String, Object> map = new LinkedHashMap<>();
        pos++; // skip {
        skipSpaces();
        if (text.charAt(pos) == '}') {
            pos++;
            return map;
        }
        while (true) {
            skipSpaces();
            String key = readString();
            skipSpaces();
            expect(":");
            map.put(key, readValue());
            skipSpaces();
            char c = text.charAt(pos++);
            if (c == '}') {
                return map;
            }
            if (c != ',') {
                throw new IllegalArgumentException("Expected , or } at position " + (pos - 1));
            }
        }
    }

    private List<Object> readArray() {
        List<Object> list = new ArrayList<>();
        pos++; // skip [
        skipSpaces();
        if (text.charAt(pos) == ']') {
            pos++;
            return list;
        }
        while (true) {
            list.add(readValue());
            skipSpaces();
            char c = text.charAt(pos++);
            if (c == ']') {
                return list;
            }
            if (c != ',') {
                throw new IllegalArgumentException("Expected , or ] at position " + (pos - 1));
            }
        }
    }

    private String readString() {
        expect("\"");
        StringBuilder out = new StringBuilder();
        while (true) {
            char c = text.charAt(pos++);
            if (c == '"') {
                return out.toString();
            }
            if (c == '\\') {
                char escaped = text.charAt(pos++);
                switch (escaped) {
                    case 'n': out.append('\n'); break;
                    case 'r': out.append('\r'); break;
                    case 't': out.append('\t'); break;
                    case 'u':
                        out.append((char) Integer.parseInt(text.substring(pos, pos + 4), 16));
                        pos += 4;
                        break;
                    default: out.append(escaped); // \" \\ \/
                }
            } else {
                out.append(c);
            }
        }
    }

    private Double readNumber() {
        int start = pos;
        while (pos < text.length() && "+-0123456789.eE".indexOf(text.charAt(pos)) >= 0) {
            pos++;
        }
        if (start == pos) {
            throw new IllegalArgumentException("Unexpected character at position " + pos);
        }
        return Double.parseDouble(text.substring(start, pos));
    }

    private void expect(String word) {
        if (!text.startsWith(word, pos)) {
            throw new IllegalArgumentException("Expected " + word + " at position " + pos);
        }
        pos += word.length();
    }

    private void skipSpaces() {
        while (pos < text.length() && Character.isWhitespace(text.charAt(pos))) {
            pos++;
        }
    }
}
