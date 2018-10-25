package aws.purchase.mock.model;

public class ValidationRequest {
    private String cartId;

    public ValidationRequest() {
    }

    public ValidationRequest(final String cartId) {
        this.cartId = cartId;
    }

    public String getCartId() {
        return cartId;
    }

    public void setCartId(final String cartId) {
        this.cartId = cartId;
    }
}
