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