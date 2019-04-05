package de.tutorial.service;

import java.util.List;
import java.util.Optional;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import de.tutorial.exception.CustomerAlreadyExistsException;
import de.tutorial.exception.CustomerNotFoundException;
import de.tutorial.model.Customer;

public class CustomerServiceTest {
    private CustomerService serviceUnderTest;

    @Before
    public void setUp() {
        serviceUnderTest = new CustomerService();
    }

    @Test
    public void getCustomers_noCustomers_shouldReturnEmptyList() {
        final List<Customer> actualCustomers = serviceUnderTest.getCustomers();
        assertEquals(0, actualCustomers.size());
    }

    @Test
    public void getCustomers_twoCustomers_shouldReturnListOfTwo() throws Exception {
        serviceUnderTest.addCustomer(testCustomer("customerA", "example1@mail.com", 1));
        serviceUnderTest.addCustomer(testCustomer("customerB", "example2@mail.com", 2));
        final List<Customer> actualCustomers = serviceUnderTest.getCustomers();
        assertEquals(2, actualCustomers.size());
    }

    @Test(expected = UnsupportedOperationException.class)
    public void getCustomers_shouldReturnUnmodifiableList() {
        final List<Customer> actualCustomers = serviceUnderTest.getCustomers();
        actualCustomers.add(testCustomer("customerA", "example@mail.com", 1));
    }

    @Test
    public void addCustomer_shouldNotModifyPreviousQueries() throws Exception {
        final List<Customer> actualCustomers = serviceUnderTest.getCustomers();
        serviceUnderTest.addCustomer(testCustomer("customerB", "example@mail.com", 2));
        assertEquals(0, actualCustomers.size());
    }

    @Test
    public void addCustomers_withSameAttributesExceptName_shouldBeOk() throws Exception {
        serviceUnderTest.addCustomer(testCustomer("customerA", "example@mail.com", 1));
        serviceUnderTest.addCustomer(testCustomer("customerB", "example@mail.com", 1));
        final List<Customer> actualCustomers = serviceUnderTest.getCustomers();
        assertEquals(2, actualCustomers.size());
    }

    @Test(expected = CustomerAlreadyExistsException.class)
    public void addCustomers_withSameName_shouldThrowException() throws Exception {
        serviceUnderTest.addCustomer(testCustomer("customerA", "example1@mail.com", 1));
        serviceUnderTest.addCustomer(testCustomer("customerA", "example2@mail.com", 2));
    }

    @Test
    public void getCustomer_whenNotExists_shouldReturnEmptyOptional() throws Exception {
        serviceUnderTest.addCustomer(testCustomer("customerA", "example1@mail.com", 1));
        serviceUnderTest.addCustomer(testCustomer("customerB", "example2@mail.com", 2));
        final Optional<Customer> customer = serviceUnderTest.getCustomer("customerC");
        assertFalse(customer.isPresent());
    }

    @Test
    public void getCustomer_whenExists_shouldHaveCorrectAttributes() throws Exception {
        serviceUnderTest.addCustomer(testCustomer("customerA", "example1@mail.com", 1));
        serviceUnderTest.addCustomer(testCustomer("customerB", "example2@mail.com", 2));
        final Customer customer = serviceUnderTest.getCustomer("customerA").get();
        assertEquals("customerA", customer.getName());
        assertEquals("example1@mail.com", customer.getMailAddress());
        assertEquals(1, customer.getCategory());
    }

    @Test(expected = CustomerNotFoundException.class)
    public void deleteCustomer_whenNotExists_shouldThrowException() throws Exception {
        serviceUnderTest.addCustomer(testCustomer("customerA", "example1@mail.com", 1));
        serviceUnderTest.addCustomer(testCustomer("customerB", "example2@mail.com", 2));
        serviceUnderTest.deleteCustomer("customerC");
    }

    @Test
    public void deleteCustomer_whenExists_shouldDeleteTheCustomer() throws Exception {
        serviceUnderTest.addCustomer(testCustomer("customerA", "example1@mail.com", 1));
        serviceUnderTest.addCustomer(testCustomer("customerB", "example2@mail.com", 2));
        serviceUnderTest.deleteCustomer("customerA");
        final Optional<Customer> customer = serviceUnderTest.getCustomer("customerA");
        assertFalse(customer.isPresent());
    }

    private static Customer testCustomer(final String name, final String mailAddress, final int category) {
        return new Customer(name, mailAddress, category);
    }
}
