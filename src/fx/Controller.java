package fx;

import data.Product;
import data.ShoppingCartLine;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.Initializable;
import javafx.scene.control.*;

import java.io.*;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.ResourceBundle;

public class Controller implements Initializable
{
    public ListView lstAvailableIngredients;
    public Label lblCode;
    public ListView lstShoppingCart;
    public Label lblTotalPrice;
    public Label lblName;
    public Label lblPrice;
    public Button btnAdd;
    public Button btnRemove;
    public Button btnPrint;

    private ArrayList<ShoppingCartLine> shoppingCartProducts;
    private float totalPrice;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle)
    {
        loadIngredients();
        shoppingCartProducts = new ArrayList<>();
        totalPrice = 0;

        lstAvailableIngredients.getSelectionModel()
                .selectedItemProperty().addListener(
                    new ChangeListener<Product>()
                    {
                        @Override
                        public void changed(
                                ObservableValue<? extends Product> obs,
                                Product oldSelected, Product newSelected)
                        {
                            updateLabelsInformation(newSelected);
                        }
                    }
        );

        btnAdd.setOnAction(new EventHandler<ActionEvent>()
        {
            @Override
            public void handle(ActionEvent actionEvent)
            {
                addProductToShoppingCart();
            }
        });

        btnRemove.setOnAction(new EventHandler<ActionEvent>()
        {
            @Override
            public void handle(ActionEvent actionEvent)
            {
                RemoveProductToShoppingCart();
            }
        });

        btnPrint.setOnAction(new EventHandler<ActionEvent>()
        {
            @Override
            public void handle(ActionEvent actionEvent)
            {
                printTicket();
            }
        });
    }


    private void updateProducts(float difference)
    {
        lstShoppingCart.getItems().setAll(shoppingCartProducts);
        totalPrice = totalPrice + difference;
        DecimalFormat formatter = new DecimalFormat("0.00");
        lblTotalPrice.setText("TOTAL: " +
                formatter.format(totalPrice) + " EUR");
    }


    private void showErrorPopUp(String header, String content)
    {
        Alert dialog = new Alert(Alert.AlertType.ERROR);
        dialog.setHeaderText(header);
        dialog.setContentText(content);
        dialog.showAndWait();
    }


    private void addProductToShoppingCart()
    {
        if(lstAvailableIngredients
                    .getSelectionModel().getSelectedItem() == null)
            showErrorPopUp("There is no product selected!",
                    "Select one before clicking");
        else
        {
            boolean found = false;
            int index = 0;
            Product productSelected = (Product) lstAvailableIngredients
                    .getSelectionModel().getSelectedItem();

            while (!found && index < shoppingCartProducts.size())
            {
                ShoppingCartLine shoppingProduct =
                        shoppingCartProducts.get(index);

                if (shoppingProduct.getCode() == productSelected.getCode())
                {
                    found = true;
                    shoppingProduct.setAmount(shoppingProduct.getAmount() + 1);
                }
                index++;
            }

            if (!found)
                shoppingCartProducts.add(
                        new ShoppingCartLine(productSelected));

            updateProducts(productSelected.getPrice());
        }
    }


    private void RemoveProductToShoppingCart()
    {
        if(lstShoppingCart
                    .getSelectionModel().getSelectedItem() == null)
            showErrorPopUp("There is no product selected " +
                            "in the shopping cart!",
                    "Select one before clicking");
        else
        {
            ShoppingCartLine productSelected = (ShoppingCartLine) lstShoppingCart
                    .getSelectionModel().getSelectedItem();

            boolean found = false;
            int index = 0;

            while (!found && index < shoppingCartProducts.size())
            {
                ShoppingCartLine shoppingProduct = shoppingCartProducts.get(index);

                if (shoppingProduct.getCode() == productSelected.getCode())
                {
                    found = true;
                    if (productSelected.getAmount() == 1)
                        shoppingCartProducts.remove(index);
                    else
                        shoppingProduct.setAmount(productSelected.getAmount() - 1);
                }
                index++;
            }

            updateProducts(-productSelected.getPrice());
        }
    }


    public void printTicket()
    {
        if(shoppingCartProducts.size() == 0)
            showErrorPopUp("The shopping cart is empty!",
                    "Introduce any product for print a ticket");
        else
        {
            DecimalFormat formatter = new DecimalFormat("0.00");
            try
            {
                String path = "ticket.txt";
                FileWriter ticket = new FileWriter(path);
                for(ShoppingCartLine product : shoppingCartProducts)
                {
                    ticket.write(product.getName() + " (" +
                            product.getAmount() + " x " +
                            formatter.format(product.getPrice()) + ")\n");
                }
                ticket.write("\nTOTAL: " +
                        formatter.format(totalPrice) + " €");
                ticket.close();
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
    }

    private void updateLabelsInformation(Product newSelected)
    {
        lblCode.setText("Code: " + newSelected.getCode());
        lblName.setText("Name: " + newSelected.getName());
        lblPrice.setText("Price: " + newSelected.getPrice());
    }


    private void loadIngredients()
    {
        String path = "products.txt";
        BufferedReader fileProducts;
        ArrayList<Product> products = new ArrayList<>();

        if(! new File(path).exists())
            showErrorPopUp(path + " not found!", "");

        else
        {
            try
            {
                fileProducts = new BufferedReader(new FileReader(path));
                String line;
                do
                {
                    line = fileProducts.readLine();
                    if (line != null && !(line.equals("")))
                    {
                        products.add(
                                new Product(
                                        Integer.parseInt(line.split(";")[0]),
                                        line.split(";")[1],
                                        Float.parseFloat(line.split(";")[2])));
                    }
                }
                while (line != null && !(line.equals("")));
                fileProducts.close();
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }

            Collections.sort(products);
            lstAvailableIngredients.getItems().setAll(products);
        }
    }
}
