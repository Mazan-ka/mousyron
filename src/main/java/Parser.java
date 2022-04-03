import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Parser {
    static List<Token> tokens;
    static List<Expression> expressions = new LinkedList<>();
    static int i = 0;
    static int start = 0;

    static void parser(List<Token> tokens) {
        Parser.tokens = tokens;
        int length = tokens.size();

        for (i = 0; i < length; i++) {
//            System.out.println(i);
//            System.out.println(start);
            try {
                start = i;
                if (assign(i)) { //проверка на первый основной терминал
                    if (i == start) { //случай вида m = 100;
                        i += 3;
                        StringBuffer buffer = new StringBuffer();
                        for (int j = start; j <= 2; j++) {
                            buffer.append(tokens.get(j).getValue());
                        }
                        expressions.add(new Expression(buffer));
                    } else { //случай m = 100 (+1)*
                        StringBuffer buffer = new StringBuffer();
                        for (int j = start; j <= i; j++) {
                            buffer.append(tokens.get(j).getValue());
                        }
                        expressions.add(new Expression(buffer)); //добавление полученного выражения в list
                    }
                }
            } catch (SyntaxException1 e) {
                System.out.println("\nСинтаксическая ошибка!");
                System.out.println("-- " + e.getExplanation());
            }
        }
    }


// _____ПРОВЕРКА ТЕРМИНАЛОВ____________________________________________________

    static boolean assign(int i) throws SyntaxException1 { //основной терминал
        if (i + 2 > tokens.size()-1) throw new SyntaxException1("Не хватает лексем/терминалов - " + i);
        else return var(i) & assign_op(i+1) & expr_value(i+2);
    }

//    static boolean while_key() throws SyntaxException { //основной терминал
//        return
//    }

    static boolean expr_value(int i) throws SyntaxException1 {
        if (value(i)) {
            try {
                i++;
                op_value(i);
            } catch (SyntaxException2 ignored) {}
        }
        return true;
    }

    //пока не обрабатывается ошибка в коде вида: sum = 100 +;
    static void op_value(int i) throws SyntaxException1, SyntaxException2 {
        //value(i + 1); /*throw new SyntaxException1("Не хватает лексем/терминалов - " + i);*/
        try {
            if (i + 1 == tokens.size() - 1 && op(i) && value(i + 1)) { //случай последнего op_value и конца токенов
                Parser.i = i + 1;
                return;
            }

            if (i + 1 < tokens.size() - 1 && op(i) && value(i + 1)) { //случай продолжения токенов
                Parser.i = i + 2;
                op_value(Parser.i);
            }
        } catch (SyntaxException1 e) { //обработка положительного исключения
            Parser.i--;
            throw new SyntaxException2("");
        }
    }

    static boolean value(int i) throws SyntaxException1 {
        try {
            var(i);
        }
        catch (SyntaxException1 e0) {
            digit(i);
        }
        return true;
    }


//  _____ПРОВЕРКА ЛЕКСЕМ________________________________________________________

    static boolean var(int i) throws SyntaxException1 {
        Pattern p = Lexer.lexems.get("VAR");
        Matcher m = p.matcher(tokens.get(i).getValue()); //создание matcher с нужной регуляркой и загрузка в него токена

        //создание исключения в случае несовпадения лексем или возврат true
        if (!m.matches()) throw new SyntaxException1("Error: token number - " + i);
        else return true;
    }

    static boolean assign_op(int i) throws SyntaxException1 {
        Pattern p = Lexer.lexems.get("ASSIGN_OP");
        Matcher m = p.matcher(tokens.get(i).getValue());

        if (!m.matches()) throw new SyntaxException1("Error: token number - " + i);
        else return true;
    }

    static void digit(int i) throws SyntaxException1 {
        Pattern p = Lexer.lexems.get("DIGIT");
        Matcher m = p.matcher(tokens.get(i).getValue());

        if (!m.matches()) throw new SyntaxException1("Error: token number - " + i);
    }

    static boolean op(int i) throws SyntaxException1 {
        Pattern p = Lexer.lexems.get("OP");
        Matcher m = p.matcher(tokens.get(i).getValue());

        if (!m.matches()) throw new SyntaxException1("Error: token number - " + i);
        else return true;
    }
}
