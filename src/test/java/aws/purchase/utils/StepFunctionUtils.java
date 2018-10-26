package aws.purchase.utils;

import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.stepfunctions.AWSStepFunctions;
import com.amazonaws.services.stepfunctions.AWSStepFunctionsClientBuilder;
import com.amazonaws.services.stepfunctions.builder.StateMachine;
import com.amazonaws.services.stepfunctions.model.*;

import java.util.Collections;
import java.util.List;

import static com.amazonaws.services.stepfunctions.builder.StepFunctionBuilder.end;
import static com.amazonaws.services.stepfunctions.builder.StepFunctionBuilder.passState;

public final class StepFunctionUtils {

    private StepFunctionUtils() {
    }

    public static AWSStepFunctions createDefaultStepFunctionsClient() {
        AwsClientBuilder.EndpointConfiguration endpointConfiguration
                = new AwsClientBuilder.EndpointConfiguration("http://localhost:4584", "us-east-1");
        return AWSStepFunctionsClientBuilder.standard()
                .withEndpointConfiguration(endpointConfiguration)
                .build();
    }

    public static void deleteStateMachine(String name) {
        AWSStepFunctions client = createDefaultStepFunctionsClient();
        ListStateMachinesResult listStateMachinesResult = getListStateMachinesResult();
        listStateMachinesResult.getStateMachines().stream().filter(sm -> sm.getName().equals(name)).findFirst().ifPresent(machine -> {
            DeleteStateMachineRequest deleteStateMachineRequest = new DeleteStateMachineRequest();
            deleteStateMachineRequest.setStateMachineArn(machine.getStateMachineArn());
            DeleteStateMachineResult deleteStateMachineResult = client.deleteStateMachine(deleteStateMachineRequest);
            System.out.println(deleteStateMachineResult.getSdkHttpMetadata().getHttpStatusCode());
            System.out.println("Deleted state machine name " + name + " with arn + " + machine.getStateMachineArn());
        });
    }

    public static String getStateMachineArnByName(String name) {
        return getListStateMachinesResult().getStateMachines()
                .stream()
                .filter(sm -> sm.getName().equals(name))
                .findFirst()
                .map(c -> c.getStateMachineArn())
                .orElse("");
    }

    public static ListStateMachinesResult getListStateMachinesResult() {
        AWSStepFunctions client = createDefaultStepFunctionsClient();
        ListStateMachinesRequest listStateMachinesRequest = new ListStateMachinesRequest();
        listStateMachinesRequest.setMaxResults(1000);
        return client.listStateMachines(listStateMachinesRequest);
    }

    public static List<ExecutionListItem> getExecutionsByStateMachineArn(String arn) {
        AWSStepFunctions client = createDefaultStepFunctionsClient();
        ListExecutionsRequest listExecutionsRequest = new ListExecutionsRequest();
        listExecutionsRequest.setStateMachineArn(arn);
        try {
            ListExecutionsResult listExecutionsResult = client.listExecutions(listExecutionsRequest);
            return listExecutionsResult.getExecutions();
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
            return Collections.emptyList();
        }
    }

    public static CreateStateMachineResult createStateMachine(String name, String role) {
        AWSStepFunctions client = createDefaultStepFunctionsClient();
        CreateStateMachineRequest request = new CreateStateMachineRequest()
                .withName(name)
                .withRoleArn(role)
                .withDefinition(stateMachine());
        return client.createStateMachine(request);

    }

    public static StateMachine stateMachine() {
        StateMachine stateMachine = StateMachine.builder()
                .startAt("StartStep")
                .state("StartStep",
                        passState()
                                .transition(end())).build();
        System.out.println(stateMachine.toPrettyJson());
        return stateMachine;
    }
}
