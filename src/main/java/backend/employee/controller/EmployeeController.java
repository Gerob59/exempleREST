package backend.employee.controller;

import backend.employee.assembler.EmployeeModelAssembler;
import backend.employee.entity.Employee;
import backend.employee.notFound.EmployeeNotFoundException;
import backend.employee.repository.EmployeeRepository;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.IanaLinkRelations;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;



import java.util.List;
import java.util.stream.Collectors;

@RestController
public class EmployeeController {
    private EmployeeRepository repository;

    private final EmployeeModelAssembler assembler;

    public EmployeeController(EmployeeRepository repository, EmployeeModelAssembler assembler)
    {
        this.repository = repository;
        this.assembler = assembler;
    }

    @GetMapping("/employee")
    public CollectionModel<EntityModel<Employee>> all()
    {
        List<EntityModel<Employee>> employee = repository.findAll().stream()
                .map(assembler::toModel)
                .collect(Collectors.toList());

        return CollectionModel.of(employee, linkTo(methodOn(EmployeeController.class).all()).withSelfRel());
    }

    @GetMapping("/employee/{id}")
    public EntityModel<Employee> byID(@PathVariable Long id)
    {
        Employee employee = repository.findById(id)
                .orElseThrow(() -> new EmployeeNotFoundException(id));

        return assembler.toModel(employee);
    }

    @PostMapping("/employee")
    public ResponseEntity<?> newEmployee(@RequestBody Employee newEmployee)
    {
        EntityModel<Employee> entityModel = assembler.toModel(repository.save(newEmployee));

        return ResponseEntity
                .created(entityModel.getRequiredLink(IanaLinkRelations.SELF).toUri())
                .body(entityModel);
    }

    @PutMapping("/employee/{id}")
    public  ResponseEntity<?> replaceEmployee(@RequestBody Employee newEmployee, @PathVariable Long id)
    {
        Employee updatedEmployee = repository.findById(id)
                .map(employee -> {
                    employee.setName(newEmployee.getName());
                    return repository.save(employee);
                })
                .orElseGet(() -> {
                    newEmployee.setId(id);
                    return repository.save(newEmployee);
                });
        EntityModel<Employee> entityModel = assembler.toModel(updatedEmployee);

        return ResponseEntity
                .created(entityModel.getRequiredLink(IanaLinkRelations.SELF).toUri())
                .body(entityModel);
    }

    @DeleteMapping("/employee/{id}")
    public ResponseEntity<?> deleteEmployee(@PathVariable long id)
    {
        repository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}