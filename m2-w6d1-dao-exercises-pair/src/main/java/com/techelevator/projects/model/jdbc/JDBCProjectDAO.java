package com.techelevator.projects.model.jdbc;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;

import com.techelevator.projects.model.Department;
import com.techelevator.projects.model.Employee;
import com.techelevator.projects.model.Project;
import com.techelevator.projects.model.ProjectDAO;

public class JDBCProjectDAO implements ProjectDAO {

	private JdbcTemplate jdbcTemplate;

	public JDBCProjectDAO(DataSource dataSource) {
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}
	
	@Override
	public List<Project> getAllActiveProjects() {
		ArrayList<Project> projects = new ArrayList<>();
		String sqlFindAllProjects = "SELECT * FROM project";
		SqlRowSet results = jdbcTemplate.queryForRowSet(sqlFindAllProjects);
		while(results.next()) {
			Project theProject = mapRowToProject(results);
			projects.add(theProject);
		} return projects;	
	}

	@Override
	public void removeEmployeeFromProject(Long projectId, Long employeeId) {
		String removeEmployeeFromProject = "DELETE FROM project_employee WHERE employee_id =? AND project_id = ?";
		jdbcTemplate.update(removeEmployeeFromProject, employeeId, projectId);
	}

	@Override
	public void addEmployeeToProject(Long projectId, Long employeeId) {
		String addEmployeeToProject = "UPDATE project_employee Set project_id = ? WHERE employee_id = ?";
		jdbcTemplate.update(addEmployeeToProject, projectId, employeeId);
	}

	
	private Project mapRowToProject(SqlRowSet results) {
		Project theProject;
		theProject = new Project();
		theProject.setId(results.getLong("project_id"));
		theProject.setName(results.getString("name"));
		if(results.getDate("from_date") != null) {
		theProject.setStartDate(results.getDate("from_date").toLocalDate());
		} 
		if(results.getDate("to_date") != null) {
		theProject.setEndDate(results.getDate("to_date").toLocalDate());
		}
		return theProject;
	}
	
	public Employee createEmployee(String firstName, String lastName, LocalDate birthDate, String gender, LocalDate hireDate, long departmentId) {
		String sqlCreateEmployee = "INSERT INTO employee (first_name, last_name, birth_date, gender, hire_date, department_id) VALUES (?, ?, ?, ?, ?, ?)";
		jdbcTemplate.update(sqlCreateEmployee, firstName, lastName, birthDate, gender, hireDate, departmentId);
		
		String sqlPullCreatedEmployee = "SELECT * FROM employee WHERE first_name = ?";
		SqlRowSet results = jdbcTemplate.queryForRowSet(sqlPullCreatedEmployee, firstName);
		List<Employee> listOfEmployees = new ArrayList<>();
		Employee theEmployee = null;
		
		while(results.next()) {	
			theEmployee = new Employee();
			theEmployee.setId(results.getLong("employee_id"));
			theEmployee.setFirstName(results.getString("first_name"));
			theEmployee.setLastName(results.getString("last_name"));		
			theEmployee.setGender(results.getString("gender").charAt(0));
			theEmployee.setHireDate(results.getDate("hire_date").toLocalDate());
			theEmployee.setDepartmentId(results.getLong("department_id"));
			theEmployee.setBirthDay(results.getDate("birth_date").toLocalDate());
			listOfEmployees.add(theEmployee);
		} return theEmployee;
		
	}

}



