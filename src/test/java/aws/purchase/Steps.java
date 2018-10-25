package aws.purchase;

import aws.purchase.lambda.model.PurchaseInput;
import aws.purchase.utils.FileUtils;
import aws.purchase.utils.LambdaUtils;
import com.amazonaws.services.lambda.AWSLambda;
import com.amazonaws.services.lambda.model.DeleteFunctionRequest;
import com.amazonaws.services.lambda.model.DeleteFunctionResult;
import com.amazonaws.services.lambda.model.InvokeResult;
import com.amazonaws.services.lambda.model.ResourceNotFoundException;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import org.apache.http.HttpStatus;
import org.junit.Assert;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class Steps {
    private static final String PURCHASE_FUNCTION = "purchase_function";
    private static final String MOCK_VALIDATION_FUNCTION = "mock_validation_function";
    private static final String MOCK_VALIDATION_LAMBDA_HANDLER = "aws.purchase.mock.lambda.ValidationLambda::handleRequest";
    private static final String PURCHASE_LAMBDA_HANDLER = "aws.purchase.lambda.PurchaseProcessLambda::handleRequest";
    private static final String PURCHASE_FILE_NAME = "purchase-lambda-1.0.0.jar";
    private static final String MOCKS_FILE_NAME = "purchase-lambda-1.0.0-mocks-tests.jar";
    private static final String VALIDATION_LAMBDA_ARN = "VALIDATION_LAMBDA_ARN";
    private static final String CART_ID = "1";

    @When("^delete resources$")
    public void deleteResources() throws Exception {
        System.out.println("start delete resources");

        final AWSLambda client = LambdaUtils.createDefaultLambdaClient();
        final List<String> functions = Arrays.asList(PURCHASE_FUNCTION, MOCK_VALIDATION_FUNCTION);
        functions.stream().forEach(function -> {
            final DeleteFunctionRequest deleteFunctionRequest = new DeleteFunctionRequest().withFunctionName(function);
            try {
                final DeleteFunctionResult deleteFunctionResult = client.deleteFunction(deleteFunctionRequest);
                Assert.assertEquals(HttpStatus.SC_OK, deleteFunctionResult.getSdkHttpMetadata().getHttpStatusCode());
            } catch (final ResourceNotFoundException ex) {
                System.out.println(ex.getMessage());
            }
        });
    }

    @Then("^create and call lambda$")
    public void createLambda() throws Exception {
        System.out.println("start create and call lambda");

        final File purchaseCode = FileUtils.find(PURCHASE_FILE_NAME);
        final File mocksCode = FileUtils.find(MOCKS_FILE_NAME);

        final String mockFunctionArn = LambdaUtils.createFunction(MOCK_VALIDATION_FUNCTION, MOCK_VALIDATION_LAMBDA_HANDLER, mocksCode, Collections.EMPTY_MAP);

        final HashMap<String, String> vars = new HashMap<>();
        vars.putIfAbsent(VALIDATION_LAMBDA_ARN, mockFunctionArn);
        final String purchaseFunctionArn = LambdaUtils.createFunction(PURCHASE_FUNCTION, PURCHASE_LAMBDA_HANDLER, purchaseCode, vars);

        final PurchaseInput request = new PurchaseInput(CART_ID);
        final InvokeResult response = LambdaUtils.invokeLambda(purchaseFunctionArn, request);

        final String payload = new String(response.getPayload().array(), StandardCharsets.UTF_8.name());
        Assert.assertEquals(HttpStatus.SC_OK, response.getSdkHttpMetadata().getHttpStatusCode());
        Assert.assertTrue(payload.contains("cart exists"));
    }
}
