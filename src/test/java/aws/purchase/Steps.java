package aws.purchase;

import aws.purchase.lambda.model.PurchaseInput;
import aws.purchase.utils.FileUtils;
import aws.purchase.utils.LambdaUtils;
import aws.purchase.utils.StepFunctionUtils;
import com.amazonaws.services.lambda.AWSLambda;
import com.amazonaws.services.lambda.model.DeleteFunctionRequest;
import com.amazonaws.services.lambda.model.DeleteFunctionResult;
import com.amazonaws.services.lambda.model.InvokeResult;
import com.amazonaws.services.lambda.model.ResourceNotFoundException;
import com.amazonaws.services.stepfunctions.model.CreateStateMachineResult;
import com.amazonaws.services.stepfunctions.model.ExecutionListItem;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import org.apache.http.HttpStatus;
import org.junit.Assert;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

public class Steps {
    private static final String PURCHASE_FUNCTION = "purchase_function";
    private static final String STEP_FUNCTION = "misha-state-machine";
    private static final String PURCHASE_LAMBDA_HANDLER = "aws.purchase.lambda.PurchaseProcessLambda::handleRequest";
    private static final String PURCHASE_FILE_NAME = "purchase-lambda-1.0.0.jar";
    private static final String PURCHASE_STEP_FUNCTION_ARN_ENV = "PURCHASE_STEP_FUNCTION_ARN_ENV";
    public static final String ROLE = "arn:aws:iam::0123456789:role/service-role/MyRole";
    private static final String CART_ID = "1";

    @When("^delete resources$")
    public void deleteResources() throws Exception {
        System.out.println("start delete resources");

        final AWSLambda client = LambdaUtils.createDefaultLambdaClient();
        final DeleteFunctionRequest deleteFunctionRequest = new DeleteFunctionRequest().withFunctionName(PURCHASE_FUNCTION);
        try {
            final DeleteFunctionResult deleteFunctionResult = client.deleteFunction(deleteFunctionRequest);
            Assert.assertEquals(HttpStatus.SC_OK, deleteFunctionResult.getSdkHttpMetadata().getHttpStatusCode());
        } catch (final ResourceNotFoundException ex) {
            System.out.println(ex.getMessage());
        }
    }

    @Then("^create and call lambda$")
    public void createLambda() throws Exception {
        System.out.println("start create and call lambda");

//        String existingStateMachineArn = StepFunctionUtils.getStateMachineArnByName(STEP_FUNCTION);
//        List<ExecutionListItem> existingExecutions = StepFunctionUtils.getExecutionsByStateMachineArn(existingStateMachineArn);
//        System.out.println(">>>> size before" + existingExecutions.size());
//
//        StepFunctionUtils.deleteStateMachine(STEP_FUNCTION);
//
//        CreateStateMachineResult stateMachine = StepFunctionUtils.createStateMachine(STEP_FUNCTION, ROLE);
//        String stateMachineArn = stateMachine.getStateMachineArn();

        final File purchaseCode = FileUtils.find(PURCHASE_FILE_NAME);
        final HashMap<String, String> vars = new HashMap<>();
        //vars.putIfAbsent(PURCHASE_STEP_FUNCTION_ARN_ENV, stateMachineArn);
        final String purchaseFunctionArn = LambdaUtils.createFunction(PURCHASE_FUNCTION, PURCHASE_LAMBDA_HANDLER, purchaseCode, vars);

        final PurchaseInput request = new PurchaseInput(CART_ID);
        final InvokeResult response = LambdaUtils.invokeLambda(purchaseFunctionArn, request);

        final String payload = new String(response.getPayload().array(), StandardCharsets.UTF_8.name());
        Assert.assertEquals(HttpStatus.SC_OK, response.getSdkHttpMetadata().getHttpStatusCode());
        Assert.assertTrue(payload.contains("Here it is!"));

//        List<ExecutionListItem> executions = StepFunctionUtils.getExecutionsByStateMachineArn(stateMachineArn);
//        System.out.println(">>>> size after" + executions.size());
//        Collections.sort(executions, Comparator.comparing(ExecutionListItem::getStartDate).reversed());
//
//        List<ExecutionListItem> delta = executions.stream().filter(execution -> !existingExecutions.contains(execution)).collect(Collectors.toList());
//        System.out.println("delta.size: " + delta.size());
//        if (delta.size() == 1) {
//            ExecutionListItem executionListItem = delta.get(0);
//            System.out.println("status: " + executionListItem.getStatus());
//            System.out.println("start date: " + executionListItem.getStartDate());
//        }

    }
}
