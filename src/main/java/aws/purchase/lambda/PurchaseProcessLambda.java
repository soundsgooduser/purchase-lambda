package aws.purchase.lambda;

import aws.purchase.lambda.model.PurchaseInput;
import aws.purchase.lambda.model.PurchaseOutput;
import com.amazonaws.services.lambda.model.InvokeResult;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;

import java.nio.charset.Charset;

public class PurchaseProcessLambda implements RequestHandler<PurchaseInput, PurchaseOutput> {
    private static final String VALIDATION_LAMBDA_ARN = "VALIDATION_LAMBDA_ARN";

    @Override
    public PurchaseOutput handleRequest(final PurchaseInput request, final Context context) {
        final String arn = System.getenv(VALIDATION_LAMBDA_ARN);
        final LambdaClient lambdaClient = new LambdaClient();
        final InvokeResult result = lambdaClient.invoke(arn, "{\"cartId\":\"" + request.getCartId() + "\"}");
        final String response = new String(result.getPayload().array(), Charset.forName("UTF-8"));
        return new PurchaseOutput(response);
    }
}
