package org.broadleafcommerce.order.domain;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MapKey;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.TableGenerator;
import javax.persistence.Transient;

import org.broadleafcommerce.common.domain.Auditable;
import org.broadleafcommerce.offer.domain.Offer;
import org.broadleafcommerce.offer.domain.OfferImpl;
import org.broadleafcommerce.profile.domain.ContactInfo;
import org.broadleafcommerce.profile.domain.ContactInfoImpl;
import org.broadleafcommerce.profile.domain.Customer;
import org.broadleafcommerce.profile.domain.CustomerImpl;
import org.broadleafcommerce.type.OrderType;
import org.broadleafcommerce.util.money.Money;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name = "BLC_ORDER")
public class OrderImpl implements Order, Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(generator = "OrderId", strategy = GenerationType.TABLE)
    @TableGenerator(name = "OrderId", table = "SEQUENCE_GENERATOR", pkColumnName = "ID_NAME", valueColumnName = "ID_VAL", pkColumnValue = "OrderImpl", allocationSize = 1)
    @Column(name = "ORDER_ID")
    private Long id;

    @Embedded
    private Auditable auditable;

    @Column(name = "TYPE")
    @Enumerated(EnumType.STRING)
    private OrderType type;

    @ManyToOne(targetEntity = CustomerImpl.class)
    @JoinColumn(name = "CUSTOMER_ID", nullable = false)
    private Customer customer;
	
	//TODO: changed FetchType to lazy because table did not exist yet; this should be revisited
    @ManyToOne(fetch = FetchType.LAZY, targetEntity = ContactInfoImpl.class)
    @JoinColumn(name = "CONTACT_INFO_ID")
    private ContactInfo contactInfo;

    @Column(name = "ORDER_STATUS")
    private String status;

    @Column(name = "ORDER_SUBTOTAL")
    private BigDecimal subTotal;

    @Column(name = "ORDER_TOTAL")
    private BigDecimal total;

    @OneToMany(mappedBy = "id", targetEntity = OrderItemImpl.class)
    @MapKey(name = "id")
    private List<OrderItem> orderItems;

    @OneToMany(mappedBy = "orderId", targetEntity = FulfillmentGroupImpl.class)
    @MapKey(name = "id")
    private List<FulfillmentGroup> fulfillmentGroups;

    @OneToMany(mappedBy = "id", targetEntity = OfferImpl.class)
    @MapKey(name = "id")
    private List<Offer> candidateOffers;
    
    @Transient
    private boolean markedForOffer;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Auditable getAuditable() {
        return auditable;
    }

    public void setAuditable(Auditable auditable) {
        this.auditable = auditable;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String orderStatus) {
        this.status = orderStatus;
    }

    public Money getSubTotal() {
        return subTotal == null ? null : new Money(subTotal);
    }

    public void setSubTotal(Money subTotal) {
        this.subTotal = Money.toAmount(subTotal);
    }

    public void setCandidateOffers(List<Offer> candidateOffers) {
        this.candidateOffers = candidateOffers;
    }

    public Money getTotal() {
        return total == null ? null : new Money(total);
    }

    public void setTotal(Money orderTotal) {
        this.total = Money.toAmount(orderTotal);
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public ContactInfo getContactInfo() {
        return contactInfo;
    }

    public void setContactInfo(ContactInfo contactInfo) {
        this.contactInfo = contactInfo;
    }

    public OrderType getType() {
        return type;
    }

    public void setType(OrderType type) {
        this.type = type;
    }

    public List<OrderItem> getOrderItems() {
        return orderItems;
    }

    public void setOrderItems(List<OrderItem> orderItems) {
        this.orderItems = orderItems;
    }

    public List<FulfillmentGroup> getFulfillmentGroups() {
        return fulfillmentGroups;
    }

    public void setFulfillmentGroups(List<FulfillmentGroup> fulfillmentGroups) {
        this.fulfillmentGroups = fulfillmentGroups;
    }

    @Override
    public void addCandidateOffer(Offer offer) {
        candidateOffers.add(offer);
    }

    @Override
    public List<Offer> getCandidateOffers() {
        return candidateOffers;
    }

    @Override
    public void setCandaditeOffers(List<Offer> offers) {
        this.candidateOffers = offers;

    }

	@Override
	public void removeAllOffers() {
		if (candidateOffers != null) {
    		candidateOffers.clear();
    	}
		if (getOrderItems() != null) {
			for (OrderItem item : getOrderItems()) {
				item.removeAllOffers();
			}
		}

		if (getFulfillmentGroups() != null) {
			for (FulfillmentGroup fg : getFulfillmentGroups()) {
				fg.removeAllOffers();
			}
		}
	}

	public boolean isMarkedForOffer() {
		return markedForOffer;
	}

	public void setMarkedForOffer(boolean markedForOffer) {
		this.markedForOffer = markedForOffer;
	}
	
	
}
