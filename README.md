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
 - An amount of money is represented by a long value and each unit of that value corresponds to a cent.  
 - Hence, the minimum amount of a any given currency that can be transfer between accounts is a cent.  
 - For the sake of simplicity, the multi-currency support is very basic: 
the currency of the source account, the target account and the transfer must match.  
 - Two concurrent transfers do not block each other unless they refer to the same account or couple of accounts.  
   