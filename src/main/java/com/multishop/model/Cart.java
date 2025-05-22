package com.multishop.model;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class Cart implements Serializable
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public Map<Long, CartItem> cartItems = new HashMap<>();
    public double totalAmount;
    
    
    public Map<Long, CartItem> getCartItems() {
		return cartItems;
	}

	public void setCartItems(Map<Long, CartItem> cartItems) {
		this.cartItems = cartItems;
	}

	public double getTotalPrice() {
		return totalAmount;
	}

	public void setTotalPrice(double totalPrice) {
		this.totalAmount = totalPrice;
	}

	public void addItem(CartItem item) 
    {
        if (cartItems.containsKey(item.getProduct().productId)) 
        {
            CartItem existingItem = cartItems.get(item.getProduct().productId);
            existingItem.setQuantity(existingItem.getQuantity() + item.getQuantity());
        } else 
            cartItems.put(item.getProduct().productId, item);

        // Recalculate total price
        calculateTotal();
    }
	
	public void removeItem(long itemId) 
	{
        if (cartItems.containsKey(itemId)) 
        {
            cartItems.remove(itemId);
            calculateTotal();
        } else {
            System.out.println("Item not found in the cart");
        }
    }
    
    public void updateQuantity(long itemId, int quantity) {
        if (cartItems.containsKey(itemId)) 
        {
            CartItem item = cartItems.get(itemId);
            if (quantity <= 0) 
                cartItems.remove(itemId);
            else 
                item.setQuantity(quantity);
            calculateTotal();
        } 
        else 
            System.out.println("Item not found in the cart");
    }

    private void calculateTotal() 
    {
        totalAmount = 0;
        for (CartItem item : cartItems.values()) {
            totalAmount += item.getProduct().unitPrice * item.getQuantity();
        }
    }
    
    public int size()
    {
    	return cartItems.size();
    }
}
