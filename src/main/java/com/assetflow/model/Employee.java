
package com.assetflow.model;

public class Employee {

    private int employeeId;
    private String employeeCode;
    private String employeeName;
    private String department;
    private String designation;
    private String email;
    private String employmentStatus;

    public Employee(int employeeId, String employeeCode,
                    String employeeName, String department,
                    String designation, String email,
                    String employmentStatus) {
        this.employeeId = employeeId;
        this.employeeCode = employeeCode;
        this.employeeName = employeeName;
        this.department = department;
        this.designation = designation;
        this.email = email;
        this.employmentStatus = employmentStatus;
    }

    public int getEmployeeId() {
        return employeeId;
    }

    public String getEmployeeCode() {
        return employeeCode;
    }

    public String getEmployeeName() {
        return employeeName;
    }

    public String getDepartment() {
        return department;
    }

    public String getDesignation() {
        return designation;
    }

    public String getEmail() {
        return email;
    }

    public String getEmploymentStatus() {
        return employmentStatus;
    }
}

