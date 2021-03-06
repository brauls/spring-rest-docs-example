package de.tutorial.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.tutorial.exception.CustomerAlreadyExistsException;
import de.tutorial.exception.CustomerNotFoundException;
import de.tutorial.model.Customer;
import de.tutorial.model.ErrorResponse;
import de.tutorial.service.CustomerService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static de.tutorial.controller.RestControllerExceptionHandler.ALREADY_EXISTS_HINT;
import static de.tutorial.controller.RestControllerExceptionHandler.NOT_FOUND_HINT;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest(CustomerController.class)
@AutoConfigureRestDocs(outputDir = "target/snippets")
public class CustomerControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CustomerService customerService;

    @Before
    public void setUp() {
        when(customerService.getCustomers()).thenReturn(testCustomers());
    }

    @Test
    public void getCustomers_shouldReturnCustomersAsJson_withOkStatus() throws Exception {
        mockMvc.perform(get("/customers").accept(MediaType.APPLICATION_JSON))
               .andExpect(status().isOk())
               .andExpect(content().json(testCustomersJson()))
               .andDo(document("{class_name}/get_customers_ok"));
    }

    @Test
    public void getCustomer_whenExists_shouldReturnCustomerAsJson_withOkStatus() throws Exception {
        when(customerService.getCustomer("customerA")).thenReturn(Optional.of(testCustomer()));
        mockMvc.perform(get("/customers/{name}", "customerA").accept(MediaType.APPLICATION_JSON))
               .andExpect(status().isOk())
               .andExpect(content().json(testCustomerJson()))
               .andDo(document(
                   "{class_name}/get_customer_ok",
                   responseFields(
                       fieldWithPath("name")
                           .description("The name of the customer"),
                       fieldWithPath("mailAddress")
                           .description("The eMail address of the customer"),
                       fieldWithPath("category")
                           .description("The category of the customer; 1 for highest importance, 3 for lowest")),
                   pathParameters(
                       parameterWithName("name").description("The name of the customer that is requested"))));
    }

    @Test
    public void getCustomer_whenNotExists_shouldReturnErrorResponse_withNotFoundStatus() throws Exception {
        when(customerService.getCustomer("customerA")).thenReturn(Optional.empty());
        mockMvc.perform(get("/customers/{name}", "customerA").accept(MediaType.APPLICATION_JSON))
               .andExpect(status().isNotFound())
               .andExpect(content().json(notFoundResponseJson()))
               .andDo(document(
                   "{class_name}/get_customer_notFound",
                   pathParameters(
                       parameterWithName("name").description("The name of the customer that is requested"))));
    }

    @Test
    public void addCustomer_whenNotExists_shouldReturnCreatedStatus() throws Exception {
        doNothing().when(customerService).addCustomer(any());
        mockMvc.perform(post("/customers").content(testCustomerJson()).contentType(MediaType.APPLICATION_JSON))
               .andExpect(status().isCreated())
               .andDo(document("{class_name}/post_customer_created"));
    }

    @Test
    public void addCustomer_whenAlreadyExists_shouldReturnErrorResponse_withConflictStatus() throws Exception {
        final String message = "A customer with name customerA already exists";
        doThrow(new CustomerAlreadyExistsException(message)).when(customerService).addCustomer(any());
        mockMvc.perform(post("/customers").content(testCustomerJson()).contentType(MediaType.APPLICATION_JSON))
               .andExpect(status().isConflict())
               .andExpect(content().json(conflictResponseJson(message)))
               .andDo(document("{class_name}/post_customer_conflict"));
    }

    @Test
    public void deleteCustomer_whenExists_shouldReturnOkStatus() throws Exception {
        doNothing().when(customerService).deleteCustomer("customerA");
        mockMvc.perform(delete("/customers/{name}", "customerA"))
               .andExpect(status().isOk())
               .andDo(document(
                   "{class_name}/delete_customer_ok",
                   pathParameters(
                       parameterWithName("name").description("The name of the customer that shall be deleted"))));
    }

    @Test
    public void deleteCustomer_whenNotExists_shouldReturnErrorResponse_withNotFoundStatus() throws Exception {
        final String message = "A customer with name customerA does not exist";
        doThrow(new CustomerNotFoundException(message)).when(customerService).deleteCustomer("customerA");
        mockMvc.perform(delete("/customers/{name}", "customerA"))
               .andExpect(status().isNotFound())
               .andExpect(content().json(notFoundResponseJson()))
               .andDo(document(
                   "{class_name}/delete_customer_notFound",
                   pathParameters(
                       parameterWithName("name").description("The name of the customer that shall be deleted"))));
    }

    private static Customer testCustomer() {
        return new Customer("customerA", "example@mail.com", 1);
    }

    private static List<Customer> testCustomers() {
        final List<Customer> customers = new ArrayList<>();
        customers.add(new Customer("customerA", "example1@mail.com", 1));
        customers.add(new Customer("customerB", "example2@mail.com", 2));
        customers.add(new Customer("customerC", "example3@mail.com", 3));
        return customers;
    }

    private static String testCustomerJson() throws JsonProcessingException {
        final ObjectMapper mapper = new ObjectMapper();
        return mapper.writeValueAsString(testCustomer());
    }

    private static String testCustomersJson() throws JsonProcessingException {
        final ObjectMapper mapper = new ObjectMapper();
        return mapper.writeValueAsString(testCustomers());
    }

    private static String notFoundResponseJson() throws JsonProcessingException {
        final ObjectMapper mapper = new ObjectMapper();
        final String reason = "A customer with name customerA does not exist";
        final ErrorResponse response = new ErrorResponse(reason, NOT_FOUND_HINT);
        return mapper.writeValueAsString(response);
    }

    private static String conflictResponseJson(final String reason) throws JsonProcessingException {
        final ObjectMapper mapper = new ObjectMapper();
        final ErrorResponse response = new ErrorResponse(reason, ALREADY_EXISTS_HINT);
        return mapper.writeValueAsString(response);
    }
}
