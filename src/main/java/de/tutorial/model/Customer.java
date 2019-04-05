package de.tutorial.model;

public class Customer {
    private final String name;
    private final String mailAddress;
    private final int category;

    public Customer(final String name, final String mailAddress, final int category) {
        this.name = name;
        this.mailAddress = mailAddress;
        this.category = category;
    }

    public Customer(final Customer other) {
        name = other.name;
        mailAddress = other.mailAddress;
        category = other.category;
    }

    public String getName() {
        return name;
    }

    public String getMailAddress() {
        return mailAddress;
    }

    public int getCategory() {
        return category;
    }
}
