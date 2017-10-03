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
		jdbcTemplate.execute("INSERT INTO project(first_name, last_name, birth_date, gender, hire_date) VALUES ('John', 'Doe', '2000-01-01', 'M', '1999-09-10')");
		jdbcTemplate.execute("INSERT INTO project(first_name, last_name, birth_date, gender, hire_date) VALUES ('Jarred', 'Kulich', '2000-01-01', 'F', '1969-4-20')");

		dao = new JDBCEmployeeDAO(dataSource);
	}

	/* After each test, we rollback any changes that were made to the database so that
	 * everything is clean for the next test */
	@After
	public void rollback() throws SQLException {
		dataSource.getConnection().rollback();
	}	
	public void testGetAllEmployees() {
		int numberOfExistingEmployees = dao.getAllEmployees().size();
	//	String empName = "MY NEW TEST DEPT";
	//	Employee newEmp = dao.createEmployee (empName);
	//	String empName2 = "THE SECOND ONE";
	//	Employee newEmp2 = dao.createEmployee(empName2);
		
		List<Employee> empList =dao.getAllEmployees();
		
		assertNotNull(empList);
		assertEquals(empList.size(), 2 + numberOfExistingEmployees);
	}
	

}
