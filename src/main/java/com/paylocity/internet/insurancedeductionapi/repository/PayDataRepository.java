package com.paylocity.internet.insurancedeductionapi.repository;

import com.paylocity.internet.insurancedeductionapi.model.PayData;
import org.socialsignin.spring.data.dynamodb.repository.EnableScan;
import org.springframework.data.repository.CrudRepository;
import springfox.documentation.annotations.Cacheable;

import java.util.List;

@EnableScan
public interface PayDataRepository extends CrudRepository<PayData, String> {
    @Cacheable(value = "payData")
    List<PayData> findByPayData(String payData);
}
