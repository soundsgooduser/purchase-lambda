package aws.purchase.lambda;

import aws.purchase.lambda.model.PurchaseInput;
import aws.purchase.lambda.model.PurchaseOutput;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.stepfunctions.AWSStepFunctions;
import com.amazonaws.services.stepfunctions.AWSStepFunctionsClientBuilder;
import com.amazonaws.services.stepfunctions.model.StartExecutionRequest;

public class PurchaseProcessLambda implements RequestHandler<PurchaseInput, PurchaseOutput> {
    private static final String PURCHASE_STEP_FUNCTION_ARN_ENV = "PURCHASE_STEP_FUNCTION_ARN_ENV";

    @Override
    public PurchaseOutput handleRequest(final PurchaseInput request, final Context context) {
        String purchaseStepFunctionARN = System.getenv(PURCHASE_STEP_FUNCTION_ARN_ENV);
        StartExecutionRequest executionRequest = (new StartExecutionRequest())
                .withStateMachineArn(purchaseStepFunctionARN)
                .withInput("{\"cartId\":\"" + request.getCartId() + "\"}");
        getStepFunctions().startExecution(executionRequest);
        return new PurchaseOutput("after step function called");
    }

    private static AWSStepFunctions getStepFunctions() {
        AwsClientBuilder.EndpointConfiguration endpointConfiguration
                = new AwsClientBuilder.EndpointConfiguration("http://localhost:4584", "us-east-1");
        return AWSStepFunctionsClientBuilder
                .standard()
                .withEndpointConfiguration(endpointConfiguration)
                .build();
    }
}
