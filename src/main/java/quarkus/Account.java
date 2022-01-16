package quarkus;

import java.math.BigDecimal;
import java.util.Objects;

public class Account {
    public Long accountNumber;
    public Long customerNumber;
    public String customerName;
    public BigDecimal balance;
    public AccountStatus accountStatus = AccountStatus.OPEN;

    public Account() {
        /* No args constructor */
    }

    public Account(Long accountNumber, Long customerNumber, String customerName, BigDecimal balance) {
        this.accountNumber = accountNumber;
        this.customerNumber = customerNumber;
        this.customerName = customerName;
        this.balance = balance;
    }

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
