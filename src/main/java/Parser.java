import java.text.ParseException;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;


public class Parser {
    private final List<Token> tokens;
    private Token currenToken;
    ListIterator<Token> it;
    private final ListIterator<Token> iterator;
    static List<Expression> expressions = new LinkedList<>();


    public Parser(List<Token> tokens) { // конструктор парсера, принимающий лист с токенами от лексера
        this.tokens = tokens;
        iterator = tokens.listIterator();
    }

    public void parser() {
        it = tokens.listIterator();
        while (iterator.hasNext()) {
            expr();
        }

        /*int temp;
        while(i <= length - 1) {
            temp = i;
            try {
                if (expr())
            } catch (SyntaxException1 e) {
                System.out.println("\nСинтаксическая ошибка!");
                System.out.println("-- " + e.getExplanation());
                if (i == 0 || i == temp) return;
            }
        }*/
    }


// _____ПРОВЕРКА_НЕТЕРМИНАЛОВ____________________________________________________

    void expr() {
        try {
            if (assign() /*|| while_()*/) {
                StringBuffer buffer = new StringBuffer();
                prev();

                while (true) { //добавление полученного выражения в list
                    buffer.append(it.next().getValue());
                    it.previous();
                    if (it.next().getValue().toString().equals(currenToken.getValue().toString())) {
                        next();
                        break;
                    }
                }
                expressions.add(new Expression(buffer));
            } else {
                throw new ParseException("ASSIGN or WHILE expected, but: ", tokens.listIterator().nextIndex());
            }
        } catch (ParseException pe) {
            System.out.println(pe.getMessage() + ", " + pe.getErrorOffset());
        }


//        } else if (while_keyword()) { //нетерминал while


        /*start = i;
        if (assign()) { //проверка на терминал присваивания
            StringBuffer buffer = new StringBuffer(); //добавление полученного выражения в list

            for (int j = start; j <= i - 1; j++) {
                buffer.append(tokens.get(j).getValue());
            }
            expressions.add(new Expression(buffer));
            return true;
        }

        if (while_keyword(i) && condition_while() && body_while()) { //проверка на while терминал
            StringBuffer buffer = new StringBuffer(); //добавление полученного выражения в list

            for (int j = start; j <= i - 1; j++) {
                buffer.append(tokens.get(j).getValue());
            }
            expressions.add(new Expression(buffer));
            return true;
        }
        return false;*/
    }

    protected boolean assign() throws ParseException { //основной терминал assign
        if (var()) {
            if (assign_op()) {
                return expr_value();
            } else {
                throw new ParseException("ASSIGN_OP expected, but", tokens.listIterator().nextIndex());
            }
        } return false;
    }

    protected boolean expr_value() throws ParseException {
        if (value()) {
            if (!tokens.listIterator().hasNext()) return true;
            else return op_value();
        } else {
            throw new ParseException("VAR or DIGIT expected, but", tokens.listIterator().nextIndex());
        }

        /*if (i == tokens.size() - 1 && value()) return true; //случай последнего op_value и конца токенов типа a=100
        int temp = i;
        if (value() && op_v2(i)) { //случай конца нетерминала assign, но не конца токенов типа a=100
            return true;
        } else if (temp != i) i = temp;
        if (value()) {
            try {
                op_value();
            } catch (SyntaxException2 ignored) {}
        }
        return true;*/
    }

    protected boolean op_value() throws ParseException {
        if (op()) {
            if (value()) {
                if (iterator.hasNext() && op()) {
                    prev();
                    return op_value();
                } return true;
            } else {
                throw new ParseException("VAR or DIGIT expected, but", tokens.listIterator().nextIndex());
            }
        } else {
            throw new ParseException("OP expected, but", tokens.listIterator().nextIndex());
        }

        //value(i + 1); throw new SyntaxException1("Не хватает лексем/терминалов - " + i);

        //проверка ошибки в коде вида: sum = 100 +;
        /*int temp = i;
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
        }*/
    }

    protected boolean value() {
        return var() || digit();
    }

    /*boolean condition_while() throws SyntaxException1 {
        int temp = i; //создание временно буфера для возвращения индекса к начальному значению в случае неуспеха
        if ((left_bracket(i) && compare() && right_bracket(i))) {
            return true;
        } else {
            if (temp != i) i = temp;
            if (left_bracket(i) && var_bool(i) && right_bracket(i)) {
                return true;
            } else {
                if (temp != i) i = temp;
                return false;
            }
        }
    }

    boolean compare() throws SyntaxException1 {
        int temp = i;
        if (value() && op_bool(i) && value()) {
            return true;
        } else {
            if (temp != i) i = temp;
            return false;
        }
    }

    boolean body_while() throws SyntaxException1 {
        if (start_while(i)) {
            expr();
            return finish_while(i);
        }
        return false;
    }*/


//  _____ПРОВЕРКА_ЛЕКСЕМ/ТЕРМИНАЛОВ________________________________________________________

    // старый метод для терминалов
/*    static boolean var(int i) throws SyntaxException1 {
        Pattern p = Lexer.lexems.get("VAR");
        Matcher m = p.matcher(tokens.get(i).getValue()); //создание matcher с нужной регуляркой и загрузка в него токена

        //создание исключения в случае несовпадения лексем или возврат true
        if (!m.matches()) throw new SyntaxException1("Error: token number - " + i);
        else {
            Parser.i++;
            return true;
        }
    }*/

    protected boolean var() {
        return checkToken("VAR");
    }

    protected boolean assign_op() {
       return checkToken("ASSIGN_OP");
    }

    protected boolean digit() {
        return checkToken("DIGIT");
    }

    protected boolean op() {
        return checkToken("OP");
    }

    protected boolean while_keyword() {
        return checkToken("WHILE");
    }

    protected boolean left_bracket() {
        return checkToken("L_BRACKET");
    }

    protected boolean right_bracket() {
        return checkToken("R_BRACKET");
    }

    protected boolean op_bool() {
        return checkToken("OP_BOOL");
    }

    protected boolean var_bool(int i) {
        return checkToken("BOOL_KEYWORD");
    }

    protected boolean start_while() {
        return checkToken("START_BODY");
    }

    protected boolean finish_while() {
        return checkToken("FINISH_BODY");
    }

    protected void next() {
        this.currenToken = iterator.next();
    }

    protected void prev() {
        this.currenToken = iterator.previous();
    }

    protected boolean checkToken(String name) {
        next();
        boolean result = this.currenToken.getType().equals(name);
        if (!result) {
            prev();
        }
        return result;
    }
}
