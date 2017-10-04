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

import com.techelevator.projects.model.Employee;

public class JDBCEmployeeDAOTest {
	private static SingleConnectionDataSource dataSource;
	private JDBCEmployeeDAO dao;
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
		jdbcTemplate.update("INSERT INTO department (department_id, name) VALUES (1, 'test department')");
		jdbcTemplate.update("INSERT INTO department (department_id, name) VALUES (2, 'test department 2')");
//		jdbcTemplate.execute("INSERT INTO employee(first_name, last_name, birth_date, gender, hire_date) VALUES ('John', 'Doe', '2000-01-01', 'M', '1999-09-10')");
//		jdbcTemplate.execute("INSERT INTO employee(first_name, last_name, birth_date, gender, hire_date) VALUES ('Jarred', 'Kulich', '2000-01-01', 'F', '1969-4-20')");

		dao = new JDBCEmployeeDAO(dataSource);
	}

	/* After each test, we rollback any changes that were made to the database so that
	 * everything is clean for the next test */
	@After
	public void rollback() throws SQLException {
		dataSource.getConnection().rollback();
	}	
	@Test
	public void testGetAllEmployees() {
		List<Employee> empList = new ArrayList<>();
		empList.add(dao.createEmployee("Jared", "Awesome", LocalDate.parse("1992-10-03"), "F", LocalDate.parse("2017-10-03"), 1));
		
		int numberOfExistingEmployees = dao.getAllEmployees().size();
		
		assertNotNull(empList);
		assertEquals(1, dao.getAllEmployees().size());
		assertEquals(empList.size(), numberOfExistingEmployees);
	}
	

	@Test
	public void testSearchEmployeesByName() {
		List<Employee> empList = new ArrayList<>();
		empList.add(dao.createEmployee("Jared", "Awesome", LocalDate.parse("1992-10-03"), "F", LocalDate.parse("2017-10-03"), 1));
		
		assertEquals("Jared", empList.get(0).getFirstName());
		assertEquals("Awesome", empList.get(0).getLastName());
	}

	@Test
	public void testGetEmployeesByDepartmentId() {
		List<Employee> empList = new ArrayList<>();
		empList.add(dao.createEmployee("Jared", "Awesome", LocalDate.parse("1992-10-03"), "F", LocalDate.parse("2017-10-03"), 1));
		empList.add(dao.createEmployee("AA Ron", "Bro", LocalDate.parse("1982-10-03"), "M", LocalDate.parse("2015-10-03"), 1));
				
		assertEquals(1, empList.get(0).getDepartmentId());
		assertEquals("Jared", empList.get(0).getFirstName());
		assertEquals("Awesome", empList.get(0).getLastName());
	}

	@Test
	public void testGetEmployeesWithoutProjects() {
		List<Employee> empListWithOutProject = new ArrayList<>();
		List<Employee> empList = new ArrayList<>();
		
		empListWithOutProject.add(dao.createEmployee("Jared", "Awesome", LocalDate.parse("1992-10-03"), "F", LocalDate.parse("2017-10-03"), 1));
		empList.add(dao.createEmployee("AA Ron", "Bro", LocalDate.parse("1982-10-03"), "M", LocalDate.parse("2015-10-03"), 1));
		
	
		Long number = empList.get(0).getId();
		jdbcTemplate.update("INSERT INTO project_employee (project_id, employee_id) VALUES (1, ?)", number);
		
		
		assertEquals(1, dao.getEmployeesWithoutProjects().size());
		
		
	}

	@Test
	public void testGetEmployeesByProjectId() {
		List<Employee> empListWithOutProject = new ArrayList<>();
		List<Employee> empList = new ArrayList<>();
		
		empListWithOutProject.add(dao.createEmployee("Jared", "Awesome", LocalDate.parse("1992-10-03"), "F", LocalDate.parse("2017-10-03"), 1));
		empList.add(dao.createEmployee("AA Ron", "Bro", LocalDate.parse("1982-10-03"), "M", LocalDate.parse("2015-10-03"), 1));
		
		Long aaronsEID = empList.get(0).getId();
		jdbcTemplate.update("INSERT INTO project_employee (project_id, employee_id) VALUES (1, ?)", aaronsEID);
		
		
		assertEquals(1, dao.getEmployeesByProjectId((long) 1).size());	
	}

	@Test
	public void testChangeEmployeeDepartment() {
		List<Employee> empList = new ArrayList<>();		
		empList.add(dao.createEmployee("AA Ron", "Bro", LocalDate.parse("1982-10-03"), "M", LocalDate.parse("2015-10-03"), 1));
		Long aaronsEID = empList.get(0).getId();
		Long deptNumber = (long)2;
		
		
		dao.changeEmployeeDepartment(aaronsEID, deptNumber);
		
		assertEquals("AA Ron", dao.getEmployeesByDepartmentId((long)2).get(0).getFirstName());	
	}

}
