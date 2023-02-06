package backend.employee.notFound;

public class EmployeeNotFoundException extends RuntimeException {
    public EmployeeNotFoundException(Long id)  {
        super("Could nor find employee " + id);
    }
}
