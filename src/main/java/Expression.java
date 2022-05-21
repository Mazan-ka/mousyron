import java.util.*;

public class Expression {
    private final List<Token> EXPRESSION = new LinkedList<>();

    public void addToken (Token t) {
        EXPRESSION.add(t);
    }

    public Token takeToken(int index) {
        return EXPRESSION.get(index);
    }

    @Override
    public String toString() {
        StringBuilder expr = new StringBuilder();
        for (Token token : EXPRESSION) {
            expr.append(token.getValue());
        }
        return "Expression: " + expr;
    }
}
