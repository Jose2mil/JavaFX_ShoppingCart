package data;

public class Product implements Comparable<Product>
{
    int code;
    String name;
    float price;

    public Product(int code, String name, float price)
    {
        this.code = code;
        this.name = name;
        this.price = price;
    }

    public int getCode() { return code; }
    public String getName() { return name; }
    public float getPrice() { return price; }

    @Override
    public int compareTo(Product other)
    {
        return name.compareTo(other.getName());
    }

    public String toString()
    {
        return name;
    }
}
