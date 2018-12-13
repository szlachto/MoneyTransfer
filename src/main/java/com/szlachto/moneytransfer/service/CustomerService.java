package com.szlachto.moneytransfer.service;

import com.szlachto.moneytransfer.dao.CustomerDAO;
import com.szlachto.moneytransfer.dao.DAOFactory;
import com.szlachto.moneytransfer.model.Customer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

@Path("/customer")
@Produces(MediaType.APPLICATION_JSON)
public class CustomerService {
    private static final Logger LOGGER = LogManager.getLogger(CustomerService.class);
    private static final DAOFactory DAO_FACTORY = DAOFactory.getDAOFactory(DAOFactory.H2);
    private static final CustomerDAO CUSTOMER_DAO = DAO_FACTORY.getCustomerDAO();

    @GET
    @Path("/{customerName}")
    public Customer getCustomerByName(@PathParam("customerName") String customerName) {
        LOGGER.debug("Request Received for get Customer by Name " + customerName);
        final Customer customer = CUSTOMER_DAO.getCustomerByName(customerName);
        if (isNull(customer)) {
            throw new WebApplicationException("Customer Not Found", Response.Status.NOT_FOUND);
        }
        return customer;
    }

    @POST
    @Path("/create")
    public Customer createCustomer(Customer customer) {
        if (nonNull(CUSTOMER_DAO.getCustomerByName(customer.getCustomerName()))) {
            throw new WebApplicationException("Customer name already exist", Response.Status.BAD_REQUEST);
        }
        final long uId = CUSTOMER_DAO.insertCustomer(customer);
        return CUSTOMER_DAO.getCustomerById(uId);
    }

    @DELETE
    @Path("/{customerId}")
    public Response deleteCustomerById(@PathParam("customerId") long customerId) {
        int deleteCount = CUSTOMER_DAO.deleteCustomerById(customerId);
        if (deleteCount == 1) {
            return Response.status(Response.Status.OK).build();
        } else {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
    }
}
