public class Wrapper<T> {
    T item;
    Wrapper<T> prev;
    Wrapper<T> next;

    public Wrapper(T item) {
        this.item = item;
        this.prev = null;
        this.next = null;
    }
}
