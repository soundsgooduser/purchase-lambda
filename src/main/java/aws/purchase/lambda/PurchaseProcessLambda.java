package aws.purchase.lambda;

import aws.purchase.lambda.model.PurchaseInput;
import aws.purchase.lambda.model.PurchaseOutput;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.stepfunctions.AWSStepFunctions;
import com.amazonaws.services.stepfunctions.AWSStepFunctionsClientBuilder;
import com.amazonaws.services.stepfunctions.model.StartExecutionRequest;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class PurchaseProcessLambda implements RequestHandler<PurchaseInput, PurchaseOutput> {
    private static final String PURCHASE_STEP_FUNCTION_ARN_ENV = "PURCHASE_STEP_FUNCTION_ARN_ENV";

    @Override
    public PurchaseOutput handleRequest(final PurchaseInput request, final Context context) {
        String purchaseStepFunctionARN = System.getenv(PURCHASE_STEP_FUNCTION_ARN_ENV);
        StartExecutionRequest executionRequest = (new StartExecutionRequest())
                .withStateMachineArn(purchaseStepFunctionARN)
                .withInput("{\"cartId\":\"" + request.getCartId() + "\"}");
        //getStepFunctions().startExecution(executionRequest);
        String call = call();
        return new PurchaseOutput(call);
    }

    private static AWSStepFunctions getStepFunctions() {
        AwsClientBuilder.EndpointConfiguration endpointConfiguration
                = new AwsClientBuilder.EndpointConfiguration("http://localhost:4584", "us-east-1");
        return AWSStepFunctionsClientBuilder
                .standard()
                .withEndpointConfiguration(endpointConfiguration)
                .build();
    }

    public static String call() {
        try {
            String url = "http://wiremock:8080/get/this";

            HttpClient client = HttpClientBuilder.create().build();
            HttpGet request = new HttpGet(url);

            HttpResponse response = client.execute(request);

            System.out.println("Response Code : "
                    + response.getStatusLine().getStatusCode());

            BufferedReader rd = new BufferedReader(
                    new InputStreamReader(response.getEntity().getContent()));

            StringBuffer result = new StringBuffer();
            String line = "";
            while ((line = rd.readLine()) != null) {
                result.append(line);
            }
            return result.toString();
        } catch (Exception e) {
            throw new RuntimeException(e);

        }

    }
}
