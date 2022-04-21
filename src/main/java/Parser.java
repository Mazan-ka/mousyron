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
            try {
                expr();
            } catch (SyntaxException1 e) {
                System.out.println("\nСинтаксическая ошибка!");
                System.out.println("-- " + e.getExplanation());
            }
            i--;
        }
    }


// _____ПРОВЕРКА_НЕТЕРМИНАЛОВ____________________________________________________

    static void expr() throws SyntaxException1 {
        start = i;
        if (assign()) { //проверка на терминал присваивания
            StringBuffer buffer = new StringBuffer(); //добавление полученного выражения в list

            for (int j = start; j <= i - 1; j++) {
                buffer.append(tokens.get(j).getValue());
            }
            expressions.add(new Expression(buffer));
        }

//        if (while_keyword(i) && condition_while(i + 1) && body_while(i)) { //проверка на while терминал
//
//        }
    }

    static boolean assign() throws SyntaxException1 { //основной терминал
        if (i + 2 > tokens.size()-1) throw new SyntaxException1("Не хватает лексем/терминалов - " + i);
        else return var(i) & assign_op(i) & expr_value();
    }

    static boolean expr_value() throws SyntaxException1 {
        if (i == tokens.size() - 1 && value()) return true; //случай последнего op_value и конца токенов типа a=100

        int temp = i;
        if (value() && op_v2(i)) { //случай конца нетерминала assign, но не конца токенов типа a=100
            return true;
        } else if (temp != i) i = temp;

        if (value()) {
            try {
                op_value();
            } catch (SyntaxException2 ignored) {}
        }
        return true;
    }

    static void op_value() throws SyntaxException1, SyntaxException2 {
        //value(i + 1); /*throw new SyntaxException1("Не хватает лексем/терминалов - " + i);*/

        //проверка ошибки в коде вида: sum = 100 +;
        int temp = i;
        if (i == tokens.size() - 1 && op(i))  throw new SyntaxException1("Не хватает лексем/терминалов - " + i);
        else if (temp != i) i--;

        try {
            if (i + 1 == tokens.size() - 1 && op(i) && value()) { //случай последнего op_value и конца токенов
                return;
            }

            temp = i;
            if (op(i) && value() && op_v2(i)) { //случай конца нетерминала assign, но не конца токенов
                return;
            } else if (temp != i) i = temp;

            if (i + 1 < tokens.size() - 1 && op(i) && value()) { //случай продолжения токенов
                op_value();
            }
        } catch (SyntaxException1 e) { //обработка положительного исключения
            Parser.i--;
            throw new SyntaxException2("");
        }
    }

    static boolean value() throws SyntaxException1 {
        try {
            var(i);
        }
        catch (SyntaxException1 e0) {
            digit(i);
        }
        return true;
    }

    static boolean condition_while(int i) throws SyntaxException1 {
        if ((left_bracket(i) && compare(i + 1) && right_bracket(i + 4))) {
            return true;
        } else return left_bracket(i) && var_bool(i + 1) && right_bracket(i + 2);
    }

    static boolean compare(int i) throws SyntaxException1 {
        return (value() && op_bool(i + 1) && value());
    }

    static boolean body_while(int i) throws SyntaxException1 {
        if (start_while(i)) {
            expr();
            return finish_while(Parser.i);
        }
        return false;
    }


//  _____ПРОВЕРКА_ЛЕКСЕМ/ТЕРМИНАЛОВ________________________________________________________

    static boolean var(int i) throws SyntaxException1 {
        Pattern p = Lexer.lexems.get("VAR");
        Matcher m = p.matcher(tokens.get(i).getValue()); //создание matcher с нужной регуляркой и загрузка в него токена

        //создание исключения в случае несовпадения лексем или возврат true
        if (!m.matches()) throw new SyntaxException1("Error: token number - " + i);
        else {
            Parser.i++;
            return true;
        }
    }

    static boolean assign_op(int i) throws SyntaxException1 {
        Pattern p = Lexer.lexems.get("ASSIGN_OP");
        Matcher m = p.matcher(tokens.get(i).getValue());

        if (!m.matches()) throw new SyntaxException1("Error: token number - " + i);
        else {
            Parser.i++;
            return true;
        }
    }

    static void digit(int i) throws SyntaxException1 {
        Pattern p = Lexer.lexems.get("DIGIT");
        Matcher m = p.matcher(tokens.get(i).getValue());

        if (!m.matches()) throw new SyntaxException1("Error: token number - " + i);
        else Parser.i++;
    }

    static boolean op(int i) throws SyntaxException1 {
        Pattern p = Lexer.lexems.get("OP");
        Matcher m = p.matcher(tokens.get(i).getValue());

        if (!m.matches()) throw new SyntaxException1("Error: token number - " + i);
        else {
            Parser.i++;
            return true;
        }
    }

    static boolean op_v2(int i) throws SyntaxException1 { //терминал для проверки конца assign токена его окончание
        Pattern p = Lexer.lexems.get("OP");
        Matcher m = p.matcher(tokens.get(i).getValue());

        if (!m.matches()) return true;
        else {
            Parser.i++;
            return false;
        }
    }

    static boolean while_keyword(int i) throws SyntaxException1 {
        Pattern p = Lexer.var_lexems.get("WHILE_KEYWORD");
        Matcher m = p.matcher(tokens.get(i).getValue());

        if (!m.matches()) throw new SyntaxException1("Error: token number - " + i);
        else {
            Parser.i++;
            return true;
        }
    }

    static boolean left_bracket(int i) throws SyntaxException1 {
        Pattern p = Lexer.lexems.get("L_BRACKET");
        Matcher m = p.matcher(tokens.get(i).getValue());

        if (!m.matches()) throw new SyntaxException1("Error: token number - " + i);
        else {
            Parser.i++;
            return true;
        }
    }

    static boolean right_bracket(int i) throws SyntaxException1 {
        Pattern p = Lexer.lexems.get("R_BRACKET");
        Matcher m = p.matcher(tokens.get(i).getValue());

        if (!m.matches()) throw new SyntaxException1("Error: token number - " + i);
        else {
            Parser.i++;
            return true;
        }
    }

    static boolean op_bool(int i) throws SyntaxException1 {
        Pattern p = Lexer.lexems.get("OP_BOOL");
        Matcher m = p.matcher(tokens.get(i).getValue());

        if (!m.matches()) throw new SyntaxException1("Error: token number - " + i);
        else {
            Parser.i++;
            return true;
        }
    }

    static boolean var_bool(int i) throws SyntaxException1 {
        Pattern p = Lexer.var_lexems.get("BOOL_KEYWORD");
        Matcher m = p.matcher(tokens.get(i).getValue());

        if (!m.matches()) throw new SyntaxException1("Error: token number - " + i);
        else {
            Parser.i++;
            return true;
        }
    }

    static boolean start_while(int i) throws SyntaxException1 {
        Pattern p = Lexer.var_lexems.get("START_BODY");
        Matcher m = p.matcher(tokens.get(i).getValue());

        if (!m.matches()) throw new SyntaxException1("Error: token number - " + i);
        else {
            Parser.i++;
            return true;
        }
    }

    static boolean finish_while(int i) throws SyntaxException1 {
        Pattern p = Lexer.var_lexems.get("FINISH_BODY");
        Matcher m = p.matcher(tokens.get(i).getValue());

        if (!m.matches()) throw new SyntaxException1("Error: token number - " + i);
        else {
            Parser.i++;
            return true;
        }
    }
}
