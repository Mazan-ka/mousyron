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
        iterator = tokens.listIterator(); //присваиваем первому итератору лист с токенами
    }

    public void parser() {
        it = tokens.listIterator(); //присваиваем второму итератору лист с токенами
        while (iterator.hasNext()) {
            try {
                expr(); //вызов основного нетерминала expr
            } catch (ParseException pe) {
                System.out.println(pe.getMessage() + pe.getErrorOffset());
                return;
            }
        }
    }


// _____ПРОВЕРКА_НЕТЕРМИНАЛОВ____________________________________________________

    void expr() throws ParseException {
        if (assign() /*|| while_()*/) {
            StringBuffer buffer = new StringBuffer();
            prev(); //двигаем назад основной итератор, для того чтобы конец expression был верный

            while (true) { //добавление полученного выражения в list
                buffer.append(it.next().getValue());
                it.previous(); //двигаем назад второй итератор, для того чтобы начало следующего expression был верный
                if (it.next().getValue().toString().equals(currenToken.getValue().toString())) { //если два итератора оказались равны, то прекращаем шагать и записываем всё в новый лист
                    next(); //возвращаем на место основной итератор
                    break;
                }
            }
            expressions.add(new Expression(buffer));
        } else {
            throw new ParseException("ASSIGN or WHILE expected, but ", iterator.nextIndex());
        }

//        } else if (while_keyword()) { //нетерминал while
    }

    protected boolean assign() throws ParseException { //основной терминал assign
        if (var()) {
            if (assign_op()) {
                return expr_value();
            } else {
                throw new ParseException("ASSIGN_OP expected, but", iterator.nextIndex());
            }
        } return false;
    }

    protected boolean expr_value() throws ParseException {
        if (value()) {
            if (!iterator.hasNext()) return true;
            else return op_value();
        } else {
            throw new ParseException("VAR or DIGIT expected, but", iterator.nextIndex());
        }
    }

    protected boolean op_value() throws ParseException {
        if (op()) {
            if (value()) {
                if (iterator.hasNext() && op()) {
                    prev();
                    return op_value(); //рекурсивный вызов функции для случаев с большим количеством операций
                } return true;
            } else {
                throw new ParseException("VAR or DIGIT expected, but", iterator.nextIndex());
            }
        } else {
            throw new ParseException("OP expected, but", iterator.nextIndex());
        }
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
        this.currenToken = iterator.next(); //двигаем итератор по листу токенов на один элемент вперед
    }

    protected void prev() {
        this.currenToken = iterator.previous(); //двигаем итератор по листу токенов на один элемент назад
    }

    protected boolean checkToken(String name) {
        next();
        boolean result = this.currenToken.getType().equals(name); //проверяем, равен ли текущий токен тому, что передали в переменную name
        if (!result) { //если токен не совпал, то двигаем итератор назад
            prev();
        }
        return result;
    }
}
