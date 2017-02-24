package de.mpg.mpdl.auth.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

@Entity
@Table(name = "grants", uniqueConstraints = { @UniqueConstraint(columnNames = { "role", "target_id" }) })
public class Grant extends AbstractEntity {

	public enum TargetType {
		CONTEXT, ITEM
	}

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	@OneToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "role", nullable = false)
	private UserRole role;

	@Column(name = "target_type", nullable = false)
	private TargetType targetType;

	@Column(name = "target_id", nullable = false)
	private String targetId;

	Grant() {
	}

	public Grant(UserRole role, TargetType targetType, String targetId) {
		this.role = role;
		this.targetType = targetType;
		this.targetId = targetId;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public UserRole getRole() {
		return role;
	}

	public void setRole(UserRole role) {
		this.role = role;
	}

	public TargetType getTargetType() {
		return targetType;
	}

	public void setTargetType(TargetType targetType) {
		this.targetType = targetType;
	}

	public String getTargetId() {
		return targetId;
	}

	public void setTargetId(String targetId) {
		this.targetId = targetId;
	}

}
