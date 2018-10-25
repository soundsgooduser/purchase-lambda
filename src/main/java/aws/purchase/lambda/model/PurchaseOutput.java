package aws.purchase.lambda.model;

public class PurchaseOutput {
    private String result;

    public PurchaseOutput() {

    }

    public PurchaseOutput(final String result) {
        this.result = result;
    }

    public String getResult() {
        return result;
    }

    public void setResult(final String result) {
        this.result = result;
    }
}
