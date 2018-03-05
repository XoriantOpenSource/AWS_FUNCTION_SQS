# AWS_FUNCTION_SQS
This project demonstrates how to connect AWS database and SQS. It usages AWS Java SDK to achieve this.

# Prerequisites
1.	AWS Account with sufficient balance to deploy 
2.	Pre-configured AWS database service 
3.	Pre-configured SQS FIFO queue(s)
4.	Role with permission to read / write SQS ( select role and SQS read-write permission)


# Steps:
1.	Add valid DB connection string, username & password in LambdaFunctionHandler
2.	Build maven project 
3.	Login to AWS console
4.	From services select Lambda 
5.	Click on create Function 
6.	Select Author from scratch
7.	Give name as  myLambdaFunction
8.	Select runtime as Java 8
9.	Select valid role
10.	Select create Button 
11.	In Function Code Section
a.	 upload Jar file generated in Step #2
b.	In Handler text field: enter org.xor.lambda. LambdaFunctionHandler:: handleRequest
12.	 Save Function
