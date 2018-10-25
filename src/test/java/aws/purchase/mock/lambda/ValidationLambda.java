package aws.purchase.mock.lambda;

import aws.purchase.mock.model.ValidationRequest;
import aws.purchase.mock.model.ValidationResponse;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;

import java.util.Arrays;
import java.util.List;

public class ValidationLambda implements RequestHandler<ValidationRequest, ValidationResponse> {

    @Override
    public ValidationResponse handleRequest(final ValidationRequest validationRequest, final Context context) {
        final List<String> carts = Arrays.asList("1", "2");
        if (carts.contains(validationRequest.getCartId())) {
            return new ValidationResponse("cart exists");
        }
        return new ValidationResponse("cart does not exist");
    }
}
