package pt.ist.fenixframework.example.tpcw;

public interface TransactionalCommand<T> {
    public T doIt();
}
