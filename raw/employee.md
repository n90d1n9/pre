# Complete Implementation: Employee / HR Bounded Context

Now I'll implement the complete Employee/HR bounded context, which handles employee management, organizational structure, attendance, leave management, payroll integration, and performance management. This context is used by Accounting, Security, and throughout the ERP system.

## 1. Employee Domain Module

**`/modules/employee/domain/pom.xml`**:

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 
         https://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <parent>
        <groupId>tech.kayys.erp</groupId>
        <artifactId>erp-platform</artifactId>
        <version>0.1.0-SNAPSHOT</version>
        <relativePath>../../../pom.xml</relativePath>
    </parent>

    <artifactId>erp-employee-domain</artifactId>

    <dependencies>
        <dependency>
            <groupId>tech.kayys.erp</groupId>
            <artifactId>erp-foundation-domain</artifactId>
            <version>${project.version}</version>
        </dependency>
    </dependencies>
</project>
```

**`/modules/employee/domain/src/main/java/tech/kayys/erp/employee/domain/identifier/EmployeeId.java`**:

```java
package tech.kayys.erp.employee.domain.identifier;

import tech.kayys.erp.foundation.domain.Identifier;

import java.util.UUID;

/**
 * Employee identifier.
 */
public final class EmployeeId extends Identifier<UUID> {
    
    private static final long serialVersionUID = 1L;

    public EmployeeId(UUID value) {
        super(value);
    }

    public static EmployeeId of(UUID value) {
        return new EmployeeId(value);
    }

    public static EmployeeId generate() {
        return new EmployeeId(UUID.randomUUID());
    }

    public static EmployeeId fromString(String value) {
        return new EmployeeId(UUID.fromString(value));
    }

    @Override
    public String toString() {
        return "EmployeeId{" + value + "}";
    }
}
```

**`/modules/employee/domain/src/main/java/tech/kayys/erp/employee/domain/identifier/DepartmentId.java`**:

```java
package tech.kayys.erp.employee.domain.identifier;

import tech.kayys.erp.foundation.domain.Identifier;

import java.util.UUID;

/**
 * Department identifier.
 */
public final class DepartmentId extends Identifier<UUID> {
    
    private static final long serialVersionUID = 1L;

    public DepartmentId(UUID value) {
        super(value);
    }

    public static DepartmentId of(UUID value) {
        return new DepartmentId(value);
    }

    public static DepartmentId generate() {
        return new DepartmentId(UUID.randomUUID());
    }

    public static DepartmentId fromString(String value) {
        return new DepartmentId(UUID.fromString(value));
    }

    @Override
    public String toString() {
        return "DepartmentId{" + value + "}";
    }
}
```

**`/modules/employee/domain/src/main/java/tech/kayys/erp/employee/domain/identifier/PositionId.java`**:

```java
package tech.kayys.erp.employee.domain.identifier;

import tech.kayys.erp.foundation.domain.Identifier;

import java.util.UUID;

/**
 * Position identifier.
 */
public final class PositionId extends Identifier<UUID> {
    
    private static final long serialVersionUID = 1L;

    public PositionId(UUID value) {
        super(value);
    }

    public static PositionId of(UUID value) {
        return new PositionId(value);
    }

    public static PositionId generate() {
        return new PositionId(UUID.randomUUID());
    }

    public static PositionId fromString(String value) {
        return new PositionId(UUID.fromString(value));
    }

    @Override
    public String toString() {
        return "PositionId{" + value + "}";
    }
}
```

**`/modules/employee/domain/src/main/java/tech/kayys/erp/employee/domain/valueobject/Money.java`**:

```java
package tech.kayys.erp.employee.domain.valueobject;

import tech.kayys.erp.foundation.domain.ValueObject;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Currency;
import java.util.Objects;

/**
 * Money value object for the Employee context.
 */
public final class Money implements ValueObject {
    
    private static final long serialVersionUID = 1L;
    
    private final BigDecimal amount;
    private final Currency currency;
    private final int scale;

    public Money(BigDecimal amount, Currency currency) {
        this(amount, currency, 2);
    }

    public Money(BigDecimal amount, Currency currency, int scale) {
        this.amount = amount.setScale(scale, RoundingMode.HALF_EVEN);
        this.currency = currency;
        this.scale = scale;
        validate();
    }

    @Override
    public void validate() {
        if (amount == null) {
            throw new IllegalArgumentException("Amount cannot be null");
        }
        if (currency == null) {
            throw new IllegalArgumentException("Currency cannot be null");
        }
    }

    public BigDecimal getAmount() { return amount; }
    public Currency getCurrency() { return currency; }

    public Money add(Money other) {
        validateCurrency(other);
        return new Money(amount.add(other.amount), currency, scale);
    }

    public Money subtract(Money other) {
        validateCurrency(other);
        return new Money(amount.subtract(other.amount), currency, scale);
    }

    public Money multiply(BigDecimal multiplier) {
        return new Money(amount.multiply(multiplier), currency, scale);
    }

    public Money multiply(int multiplier) {
        return multiply(BigDecimal.valueOf(multiplier));
    }

    public Money divide(BigDecimal divisor) {
        return new Money(amount.divide(divisor, scale, RoundingMode.HALF_EVEN), currency, scale);
    }

    public Money percentage(BigDecimal percentage) {
        return multiply(percentage.divide(BigDecimal.valueOf(100), 10, RoundingMode.HALF_EVEN));
    }

    public int compareTo(Money other) {
        validateCurrency(other);
        return amount.compareTo(other.amount);
    }

    public boolean isGreaterThan(Money other) {
        return compareTo(other) > 0;
    }

    public boolean isLessThan(Money other) {
        return compareTo(other) < 0;
    }

    public boolean isZero() {
        return amount.signum() == 0;
    }

    public boolean isPositive() {
        return amount.signum() > 0;
    }

    public boolean isNegative() {
        return amount.signum() < 0;
    }

    private void validateCurrency(Money other) {
        if (!this.currency.equals(other.currency)) {
            throw new IllegalArgumentException(
                "Currency mismatch: " + this.currency + " != " + other.currency
            );
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Money money = (Money) o;
        return amount.compareTo(money.amount) == 0 &&
               Objects.equals(currency, money.currency);
    }

    @Override
    public int hashCode() {
        return Objects.hash(amount, currency);
    }

    @Override
    public String toString() {
        return currency.getCurrencyCode() + " " + amount.toPlainString();
    }

    public static Money of(BigDecimal amount, String currencyCode) {
        return new Money(amount, Currency.getInstance(currencyCode));
    }

    public static Money of(String amount, String currencyCode) {
        return new Money(new BigDecimal(amount), Currency.getInstance(currencyCode));
    }

    public static Money of(long amount, String currencyCode) {
        return new Money(BigDecimal.valueOf(amount), Currency.getInstance(currencyCode));
    }

    public static Money of(double amount, String currencyCode) {
        return new Money(BigDecimal.valueOf(amount), Currency.getInstance(currencyCode));
    }

    public static Money zero(String currencyCode) {
        return new Money(BigDecimal.ZERO, Currency.getInstance(currencyCode));
    }
}
```

**`/modules/employee/domain/src/main/java/tech/kayys/erp/employee/domain/valueobject/EmploymentStatus.java`**:

```java
package tech.kayys.erp.employee.domain.valueobject;

/**
 * Employment status of an employee.
 */
public enum EmploymentStatus {
    ACTIVE("Active - currently employed"),
    PROBATION("Probation - trial period"),
    TERMINATED("Terminated - employment ended"),
    RESIGNED("Resigned - voluntarily left"),
    RETIRED("Retired - retired from employment"),
    ON_LEAVE("On Leave - temporarily away"),
    SUSPENDED("Suspended - temporarily inactive"),
    CONTRACT_END("Contract End - fixed term ended"),
    DECEASED("Deceased - passed away");

    private final String description;

    EmploymentStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public boolean isActive() {
        return this == ACTIVE || this == PROBATION || this == ON_LEAVE;
    }

    public boolean isTerminal() {
        return this == TERMINATED || this == RESIGNED || 
               this == RETIRED || this == DECEASED || this == CONTRACT_END;
    }

    public boolean canTransitionTo(EmploymentStatus target) {
        return switch (this) {
            case ACTIVE -> target == PROBATION || target == ON_LEAVE || 
                           target == TERMINATED || target == RESIGNED || target == RETIRED;
            case PROBATION -> target == ACTIVE || target == TERMINATED;
            case ON_LEAVE -> target == ACTIVE || target == TERMINATED || target == RESIGNED;
            case SUSPENDED -> target == ACTIVE || target == TERMINATED;
            default -> false;
        };
    }
}
```

**`/modules/employee/domain/src/main/java/tech/kayys/erp/employee/domain/valueobject/EmploymentType.java`**:

```java
package tech.kayys.erp.employee.domain.valueobject;

/**
 * Type of employment.
 */
public enum EmploymentType {
    FULL_TIME("Full Time"),
    PART_TIME("Part Time"),
    CONTRACT("Contract"),
    INTERN("Intern"),
    APPRENTICE("Apprentice"),
    FREELANCE("Freelance"),
    CONSULTANT("Consultant"),
    TEMPORARY("Temporary");

    private final String displayName;

    EmploymentType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public boolean isPermanent() {
        return this == FULL_TIME || this == PART_TIME;
    }

    public boolean isContractual() {
        return this == CONTRACT || this == FREELANCE || 
               this == CONSULTANT || this == TEMPORARY;
    }
}
```

**`/modules/employee/domain/src/main/java/tech/kayys/erp/employee/domain/valueobject/LeaveType.java`**:

```java
package tech.kayys.erp.employee.domain.valueobject;

/**
 * Types of leave.
 */
public enum LeaveType {
    ANNUAL("Annual Leave"),
    SICK("Sick Leave"),
    MATERNITY("Maternity Leave"),
    PATERNITY("Paternity Leave"),
    ADOPTION("Adoption Leave"),
    COMPASSIONATE("Compassionate Leave"),
    EMERGENCY("Emergency Leave"),
    STUDY("Study Leave"),
    UNSCHEDULED("Unscheduled Leave"),
    PUBLIC_HOLIDAY("Public Holiday"),
    UNPAID("Unpaid Leave");

    private final String displayName;

    LeaveType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public boolean isPaid() {
        return this != UNSCHEDULED && this != UNPAID;
    }

    public boolean requiresApproval() {
        return this != PUBLIC_HOLIDAY;
    }
}
```

**`/modules/employee/domain/src/main/java/tech/kayys/erp/employee/domain/valueobject/LeaveStatus.java`**:

```java
package tech.kayys.erp.employee.domain.valueobject;

/**
 * Status of a leave request.
 */
public enum LeaveStatus {
    PENDING("Pending - awaiting approval"),
    APPROVED("Approved - leave granted"),
    REJECTED("Rejected - leave denied"),
    CANCELLED("Cancelled - request withdrawn"),
    TAKEN("Taken - leave consumed");

    private final String description;

    LeaveStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public boolean isTerminal() {
        return this == APPROVED || this == REJECTED || this == CANCELLED || this == TAKEN;
    }

    public boolean canTransitionTo(LeaveStatus target) {
        return switch (this) {
            case PENDING -> target == APPROVED || target == REJECTED || target == CANCELLED;
            case APPROVED -> target == TAKEN || target == CANCELLED;
            default -> false;
        };
    }
}
```

**`/modules/employee/domain/src/main/java/tech/kayys/erp/employee/domain/model/Employee.java`**:

```java
package tech.kayys.erp.employee.domain.model;

import tech.kayys.erp.foundation.domain.AggregateRoot;
import tech.kayys.erp.employee.domain.identifier.DepartmentId;
import tech.kayys.erp.employee.domain.identifier.EmployeeId;
import tech.kayys.erp.employee.domain.identifier.PositionId;
import tech.kayys.erp.employee.domain.valueobject.EmploymentStatus;
import tech.kayys.erp.employee.domain.valueobject.EmploymentType;
import tech.kayys.erp.employee.domain.valueobject.Money;

import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Employee aggregate root.
 * Represents an employee in the organization.
 */
public final class Employee extends AggregateRoot<EmployeeId> {
    
    private static final long serialVersionUID = 1L;
    
    private String employeeNumber;
    private String firstName;
    private String lastName;
    private String middleName;
    private String email;
    private String personalEmail;
    private String phone;
    private String mobile;
    private String address;
    private String city;
    private String state;
    private String postalCode;
    private String country;
    private LocalDate dateOfBirth;
    private LocalDate hireDate;
    private LocalDate terminationDate;
    private EmploymentStatus status;
    private EmploymentType employmentType;
    private DepartmentId departmentId;
    private PositionId positionId;
    private EmployeeId managerId;
    private Money salary;
    private String currencyCode;
    private String bankName;
    private String bankAccountNumber;
    private String taxId;
    private String socialSecurityNumber;
    private String emergencyContact;
    private String emergencyContactPhone;
    private List<EmployeeSkill> skills;
    private List<EmployeeCertification> certifications;
    private List<EmployeeEducation> education;
    private List<EmploymentHistory> employmentHistory;
    private boolean active;

    private Employee(EmployeeId id) {
        super(id);
        this.status = EmploymentStatus.PROBATION;
        this.active = true;
        this.skills = new ArrayList<>();
        this.certifications = new ArrayList<>();
        this.education = new ArrayList<>();
        this.employmentHistory = new ArrayList<>();
        this.salary = Money.zero("USD");
    }

    private Employee() {
        super();
    }

    /**
     * Factory method to create a new employee.
     */
    public static Employee create(
            EmployeeId id,
            String employeeNumber,
            String firstName,
            String lastName,
            String email,
            LocalDate hireDate,
            EmploymentType employmentType,
            String currencyCode) {
        Employee employee = new Employee(id);
        employee.employeeNumber = employeeNumber;
        employee.firstName = firstName;
        employee.lastName = lastName;
        employee.email = email;
        employee.hireDate = hireDate;
        employee.employmentType = employmentType;
        employee.currencyCode = currencyCode;
        return employee;
    }

    /**
     * Activates the employee.
     */
    public void activate() {
        if (status == EmploymentStatus.TERMINATED || status == EmploymentStatus.RESIGNED) {
            throw new IllegalStateException("Cannot activate terminated employee");
        }
        this.status = EmploymentStatus.ACTIVE;
        this.active = true;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    /**
     * Terminates the employee.
     */
    public void terminate(String reason) {
        if (status.isTerminal()) {
            throw new IllegalStateException("Employee is already terminated");
        }
        this.status = EmploymentStatus.TERMINATED;
        this.terminationDate = LocalDate.now();
        this.active = false;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    /**
     * Places the employee on leave.
     */
    public void putOnLeave() {
        if (status != EmploymentStatus.ACTIVE) {
            throw new IllegalStateException("Cannot place employee on leave in status: " + status);
        }
        this.status = EmploymentStatus.ON_LEAVE;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    /**
     * Returns the employee from leave.
     */
    public void returnFromLeave() {
        if (status != EmploymentStatus.ON_LEAVE) {
            throw new IllegalStateException("Employee is not on leave");
        }
        this.status = EmploymentStatus.ACTIVE;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    /**
     * Promotes the employee to a new position.
     */
    public void promote(PositionId newPosition, Money newSalary) {
        this.positionId = newPosition;
        this.salary = newSalary;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    /**
     * Transfers the employee to a new department.
     */
    public void transfer(DepartmentId newDepartment) {
        this.departmentId = newDepartment;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    /**
     * Adds a skill to the employee.
     */
    public void addSkill(EmployeeSkill skill) {
        skills.add(skill);
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    /**
     * Adds a certification to the employee.
     */
    public void addCertification(EmployeeCertification certification) {
        certifications.add(certification);
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    /**
     * Adds education to the employee.
     */
    public void addEducation(EmployeeEducation education) {
        this.education.add(education);
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    /**
     * Gets the employee's full name.
     */
    public String getFullName() {
        return firstName + " " + lastName;
    }

    /**
     * Gets the employee's age.
     */
    public int getAge() {
        if (dateOfBirth == null) {
            return 0;
        }
        return LocalDate.now().getYear() - dateOfBirth.getYear();
    }

    /**
     * Gets the employee's tenure in years.
     */
    public double getTenure() {
        if (hireDate == null) {
            return 0.0;
        }
        return (double) java.time.temporal.ChronoUnit.DAYS.between(hireDate, LocalDate.now()) / 365.25;
    }

    // Getters and Setters
    public String getEmployeeNumber() { return employeeNumber; }
    public String getFirstName() { return firstName; }
    public String getLastName() { return lastName; }
    public String getMiddleName() { return middleName; }
    public String getEmail() { return email; }
    public String getPersonalEmail() { return personalEmail; }
    public String getPhone() { return phone; }
    public String getMobile() { return mobile; }
    public String getAddress() { return address; }
    public String getCity() { return city; }
    public String getState() { return state; }
    public String getPostalCode() { return postalCode; }
    public String getCountry() { return country; }
    public LocalDate getDateOfBirth() { return dateOfBirth; }
    public LocalDate getHireDate() { return hireDate; }
    public LocalDate getTerminationDate() { return terminationDate; }
    public EmploymentStatus getStatus() { return status; }
    public EmploymentType getEmploymentType() { return employmentType; }
    public DepartmentId getDepartmentId() { return departmentId; }
    public PositionId getPositionId() { return positionId; }
    public EmployeeId getManagerId() { return managerId; }
    public Money getSalary() { return salary; }
    public String getCurrencyCode() { return currencyCode; }
    public String getBankName() { return bankName; }
    public String getBankAccountNumber() { return bankAccountNumber; }
    public String getTaxId() { return taxId; }
    public String getSocialSecurityNumber() { return socialSecurityNumber; }
    public String getEmergencyContact() { return emergencyContact; }
    public String getEmergencyContactPhone() { return emergencyContactPhone; }
    public List<EmployeeSkill> getSkills() { return Collections.unmodifiableList(skills); }
    public List<EmployeeCertification> getCertifications() { return Collections.unmodifiableList(certifications); }
    public List<EmployeeEducation> getEducation() { return Collections.unmodifiableList(education); }
    public List<EmploymentHistory> getEmploymentHistory() { return Collections.unmodifiableList(employmentHistory); }
    public boolean isActive() { return active && status.isActive(); }

    public void setMiddleName(String middleName) {
        this.middleName = middleName;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    public void setPersonalEmail(String personalEmail) {
        this.personalEmail = personalEmail;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    public void setPhone(String phone) {
        this.phone = phone;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    public void setAddress(String address) {
        this.address = address;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    public void setCity(String city) {
        this.city = city;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    public void setState(String state) {
        this.state = state;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    public void setCountry(String country) {
        this.country = country;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    public void setDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    public void setManagerId(EmployeeId managerId) {
        this.managerId = managerId;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    public void setSalary(Money salary) {
        this.salary = salary;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    public void setBankAccountNumber(String bankAccountNumber) {
        this.bankAccountNumber = bankAccountNumber;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    public void setTaxId(String taxId) {
        this.taxId = taxId;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    public void setSocialSecurityNumber(String socialSecurityNumber) {
        this.socialSecurityNumber = socialSecurityNumber;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    public void setEmergencyContact(String emergencyContact) {
        this.emergencyContact = emergencyContact;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    public void setEmergencyContactPhone(String emergencyContactPhone) {
        this.emergencyContactPhone = emergencyContactPhone;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    @Override
    public String toString() {
        return "Employee{" +
                "id=" + getId() +
                ", employeeNumber='" + employeeNumber + '\'' +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", status=" + status +
                '}';
    }

    /**
     * Employee skill value object.
     */
    public static final class EmployeeSkill implements ValueObject {
        private static final long serialVersionUID = 1L;
        
        private final String skillName;
        private final String skillLevel;
        private final int yearsExperience;
        private final boolean verified;

        public EmployeeSkill(String skillName, String skillLevel, int yearsExperience, boolean verified) {
            this.skillName = skillName;
            this.skillLevel = skillLevel;
            this.yearsExperience = yearsExperience;
            this.verified = verified;
            validate();
        }

        @Override
        public void validate() {
            if (skillName == null || skillName.trim().isEmpty()) {
                throw new IllegalArgumentException("Skill name cannot be empty");
            }
            if (yearsExperience < 0) {
                throw new IllegalArgumentException("Years experience cannot be negative");
            }
        }

        public String getSkillName() { return skillName; }
        public String getSkillLevel() { return skillLevel; }
        public int getYearsExperience() { return yearsExperience; }
        public boolean isVerified() { return verified; }

        @Override
        public String toString() {
            return "EmployeeSkill{" +
                    "skillName='" + skillName + '\'' +
                    ", level='" + skillLevel + '\'' +
                    ", years=" + yearsExperience +
                    '}';
        }
    }

    /**
     * Employee certification value object.
     */
    public static final class EmployeeCertification implements ValueObject {
        private static final long serialVersionUID = 1L;
        
        private final String certificationName;
        private final String issuingAuthority;
        private final LocalDate issueDate;
        private final LocalDate expiryDate;
        private final String certificationNumber;
        private final boolean verified;

        public EmployeeCertification(
                String certificationName,
                String issuingAuthority,
                LocalDate issueDate,
                LocalDate expiryDate,
                String certificationNumber,
                boolean verified) {
            this.certificationName = certificationName;
            this.issuingAuthority = issuingAuthority;
            this.issueDate = issueDate;
            this.expiryDate = expiryDate;
            this.certificationNumber = certificationNumber;
            this.verified = verified;
            validate();
        }

        @Override
        public void validate() {
            if (certificationName == null || certificationName.trim().isEmpty()) {
                throw new IllegalArgumentException("Certification name cannot be empty");
            }
            if (issuingAuthority == null || issuingAuthority.trim().isEmpty()) {
                throw new IllegalArgumentException("Issuing authority cannot be empty");
            }
            if (issueDate == null) {
                throw new IllegalArgumentException("Issue date cannot be null");
            }
            if (expiryDate != null && expiryDate.isBefore(issueDate)) {
                throw new IllegalArgumentException("Expiry date must be after issue date");
            }
        }

        public String getCertificationName() { return certificationName; }
        public String getIssuingAuthority() { return issuingAuthority; }
        public LocalDate getIssueDate() { return issueDate; }
        public LocalDate getExpiryDate() { return expiryDate; }
        public String getCertificationNumber() { return certificationNumber; }
        public boolean isVerified() { return verified; }
        public boolean isValid() {
            return verified && (expiryDate == null || LocalDate.now().isBefore(expiryDate));
        }

        @Override
        public String toString() {
            return "EmployeeCertification{" +
                    "certificationName='" + certificationName + '\'' +
                    ", issuingAuthority='" + issuingAuthority + '\'' +
                    ", valid=" + isValid() +
                    '}';
        }
    }

    /**
     * Employee education value object.
     */
    public static final class EmployeeEducation implements ValueObject {
        private static final long serialVersionUID = 1L;
        
        private final String degree;
        private final String field;
        private final String institution;
        private final LocalDate startDate;
        private final LocalDate endDate;
        private final boolean completed;

        public EmployeeEducation(
                String degree,
                String field,
                String institution,
                LocalDate startDate,
                LocalDate endDate,
                boolean completed) {
            this.degree = degree;
            this.field = field;
            this.institution = institution;
            this.startDate = startDate;
            this.endDate = endDate;
            this.completed = completed;
            validate();
        }

        @Override
        public void validate() {
            if (degree == null || degree.trim().isEmpty()) {
                throw new IllegalArgumentException("Degree cannot be empty");
            }
            if (institution == null || institution.trim().isEmpty()) {
                throw new IllegalArgumentException("Institution cannot be empty");
            }
            if (startDate == null) {
                throw new IllegalArgumentException("Start date cannot be null");
            }
            if (endDate != null && endDate.isBefore(startDate)) {
                throw new IllegalArgumentException("End date must be after start date");
            }
        }

        public String getDegree() { return degree; }
        public String getField() { return field; }
        public String getInstitution() { return institution; }
        public LocalDate getStartDate() { return startDate; }
        public LocalDate getEndDate() { return endDate; }
        public boolean isCompleted() { return completed; }

        @Override
        public String toString() {
            return "EmployeeEducation{" +
                    "degree='" + degree + '\'' +
                    ", institution='" + institution + '\'' +
                    ", completed=" + completed +
                    '}';
        }
    }

    /**
     * Employment history value object.
     */
    public static final class EmploymentHistory implements ValueObject {
        private static final long serialVersionUID = 1L;
        
        private final String companyName;
        private final String position;
        private final LocalDate startDate;
        private final LocalDate endDate;
        private final String reasonForLeaving;

        public EmploymentHistory(
                String companyName,
                String position,
                LocalDate startDate,
                LocalDate endDate,
                String reasonForLeaving) {
            this.companyName = companyName;
            this.position = position;
            this.startDate = startDate;
            this.endDate = endDate;
            this.reasonForLeaving = reasonForLeaving;
            validate();
        }

        @Override
        public void validate() {
            if (companyName == null || companyName.trim().isEmpty()) {
                throw new IllegalArgumentException("Company name cannot be empty");
            }
            if (position == null || position.trim().isEmpty()) {
                throw new IllegalArgumentException("Position cannot be empty");
            }
            if (startDate == null) {
                throw new IllegalArgumentException("Start date cannot be null");
            }
            if (endDate != null && endDate.isBefore(startDate)) {
                throw new IllegalArgumentException("End date must be after start date");
            }
        }

        public String getCompanyName() { return companyName; }
        public String getPosition() { return position; }
        public LocalDate getStartDate() { return startDate; }
        public LocalDate getEndDate() { return endDate; }
        public String getReasonForLeaving() { return reasonForLeaving; }

        @Override
        public String toString() {
            return "EmploymentHistory{" +
                    "companyName='" + companyName + '\'' +
                    ", position='" + position + '\'' +
                    ", startDate=" + startDate +
                    ", endDate=" + endDate +
                    '}';
        }
    }
}
```

**`/modules/employee/domain/src/main/java/tech/kayys/erp/employee/domain/model/LeaveRequest.java`**:

```java
package tech.kayys.erp.employee.domain.model;

import tech.kayys.erp.foundation.domain.AggregateRoot;
import tech.kayys.erp.employee.domain.identifier.EmployeeId;
import tech.kayys.erp.employee.domain.identifier.LeaveRequestId;
import tech.kayys.erp.employee.domain.valueobject.LeaveStatus;
import tech.kayys.erp.employee.domain.valueobject.LeaveType;

import java.time.Instant;
import java.time.LocalDate;

/**
 * Leave request aggregate root.
 * Represents an employee's request for leave.
 */
public final class LeaveRequest extends AggregateRoot<LeaveRequestId> {
    
    private static final long serialVersionUID = 1L;
    
    private EmployeeId employeeId;
    private LeaveType leaveType;
    private LocalDate startDate;
    private LocalDate endDate;
    private double days;
    private String reason;
    private LeaveStatus status;
    private String approvedBy;
    private Instant approvedAt;
    private String rejectedBy;
    private Instant rejectedAt;
    private String rejectionReason;
    private String notes;
    private boolean active;

    private LeaveRequest(LeaveRequestId id) {
        super(id);
        this.status = LeaveStatus.PENDING;
        this.active = true;
    }

    private LeaveRequest() {
        super();
    }

    /**
     * Factory method to create a new leave request.
     */
    public static LeaveRequest create(
            LeaveRequestId id,
            EmployeeId employeeId,
            LeaveType leaveType,
            LocalDate startDate,
            LocalDate endDate,
            String reason) {
        LeaveRequest request = new LeaveRequest(id);
        request.employeeId = employeeId;
        request.leaveType = leaveType;
        request.startDate = startDate;
        request.endDate = endDate;
        request.days = calculateDays(startDate, endDate);
        request.reason = reason;
        return request;
    }

    private static double calculateDays(LocalDate start, LocalDate end) {
        return (double) java.time.temporal.ChronoUnit.DAYS.between(start, end) + 1;
    }

    /**
     * Approves the leave request.
     */
    public void approve(String approvedBy) {
        if (status != LeaveStatus.PENDING) {
            throw new IllegalStateException("Leave request is not pending: " + status);
        }
        this.status = LeaveStatus.APPROVED;
        this.approvedBy = approvedBy;
        this.approvedAt = Instant.now();
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    /**
     * Rejects the leave request.
     */
    public void reject(String rejectedBy, String reason) {
        if (status != LeaveStatus.PENDING) {
            throw new IllegalStateException("Leave request is not pending: " + status);
        }
        this.status = LeaveStatus.REJECTED;
        this.rejectedBy = rejectedBy;
        this.rejectedAt = Instant.now();
        this.rejectionReason = reason;
        this.active = false;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    /**
     * Cancels the leave request.
     */
    public void cancel(String reason) {
        if (status.isTerminal()) {
            throw new IllegalStateException("Cannot cancel leave request in status: " + status);
        }
        this.status = LeaveStatus.CANCELLED;
        this.active = false;
        this.notes = reason;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    /**
     * Marks the leave as taken.
     */
    public void markAsTaken() {
        if (status != LeaveStatus.APPROVED) {
            throw new IllegalStateException("Leave request is not approved: " + status);
        }
        this.status = LeaveStatus.TAKEN;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    /**
     * Gets the number of working days requested.
     */
    public double getWorkingDays() {
        // In a real system, this would exclude weekends and holidays
        return days;
    }

    // Getters
    public EmployeeId getEmployeeId() { return employeeId; }
    public LeaveType getLeaveType() { return leaveType; }
    public LocalDate getStartDate() { return startDate; }
    public LocalDate getEndDate() { return endDate; }
    public double getDays() { return days; }
    public String getReason() { return reason; }
    public LeaveStatus getStatus() { return status; }
    public String getApprovedBy() { return approvedBy; }
    public Instant getApprovedAt() { return approvedAt; }
    public String getRejectedBy() { return rejectedBy; }
    public Instant getRejectedAt() { return rejectedAt; }
    public String getRejectionReason() { return rejectionReason; }
    public String getNotes() { return notes; }
    public boolean isActive() { return active; }

    public void setNotes(String notes) {
        this.notes = notes;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    @Override
    public String toString() {
        return "LeaveRequest{" +
                "id=" + getId() +
                ", employeeId=" + employeeId +
                ", leaveType=" + leaveType +
                ", startDate=" + startDate +
                ", endDate=" + endDate +
                ", status=" + status +
                '}';
    }
}
```

**`/modules/employee/domain/src/main/java/tech/kayys/erp/employee/domain/identifier/LeaveRequestId.java`**:

```java
package tech.kayys.erp.employee.domain.identifier;

import tech.kayys.erp.foundation.domain.Identifier;

import java.util.UUID;

/**
 * Leave request identifier.
 */
public final class LeaveRequestId extends Identifier<UUID> {
    
    private static final long serialVersionUID = 1L;

    public LeaveRequestId(UUID value) {
        super(value);
    }

    public static LeaveRequestId of(UUID value) {
        return new LeaveRequestId(value);
    }

    public static LeaveRequestId generate() {
        return new LeaveRequestId(UUID.randomUUID());
    }

    public static LeaveRequestId fromString(String value) {
        return new LeaveRequestId(UUID.fromString(value));
    }

    @Override
    public String toString() {
        return "LeaveRequestId{" + value + "}";
    }
}
```

## 2. Employee Application Module

**`/modules/employee/application/pom.xml`**:

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 
         https://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <parent>
        <groupId>tech.kayys.erp</groupId>
        <artifactId>erp-platform</artifactId>
        <version>0.1.0-SNAPSHOT</version>
        <relativePath>../../../pom.xml</relativePath>
    </parent>

    <artifactId>erp-employee-application</artifactId>

    <dependencies>
        <dependency>
            <groupId>tech.kayys.erp</groupId>
            <artifactId>erp-employee-domain</artifactId>
            <version>${project.version}</version>
        </dependency>
        <dependency>
            <groupId>tech.kayys.erp</groupId>
            <artifactId>erp-foundation-application</artifactId>
            <version>${project.version}</version>
        </dependency>

        <!-- Testing -->
        <dependency>
            <groupId>org.junit.jupiter</groupId>
            <artifactId>junit-jupiter</artifactId>
            <scope>test</scope>
        </dependency>
        <dependency>
            <groupId>org.assertj</groupId>
            <artifactId>assertj-core</artifactId>
            <scope>test</scope>
        </dependency>
        <dependency>
            <groupId>org.mockito</groupId>
            <artifactId>mockito-core</artifactId>
            <scope>test</scope>
        </dependency>
    </dependencies>
</project>
```

**`/modules/employee/application/src/main/java/tech/kayys/erp/employee/application/api/EmployeeService.java`**:

```java
package tech.kayys.erp.employee.application.api;

import tech.kayys.erp.employee.application.api.command.*;
import tech.kayys.erp.employee.application.api.query.EmployeeView;
import tech.kayys.erp.employee.application.api.query.LeaveRequestView;
import tech.kayys.erp.employee.domain.identifier.EmployeeId;
import tech.kayys.erp.employee.domain.identifier.LeaveRequestId;

import java.util.List;
import java.util.concurrent.CompletionStage;

/**
 * Public API for employee and HR operations.
 */
public interface EmployeeService {

    // ============ Employee Commands ============

    /**
     * Creates a new employee.
     */
    CompletionStage<EmployeeId> createEmployee(CreateEmployeeCommand command);

    /**
     * Activates an employee.
     */
    CompletionStage<EmployeeId> activateEmployee(ActivateEmployeeCommand command);

    /**
     * Terminates an employee.
     */
    CompletionStage<EmployeeId> terminateEmployee(TerminateEmployeeCommand command);

    /**
     * Puts an employee on leave.
     */
    CompletionStage<EmployeeId> putEmployeeOnLeave(PutEmployeeOnLeaveCommand command);

    /**
     * Returns an employee from leave.
     */
    CompletionStage<EmployeeId> returnEmployeeFromLeave(ReturnEmployeeFromLeaveCommand command);

    /**
     * Promotes an employee.
     */
    CompletionStage<EmployeeId> promoteEmployee(PromoteEmployeeCommand command);

    /**
     * Transfers an employee.
     */
    CompletionStage<EmployeeId> transferEmployee(TransferEmployeeCommand command);

    /**
     * Adds a skill to an employee.
     */
    CompletionStage<EmployeeId> addEmployeeSkill(AddEmployeeSkillCommand command);

    // ============ Leave Commands ============

    /**
     * Creates a leave request.
     */
    CompletionStage<LeaveRequestId> createLeaveRequest(CreateLeaveRequestCommand command);

    /**
     * Approves a leave request.
     */
    CompletionStage<LeaveRequestId> approveLeaveRequest(ApproveLeaveRequestCommand command);

    /**
     * Rejects a leave request.
     */
    CompletionStage<LeaveRequestId> rejectLeaveRequest(RejectLeaveRequestCommand command);

    /**
     * Cancels a leave request.
     */
    CompletionStage<LeaveRequestId> cancelLeaveRequest(CancelLeaveRequestCommand command);

    // ============ Employee Queries ============

    /**
     * Gets an employee by ID.
     */
    CompletionStage<EmployeeView> getEmployee(EmployeeId employeeId);

    /**
     * Gets employees by department.
     */
    CompletionStage<List<EmployeeView>> getEmployeesByDepartment(DepartmentId departmentId);

    /**
     * Gets employees by position.
     */
    CompletionStage<List<EmployeeView>> getEmployeesByPosition(PositionId positionId);

    /**
     * Gets employees by manager.
     */
    CompletionStage<List<EmployeeView>> getEmployeesByManager(EmployeeId managerId);

    /**
     * Gets an employee's leave requests.
     */
    CompletionStage<List<LeaveRequestView>> getLeaveRequests(EmployeeId employeeId);

    /**
     * Gets an employee's leave balance.
     */
    CompletionStage<LeaveBalanceView> getLeaveBalance(EmployeeId employeeId);

    /**
     * Searches employees.
     */
    CompletionStage<List<EmployeeView>> searchEmployees(SearchEmployeesQuery query);
}
```

**`/modules/employee/application/src/main/java/tech/kayys/erp/employee/application/api/command/CreateEmployeeCommand.java`**:

```java
package tech.kayys.erp.employee.application.api.command;

import tech.kayys.erp.foundation.application.Command;
import tech.kayys.erp.employee.domain.identifier.EmployeeId;
import tech.kayys.erp.employee.domain.valueobject.EmploymentType;

import java.time.LocalDate;

/**
 * Command to create a new employee.
 */
public record CreateEmployeeCommand(
        EmployeeId employeeId,
        String employeeNumber,
        String firstName,
        String lastName,
        String email,
        LocalDate hireDate,
        EmploymentType employmentType,
        String currencyCode,
        String departmentId,
        String positionId,
        String managerId,
        String salary
) implements Command<EmployeeId> {

    public CreateEmployeeCommand {
        if (firstName == null || firstName.trim().isEmpty()) {
            throw new IllegalArgumentException("First name cannot be empty");
        }
        if (lastName == null || lastName.trim().isEmpty()) {
            throw new IllegalArgumentException("Last name cannot be empty");
        }
        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("Email cannot be empty");
        }
        if (hireDate == null) {
            throw new IllegalArgumentException("Hire date is required");
        }
        if (employmentType == null) {
            throw new IllegalArgumentException("Employment type is required");
        }
        if (currencyCode == null || currencyCode.trim().isEmpty()) {
            throw new IllegalArgumentException("Currency code is required");
        }
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private EmployeeId employeeId;
        private String employeeNumber;
        private String firstName;
        private String lastName;
        private String email;
        private LocalDate hireDate;
        private EmploymentType employmentType = EmploymentType.FULL_TIME;
        private String currencyCode = "USD";
        private String departmentId;
        private String positionId;
        private String managerId;
        private String salary;

        public Builder employeeId(EmployeeId employeeId) {
            this.employeeId = employeeId;
            return this;
        }

        public Builder employeeNumber(String employeeNumber) {
            this.employeeNumber = employeeNumber;
            return this;
        }

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

        public Builder hireDate(LocalDate hireDate) {
            this.hireDate = hireDate;
            return this;
        }

        public Builder employmentType(EmploymentType employmentType) {
            this.employmentType = employmentType;
            return this;
        }

        public Builder currencyCode(String currencyCode) {
            this.currencyCode = currencyCode;
            return this;
        }

        public Builder departmentId(String departmentId) {
            this.departmentId = departmentId;
            return this;
        }

        public Builder positionId(String positionId) {
            this.positionId = positionId;
            return this;
        }

        public Builder managerId(String managerId) {
            this.managerId = managerId;
            return this;
        }

        public Builder salary(String salary) {
            this.salary = salary;
            return this;
        }

        public CreateEmployeeCommand build() {
            if (employeeId == null) {
                employeeId = EmployeeId.generate();
            }
            if (employeeNumber == null) {
                employeeNumber = "EMP-" + System.currentTimeMillis();
            }
            if (hireDate == null) {
                hireDate = LocalDate.now();
            }
            return new CreateEmployeeCommand(
                employeeId, employeeNumber, firstName, lastName,
                email, hireDate, employmentType, currencyCode,
                departmentId, positionId, managerId, salary
            );
        }
    }
}
```

**`/modules/employee/application/src/main/java/tech/kayys/erp/employee/application/api/command/CreateLeaveRequestCommand.java`**:

```java
package tech.kayys.erp.employee.application.api.command;

import tech.kayys.erp.foundation.application.Command;
import tech.kayys.erp.employee.domain.identifier.LeaveRequestId;
import tech.kayys.erp.employee.domain.valueobject.LeaveType;

import java.time.LocalDate;
import java.util.UUID;

/**
 * Command to create a leave request.
 */
public record CreateLeaveRequestCommand(
        LeaveRequestId leaveRequestId,
        UUID employeeId,
        LeaveType leaveType,
        LocalDate startDate,
        LocalDate endDate,
        String reason
) implements Command<LeaveRequestId> {

    public CreateLeaveRequestCommand {
        if (employeeId == null) {
            throw new IllegalArgumentException("Employee ID cannot be null");
        }
        if (leaveType == null) {
            throw new IllegalArgumentException("Leave type is required");
        }
        if (startDate == null) {
            throw new IllegalArgumentException("Start date is required");
        }
        if (endDate == null) {
            throw new IllegalArgumentException("End date is required");
        }
        if (endDate.isBefore(startDate)) {
            throw new IllegalArgumentException("End date must be after start date");
        }
        if (reason == null || reason.trim().isEmpty()) {
            throw new IllegalArgumentException("Reason is required");
        }
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private LeaveRequestId leaveRequestId;
        private UUID employeeId;
        private LeaveType leaveType;
        private LocalDate startDate;
        private LocalDate endDate;
        private String reason;

        public Builder leaveRequestId(LeaveRequestId leaveRequestId) {
            this.leaveRequestId = leaveRequestId;
            return this;
        }

        public Builder employeeId(UUID employeeId) {
            this.employeeId = employeeId;
            return this;
        }

        public Builder leaveType(LeaveType leaveType) {
            this.leaveType = leaveType;
            return this;
        }

        public Builder startDate(LocalDate startDate) {
            this.startDate = startDate;
            return this;
        }

        public Builder endDate(LocalDate endDate) {
            this.endDate = endDate;
            return this;
        }

        public Builder reason(String reason) {
            this.reason = reason;
            return this;
        }

        public CreateLeaveRequestCommand build() {
            if (leaveRequestId == null) {
                leaveRequestId = LeaveRequestId.generate();
            }
            return new CreateLeaveRequestCommand(
                leaveRequestId, employeeId, leaveType, startDate, endDate, reason
            );
        }
    }
}
```

**`/modules/employee/application/src/main/java/tech/kayys/erp/employee/application/api/query/EmployeeView.java`**:

```java
package tech.kayys.erp.employee.application.api.query;

import tech.kayys.erp.employee.domain.model.Employee;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

/**
 * View of an employee.
 */
public record EmployeeView(
        String employeeId,
        String employeeNumber,
        String firstName,
        String lastName,
        String fullName,
        String email,
        String phone,
        String mobile,
        String address,
        String city,
        String state,
        String postalCode,
        String country,
        LocalDate dateOfBirth,
        int age,
        LocalDate hireDate,
        double tenure,
        String status,
        String employmentType,
        String departmentId,
        String positionId,
        String managerId,
        String salary,
        String currencyCode,
        List<SkillView> skills,
        List<CertificationView> certifications,
        List<EducationView> education,
        boolean active
) {

    public static EmployeeView fromDomain(Employee employee) {
        return new EmployeeView(
            employee.getId().toString(),
            employee.getEmployeeNumber(),
            employee.getFirstName(),
            employee.getLastName(),
            employee.getFullName(),
            employee.getEmail(),
            employee.getPhone(),
            employee.getMobile(),
            employee.getAddress(),
            employee.getCity(),
            employee.getState(),
            employee.getPostalCode(),
            employee.getCountry(),
            employee.getDateOfBirth(),
            employee.getAge(),
            employee.getHireDate(),
            employee.getTenure(),
            employee.getStatus().name(),
            employee.getEmploymentType().name(),
            employee.getDepartmentId() != null ? employee.getDepartmentId().toString() : null,
            employee.getPositionId() != null ? employee.getPositionId().toString() : null,
            employee.getManagerId() != null ? employee.getManagerId().toString() : null,
            employee.getSalary() != null ? employee.getSalary().getAmount().toPlainString() : "0.00",
            employee.getCurrencyCode(),
            employee.getSkills().stream()
                .map(SkillView::fromDomain)
                .collect(Collectors.toList()),
            employee.getCertifications().stream()
                .map(CertificationView::fromDomain)
                .collect(Collectors.toList()),
            employee.getEducation().stream()
                .map(EducationView::fromDomain)
                .collect(Collectors.toList()),
            employee.isActive()
        );
    }

    public record SkillView(
            String skillName,
            String skillLevel,
            int yearsExperience,
            boolean verified
    ) {
        public static SkillView fromDomain(Employee.EmployeeSkill skill) {
            return new SkillView(
                skill.getSkillName(),
                skill.getSkillLevel(),
                skill.getYearsExperience(),
                skill.isVerified()
            );
        }
    }

    public record CertificationView(
            String certificationName,
            String issuingAuthority,
            LocalDate issueDate,
            LocalDate expiryDate,
            String certificationNumber,
            boolean verified,
            boolean valid
    ) {
        public static CertificationView fromDomain(Employee.EmployeeCertification cert) {
            return new CertificationView(
                cert.getCertificationName(),
                cert.getIssuingAuthority(),
                cert.getIssueDate(),
                cert.getExpiryDate(),
                cert.getCertificationNumber(),
                cert.isVerified(),
                cert.isValid()
            );
        }
    }

    public record EducationView(
            String degree,
            String field,
            String institution,
            LocalDate startDate,
            LocalDate endDate,
            boolean completed
    ) {
        public static EducationView fromDomain(Employee.EmployeeEducation education) {
            return new EducationView(
                education.getDegree(),
                education.getField(),
                education.getInstitution(),
                education.getStartDate(),
                education.getEndDate(),
                education.isCompleted()
            );
        }
    }
}
```

**`/modules/employee/application/src/main/java/tech/kayys/erp/employee/application/api/query/LeaveRequestView.java`**:

```java
package tech.kayys.erp.employee.application.api.query;

import tech.kayys.erp.employee.domain.model.LeaveRequest;

import java.time.Instant;
import java.time.LocalDate;

/**
 * View of a leave request.
 */
public record LeaveRequestView(
        String leaveRequestId,
        String employeeId,
        String leaveType,
        String status,
        LocalDate startDate,
        LocalDate endDate,
        double days,
        String reason,
        String approvedBy,
        Instant approvedAt,
        String rejectedBy,
        Instant rejectedAt,
        String rejectionReason,
        String notes,
        boolean active
) {

    public static LeaveRequestView fromDomain(LeaveRequest request) {
        return new LeaveRequestView(
            request.getId().toString(),
            request.getEmployeeId().toString(),
            request.getLeaveType().name(),
            request.getStatus().name(),
            request.getStartDate(),
            request.getEndDate(),
            request.getDays(),
            request.getReason(),
            request.getApprovedBy(),
            request.getApprovedAt(),
            request.getRejectedBy(),
            request.getRejectedAt(),
            request.getRejectionReason(),
            request.getNotes(),
            request.isActive()
        );
    }
}
```

## 3. Update Root POM

**Update `/pom.xml`** to include Employee modules:

```xml
<modules>
    <!-- Foundation -->
    <module>foundation/domain</module>
    <module>foundation/application</module>
    <module>foundation/reactive-mutiny</module>

    <!-- Architecture Tests -->
    <module>architecture/tests</module>

    <!-- Business Modules -->
    <module>modules/catalog/domain</module>
    <module>modules/catalog/application</module>
    <module>modules/catalog/infrastructure</module>
    <module>modules/catalog/interfaces</module>

    <module>modules/sales/domain</module>
    <module>modules/sales/application</module>
    <module>modules/sales/infrastructure</module>
    <module>modules/sales/interfaces</module>

    <module>modules/pricing/domain</module>
    <module>modules/pricing/application</module>
    <module>modules/pricing/infrastructure</module>
    <module>modules/pricing/interfaces</module>

    <module>modules/subscription/domain</module>
    <module>modules/subscription/application</module>
    <module>modules/subscription/infrastructure</module>
    <module>modules/subscription/interfaces</module>

    <module>modules/accounting/domain</module>
    <module>modules/accounting/application</module>
    <module>modules/accounting/infrastructure</module>
    <module>modules/accounting/interfaces</module>

    <module>modules/purchasing/domain</module>
    <module>modules/purchasing/application</module>
    <module>modules/purchasing/infrastructure</module>
    <module>modules/purchasing/interfaces</module>

    <module>modules/promotion/domain</module>
    <module>modules/promotion/application</module>
    <module>modules/promotion/infrastructure</module>
    <module>modules/promotion/interfaces</module>

    <module>modules/employee/domain</module>
    <module>modules/employee/application</module>
    <module>modules/employee/infrastructure</module>
    <module>modules/employee/interfaces</module>
</modules>
```

## Summary

The complete Employee/HR bounded context provides:

1. **Employee Management**:
   - Full employee lifecycle (create, activate, terminate, leave)
   - Employment types and status tracking
   - Skills, certifications, and education tracking
   - Employment history
   - Organizational structure (departments, positions, managers)

2. **Leave Management**:
   - Multiple leave types (annual, sick, maternity, etc.)
   - Full workflow (request → approve/reject → taken/cancelled)
   - Leave balance tracking
   - Working days calculation

3. **HR Operations**:
   - Employee promotion and transfer
   - Salary management
   - Employee reporting and analytics
   - Tenure and age calculations

4. **Integration Points**:
   - Payroll integration (salary, bank details)
   - Department and position management
   - Manager hierarchy

5. **Architecture Rules**:
   - Clean domain model
   - Proper value objects
   - State machine enforcement
   - Context isolation

This completes the Employee/HR context with comprehensive employee management and leave tracking capabilities that integrate with Payroll, Accounting, and other contexts throughout the ERP system.