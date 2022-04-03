import java.util.regex.*;
import java.util.*;
import java.io.File;
import java.io.IOException;
import java.io.FileReader;
import java.io.BufferedReader;

public class Lexer {
    static Map<String, Pattern> lexems = new HashMap<>();
    static Map<String, Pattern> var_lexems = new HashMap<>();
    static List<Token> tokens = new LinkedList<>();

    static { //определение регулярных выражений для лексем
        lexems.put("OP", Pattern.compile("^\\+|-|/|\\*$"));
        lexems.put("OP_BOOL", Pattern.compile("^>|<|==|!|!=$"));
        lexems.put("ASSIGN_OP", Pattern.compile("^=$"));
        lexems.put("DIGIT", Pattern.compile("^0|([1-9][0-9]*)$"));

        lexems.put("R_BRACKET", Pattern.compile("^\\)$"));
        lexems.put("L_BRACKET", Pattern.compile("^\\($"));
        lexems.put("START_BODY", Pattern.compile("^\\{$"));
        lexems.put("FINISH_BODY", Pattern.compile("^}$"));
        lexems.put("COMMENTS", Pattern.compile("^#$"));

        lexems.put("VAR", Pattern.compile("^[a-z][a-z0-9]*$"));

        var_lexems.put("WHILE_KEYWORD", Pattern.compile("^while$"));
        var_lexems.put("FOR_KEYWORD", Pattern.compile("^for$"));
        var_lexems.put("IF_KEYWORD", Pattern.compile("^if$"));
        var_lexems.put("ELSE_KEYWORD", Pattern.compile("^else$"));
        var_lexems.put("BOOL_KEYWORD", Pattern.compile("^true|false$"));
    }

    //главная main функция
    public static void main(String[] args) {
        //String src = "num = ((100+1) - (50-22)) + 90; sum = 100/50;"; //основная строка
        try {
            lexer(Reader()); //вызов основного метода лексера

            System.out.println(Reader());
            System.out.println("Tokens: ");
            for (Token t : tokens) { System.out.println(t); }
        } catch (GrammarException e) {
            System.out.println("Грамматическая ошибка!");
        }

        //вызов парсера (анализ синтаксиса программы)
        Parser.parser(tokens);
        System.out.print("\n");
        for (Expression s: Parser.expressions) {
            System.out.println(s);
        }
    }

    static String Reader() throws GrammarException {
        String src = "";
        try {
            File file = new File("write_your_code_here.txt"); //создание объекта с файлом

            if (!file.exists()) file.createNewFile(); //создание файла, если его нет

            BufferedReader bf = new BufferedReader(new FileReader(file)); //открытие потока BufferReader, с помощью которого будет считываться файл
            String buffer = "";

            while ((buffer = bf.readLine()) != null) { //считывание одной строки с файла и занесение её в буфер
                src = src.concat(buffer); //склеивание основной строки с буфером
            }

            bf.close();
        } catch (IOException e) { //ловля исключений на открытие и закрытие потока
            System.out.println("File creating or opening error! " + e);
        }

        if (src.equals(";") || src.equals("") || src.equals(" ")) { //проверка корректности строки
            throw new GrammarException(); //создание исключения об ошибке синтаксиса
        }

        return src;
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

            if (chArray[i] == ' ' || chArray[i] == ';') { //Пропуск пробелов или точек с запятой в строке
                buffer.reverse();
                buffer.deleteCharAt(0);
                buffer.reverse();
                i++;
                continue;
            }

            isValid = false;

            for (String lexemName : lexems.keySet()) {
                Pattern p = lexems.get(lexemName);
                Pattern p_sec = lexems.get("VAR"); //специальный паттерн для var лексем
                m = p_sec.matcher(buffer);

                if (m.matches()) { //проверка для отделения ключевых слов от названий переменных
                    boolean isValid_var = false;

                    for (String lexemName_var : var_lexems.keySet()) {
                        Pattern p_var = var_lexems.get(lexemName_var);
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

                if (buffString.equals("COMMENTS")) { //условие для нахождения комментария в коде
                    i--;
                    while (chArray[i] != ';') i++;

                    buffer.setLength(0);
                    continue;
                }

                tokens.add(new Token(buffString, buffer));
                buffer.setLength(0);
                continue;
            }

            if (chArray[i+1] == ';') { //условие конечной лексемы в строке
                tokens.add(new Token(buffString, buffer));
                buffer.setLength(0);
                i++;
                continue;
            }

            i++;
        }
    }
}

class SyntaxException1 extends Exception{
    private final String Explanation;
    public SyntaxException1 (String Explanation) {
        this.Explanation = Explanation;
    }

    public String getExplanation() {
        return Explanation;
    }
}

class SyntaxException2 extends Exception{
    private final String Explanation;
    public SyntaxException2 (String Explanation) {
        this.Explanation = Explanation;
    }

    public String getExplanation() {
        return Explanation;
    }
}

class GrammarException extends Exception{}


