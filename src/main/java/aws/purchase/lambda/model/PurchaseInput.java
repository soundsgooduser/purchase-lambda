package aws.purchase.lambda.model;

public class PurchaseInput {
    private String cartId;

    public PurchaseInput() {
    }

    public PurchaseInput(final String cartId) {
        this.cartId = cartId;
    }

    public String getCartId() {
        return cartId;
    }

    public void setCartId(final String cartId) {
        this.cartId = cartId;
    }
}
