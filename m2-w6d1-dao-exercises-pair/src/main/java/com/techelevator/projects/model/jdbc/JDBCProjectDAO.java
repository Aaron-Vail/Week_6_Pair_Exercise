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
	
	public Project createProject(String name, LocalDate fromDate, LocalDate toDate) {
		Project project = new Project();
		project.setName(name);
		project.setStartDate(fromDate);
		project.setEndDate(toDate);
		String sqlCreateProject = "INSERT INTO project(name, from_date, to_date)"
															+ "VALUES (?, ?, ?) RETURNING project_id";
		project.setId(jdbcTemplate.queryForObject(sqlCreateProject, Long.class, name, fromDate, toDate));
		return project;
		
	}
	
	public Employee createEmployee(Long employeeId, String firstName, String lastName, LocalDate birthDate, char gender, LocalDate hireDate) {
		Employee employee = new Employee();
		employee.setFirstName(firstName);
		employee.setLastName(lastName);
		employee.setBirthDay(birthDate);
		employee.setGender(gender);
		employee.setHireDate(hireDate);
		String sqlCreateEmployee = "INSERT INTO employee(first_name, last_name, birth_date, gender, hire_date)"
															+ "VALUES (?, ?, ?, ?, ?) RETURNING employee_id";
		employee.setId(jdbcTemplate.queryForObject(sqlCreateEmployee,  Long.class, firstName, lastName, birthDate, gender, hireDate));
		return employee;
		
	}

}



