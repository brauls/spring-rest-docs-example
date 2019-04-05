package de.tutorial.controller;

import java.util.List;
import java.util.Optional;

import javax.validation.constraints.NotNull;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import de.tutorial.exception.CustomerAlreadyExistsException;
import de.tutorial.exception.CustomerNotFoundException;
import de.tutorial.model.Customer;
import de.tutorial.service.CustomerService;

@RestController
@RequestMapping("/customers")
public class CustomerController {
    final CustomerService customerService;

    public CustomerController(final CustomerService customerService) {
        Assert.notNull(customerService, "CustomerService must not be null");
        this.customerService = customerService;
    }

    @GetMapping
    public List<Customer> getCustomers() {
        return customerService.getCustomers();
    }

    @GetMapping("/{name}")
    public ResponseEntity<Customer> getCustomer(@PathVariable("name") final @NotNull String name)
        throws CustomerNotFoundException {

        final Optional<Customer> customer = customerService.getCustomer(name);
        if (customer.isPresent()) {
            return ResponseEntity.ok(customer.get());
        }
        throw new CustomerNotFoundException(String.format("A customer with name %s does not exist", name));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void addCustomer(@RequestBody final Customer customer) throws CustomerAlreadyExistsException {
        customerService.addCustomer(customer);
    }

    @DeleteMapping("/{name}")
    @ResponseStatus(HttpStatus.OK)
    public void deleteCustomer(@PathVariable("name") final String name) throws CustomerNotFoundException {
        customerService.deleteCustomer(name);
    }
}
