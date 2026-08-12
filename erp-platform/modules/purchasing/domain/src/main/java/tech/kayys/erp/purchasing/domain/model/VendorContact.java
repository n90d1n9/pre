package tech.kayys.erp.purchasing.domain.model;

import tech.kayys.erp.foundation.domain.ValueObject;

import java.util.Objects;

/**
 * Vendor contact value object.
 */
public final class VendorContact implements ValueObject {
    
    private static final long serialVersionUID = 1L;
    
    private final String firstName;
    private final String lastName;
    private final String email;
    private final String phone;
    private final String mobile;
    private final String jobTitle;
    private final String department;
    private final boolean primary;
    private final boolean active;

    public VendorContact(
            String firstName,
            String lastName,
            String email,
            String phone,
            String mobile,
            String jobTitle,
            String department,
            boolean primary,
            boolean active) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.phone = phone;
        this.mobile = mobile;
        this.jobTitle = jobTitle;
        this.department = department;
        this.primary = primary;
        this.active = active;
        validate();
    }

    @Override
    public void validate() {
        if (firstName == null || firstName.trim().isEmpty()) {
            throw new IllegalArgumentException("First name cannot be empty");
        }
        if (lastName == null || lastName.trim().isEmpty()) {
            throw new IllegalArgumentException("Last name cannot be empty");
        }
        if (email != null && !email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            throw new IllegalArgumentException("Invalid email format");
        }
    }

    public String getFirstName() { return firstName; }
    public String getLastName() { return lastName; }
    public String getFullName() { return firstName + " " + lastName; }
    public String getEmail() { return email; }
    public String getPhone() { return phone; }
    public String getMobile() { return mobile; }
    public String getJobTitle() { return jobTitle; }
    public String getDepartment() { return department; }
    public boolean isPrimary() { return primary; }
    public boolean isActive() { return active; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        VendorContact that = (VendorContact) o;
        return primary == that.primary &&
               Objects.equals(email, that.email);
    }

    @Override
    public int hashCode() {
        return Objects.hash(email, primary);
    }

    @Override
    public String toString() {
        return "VendorContact{" +
                "fullName='" + getFullName() + '\'' +
                ", email='" + email + '\'' +
                ", primary=" + primary +
                '}';
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String firstName;
        private String lastName;
        private String email;
        private String phone;
        private String mobile;
        private String jobTitle;
        private String department;
        private boolean primary = false;
        private boolean active = true;

        public Builder firstName(String firstName) {
            this.firstName = firstName;
            return this;
        }

        public Builder lastName(String lastName) {
            this.lastName = lastName;
            return this;
        }

        public Builder email(String email) {
            this.email = email;
            return this;
        }

        public Builder phone(String phone) {
            this.phone = phone;
            return this;
        }

        public Builder mobile(String mobile) {
            this.mobile = mobile;
            return this;
        }

        public Builder jobTitle(String jobTitle) {
            this.jobTitle = jobTitle;
            return this;
        }

        public Builder department(String department) {
            this.department = department;
            return this;
        }

        public Builder primary(boolean primary) {
            this.primary = primary;
            return this;
        }

        public Builder active(boolean active) {
            this.active = active;
            return this;
        }

        public VendorContact build() {
            return new VendorContact(
                firstName, lastName, email, phone, mobile,
                jobTitle, department, primary, active
            );
        }
    }
}