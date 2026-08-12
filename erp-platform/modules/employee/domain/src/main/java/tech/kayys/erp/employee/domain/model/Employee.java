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