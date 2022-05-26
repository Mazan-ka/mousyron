public class GurLinkedList<T> {
    private int SIZE = 0;
    private Wrapper<T> first;
    private Wrapper<T> last;

    public GurLinkedList() {
        first = null;
        last = null;
    }

    public boolean isEmpty() { return first == null; }

    public void addFirst(T item) {
        Wrapper<T> temp = new Wrapper<>(item);
        SIZE++;

        if (isEmpty()) {
            last = temp;
        } else {
            first.prev = temp;
            temp.next = first;
        }
        first = temp;
    }

    public void addLast(T item) {
        Wrapper<T> temp = new Wrapper<>(item);
        SIZE++;

        if (isEmpty()) {
            first = temp;
        } else {
            last.next = temp;
            temp.prev = last;
        }
        last = temp;
    }

    public void addByIndex(int index, T item) {
        Wrapper<T> temp = new Wrapper<>(item);
        Wrapper<T> current = first;
        int i = 0;
        SIZE++;

        while (current.next != null && i != index) { //ищем элемент по нужному нам индексу
            current = current.next;
            i++;
        }
        if (current.next == null) { //если наш элемент оказался последним
            current.next = temp;
            temp.prev = current;
            last = temp;
        } else {                   //если наш элемент оказался не последним
            current.next.prev = temp;
            temp.next = current.next;

            current.next = temp;
            temp.prev = current;
        }
    }

    public T removeFirst() {
        Wrapper<T> current = first;
        SIZE--;

        if (current.next == null) {
            first = null;
        } else {
            first = current.next;
            first.prev = null;
        }
        return current.item;
    }

    public T removeLast() {
        Wrapper<T> current = last;
        SIZE--;

        if (current.prev == null) {
            last = null;
        } else {
            last = current.prev;
            last.next = null;
        }
        return current.item;
    }

    public T removeByIndex(int index) {
        Wrapper<T> current = first;
        int i = 0;
        SIZE--;

        while (current.next != null && i != index) {
            current = current.next;
            i++;
        }

        if (current.next == null) { //если наш элемент оказался последним
            last = current.prev;
            current.prev.next = null;
        } else {                    //если наш элемент оказался не последним
            current.prev.next = current.next;
            current.next.prev = current.prev;

            current.next = null;
        }
        current.prev = null;
        return current.item;
    }

    public int length() { return SIZE; }

    public T getByIndex(int index) {
        Wrapper<T> current = first;
        int i = 0;

        while (current.next != null && i != index) {
            current = current.next;
            i++;
        }
        return current.item;
    }
}
