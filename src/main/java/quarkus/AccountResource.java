package quarkus;

import org.jboss.resteasy.annotations.Body;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.json.Json;
import javax.json.JsonObjectBuilder;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

@Path("/accounts")
@ApplicationScoped
public class AccountResource {

    Set<Account> accounts = new HashSet<>();

    @PostConstruct
    public void setup() {
        accounts.add(new Account(1L, 31L, "Max Karavaev", new BigDecimal("678000")));
        accounts.add(new Account(2L, 18L, "Artem Zhvikov", new BigDecimal("100")));
        accounts.add(new Account(3L, 12L, "Alexander Korneev", new BigDecimal("500")));
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Set<Account> allAccounts() {
        return accounts;
    }

    @GET
    @Path("/{accountNumber}")
    @Produces(MediaType.APPLICATION_JSON)
    public Account getAccount(@PathParam("accountNumber") Long accountNumber) {
        return findAccountByNumber(accountNumber);
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response createAccount(Account account) {
        if (account.getAccountNumber() == null) {
            throw new WebApplicationException("No account number specified", 400);
        }

        accounts.add(account);
        return Response.status(201).entity(account).build();
    }

    @GET
    @Path("/withdraw")
    @Produces(MediaType.TEXT_PLAIN)
    public Response withdrawFunds(@QueryParam("accountNumber") Long accountNumber,
                                  @QueryParam("value") BigDecimal value) {
        Account account = findAccountByNumber(accountNumber);
        account.withdrawFunds(value);
        return Response.ok("Funds have been withdrawn from account number " + accountNumber).build();
    }

    @GET
    @Path("/deposit")
    public Response depositFunds(@QueryParam("accountNumber") Long accountNumber,
                                 @QueryParam("value") BigDecimal value) {
        Account account = findAccountByNumber(accountNumber);
        account.addFunds(value);
        return Response.ok("Funds have been deposited into account number " + accountNumber).build();
    }

    @GET
    @Path("/delete/{accountNumber}")
    public Response deleteAccount(@PathParam("accountNumber") Long accountNumber) {
        Account account = findAccountByNumber(accountNumber);
        accounts.remove(account);
        return Response.ok("Account number " + accountNumber + " has been successfully deleted").build();
    }

    private Account findAccountByNumber(Long accountNumber) {
        return accounts.stream()
                .filter(a -> a.getAccountNumber().equals(accountNumber))
                .findFirst()
                .orElseThrow(() -> new WebApplicationException("Account number " + accountNumber
                        + " does not exist", Response.Status.NOT_FOUND));
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