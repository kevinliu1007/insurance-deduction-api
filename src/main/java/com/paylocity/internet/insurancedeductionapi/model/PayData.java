package com.paylocity.internet.insurancedeductionapi.model;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@DynamoDBTable(tableName = "EmployInfo")
public class PayData {
    @DynamoDBHashKey(attributeName = "pay_data")
    private String payData;

    @DynamoDBAttribute(attributeName = "value")
    private Double value;
}
