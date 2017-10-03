package com.techelevator.projects.model.jdbc;

import static org.junit.Assert.*;

import java.sql.SQLException;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.SingleConnectionDataSource;
import org.springframework.jdbc.support.rowset.SqlRowSet;

import com.techelevator.projects.model.Department;

public class JDBCDepartmenDAOTest {
	
	private static SingleConnectionDataSource dataSource;
	private JDBCDepartmentDAO dao;
	private JdbcTemplate jdbcTemplate;
	
	/* Before any tests are run, this method initializes the datasource for testing. */
	@BeforeClass
	public static void setupDataSource() {
		dataSource = new SingleConnectionDataSource();
		dataSource.setUrl("jdbc:postgresql://localhost:5432/world");
		dataSource.setUsername("postgres");
		dataSource.setPassword("postgres1");
		/* The following line disables autocommit for connections 
		 * returned by this DataSource. This allows us to rollback
		 * any changes after each test */
		dataSource.setAutoCommit(false);
	}
	
	/* After all tests have finished running, this method will close the DataSource */
	@AfterClass
	public static void closeDataSource() throws SQLException {
		dataSource.destroy();
	}

	@Before
	public void setup() {
		JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
		jdbcTemplate.update("DELETE FROM project_employee");
		jdbcTemplate.update("DELETE FROM employee");
		jdbcTemplate.update("DELETE FROM department");
		
		dao = new JDBCDepartmentDAO(dataSource);
	}
	
	@After
	public void rollback() throws SQLException {
		dataSource.getConnection().rollback();
	}
	
	@Test
	public void testGetAllDepartments() {
		fail("Not yet implemented");
	}

	@Test
	public void testSearchDepartmentsByName() {
		fail("Not yet implemented");
	}

	@Test
	public void testUpdateDepartmentName() {
		fail("Not yet implemented");
	}

	@Test
	public void testCreateDepartment() {
		String deptName = "MY NEW TEST DEPARTMENT";
		Department newDept = dao.createDepartment(deptName);
		
		assertNotNull(newDept);
		SqlRowSet results = jdbcTemplate.queryForRowSet("SELECT * FROM department");
		assertTrue("There were no departments in the database", results.next());
		assertEquals(deptName, results.getString("name"));
		assertEquals(newDept.getId(), (Long)results.getLong("department_id"));
		assertFalse("Too many rows", results.next());
	}

	@Test
	public void testGetDepartmentById() {
		fail("Not yet implemented");
	}

}
