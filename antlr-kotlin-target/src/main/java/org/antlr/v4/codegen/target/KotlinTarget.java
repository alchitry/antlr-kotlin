package org.antlr.v4.codegen.target;

import org.antlr.v4.codegen.CodeGenerator;
import org.antlr.v4.codegen.UnicodeEscapes;

import java.util.*;

public class KotlinTarget extends JavaTarget {

    protected static final String[] kotlinKeywords = {
            "abstract", "catch",
            "class", "const", "else",
            "enum", "false", "for",
            "if", "implements", "import", "is", "interface",
            "null", "package", "private", "internal",
            "public", "return",
            "this", "throw", "true", "try",
            "while", "object"
    };

    /**
     * Avoid grammar symbols in this set to prevent conflicts in gen'd code.
     */
    protected final Set<String> badWords = new HashSet<>();

    protected static final Map<Character, String> charValueEscape;

    static {
        HashMap<Character, String> map = new HashMap<>(defaultCharValueEscape);
        map.put((char) 10, "\\u000a");
        map.put((char) 11, "\\u000b");
        map.put((char) 12, "\\u000c");
        map.put((char) 13, "\\u000d");
        map.put((char) 14, "\\u000e");
        map.put((char) 15, "\\u000f");
        addEscapedChar(map, '$');
        charValueEscape = map;
    }

    public KotlinTarget(CodeGenerator gen) {
        super(gen);
    }

    @Override
    public Map<Character, String> getTargetCharValueEscape() {
        return charValueEscape;
    }

    @Override
    public String getTargetStringLiteralFromANTLRStringLiteral(CodeGenerator generator, String literal, boolean addQuotes,
                                                               boolean escapeSpecial) {
        return super.getTargetStringLiteralFromANTLRStringLiteral(generator, literal, addQuotes, escapeSpecial).replace("$", "\\$");
    }

    public Set<String> getBadWords() {
        if (badWords.isEmpty()) {
            addBadWords();
        }

        return badWords;
    }

    protected void addBadWords() {
        badWords.addAll(Arrays.asList(kotlinKeywords));
        badWords.add("rule");
        badWords.add("parserRule");
    }

    @Override
    public Set<String> getReservedWords() {
        return getBadWords();
    }

    @Override
    public String encodeInt16AsCharEscape(int v) {
        if (v < Character.MIN_VALUE || v > Character.MAX_VALUE) {
            throw new IllegalArgumentException(String.format("Cannot encode the specified value: %d", v));
        }

        return "\\u" + Integer.toHexString(v | 0x10000).substring(1, 5);
    }

    @Override
    protected void appendUnicodeEscapedCodePoint(int codePoint, StringBuilder sb) {
        //System.out.println("AAAA "+codePoint);
        UnicodeEscapes.appendEscapedCodePoint(sb, codePoint, "Java");
    }


}
