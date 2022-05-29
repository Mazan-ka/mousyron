import java.util.*;

public class Interpreter {
    private final List<Expression> expressionLinkedList;
    public static Map<String, Double> variables = new HashMap<>();


    public Interpreter(List<Expression> expr) {
        this.expressionLinkedList = expr;
    }

    public void interpreter() {
        for (Expression expr : expressionLinkedList)
            rpnToAnswer(expressionToRPN(expr));
    }


    private LinkedList<Token> expressionToRPN(Expression expr) {
        LinkedList<Token> current = new LinkedList<>();
        Stack<Token> stack = new Stack<>();

        int priority;
        for (int i = 0; i < expr.getSize(); i++) {
            priority = getPriority(expr.takeToken(i));
            if (priority == 0) {
                current.add(expr.takeToken(i));
                if (expr.takeToken(i).getType().equals("VAR")) {
                    variables.putIfAbsent(expr.takeToken(i).getValue().toString(), 0.0);
                }
            }
            if (priority == 1) stack.push(expr.takeToken(i));
            if (priority > 1) {
                while (!stack.empty()) {
                    if (getPriority(stack.peek()) >= priority) current.add(stack.pop());
                    else break;
                }
                stack.push(expr.takeToken(i));

            }
            if (priority == -1) {
                while (getPriority(stack.peek()) != 1) current.add(stack.pop());
                stack.pop();
            }
        }
        while (!stack.empty()) current.add(stack.pop());

        /*for (Token i : current) {
            System.out.print(i.getValue() + " ");
        }
        System.out.println();*/
        return current;
    }

    public void rpnToAnswer(LinkedList<Token> rpn) {
        Stack<Token> stack = new Stack<>();

        for (Token i : rpn) {
            if (getPriority(i) == 0) {
                stack.push(i);
            }
            if (getPriority(i) > 1) {
                Token a = stack.pop(), b = stack.pop();
                double dur1, dur2;

                if (i.getValue().toString().charAt(0) == '+') {
                    if (a.getType().equals("VAR")) dur1 = variables.get(a.getValue().toString());
                    else dur1 = Double.parseDouble(a.getValue().toString());

                    if (b.getType().equals("VAR")) dur2 = variables.get(b.getValue().toString());
                    else dur2 = Double.parseDouble(b.getValue().toString());

                    stack.push(new Token("DIGIT", new StringBuffer(String.valueOf(dur2 + dur1))));
                }
                if (i.getValue().toString().charAt(0) == '-') {
                    if (a.getType().equals("VAR")) dur1 = variables.get(a.getValue().toString());
                    else dur1 = Double.parseDouble(a.getValue().toString());

                    if (b.getType().equals("VAR")) dur2 = variables.get(b.getValue().toString());
                    else dur2 = Double.parseDouble(b.getValue().toString());

                    stack.push(new Token("DIGIT", new StringBuffer(String.valueOf(dur2 - dur1))));
                }
                if (i.getValue().toString().charAt(0) == '/') {
                    if (a.getType().equals("VAR")) dur1 = variables.get(a.getValue().toString());
                    else dur1 = Double.parseDouble(a.getValue().toString());

                    if (b.getType().equals("VAR")) dur2 = variables.get(b.getValue().toString());
                    else dur2 = Double.parseDouble(b.getValue().toString());

                    stack.push(new Token("DIGIT", new StringBuffer(String.valueOf(dur2 / dur1))));
                }
                if (i.getValue().toString().charAt(0) == '*') {
                    if (a.getType().equals("VAR")) dur1 = variables.get(a.getValue().toString());
                    else dur1 = Double.parseDouble(a.getValue().toString());

                    if (b.getType().equals("VAR")) dur2 = variables.get(b.getValue().toString());
                    else dur2 = Double.parseDouble(b.getValue().toString());
                    stack.push(new Token("DIGIT", new StringBuffer(String.valueOf(dur2 * dur1))));
                }
                if (i.getValue().toString().charAt(0) == '=') {
                    Double val;
                    if (a.getType().equals("DIGIT"))
                        val = Double.parseDouble(a.getValue().toString());
                    else
                        val = variables.get(a.getValue().toString());
                    variables.replace(b.getValue().toString(), val);
                }
            }
        }
        //System.out.println(variables);
    }


    private int getPriority(Token i) {
        // if(i.getType() == "WHILE_KEYWORD" || i.getType() == "FOR_KEYWORD") return 7;
        // if(i.getType() == "IF_KEYWORD") return 6;
        if (i.getValue().toString().charAt(0) == '*' || i.getValue().toString().charAt(0) == '/') return 4;
        if (i.getValue().toString().charAt(0) == '+' || i.getValue().toString().charAt(0) == '-') return 3;
        if (i.getValue().toString().charAt(0) == '>' || i.getValue().toString().charAt(0) == '<') return 3;
        if (i.getType().equals("ASSIGN_OP")) return 2;
        if (i.getType().equals("L_BRACKET") || i.getType().equals("START_BODY")) return 1;
        if (i.getType().equals("R_BRACKET") || i.getType().equals("FINISH_BODY")) return -1;
//        if(i.getType() == "ELSE_KEYWORD") return -5;
        else
            return 0;
    }
}