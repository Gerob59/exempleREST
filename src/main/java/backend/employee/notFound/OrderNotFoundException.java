package backend.employee.notFound;

public class OrderNotFoundException extends RuntimeException{
    public OrderNotFoundException(Long id)   {
        super("Could nor find order " + id);
    }
}
