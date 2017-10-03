package com.techelevator.projects.model.jdbc;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;



import javax.sql.DataSource;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;

import com.techelevator.projects.model.Department;
import com.techelevator.projects.model.Employee;
import com.techelevator.projects.model.EmployeeDAO;

public class JDBCEmployeeDAO implements EmployeeDAO {

	private JdbcTemplate jdbcTemplate;

	public JDBCEmployeeDAO(DataSource dataSource) {
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}
	
	@Override
	public List<Employee> getAllEmployees() {
		ArrayList<Employee> employees = new ArrayList<>();
		String sqlFindAllEmployees = "SELECT * "+"FROM employee";
		SqlRowSet results = jdbcTemplate.queryForRowSet(sqlFindAllEmployees, "name");
		while(results.next()) {
			Employee theEmployee = mapRowToEmployee(results);
			employees.add(theEmployee);
		} return employees;	
	}

	@Override
	public List<Employee> searchEmployeesByName(String firstNameSearch, String lastNameSearch) {
		ArrayList<Employee> employees = new ArrayList<>();
		String sqlFindEmployeesByName = "SELECT name FROM department WHERE first_name =? AND last_name =?";
		SqlRowSet results = jdbcTemplate.queryForRowSet(sqlFindEmployeesByName, firstNameSearch, lastNameSearch);
		while(results.next()) {
			Employee theEmployee = mapRowToEmployee(results);
			employees.add(theEmployee);
		} return employees;
	}

	@Override
	public List<Employee> getEmployeesByDepartmentId(long id) {
		List<Employee> listOfEmployeeIds = new ArrayList<>();
		String sqlSelectEmployeeIdsByDepartment = "SELECT * FROM employee WHERE department_id =?";
		SqlRowSet results = jdbcTemplate.queryForRowSet(sqlSelectEmployeeIdsByDepartment, id);
		while(results.next()) {
			listOfEmployeeIds.add(mapRowToEmployee(results));
		} return listOfEmployeeIds;
	}

	@Override
	public List<Employee> getEmployeesWithoutProjects() {
		List<Employee> listOfEmployeeIds = new ArrayList<>();
		String sqlSelectEmployeeIdsWithoutProjects = "SELECT * FROM employee WHERE project_id IS NULL";
		SqlRowSet results = jdbcTemplate.queryForRowSet(sqlSelectEmployeeIdsWithoutProjects);
		while(results.next()) {
			listOfEmployeeIds.add(mapRowToEmployee(results));
		} return listOfEmployeeIds;	
	}

	@Override
	public List<Employee> getEmployeesByProjectId(Long projectId) {
		List<Employee> listOfEmployeeIds = new ArrayList<>();
		String sqlSelectEmployeeIdsWithProjects = "SELECT * FROM employee WHERE project_id IS NOT NULL";
		SqlRowSet results = jdbcTemplate.queryForRowSet(sqlSelectEmployeeIdsWithProjects);
		while(results.next()) {
			listOfEmployeeIds.add(mapRowToEmployee(results));
		} return listOfEmployeeIds;	
	}

	@Override
	public void changeEmployeeDepartment(Long employeeId, Long departmentId) {
		String changeEmployeesDepartment = "UPDATE employee SET deparment_id = ? WHERE employee_id = ?";
		jdbcTemplate.update(changeEmployeesDepartment, departmentId, employeeId);
	}
	
	private Employee mapRowToEmployee(SqlRowSet results) {
		Employee theEmployee;
		theEmployee = new Employee();
		theEmployee.setId(results.getLong("id"));
		theEmployee.setFirstName(results.getString("first_name"));
		theEmployee.setLastName(results.getString("last_name"));		
		theEmployee.setGender(results.getString("gender").charAt(0));
		theEmployee.setHireDate(results.getDate("hire_date").toLocalDate());
		theEmployee.setDepartmentId(results.getLong("department_id"));
		theEmployee.setBirthDay(results.getDate("birth_date").toLocalDate());
		
		return theEmployee;
	}

}
