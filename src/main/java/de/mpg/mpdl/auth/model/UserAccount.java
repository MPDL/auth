package de.mpg.mpdl.auth.model;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import javax.persistence.Version;

@Entity
@Table(name = "users")
public class UserAccount extends AbstractEntity {
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	
	@Column(name="userid", unique=true, nullable=false)
    private String userid;
     
    @Column(name="password", nullable=false)
    private String password;
         
    @Column(name="first_name", nullable=false)
    private String firstName;
 
    @Column(name="last_name", nullable=false)
    private String lastName;
 
    @Column(name="email", nullable=false)
    private String email;
 
    @Column(name="active", nullable=false)
    private boolean active;
    
    @Column(name="organization", nullable=false)
    private String ouid;
    
    @Column(name="escidoc_id", nullable=false)
    private String exid;
 
    /*
    @ManyToMany(fetch = FetchType.EAGER, cascade = {CascadeType.MERGE})
    @JoinTable(name = "users_roles", 
             joinColumns = { @JoinColumn(name = "user_id") }, 
             inverseJoinColumns = { @JoinColumn(name = "role_id") })
    private Set<UserRole> roles = new HashSet<UserRole>();
    */
    
    @ManyToMany(fetch = FetchType.EAGER, cascade = {CascadeType.MERGE})
    @JoinTable(name = "users_grants", 
             joinColumns = { @JoinColumn(name = "user_id") }, 
             inverseJoinColumns = { @JoinColumn(name = "grant_id") })
    private Set<Grant> grants = new HashSet<Grant>();
    
    @Version
    private long version;
    
    UserAccount() {}
    
    private UserAccount(Builder builder) {
    	
    	this.userid = builder.userid;
    	this.password = builder.password;
    	this.firstName = builder.firstName;
    	this.lastName = builder.lastName;
    	this.email = builder.email;
    	this.active = builder.active;
    	this.ouid = builder.ouid;
    	this.exid = builder.exid;
    	// this.roles = builder.roles;
    	this.grants = builder.grants;
    }

    public static Builder getBuilder() {
    	return new Builder();
    }
    
	public Long getId() {
		return id;
	}
	
	public void setId(Long id) {
		this.id = id;
	}

	public String getUserid() {
		return userid;
	}

	public String getPassword() {
		return password;
	}

	public String getFirstName() {
		return firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public String getEmail() {
		return email;
	}

	public boolean isActive() {
		return active;
	}
	
	public String getOuid() {
		return ouid;
	}

	/*
	public Set<UserRole> getRoles() {
		return roles;
	}
	*/
	
	public Set<Grant> getGrants() {
		return grants;
	}

	public String getExid() {
		return exid;
	}

	public void setExid(String exid) {
		this.exid = exid;
	}

	public long getVersion() {
		return version;
	}

	public static class Builder {
		
		private String userid;
		private String password;
		private String firstName;
		private String lastName;
		private String email;
		private boolean active;
		private String ouid;
		private String exid;
		// private Set<UserRole> roles;
		private Set<Grant> grants;
		
		private Builder() {}
		
		public Builder userid(String userid) {
			this.userid = userid;
			return this;
		}
		
		public Builder password(String password) {
			this.password = password;
			return this;
		}
		
		public Builder firstName(String firstName) {
			this.firstName = firstName;
			return this;
		}
		
		public Builder lastName(String lastName) {
			this.lastName = lastName;
			return this;
		}
		
		public Builder email(String email) {
			this.email = email;
			return this;
		}
		
		public Builder active(boolean active) {
			this.active = active;
			return this;
		}
		
		public Builder ouid(String ouid) {
			this.ouid = ouid;
			return this;
		}
		
		public Builder exid(String exid) {
			this.exid = exid;
			return this;
		}
		
		/*
		public Builder roles(Set<UserRole> roles) {
			this.roles = roles;
			return this;
		}
		*/
		
		public Builder grants(Set<Grant> grants) {
			this.grants = grants;
			return this;
		}
		
		public UserAccount build() {
			UserAccount user = new UserAccount(this);
			return user;
		}
	}
}
