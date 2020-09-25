package com.paylocity.internet.insurancedeductionapi;

import com.paylocity.internet.insurancedeductionapi.controller.InsuranceDeductionController;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class InsuranceDeductionApiApplicationTests {

	@Autowired
	private InsuranceDeductionController insuranceDeductionController;

	@Test
	void contextLoads() throws Exception{
		assertThat(insuranceDeductionController).isNotNull();
	}

}
