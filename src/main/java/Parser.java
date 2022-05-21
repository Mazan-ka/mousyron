import java.text.ParseException;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;


public class Parser {
    private final List<Token> TOKEN;
    private final ListIterator<Token> ITERATOR;
    private Token currenToken;
    private ListIterator<Token> it;
    static List<Expression> expressions = new LinkedList<>();


    public Parser(List<Token> tokens) { // конструктор парсера, принимающий лист с токенами от лексера
        this.TOKEN = tokens;
        ITERATOR = tokens.listIterator(); //присваиваем первому итератору лист с токенами
    }

    public void parser() {
        it = TOKEN.listIterator(); //присваиваем второму итератору лист с токенами
        while (ITERATOR.hasNext()) {
            try {
                expr(false); //вызов основного нетерминала expr
            } catch (ParseException pe) {
                System.out.println(pe.getMessage() + pe.getErrorOffset());
                return;
            }
        }
    }


// _____ПРОВЕРКА_НЕТЕРМИНАЛОВ____________________________________________________

    protected void expr(boolean isCalled) throws ParseException {
        if (assign() || isWhile() || isIf()) {
            if (isCalled) return;
            StringBuffer buffer = new StringBuffer();

            while (true) { //добавление полученного выражения в list
                buffer.append(it.next().getValue());
                //Записываем токены в буфер пока вспомогательный итератор (it) не сравняется с основным (iterator), т.е. достигнет конца одного из expression
                if (it.nextIndex() == ITERATOR.nextIndex()) {
                    break;
                }
            }

            expressions.add(new Expression(buffer));
        } else {
            throw new ParseException("ASSIGN or WHILE or IF expected, but ", ITERATOR.nextIndex());
        }
    }

    protected boolean assign() throws ParseException { //основной нетерминал assign
        if (var()) {
            if (assignOp()) {
                return exprValue();
            } else {
                throw new ParseException("ASSIGN_OP expected, but", ITERATOR.nextIndex());
            }
        } else return false;
    }

    protected boolean isWhile() throws ParseException { //основной нетерминал while
        if (whileKeyword()) {
            if (conditionWhile()) {
                return bodyWhile();
            } else {
                throw new ParseException("CONDITION expected, but", ITERATOR.nextIndex());
            }
        } else return false;
    }

    protected boolean infParenthesisVal() throws ParseException { //нетерминал для случаев типа (...) + 100
        if (leftBracket()) {
            if (exprValue() && rightBracket()) {
                while (ITERATOR.hasNext() && op()) exprValue(); //(OP expr_value)*
                return true;
            }
        }
        return false;
    }

    protected boolean valInfParenthesis() throws ParseException { //нетерминал для случаев типа 100 + (...)
        boolean hasChain = true;
        if (value()) {
            while (ITERATOR.hasNext() && op())
                hasChain = (value() || infParenthesisVal()); //(OP (value | inf_Parenthesis_val) )*
            return hasChain;
        } else return false;
    }

    protected boolean exprValue() throws ParseException { //нетерминал для всех выражений в assign
        if (infParenthesisVal() || valInfParenthesis()) {
            return true;
        } else {
            throw new ParseException("VAR or DIGIT expected, but", ITERATOR.nextIndex());
        }
    }

    protected boolean isIf() throws ParseException {
        if (ifKeyword()) {
            if (leftBracket() && compare() && rightBracket()) {
                if (startWhile()) {
                    expr(true);
                    while (finishWhile()) { //ожидание конца тела условия if, считывание множества терминалов до '}'
                        expr(true);
                    }

                    if (ITERATOR.hasNext() && elseKeyword()) {
                        if (startWhile()) {
                            expr(true);
                            while (finishWhile()) { //ожидание конца тела условия else, считывание множества терминалов до '}'
                                expr(true);
                            }
                            return true;
                        }
                        throw new ParseException("START_WHILE expected, but", ITERATOR.nextIndex());
                    } else return true;
                }
            }
            throw new ParseException("IF or BRACKET or COMPARE expected, but", ITERATOR.nextIndex());
        } else return false;
    }

    protected boolean value() {
        return var() || digit();
    }

    protected boolean conditionWhile() { //нетерминал тела условия цикла while
        return leftBracket() && compare() && rightBracket();
    }

    protected boolean compare() { //нетерминал условия цикла while
        return value() && opBool() && value();
    }

    protected boolean bodyWhile() throws ParseException { //нетерминал тела цикла while
        if (startWhile()) {
            expr(true);
            while (finishWhile()/*!iterator.next().getType().equals("FINISH_BODY")*/) { //ожидание конца тела цикла while, считывание множества терминалов до '}'
                expr(true);
            }
            return true;
        } else {
            throw new ParseException("START WHILE expected, but", ITERATOR.nextIndex());
        }
    }


//  _____ПРОВЕРКА_ЛЕКСЕМ/ТЕРМИНАЛОВ________________________________________________________

    protected boolean var() {
        return checkToken("VAR");
    }

    protected boolean assignOp() {
        return checkToken("ASSIGN_OP");
    }

    protected boolean digit() {
        return checkToken("DIGIT");
    }

    protected boolean op() {
        return checkToken("OP");
    }

    protected boolean whileKeyword() {
        return checkToken("WHILE_KEYWORD");
    }

    protected boolean leftBracket() {
        return checkToken("L_BRACKET");
    }

    protected boolean rightBracket() {
        return checkToken("R_BRACKET");
    }

    protected boolean opBool() {
        return checkToken("OP_BOOL");
    }

    protected boolean startWhile() {
        return checkToken("START_BODY");
    }

    protected boolean finishWhile() {
        return !checkToken("FINISH_BODY");
    }

    protected boolean ifKeyword() {
        return checkToken("IF_KEYWORD");
    }

    protected boolean elseKeyword() {
        return checkToken("ELSE_KEYWORD");
    }

    protected void next() {
        currenToken = ITERATOR.next(); //двигаем итератор по листу токенов на один элемент вперед
    }

    protected void prev() {
        currenToken = ITERATOR.previous(); //двигаем итератор по листу токенов на один элемент назад
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
