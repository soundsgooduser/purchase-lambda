package aws.purchase.utils;

import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.lambda.AWSLambda;
import com.amazonaws.services.lambda.AWSLambdaClientBuilder;
import com.amazonaws.services.lambda.model.*;
import com.amazonaws.services.lambda.model.Runtime;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Map;

public final class LambdaUtils {
    private static final String REGION = "us-east-1";
    private static final String LAMBDA_HOST = "http://localhost:4574";

    private LambdaUtils() {
    }

    public static AWSLambda createDefaultLambdaClient() {
        return AWSLambdaClientBuilder.standard()
                .withEndpointConfiguration(new AwsClientBuilder.EndpointConfiguration(LAMBDA_HOST, REGION))
                .build();
    }

    public static CreateFunctionRequest createFunctionRequest(final String name, final String handler,
                                                              final FunctionCode code, final Map<String, String> vars) {
        final Environment environment = new Environment();
        environment.setVariables(vars);
        return new CreateFunctionRequest()
                .withFunctionName(name)
                .withCode(code)
                .withEnvironment(environment)
                .withHandler(handler)
                .withRuntime(Runtime.Java8)
                .withPublish(true);
    }

    public static FunctionCode createFunctionCode(final File code) throws IOException {
        final ByteBuffer file = ByteBuffer.wrap(FileUtils.readFileToByteArray(code));
        return new FunctionCode().withZipFile(file);
    }

    public static String createFunction(final String name, final String handler, final File code, final Map<String, String> vars)
            throws IOException {
        final FunctionCode mocksCode = createFunctionCode(code);
        final CreateFunctionRequest mockRequest = createFunctionRequest(name, handler, mocksCode, vars);
        final CreateFunctionResult mockResult = createDefaultLambdaClient().createFunction(mockRequest);
        return mockResult.getFunctionArn();
    }

    public static InvokeResult invokeLambda(final String arn, final Object request) throws JsonProcessingException {
        final String payload = new ObjectMapper().writeValueAsString(request);
        final InvokeRequest invokeRequest = new InvokeRequest()
                .withFunctionName(arn)
                .withPayload(payload);
        return createDefaultLambdaClient().invoke(invokeRequest);
    }
}
