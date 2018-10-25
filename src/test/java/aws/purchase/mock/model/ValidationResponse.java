package aws.purchase.mock.model;

public class ValidationResponse {
    private String message;

    public ValidationResponse() {
    }

    public ValidationResponse(final String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(final String message) {
        this.message = message;
    }
}
