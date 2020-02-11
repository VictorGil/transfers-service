# Transfers Service
  
This is an example/PoC of a REST service which processes money transfers between accounts.  
  
## Requirements
 - Maven 3  
 - Java 12 (can be changed by modifying the corresponding [Maven property](https://github.com/VictorGil/transfers-service/blob/master/transfers-service-parent/pom.xml#L19))
  
## Business Logic Overview
 - The service distinguish between two types of transfers: internal and external.
 - In the internal money transfers, both the source account and the target account
 have been created through the corresponding API call to the service which generates the account ids 
 (i.e., both accounts are internal to the service).  
 - In the case of external money transfers, either the source or the target account
 is not managed by the service (i.e., it is external) and the other account is internal.
 - When processing an internal money transfer, the transfer amount is subtracted from the source 
account and added to the target account, the concurrency model guarantees a reliable execution and 
prevents issues related to race conditions while having low performance impact. 

## Implementation Details
 - An amount of money is represented by a _long_ value and each unit of that value corresponds to a cent.  
 - Hence, the minimum amount of a any given currency that can be transferred between two accounts is one cent.  
 - For the sake of simplicity, the multi-currency support is very basic: 
the currency of the source account, the target account and the transfer must match.  
 - Two concurrent transfers do not block each other unless they refer to the same account or couple of accounts.  

## Example Request And Response Pairs
 - Example request to create a new account:  
 ```
 POST http://localhost:4567/transfers/account?currency=GBP  (no request body)
 ```
 - Example response:  
 ```
 {
    "status": "SUCCESS",
    "error_message": "N/A",
    "data": {
        "amount": 0,
        "accountId": "8a7fc0492e50"
    }
}
 ```
 - Example request to process an incoming money transfer:  
 ```
 POST http://localhost:4567/transfers/transfer
 Request body:
 {
    "source_account_id": "157016b32e6c",
    "source_account_type": "EXTERNAL",

    "target_account_id": "8a7fc0492e50",
    "target_account_type": "INTERNAL",

    "amount": 100000,
    "currency": "GBP"
}
```
  - Example response:  
```
{
    "status": "SUCCESS",
    "error_message": "N/A",
    "data": {}
}
```

## Important Test
[TransfersManagerImplConcurrencyTest.java](https://github.com/VictorGil/transfers-service/blob/master/transfers-service-core/src/test/java/net/devaction/transfersservice/core/transfersmanager/concurrency/TransfersManagerImplConcurrencyTest.java)
is used to test the service under a simulated very high load, using a configurable number of threads,
a small number of accounts and a large number of concurrent transactions among those accounts.  
We can see that despite some transfers not being able to be processed, the service is always kept
in a valid and consistent state.

## Similar Previous Project
In the past I created similar service but based on asynchronous messaging instead of (synchronous) REST.  
More information on:
- [dev.to](https://dev.to/victorgil/using-apache-kafka-to-implement-event-driven-microservices-af2)
- [source code](https://github.com/VictorGil/transfers_recording_service)
 