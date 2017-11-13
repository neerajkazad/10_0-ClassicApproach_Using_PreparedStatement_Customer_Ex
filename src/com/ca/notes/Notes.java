package com.ca.notes;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCallback;
import org.springframework.jdbc.core.PreparedStatementCreator;

import com.ca.bo.CustomerBo;
import com.ca.dao.CustomerDao;

public class Notes {
	/**
	 * This is the program that tells how we can use the Spring Jdbc Classic Approach.
	 * Steps for Developing this example:
	 * 1) Create the packages
	 * com.ca.dao //now i'm not going to write the code in test class, go and write it in dao
	 * and test class is going to call to dao class.
	 * 
	 * com.ca.bo //business object for wrapping the data and returning the data for any business object
	 * 
	 * com.ca.common //to write information related to class and gives the information to IOC Container
	 * 
	 * 2) Write Coding Start with Dao class
	 * CustomerDao class
	 * so in my CustomerDao class i have to perform the database operation and to perform the database 
	 * operation we need JdbcTemplate so without the JdbcTemplate we can't perform the database operation,
	 * so how we need to talk to JdbcTemplate, go for composition, declare JdbcTemplate as an attribute.
	 * 
	 * public class CustomerDao {
	 *    private JdbcTemplate jdbcTemplate;
	 * }
	 * Now how do i need to have the jdbcTemplate in the CustomerDao, inject jdbcTemplate in the CustomerDao
	 * as constructor argument
	 *
	 * public class CustomerDao {
	 *  private JdbcTemplate jdbcTemplate;
     *
     * 	public CustomerDao(JdbcTemplate jdbcTemplate) {
	 *	  this.jdbcTemplate = jdbcTemplate;
	 *   }   
	 * }
	 * 
	 * But to use the jdbcTemplate it need DataSource, where it needs, and go and takes the connection from
	 * DataSource, means i need to inject DataSource with the JdbcTemplate, so how i need to create DataSource 	 
	 * 
	 * write the SpringBeanConfigurationFile(application-context.xml) and configure DriverManagerDataSource class
	 * as bean
	 * 
	 * 3) application-context.xml
	 * <bean id="dataSource" class="org.springframework.jdbc.datasource.DriverManagerDataSource">
		<property name="driverClassName" value="com.mysql.jdbc.Driver"></property>
		<property name="url" value="jdbc:mysql://localhost:3306/sdb"></property>
		<property name="username" value="root"></property>
		<property name="password" value="root"></property>
	   </bean>
	   
	   What next i need JdbcTemplate, so make this as bean also in SpringBeanConfiguration File.
	   
	   <bean id="jdbcTemplate" class="org.springframework.jdbc.core.JdbcTemplate">
		<property name="dataSource" ref="dataSource"></property>
	   </bean>
	 * 
	 * What the next things i need to have CustomerDao, so Configure this also as a bean.
	 * 
	 * <bean id="customerDao" class="com.ca.dao.CustomerDao">
		<constructor-arg ref="jdbcTemplate"></constructor-arg>
	   </bean>
	 *  
	 * 4) So lets open the mysql and create the customer table in sdb database. 
	 * customer
	 * customer_no, first_nm, last_nm, mobile,email_address
	 * 
	 * Just now we created database and table with the data, so now we start writting the logic for reading the 
	 * data from database table. 
	 * 
	 * I have the CustomerDao and I have the JdbcTemplate, now i wanted to get all the customer byName
	 * public List<CustomerBO> that mean so first go and create the BO
	 * 
	 * 5) CustomerBO
	 *  public class CustomerBO {
			protected int customerNo;
			protected String firstName;
			protected String lastName;
			protected String mobile;
			protected String emailAddress;
			
			// accessor method
			// toString()
	*
	* Now i have CustomerBO in which i can populate the data/record of data from the database table into an object
	* so lets start writing the method.
	* public List<CustomerBO> getCustomers(String firstName) {
	* 
	* }
	* 
	* write two classes where in write as a inner classes because these classes is not going to use by someone,
	* only CustomerDao class is going to use that's where write two inner class inside the CustomerDao
	* GetCustomersPreparedStatementCreator and GetCustomersPreparedStatementCallback
	*
	* private final class GetCustomersPreparedStatementCreator implements PreparedStatementCreator {
		private String firstName; //my method can not accept as a parameter that's why we declare as a attribute
		//Why i'm declaring as an attribute? because the method cannot accept as an parameter, so the only
		//way the getting the data as a input is the attribute, so take it as a constructor argument 

		public GetCustomersPreparedStatementCreator(String firstName) {
			this.firstName = firstName;
		}

		@Override
		public PreparedStatement createPreparedStatement(Connection conn) throws SQLException {
			PreparedStatement pstmt = null;
			pstmt = conn.prepareStatement("select * from customer where first_nm like ?");
			pstmt.setString(1, "%"+firstName+"%");
			return pstmt;
		}

	}
	* write one more class
	* private final class GetCustomersPreparedStatementCallback implements PreparedStatementCallback<List<CustomerBO>> {

		@Override
		public List<CustomerBO> doInPreparedStatement(PreparedStatement pstmt)
				throws SQLException, DataAccessException {
			ResultSet rs = null;
			List<CustomerBO> customers = null;
			CustomerBO bo = null;
			customers = new ArrayList<>();
			rs = pstmt.executeQuery();
			while (rs.next()) {
				bo = new CustomerBO();
				bo.setCustomerNo(rs.getInt("customer_no"));
				bo.setFirstName(rs.getString("first_nm"));
				bo.setLastName(rs.getString("last_nm"));
				bo.setMobile(rs.getString("mobile"));
				bo.setEmailAddress(rs.getString("email_address"));

				customers.add(bo);
			}

			return customers;
		}

	}
	Q: Why they provided Two interface?
	A: both the Creator can use the Same Callback that' the reason they provided two interfaces rather that in one interface
	two methods, They provided two classes because we can reuse the callback for anther Creator also
	
   * And at last write the CTTest class.
   * 
   * public class CTTest {
		 public static void main(String[] args) {
			ApplicationContext context = new ClassPathXmlApplicationContext("com/ca/common/application-context.xml");
			CustomerDao cDao = context.getBean("customerDao", CustomerDao.class);
			List<CustomerBo> customers = cDao.getCustomers("M");
			for(CustomerBo bo: customers) {
				System.out.println(bo);
   *		}
   *	}
   *  }
   *
   *
	* 
	* 
	 */
}
