public class Token {
    private final String type;
    private final StringBuffer value = new StringBuffer("");

    public Token(String type, StringBuffer value) {
        this.type = type;
        this.value.append(value);
    }

    public String getType() {
        return type;
    }

    public StringBuffer getValue() {
        return value;
    }

    @Override
    public String toString() {
        return "Token " + this.type + " : " + this.value + " ";
    }
}