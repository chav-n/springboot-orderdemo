package eu.acme.demo.web.dto;

import java.math.BigDecimal;
import java.util.UUID;

public class OrderItemDto {
    private UUID itemId;
    private int units;
    private BigDecimal unitPrice;
    private BigDecimal totalPrice;

    public UUID getItemId() {
        return itemId;
    }

    public void setItemId(UUID itemId) {
        this.itemId = itemId;
    }

    public int getUnits() {
        return units;
    }

    public void setUnits(int units) {
        this.units = units;
    }

    public BigDecimal getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(BigDecimal unitPrice) {
        this.unitPrice = unitPrice;
    }

    public BigDecimal getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(BigDecimal totalPrice) {
        this.totalPrice = totalPrice;
    }

	@Override
	public String toString() {
		return "OrderItemDto [itemId=" + itemId + ", units=" + units + ", unitPrice=" + unitPrice + ", totalPrice="
				+ totalPrice + "]";
	}

    /**
     * Test for equality.
     * 
     * Compare itemId fields only if both are not null.
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
		OrderItemDto other = (OrderItemDto) obj;
		if (itemId != null && other.itemId != null && !itemId.equals(other.itemId))
			return false;
		if (totalPrice == null) {
			if (other.totalPrice != null)
				return false;
		} else if (!totalPrice.setScale(2).equals(other.totalPrice.setScale(2)))
			return false;
		if (unitPrice == null) {
			if (other.unitPrice != null)
				return false;
		} else if (!unitPrice.setScale(2).equals(other.unitPrice.setScale(2)))
			return false;
		if (units != other.units)
			return false;
		return true;
	}
}
