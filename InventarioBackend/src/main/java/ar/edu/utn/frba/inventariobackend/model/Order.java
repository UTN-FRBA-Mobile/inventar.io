package ar.edu.utn.frba.inventariobackend.model;

import jakarta.persistence.*;
import lombok.Getter;
import java.time.LocalDateTime;

/**
 * Represents an order, typically for incoming products or internal tasks.
 * Orders are identified/initiated via QR code scanning.
 */
@Getter
@Entity
@Table(name = "incoming_order")
public class Order {
	/**
	 * Auto-generated unique identifier for the order.
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "order_seq")
	@SequenceGenerator(name = "order_seq", sequenceName = "order_seq", allocationSize = 1)
	private Long id;

	/**
	 * The current state of the order.
	 */
	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private Status status;

	/**
	 * The date and time when the order was created.
	 */
	@Column(nullable = false, updatable = false)
	private LocalDateTime creationDate;

	/**
	 * The scheduled date and time for the order to be processed or fulfilled.
	 */
	private LocalDateTime scheduledDate;

	/**
	 * The date and time when the order was last modified.
	 */
	private LocalDateTime lastModifiedDate;

	/**
	 * Entity that sent or originated the order.
	 */
	private String sender;

	/**
	 * Location id associated to this order
	 */
	@Column(nullable = false)
	private Long idLocation;

	/**
	 * Default constructor required by JPA.
	 */
	public Order() {
	}

	/**
	 * Constructs a new {@code Order}.
	 *
	 * @param status
	 *            the initial state of the order
	 * @param creationDate
	 *            the creation date
	 * @param scheduledDate
	 *            the scheduled date
	 * @param lastModifiedDate
	 *            the last modified date
	 * @param idLocation
	 *            the id of the location
	 * @param sender
	 *            the originator of the order
	 */
	public Order(Status status, LocalDateTime creationDate, LocalDateTime scheduledDate, LocalDateTime lastModifiedDate,
			Long idLocation, String sender) {
		this.status = status;
		this.creationDate = creationDate;
		this.scheduledDate = scheduledDate;
		this.lastModifiedDate = lastModifiedDate;
		this.idLocation = idLocation;
		this.sender = sender;
	}

	/**
	 * Updates the status of the order.
	 */
	public void updateStatus(Status status) {
		this.status = status;
	}

	/**
	 * Set creation and last modified date when initially created.
	 */
	@PrePersist
	protected void onCreate() {
		this.creationDate = LocalDateTime.now();
		this.lastModifiedDate = LocalDateTime.now();
	}

	/**
	 * Update the last modified date once we perform an update on the order.
	 */
	@PreUpdate
	protected void onUpdate() {
		this.lastModifiedDate = LocalDateTime.now();
	}
}