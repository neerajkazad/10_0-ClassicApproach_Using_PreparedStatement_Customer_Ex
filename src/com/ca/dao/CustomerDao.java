package com.ca.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCallback;
import org.springframework.jdbc.core.PreparedStatementCreator;

import com.ca.bo.CustomerBo;

public class CustomerDao {
	private JdbcTemplate jdbcTemplate;

	// constructor injection
	public CustomerDao(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	public List<CustomerBo> getCustomers(String firstName) {
		GetCustomersPreparedStatementCreator gcpsc = new GetCustomersPreparedStatementCreator(firstName);
		GetCustomersPreparedStatementCallback gcpscb = new GetCustomersPreparedStatementCallback();
		return jdbcTemplate.execute(gcpsc, gcpscb); 
	}

	private final class GetCustomersPreparedStatementCreator implements PreparedStatementCreator {
		private String firstName;

		public GetCustomersPreparedStatementCreator(String firstName) {
			this.firstName = firstName;
		}

		@Override
		public PreparedStatement createPreparedStatement(Connection conn) throws SQLException {
			PreparedStatement pstmt = null;
			pstmt = conn.prepareStatement("select * from customer where first_nm like ?");//Here we can't write the % 
			pstmt.setString(1, "%"+firstName+"%");//that's the reason we appended it while taking the firstName itself
			return pstmt;
		}

	}

	private final class GetCustomersPreparedStatementCallback implements PreparedStatementCallback<List<CustomerBo>> {

		@Override
		public List<CustomerBo> doInPreparedStatement(PreparedStatement pstmt)
				throws SQLException, DataAccessException {
			ResultSet rs = null;
			List<CustomerBo> customers = null;
			CustomerBo bo = null;
			customers = new ArrayList<>();
			rs = pstmt.executeQuery();
			while (rs.next()) {
				bo = new CustomerBo();
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

}
