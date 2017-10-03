package com.techelevator.projects.model.jdbc;

import static org.junit.Assert.*;

import java.sql.SQLException;
import java.util.List;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.SingleConnectionDataSource;

import com.techelevator.projects.model.Department;
import com.techelevator.projects.model.Project;

public class JDBCProjectDAOTest {
	
	private static SingleConnectionDataSource dataSource;
	private JDBCProjectDAO dao;
	private JdbcTemplate jdbcTemplate;
	
	/* Before any tests are run, this method initializes the datasource for testing. runs one time in class*/ 
	@BeforeClass
	public static void setupDataSource() {
		dataSource = new SingleConnectionDataSource();
		dataSource.setUrl("jdbc:postgresql://localhost:5432/projects");
		dataSource.setUsername("postgres");
		dataSource.setPassword("postgres1");
		/* The following line disables autocommit for connections 
		 * returned by this DataSource. This allows us to rollback
		 * any changes after each test */
		dataSource.setAutoCommit(false);
	}
	
	/* After all tests have finished running, this method will close the DataSource runs once and only once after / cleans up any connections*/
	@AfterClass
	public static void closeDataSource() throws SQLException {
		dataSource.destroy();
	}

	
	@Before
	public void setup() {
		jdbcTemplate = new JdbcTemplate(dataSource);
		jdbcTemplate.update("DELETE FROM project_employee");
		jdbcTemplate.update("DELETE FROM employee");
		jdbcTemplate.update("DELETE FROM department");
		jdbcTemplate.update("DELETE FROM project");
		jdbcTemplate.execute("INSERT INTO project(name, from_date, to_date) VALUES ('Sin', '2011-11-11', '2012-12-12')");
		jdbcTemplate.execute("INSERT INTO project(name, from_date, to_date) VALUES ('Shame', '2013-08-14', '2014-09-15')");

		dao = new JDBCProjectDAO(dataSource);
	}

	/* After each test, we rollback any changes that were made to the database so that
	 * everything is clean for the next test */
	@After
	public void rollback() throws SQLException {
		dataSource.getConnection().rollback();
	}	
	
	@Test
	public void testGetAllActiveProjects() {
		int numberOfActiveProjects = dao.getAllActiveProjects().size();

		List<Project> projectList =dao.getAllActiveProjects();

		assertNotNull(projectList);
		assertEquals(projectList.size(), numberOfActiveProjects);
	}


	@Test
	public void testRemoveEmployeeFromProject() {
		dao.removeEmployeeFromProject(projectId, employeeId);
		
	}

	@Test
	public void testAddEmployeeToProject() {
		dao.addEmployeeToProject(1L, 1L);
		
		List<Project> projectList =dao.getAllActiveProjects();

		assertNotNull(projectList); dao.
		assertEauals(1L, )
	}

}
