package com.ca.test;

import java.util.List;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.ca.bo.CustomerBo;
import com.ca.dao.CustomerDao;

public class CTTest {
	public static void main(String[] args) {
		ApplicationContext context = new ClassPathXmlApplicationContext("com/ca/common/application-context.xml");
		CustomerDao cDao = context.getBean("customerDao", CustomerDao.class);
		List<CustomerBo> customers = cDao.getCustomers("M");
		for(CustomerBo bo: customers) {
			System.out.println(bo);
		}
				
	}
}
