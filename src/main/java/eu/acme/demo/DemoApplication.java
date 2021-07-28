package eu.acme.demo;

import org.modelmapper.ModelMapper;
import org.modelmapper.PropertyMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import eu.acme.demo.domain.Order;
import eu.acme.demo.web.dto.OrderDto;

@SpringBootApplication
@EnableJpaAuditing
public class DemoApplication {

	@Bean
	@Autowired
	public ModelMapper modelMapper() {
		ModelMapper modelMapper = new ModelMapper();
		modelMapper.typeMap(OrderDto.class, Order.class).addMappings(mapper -> {
			  mapper.map(src -> src.getTotalAmount(),
			      Order::setItemTotalAmount);
			});
		modelMapper.typeMap(Order.class, OrderDto.class).addMappings(mapper -> {
			  mapper.map(src -> src.getItemTotalAmount(),
			      OrderDto::setTotalAmount);
			});
		return modelMapper;
	}
	
	/**
	 * Mapping for converting Code objects into String Code Keys on ItemDto.
	 * To use:
	 * modelMapper.addMappings(itemDtoMap);
	 * 
	 */
	
	public static void main(String[] args) {
        SpringApplication.run(DemoApplication.class, args);
    }

}
