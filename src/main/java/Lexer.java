import java.util.regex.*;
import java.util.*;
import java.io.File;
import java.io.IOException;
import java.io.FileReader;
import java.io.BufferedReader;

public class Lexer {
    private final static Map<String, Pattern> LEXEMS = new HashMap<>();
    private final static Map<String, Pattern> VAR_LEXEMS = new HashMap<>();
    private final static List<Token> tokens = new LinkedList<>();

    static { //определение регулярных выражений для лексем
        LEXEMS.put("OP", Pattern.compile("^\\+|-|/|\\*$"));
        LEXEMS.put("OP_BOOL", Pattern.compile("^>|<|==|!|!=$"));
        LEXEMS.put("ASSIGN_OP", Pattern.compile("^=$"));
        LEXEMS.put("DIGIT", Pattern.compile("^0|([1-9][0-9]*)$"));

        LEXEMS.put("R_BRACKET", Pattern.compile("^\\)$"));
        LEXEMS.put("L_BRACKET", Pattern.compile("^\\($"));
        LEXEMS.put("START_BODY", Pattern.compile("^\\{$"));
        LEXEMS.put("FINISH_BODY", Pattern.compile("^}$"));
        LEXEMS.put("LIST_ASSIGN", Pattern.compile("^->$"));
        LEXEMS.put("LIST_SIGN", Pattern.compile("^\\[]?$"));

        LEXEMS.put("VAR", Pattern.compile("^[a-zA-Z][a-zA-Z0-9@]*$"));

        VAR_LEXEMS.put("WHILE_KEYWORD", Pattern.compile("^while$"));
        VAR_LEXEMS.put("FOR_KEYWORD", Pattern.compile("^for$"));
        VAR_LEXEMS.put("IF_KEYWORD", Pattern.compile("^if$"));
        VAR_LEXEMS.put("ELSE_KEYWORD", Pattern.compile("^else$"));
        VAR_LEXEMS.put("BOOL_KEYWORD", Pattern.compile("^true|false$"));
        VAR_LEXEMS.put("LINKED_LIST_DEF", Pattern.compile("^GurLinkedList@$"));
        //VAR_LEXEMS.put("LINKED_LIST_INIT", Pattern.compile("^newGurLinkedList\\(\\)$"));
    }

    //главная main функция
    public static void main(String[] args) {
        try {
            Lexer l = new Lexer();
            l.lexer(l.Reader()); //вызов основного метода лексера

            System.out.println(l.Reader());
            System.out.println("Tokens: ");
            for (Token t : tokens) {
                System.out.println(t);
            }
        } catch (GrammarException e) {
            System.out.println("Грамматическая ошибка!");
        }

        //вызов парсера (анализ синтаксиса программы)
        Parser p = new Parser(tokens);
        p.parser();
        System.out.print("\n");
        for (Expression s : Parser.expressions) {
            System.out.println(s);
        }

        //вызов интерпретатора
        Interpreter in = new Interpreter(Parser.expressions);
        in.interpreter();

        System.out.println("\n" + Interpreter.variables);

        /*GurLinkedList<Integer> list = new GurLinkedList<>();
        GurLinkedList<String> list2 = new GurLinkedList<>();
        list.addFirst(1);
        list.addLast(2);
        list.addLast(3);
        list.addFirst(5);
        list.addByIndex(0, 6);
        list2.addFirst("A");
        list2.addByIndex(0,"!");

        System.out.println("\nLinkedList\n");
        for (int i = 0; i < 4; i++) {
            System.out.println(list.getByIndex(i));
        }
        System.out.println(list2.getByIndex(0) + list2.getByIndex(1));*/
    }

    protected String Reader() throws GrammarException {
        String src = "";
        try {
            File file = new File("write_your_code_here.txt"); //создание объекта с файлом

            BufferedReader bf = new BufferedReader(new FileReader(file)); //открытие потока BufferReader, с помощью которого будет считываться файл
            String buffer;

            while ((buffer = bf.readLine()) != null) { //считывание одной строки с файла и занесение её в буфер
                if (buffer.isEmpty()) continue; //проверка строки на пустоту, к примеру просто перенос строки
                if (buffer.trim().charAt(0) == '/' && buffer.trim().charAt(1) == '/') continue; //пропускаем строку, так как она является комментарием
                src = src.concat(buffer); //склеивание основной строки с буфером
            }

            bf.close();
        } catch (IOException e) { //ловля исключений на открытие и закрытие потока
            System.out.println("File creating or opening error! " + e);
        }

        return src;
    }

    protected void lexer(String src) {
        StringBuffer buffer = new StringBuffer();
        String buffString = "";
        boolean isValid;
        int i = 0;
        int length = src.length();
        Matcher m;

        while (i < length) { //Проходимся по всем символам в строке src
            if (i == length - 1 && src.charAt(i) == ';' || src.charAt(i) == '\t') break; //Условие конца программы

            buffer.append(src.charAt(i));

            if (src.charAt(i) == ' ' || src.charAt(i) == ';') { //Пропуск пробелов или точек с запятой в строке
                buffer.reverse();
                buffer.deleteCharAt(0);
                buffer.reverse();
                i++;
                continue;
            }

            isValid = false;

            for (String lexemName : LEXEMS.keySet()) {
                Pattern p = LEXEMS.get(lexemName);
                Pattern p_sec = LEXEMS.get("VAR"); //специальный паттерн для var лексем
                m = p_sec.matcher(buffer);

                if (m.matches()) { //проверка для отделения ключевых слов от названий переменных
                    boolean isValid_var = false;

                    for (String lexemName_var : VAR_LEXEMS.keySet()) {
                        Pattern p_var = VAR_LEXEMS.get(lexemName_var);
                        m = p_var.matcher(buffer);

                        if (m.matches()) { //проверка буфера на соответствие регулярным выражениям
                            isValid = true;
                            isValid_var = true;
                            buffString = lexemName_var;
                        }
                    }

                    if (!isValid_var) {
                        isValid = true;
                        buffString = "VAR";
                    }

                    break;
                }

                m = p.matcher(buffer);

                if (m.matches()) { //проверка буфера на соответствие регулярным выражениям
                    isValid = true;
                    buffString = lexemName;
                }
            }

            if (!isValid) { //проверка конца лексемы в строке
                buffer.reverse();
                buffer.deleteCharAt(0);
                buffer.reverse();

                tokens.add(new Token(buffString, buffer));
                buffer.setLength(0);
                continue;
            }

            if (src.charAt(i + 1) == ';') { //условие конечной лексемы в строке
                tokens.add(new Token(buffString, buffer));
                buffer.setLength(0);
                i++;
                continue;
            }
            i++;
        }
    }
}

class GrammarException extends Exception {
}


