package com.ra.saucedemo.test;

import java.util.List;

import org.openqa.selenium.By;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import basetest.BaseTest;
import pages.CartPage;
import pages.CheckoutPage;
import pages.InventoryPage;
import pages.LoginPage;
import pages.PaymentPage;
import utils.CSVUtils;


public class CheckoutTests extends BaseTest {

	 @DataProvider(name = "scenarioData")
	    public Object[][] getScenarioData() {
	        List<String[]> records = CSVUtils.readCSV("testdata/testdata.csv");
	        Object[][] data = new Object[records.size()][7];
	        for (int i = 0; i < records.size(); i++) {
	            data[i] = records.get(i);
	        }
	        return data;
	    }

	    @Test(dataProvider = "scenarioData")
	    public void testValidCheckout(String scenario, String username, String password,
	                                  String firstName, String lastName, String postalCode, String productIds) {
	        if (!scenario.equals("ValidCheckout")) return;

	        test.info("Starting Valid Checkout scenario");   // <-- test inherited from BaseTest
	        LoginPage login = new LoginPage(driver);         // <-- driver inherited from BaseTest
	        InventoryPage inventory = login.login(username, password);

	        Assert.assertTrue(driver.getCurrentUrl().contains("inventory.html"), "Login failed!");
	        test.pass("Login successful");

	        inventory.addProductToCart(productIds);
	        inventory.goToCart();
	        test.info("Product added to cart: " + productIds);

	        CartPage cart = new CartPage(driver);
	        cart.proceedToCheckout();

	        CheckoutPage checkout = new CheckoutPage(driver);
	        checkout.enterUserDetails(firstName, lastName, postalCode);

	        PaymentPage payment = new PaymentPage(driver);
	        payment.finishOrder();

	        Assert.assertEquals(payment.getSuccessMessage(), "THANK YOU FOR YOUR ORDER");
	        test.pass("Order completed successfully");
	    }

	    @Test(dataProvider = "scenarioData")
	    public void testInvalidLogin(String scenario, String username, String password,
	                                 String firstName, String lastName, String postalCode, String productIds) {
	        if (!scenario.equals("InvalidLogin")) return;

	        test.info("Running Invalid Login scenario");
	        LoginPage login = new LoginPage(driver);
	        login.login(username, password);

	        String errorMsg = driver.findElement(By.cssSelector("h3[data-test='error']")).getText();
	        Assert.assertTrue(errorMsg.contains("Username and password do not match"), "Error message not displayed!");
	        test.pass("Invalid login error validated");
	    }

	    @Test(dataProvider = "scenarioData")
	    public void testMultipleProducts(String scenario, String username, String password,
	                                     String firstName, String lastName, String postalCode, String productIds) {
	        if (!scenario.equals("MultipleProducts")) return;

	        test.info("Running Multiple Products scenario");
	        LoginPage login = new LoginPage(driver);
	        InventoryPage inventory = login.login(username, password);

	        String[] products = productIds.split("\\|");
	        for (String p : products) {
	            inventory.addProductToCart(p);
	            test.info("Added product: " + p);
	        }

	        String cartCount = driver.findElement(By.className("shopping_cart_badge")).getText();
	        Assert.assertEquals(cartCount, String.valueOf(products.length), "Cart count mismatch!");
	        test.pass("Cart count validated: " + cartCount);
	    }

	}

	
	
	

