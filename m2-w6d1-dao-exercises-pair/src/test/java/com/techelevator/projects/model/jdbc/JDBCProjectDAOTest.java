package com.techelevator.projects.model.jdbc;

import static org.junit.Assert.*;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.SingleConnectionDataSource;
import org.springframework.jdbc.support.rowset.SqlRowSet;

import com.techelevator.projects.model.Department;
import com.techelevator.projects.model.Employee;
import com.techelevator.projects.model.Project;

public class JDBCProjectDAOTest {
	
	private static SingleConnectionDataSource dataSource;
	private JDBCProjectDAO dao;
	private JdbcTemplate jdbcTemplate;
	private JDBCEmployeeDAO employeeDAO;
	private long eid;
	private long pid1;
	private long pid2;
	
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
		
		pid1 = jdbcTemplate.queryForObject("INSERT INTO project (name, from_date, to_date) VALUES ('Sin', '2011-11-11', '2012-12-12') RETURNING project_id", long.class);
		pid2 = jdbcTemplate.queryForObject("INSERT INTO project (name, from_date, to_date) VALUES ('Shame', '2013-08-14', '2014-09-15') RETURNING project_id", long.class);
		eid = jdbcTemplate.queryForObject("INSERT INTO employee (first_name, last_name, birth_date, gender, hire_date) VALUES ('Jarred', 'Kulich', '2000-01-01', 'F', '1969-4-20') RETURNING employee_id", long.class);
		
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
		assertEquals(2, numberOfActiveProjects);
	}


	@Test
	public void testRemoveEmployeeFromProject() {
		
		Employee aaRon = dao.createEmployee(1L, "AA Ron", "Vail", LocalDate.parse("1981-01-03"), 'M', LocalDate.parse("2017-10-04"));
		Project testProject = dao.createProject("project 1", LocalDate.parse("2017-08-28"), LocalDate.parse("2017-12-01"));
		dao.addEmployeeToProject(testProject.getId(), aaRon.getId());
		
		assertNotNull(aaRon.getId());
		assertNotNull(testProject.getId());
		
		dao.removeEmployeeFromProject(testProject.getId(),  aaRon.getId());
		List<Employee> results = employeeDAO.getEmployeesByProjectId(testProject.getId());
		
		assertEquals(true, results.isEmpty());

		
		jdbcTemplate.update("INSERT INTO department (department_id, name) VALUES (1, 'test department')");
		List<Employee> empList = new ArrayList<>();
		empList.add(dao.createEmployee((long)1, "Jared", "Awesome", LocalDate.parse("1992-10-03"), 'F', LocalDate.parse("2017-10-03")));
		empList.add(dao.createEmployee((long)1, "AA Ron", "Bro", LocalDate.parse("1982-10-03"), 'M', LocalDate.parse("2015-10-03")));
		
		long tempEmployee = empList.get(0).getId();
		dao.removeEmployeeFromProject(pid1, tempEmployee);
		List<Project> projectList =dao.getAllActiveProjects();
		
		assertEquals(1,1);
	}

	@Test
	public void testAddEmployeeToProject() {
		jdbcTemplate.update("INSERT INTO department (department_id, name) VALUES (1, 'test department')");
		List<Employee> empList = new ArrayList<>();
		empList.add(dao.createEmployee((long)1, "Jared", "Awesome", LocalDate.parse("1992-10-03"), 'F', LocalDate.parse("2017-10-03")));
		empList.add(dao.createEmployee((long)1, "AA Ron", "Bro", LocalDate.parse("1982-10-03"), 'M', LocalDate.parse("2015-10-03")));
				
		long tempEmployee = empList.get(0).getId();		
		dao.addEmployeeToProject(pid1, tempEmployee);
	
		assertEquals("Sin", dao.getAllActiveProjects().get(0).getName());
	}

}
