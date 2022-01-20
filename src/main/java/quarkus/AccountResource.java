package quarkus;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.json.Json;
import javax.json.JsonObjectBuilder;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.transaction.Transactional;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
import java.math.BigDecimal;
import java.util.List;

@Path("/accounts")
@ApplicationScoped
public class AccountResource {

    @Inject
    EntityManager entityManager;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<Account> allAccounts() {
        return entityManager.createNamedQuery("Account.findAll", Account.class).getResultList();
    }

    @GET
    @Path("/{accountNumber}")
    @Produces(MediaType.APPLICATION_JSON)
    public Account getAccount(@PathParam("accountNumber") Long accountNumber) {
        try {
            return entityManager.createNamedQuery("Account.findByAccountNumber", Account.class)
                    .setParameter("accountNumber", accountNumber)
                    .getSingleResult();
        } catch (NoResultException nre) {
            throw new WebApplicationException("Account number " + accountNumber + " doesn't exist", 404);
        }
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Transactional
    public Account createAccount(Account account) {
        entityManager.persist(account);
        return account;
    }

    @GET
    @Path("/withdraw")
    @Produces(MediaType.APPLICATION_JSON)
    @Transactional
    public Account withdrawFunds(@QueryParam("accountNumber") Long accountNumber,
                                  @QueryParam("value") BigDecimal value) {
        Account account = getAccount(accountNumber);
        account.withdrawFunds(value);
        return account;
    }

    @GET
    @Path("/deposit")
    @Produces(MediaType.APPLICATION_JSON)
    @Transactional
    public Account depositFunds(@QueryParam("accountNumber") Long accountNumber,
                                 @QueryParam("value") BigDecimal value) {
        Account account = getAccount(accountNumber);
        account.addFunds(value);
        return account;
    }

    @GET
    @Path("/close/{accountNumber}")
    @Produces(MediaType.APPLICATION_JSON)
    @Transactional
    public Account closeAccount(@PathParam ("accountNumber") Long accountNumber) {
        Account account = getAccount(accountNumber);
        account.close();
        entityManager.persist(account);
        return account;
    }

    @DELETE
    @Path("/{accountNumber}")
    @Produces(MediaType.TEXT_PLAIN)
    @Transactional
    public Response deleteAccount(@PathParam("accountNumber") Long accountNumber) {
        Account account = getAccount(accountNumber);
        entityManager.remove(account);
        return Response.ok("Account number " + accountNumber + " has been successfully deleted").build();
    }

    @Provider
    public static class ErrorMapper implements ExceptionMapper<Exception> {

        @Override
        public Response toResponse(Exception e) {
            int code = 500;

            if (e instanceof WebApplicationException) {
                code = ((WebApplicationException) e).getResponse().getStatus();
            }

            JsonObjectBuilder entityBuilder = Json.createObjectBuilder()
                    .add("ExceptionType", e.getClass().getName())
                    .add("Code", code);

            if (e.getMessage() != null) {
                entityBuilder.add("Error", e.getMessage());
            }

            return Response.status(code).entity(entityBuilder.build()).build();
        }
    }
}