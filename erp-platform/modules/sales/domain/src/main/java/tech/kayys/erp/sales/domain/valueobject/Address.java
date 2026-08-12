package tech.kayys.erp.sales.domain.valueobject;

import tech.kayys.erp.foundation.domain.ValueObject;

import java.util.Objects;

/**
 * Address value object for shipping and billing.
 */
public final class Address implements ValueObject {
    
    private static final long serialVersionUID = 1L;
    
    private final String street;
    private final String city;
    private final String state;
    private final String postalCode;
    private final String country;

    public Address(String street, String city, String state, String postalCode, String country) {
        this.street = street;
        this.city = city;
        this.state = state;
        this.postalCode = postalCode;
        this.country = country;
        validate();
    }

    @Override
    public void validate() {
        if (street == null || street.trim().isEmpty()) {
            throw new IllegalArgumentException("Street cannot be empty");
        }
        if (city == null || city.trim().isEmpty()) {
            throw new IllegalArgumentException("City cannot be empty");
        }
        if (country == null || country.trim().isEmpty()) {
            throw new IllegalArgumentException("Country cannot be empty");
        }
    }

    public String getStreet() { return street; }
    public String getCity() { return city; }
    public String getState() { return state; }
    public String getPostalCode() { return postalCode; }
    public String getCountry() { return country; }

    public String formattedAddress() {
        StringBuilder sb = new StringBuilder();
        sb.append(street).append("\n");
        sb.append(city);
        if (state != null && !state.isEmpty()) {
            sb.append(", ").append(state);
        }
        if (postalCode != null && !postalCode.isEmpty()) {
            sb.append(" ").append(postalCode);
        }
        sb.append("\n").append(country);
        return sb.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Address address = (Address) o;
        return Objects.equals(street, address.street) &&
               Objects.equals(city, address.city) &&
               Objects.equals(state, address.state) &&
               Objects.equals(postalCode, address.postalCode) &&
               Objects.equals(country, address.country);
    }

    @Override
    public int hashCode() {
        return Objects.hash(street, city, state, postalCode, country);
    }

    @Override
    public String toString() {
        return formattedAddress();
    }

    public static Address of(String street, String city, String state, String postalCode, String country) {
        return new Address(street, city, state, postalCode, country);
    }
}