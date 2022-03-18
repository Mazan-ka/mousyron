import java.util.regex.*;
import java.util.*;

public class Lexer {
    static Map<String, Pattern> lexems = new HashMap<>();

    static { //определение регулярных выражений для лексем
        lexems.put("OP", Pattern.compile("^\\+|-|/|\\*$"));
        lexems.put("VAR", Pattern.compile("^[a-z][a-z0-9]*$"));
        //lexems.put("KEYWORDS", Pattern.compile("if|else|for"));
        lexems.put("ASSIGN_OP", Pattern.compile("^=$"));
        lexems.put("DIGIT", Pattern.compile("^0|([1-9][0-9]*)$"));
        lexems.put("R_BRACKET", Pattern.compile("^\\)$"));
        lexems.put("L_BRACKET", Pattern.compile("^\\($"));
    }

    static List<Token> tokens = new LinkedList<>();

    public static void main(String[] args) {
        String src = "num=(100+1)"; //основная строка

        match(src);//вызов основного метода лексера

        System.out.println("Tokens: ");
        for(Token t : tokens) { System.out.println(t); }
    }

    static void match(String src) {
        StringBuffer buffer = new StringBuffer("");
        String buffString = "";
        boolean isValid;
        char[] chArray = src.toCharArray();
        int i = 0;
        int length = chArray.length;
        Matcher m;

        while (i < length) { //Проходимся по всем символам в строке src
            buffer.append(chArray[i]);
            //System.out.println(buffer);

            isValid = false;

            for (String lexemName : lexems.keySet()) {
                Pattern p = lexems.get(lexemName);
                m = p.matcher(buffer);

                if (m.matches()) { //проверка буфера на соответствие регулярным выражениям
                    //System.out.println("проверка связи!");

                    isValid = true;
                    if (!buffString.equals(lexemName)) buffString = lexemName;
                }
            }

            if (!isValid & buffString.equals("")) { //проверка корректности строки
                System.out.println("Ошибка!");
                return;
            }

            if (i == length - 1 & buffer.length() == 1) tokens.add(new Token(buffString, buffer)); //условие конечного символа

            if (!isValid) { //проверка конца лексемы в строке
                //System.out.println("проверка связи!");
                System.out.println(buffer);

                buffer.reverse();
                buffer.deleteCharAt(0);
                buffer.reverse();

                //System.out.println(buffer);

                tokens.add(new Token(buffString, buffer));
                buffer.setLength(0);
                i--;
            }

            i++;
        }
    }


    static class Token {
        private final String type;
        private final StringBuffer value;

        public Token(String type, StringBuffer value) {
            this.type = type;
            this.value = value;
            System.out.println(this.value);
        }

        public String toString() {
            return "Token " + this.type + " : " + this.value + " ";
        }
    }
}


