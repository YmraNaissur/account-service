package quarkus;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Objects;

@Entity
@NamedQuery(name = "Account.findAll", query = "select a from Account a order by a.accountNumber")
@NamedQuery(name = "Account.findByAccountNumber",
        query = "select a from Account a where a.accountNumber = :accountNumber order by a.accountNumber")
public class Account {

    @Id
    @SequenceGenerator(name = "accountsSequence", sequenceName = "accounts_id_seq",
            allocationSize = 1, initialValue = 10)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "accountsSequence")
    private Long id;

    private Long accountNumber;
    private Long customerNumber;
    private String customerName;
    private BigDecimal balance;
    private AccountStatus accountStatus = AccountStatus.OPEN;

    public void markOverdrawn() {
        accountStatus = AccountStatus.OVERDRAWN;
    }

    public void removeOverdrawnStatus() {
        accountStatus = AccountStatus.OPEN;
    }

    public void close() {
        accountStatus = AccountStatus.CLOSED;
        balance = BigDecimal.valueOf(0);
    }

    public void withdrawFunds(BigDecimal funds) {
        balance = balance.subtract(funds);
        checkOverdrawnStatus();
    }

    public void addFunds(BigDecimal funds) {
        balance = balance.add(funds);
        checkOverdrawnStatus();
    }

    private void checkOverdrawnStatus() {
        if (isNegative(balance)) {
            markOverdrawn();
        } else {
            removeOverdrawnStatus();
        }
    }

    private boolean isNegative(BigDecimal value) {
        return BigDecimal.ZERO.compareTo(value) > 0;
    }

    public Long getAccountNumber() {
        return accountNumber;
    }

    public String getCustomerName() {
        return customerName;
    }

    public Long getCustomerNumber() {
        return customerNumber;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public AccountStatus getAccountStatus() {
        return accountStatus;
    }

    public void setAccountNumber(Long accountNumber) {
        this.accountNumber = accountNumber;
    }

    public void setCustomerNumber(Long customerNumber) {
        this.customerNumber = customerNumber;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    public void setAccountStatus(AccountStatus accountStatus) {
        this.accountStatus = accountStatus;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) return true;
        if (other == null || getClass() != other.getClass()) return false;
        Account otherAccount = (Account) other;
        return Objects.equals(otherAccount.getAccountNumber(), this.accountNumber)
                && Objects.equals(otherAccount.getCustomerNumber(), this.customerNumber);
    }

    @Override
    public int hashCode() {
        return Objects.hash(accountNumber, customerNumber);
    }
}
