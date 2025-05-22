package com.multishop.config;

import org.springframework.core.convert.converter.ConverterFactory;
import org.springframework.format.FormatterRegistry;
import org.springframework.lang.NonNull;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.multishop.converters.OrderStatusConverter;

public class WebConfig implements WebMvcConfigurer
{
	private final OrderStatusConverter orderStatusConverter;

    public WebConfig(OrderStatusConverter orderStatusConverter) 
    {
        this.orderStatusConverter = orderStatusConverter;
    }

    @Override
    public void addFormatters(@NonNull FormatterRegistry registry) 
    {
        registry.addConverterFactory((ConverterFactory<?, ?>) orderStatusConverter);
    }
}
