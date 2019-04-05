package de.tutorial.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import de.tutorial.exception.CustomerAlreadyExistsException;
import de.tutorial.exception.CustomerNotFoundException;
import de.tutorial.model.Customer;

@Service
public class CustomerService {
    private final List<Customer> customers = new ArrayList<>();

    public List<Customer> getCustomers() {
        return customers.stream().collect(Collectors.collectingAndThen(
            Collectors.toList(), Collections::unmodifiableList));
    }

    public Optional<Customer> getCustomer(final String name) {
        return customers.stream().filter(customer -> customer.getName().equals(name)).findFirst();
    }

    public void addCustomer(final Customer customer) throws CustomerAlreadyExistsException {
        final boolean customerAlreadyExists = customers
            .stream().anyMatch(cus -> cus.getName().equals(customer.getName()));
        if (customerAlreadyExists) {
            throw new CustomerAlreadyExistsException(
                String.format("A customer with name %s already exists", customer.getName()));
        }
        customers.add(customer);
    }

    public void deleteCustomer(final String name) throws CustomerNotFoundException {
        final boolean hasRemoved = customers.removeIf(customer -> customer.getName().equals(name));
        if (!hasRemoved) {
            throw new CustomerNotFoundException(String.format("A customer with name %s does not exist", name));
        }
    }
}
