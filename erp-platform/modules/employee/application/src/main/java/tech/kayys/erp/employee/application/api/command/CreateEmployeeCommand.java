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