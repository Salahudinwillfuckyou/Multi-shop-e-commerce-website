package com.multishop.enums;

public enum OrderStatus 
{
	PENDING(0),
	IN_TRANSIT(1),
	DELIVERED(2);
	
	private final int code;
	
	OrderStatus(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }

    public static OrderStatus fromCode(int code) {
        for (OrderStatus status : OrderStatus.values()) {
            if (status.getCode() == code) {
                return status;
            }
        }
        throw new IllegalArgumentException("Invalid code for OrderStatus: " + code);
    }
    
    public static OrderStatus fromString(String value) 
    {
        try 
        {
            return OrderStatus.valueOf(value.toUpperCase().replace(" ", "_"));
        } 
        catch (IllegalArgumentException e) 
        {
            return null;
        }
    }
}
