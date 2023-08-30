package org.antlr.v4.codegen.target;

import org.antlr.v4.codegen.CodeGenerator;
import org.antlr.v4.codegen.Target;
import org.antlr.v4.codegen.UnicodeEscapes;
import org.antlr.v4.tool.ast.GrammarAST;
import org.stringtemplate.v4.STGroup;
import org.stringtemplate.v4.StringRenderer;

import java.util.*;

public class KotlinTarget extends JavaTarget {
    /**
     * The Kotlin target can cache the code generation templates.
     */
    private static final ThreadLocal<STGroup> targetTemplates = new ThreadLocal<STGroup>();

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
    protected final Set<String> badWords = new HashSet<String>();

    protected static final Map<Character, String> charValueEscape;

    static {
        HashMap<Character, String> map = new HashMap<>();
        addEscapedChar(map, '\n', 'n');
        addEscapedChar(map, '\r', 'r');
        addEscapedChar(map, '\t', 't');
        addEscapedChar(map, '\b', 'b');
        map.put('\\', "\\\\");
        map.put('\'', "\\'");
        map.put((char) 10, "\\u000a");
        map.put((char) 11, "\\u000b");
        map.put((char) 12, "\\u000c");
        map.put((char) 13, "\\u000d");
        map.put((char) 14, "\\u000e");
        map.put((char) 15, "\\u000f");
        map.put('"', "\\\"");
        charValueEscape = map;
    }

    public KotlinTarget(CodeGenerator gen) {
        super(gen);
    }

    @Override
    public Map<Character, String> getTargetCharValueEscape() {
        return super.getTargetCharValueEscape();
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
    public int getSerializedATNSegmentLimit() {
        // 65535 is the class file format byte limit for a UTF-8 encoded string literal
        // 3 is the maximum number of bytes it takes to encode a value in the range 0-0xFFFF
        return 65535 / 3;
    }

    @Override
    protected STGroup loadTemplates() {
        STGroup result = targetTemplates.get();
        if (result == null) {
            result = super.loadTemplates();
            result.registerRenderer(String.class, new KotlinStringRenderer(), true);
            targetTemplates.set(result);
        }

        return result;
    }

    protected static class KotlinStringRenderer extends StringRenderer {

        @Override
        public String toString(Object o, String formatString, Locale locale) {
//			if ("java-escape".equals(formatString)) {
//				// 5C is the hex code for the \ itself
//				return ((String)o).replace("\\u", "\\u005Cu");
//			}
//			if (o.equals("\\f")) {
//				return "\\u000C";
//			}
//			if (formatString == null && (o instanceof String)){
//				String s = (String)o;
//				if (s.startsWith("\\")) {
//					boolean ok = true;
//					for (int i=1;i<s.length() && ok;i++) {
//						ok = Character.isDigit(s.charAt(i));
//					}
//					if (ok) {
//						int n = Integer.parseInt(s.substring(1));
//						//if (n == 14) n = 12;
//						return String.format("\\u%04X", n);
////						String hexPart = Integer.toHexString(n);
////						String res = "\\u";
////						for (int i=0;i<4-hexPart.length();i++) {
////							res = res + "0";
////						}
////						res += hexPart;
////						if (res.equals("\\u000d")) {
////							System.out.println("GOT AAA " + o + " -> " + res);
////						}
////						return res;
//					}
//				}
//			}
//
//			String res = super.toString(o, formatString, locale);
//			if (res.equals("\\u000d")) {
//				System.out.println("GOT ZZZ " + o + " -> " + res);
//			}

            return super.toString(o, formatString, locale);
        }

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
