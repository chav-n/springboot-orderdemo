package eu.acme.demo.web.dto;

import eu.acme.demo.domain.enums.OrderStatus;

import java.math.BigDecimal;
import java.util.UUID;

public class OrderLiteDto {
    private UUID id;
    private OrderStatus status;
    private String description;
    /**
     * reference code used by client system to track order
     */
    private String clientReferenceCode;
    private BigDecimal totalAmount;
    private int itemCount;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getClientReferenceCode() {
        return clientReferenceCode;
    }

    public void setClientReferenceCode(String clientReferenceCode) {
        this.clientReferenceCode = clientReferenceCode;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public int getItemCount() {
        return itemCount;
    }

    public void setItemCount(int itemCount) {
        this.itemCount = itemCount;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }
    
    public static OrderLiteDto strictCopy(OrderLiteDto orig) {
    	OrderLiteDto copy = new OrderLiteDto();
    	copy.setClientReferenceCode(orig.getClientReferenceCode());
    	copy.setDescription(orig.getDescription());
    	copy.setId(orig.getId());
    	copy.setItemCount(orig.getItemCount());
    	copy.setStatus(orig.getStatus());
    	copy.setTotalAmount(orig.getTotalAmount());
    	
    	return copy;
    }

    /**
     * Test for equality.
     * 
     * Compare id fields only if both are not null.
     * Convert BigDecimal values to values of scale "2".
     * 
     */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		OrderLiteDto other = (OrderLiteDto) obj;
		if (clientReferenceCode == null) {
			if (other.clientReferenceCode != null)
				return false;
		} else if (!clientReferenceCode.equals(other.clientReferenceCode))
			return false;
		if (description == null) {
			if (other.description != null)
				return false;
		} else if (!description.equals(other.description))
			return false;
		
		if (id != null && other.id != null && !id.equals(other.id))
			return false;
		if (itemCount != other.itemCount)
			return false;
		if (status != other.status)
			return false;
		if (totalAmount == null) {
			if (other.totalAmount != null)
				return false;
		} else if (!totalAmount.setScale(2).equals(other.totalAmount.setScale(2)))
			return false;
		return true;
	}

}
