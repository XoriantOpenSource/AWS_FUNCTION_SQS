package org.xor.lambda;

import com.amazonaws.ClientConfiguration;
import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.s3.model.Region;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClientBuilder;
import com.amazonaws.services.sqs.model.ListQueuesResult;
import com.amazonaws.services.sqs.model.Message;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.List;

public class LambdaFunctionHandler implements RequestHandler<Object, String> {

    private String jdbcUrl = "jdbc:postgresql://******.rds.amazonaws.com:5432/Sample_db"; //AWS_HOSTED_RDS_SERVER
    private String username = "username";  //AWS_DB_USER_NAME
    private String password = "pwd";    //AWS_DB_PASSWORD_NAME

    private Connection connection = null;

    @Override
    public String handleRequest(Object input, Context context) {
        context.getLogger().log("Input: " + input);
        try {
            if (connection == null)
                connection = getDBConnection();
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT version()");
            rs.next();
            System.out.println(rs.getString(1));
        } catch (Exception e) {
            System.out.println("Error while fetching data from PG. " + e);
        }
        System.out.println("Get Queue....");
        AWSCredentialsProvider awsCredentialsProvider = new DefaultAWSCredentialsProviderChain();


        AmazonSQS sqs = AmazonSQSClientBuilder.standard().withRegion(Region.US_East_2.toString())
                .withCredentials(awsCredentialsProvider)
                .withClientConfiguration(new ClientConfiguration().withRequestTimeout(10000)).build();
        System.out.println("Init Queue....");

        ListQueuesResult lq_result = sqs.listQueues();
        System.out.println("Your SQS Queue URLs:");
        for (String url : lq_result.getQueueUrls()) {
            System.out.println(url);
        }
        String queueUrl = sqs.getQueueUrl("SampleSQSQueue.fifo").getQueueUrl();
        List<Message> messages = sqs.receiveMessage(queueUrl).getMessages();
        for (Message m : messages) {
            System.out.println(m.getReceiptHandle());
            sqs.deleteMessage(queueUrl, m.getReceiptHandle());
        }

        return "Lambda Execution Done!";
    }

    private Connection getDBConnection() throws Exception {
        Connection tmpConn = null;
        System.out.println("getting Postgres DB connection...");
        try {
            Class.forName("org.postgresql.Driver");
            tmpConn = DriverManager.getConnection(jdbcUrl, username, password);
        } catch (Exception e) {
            System.out.println("Error while creating connection " + e);
            throw e;
        }
        System.out.println("Return DB connection...");
        return tmpConn;
    }

}
