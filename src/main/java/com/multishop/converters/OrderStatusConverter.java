package com.multishop.converters;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import com.multishop.enums.OrderStatus;

@Converter(autoApply = true)
public class OrderStatusConverter implements AttributeConverter<OrderStatus, Integer> {

    @Override
    public Integer convertToDatabaseColumn(OrderStatus status) {
        if (status == null) {
            return null;
        }
        return status.getCode();  // Assuming OrderStatus has a method getCode()
    }

    @Override
    public OrderStatus convertToEntityAttribute(Integer dbData) {
        if (dbData == null) {
            return null;
        }
        return OrderStatus.fromCode(dbData);
    }
    
    public OrderStatus convert(String source) 
    {
        if (source == null || source.trim().isEmpty()) 
        {
            return null;
        }
        try 
        {
            int code = Integer.parseInt(source);
            return OrderStatus.fromCode(code);
        } catch (NumberFormatException e) 
        {
            throw new IllegalArgumentException("Invalid OrderStatus: " + source);
        }
    }
}
