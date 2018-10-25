package aws.purchase.lambda;

import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.lambda.AWSLambda;
import com.amazonaws.services.lambda.AWSLambdaClientBuilder;
import com.amazonaws.services.lambda.model.InvokeRequest;
import com.amazonaws.services.lambda.model.InvokeResult;

public class LambdaClient {
    public static final String REGION = "us-east-1";
    public static final String LAMBDA_ENDPOINT = "http://localhost:4574";

    public InvokeResult invoke(final String functionName, final String payload) {
        final AWSLambdaClientBuilder builder = AWSLambdaClientBuilder.standard()
                .withEndpointConfiguration(new AwsClientBuilder.EndpointConfiguration(LAMBDA_ENDPOINT, REGION));
        final AWSLambda client = builder.build();
        final InvokeRequest req = new InvokeRequest()
                .withFunctionName(functionName)
                .withPayload(payload);
        return client.invoke(req);
    }
}
