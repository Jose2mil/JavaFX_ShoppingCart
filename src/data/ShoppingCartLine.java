package data;

public class ShoppingCartLine
{
    Product product;
    int amount;

    public ShoppingCartLine(Product product)
    {
        this.product = product;
        this.amount = 1;
    }

    public int getAmount() { return amount; }
    public void setAmount(int amount) { this.amount = amount; }
    public int getCode() { return product.getCode(); }
    public String getName() { return product.getName(); }
    public float getPrice() { return product.getPrice(); }

    public String toString()
    {
        return product.getName() + " (" + amount + ")";
    }
}
