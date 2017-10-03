package com.techelevator.projects.model.jdbc;

import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;

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

}



