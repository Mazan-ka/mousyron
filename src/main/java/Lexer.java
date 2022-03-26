import java.util.regex.*;
import java.util.*;

public class Lexer {
    static Map<String, Pattern> lexems = new HashMap<>();
    static List<Token> tokens = new LinkedList<>();

    static { //определение регулярных выражений для лексем
        lexems.put("OP", Pattern.compile("^\\+|-|/|\\*$"));
        lexems.put("VAR", Pattern.compile("^[a-z][a-z0-9]*$"));
        lexems.put("WHILE_KEYWORD", Pattern.compile("^while$"));
        lexems.put("FOR_KEYWORD", Pattern.compile("^for$"));
        lexems.put("IF_KEYWORD", Pattern.compile("^if$"));
        lexems.put("ELSE_KEYWORD", Pattern.compile("^else$"));
        lexems.put("ASSIGN_OP", Pattern.compile("^=$"));
        lexems.put("DIGIT", Pattern.compile("^0|([1-9][0-9]*)$"));
        lexems.put("R_BRACKET", Pattern.compile("^\\)$"));
        lexems.put("L_BRACKET", Pattern.compile("^\\($"));
    }

    public static void main(String[] args) {
        String src = "num = ((100+1) - (50-22)) + 90; sum = 100/50;"; //основная строка

        lexer(src); //вызов основного метода лексера

        System.out.println(src);
        System.out.println("Tokens: ");
        for (Token t : tokens) { System.out.println(t); }
    }

    static void lexer(String src) {
        StringBuffer buffer = new StringBuffer("");
        String buffString = "";
        boolean isValid;
        char[] chArray = src.toCharArray();
        int i = 0;
        int length = chArray.length;
        Matcher m;

        while (i < length) { //Проходимся по всем символам в строке src
            if (i == length - 1 & chArray[i] == ';') break; //Условие конца программы

            buffer.append(chArray[i]);

            if (chArray[i] == ' ' || chArray[i] == ';') { //Пропуск пробелов и точек с запятой в строке
                buffer.reverse();
                buffer.deleteCharAt(0);
                buffer.reverse();
                i++;
                continue;
            }

            isValid = false;

            for (String lexemName : lexems.keySet()) {
                Pattern p = lexems.get(lexemName);
                m = p.matcher(buffer);

                if (m.matches()) { //проверка буфера на соответствие регулярным выражениям
                    isValid = true;
                    buffString = lexemName;
                }
            }

            if (!isValid & buffString.equals("")) { //проверка корректности строки
                System.out.println("Ошибка!");
                return;
            }

            if (chArray[i+1] == ';') { //Условие конечной лексемы в строке
                tokens.add(new Token(buffString, buffer));
                buffer.setLength(0);
                i++;
                continue;
            }

            if (!isValid) { //проверка конца лексемы в строке
                buffer.reverse();
                buffer.deleteCharAt(0);
                buffer.reverse();

                tokens.add(new Token(buffString, buffer));
                buffer.setLength(0);
                i--;
            }

            i++;
        }
    }
}


