package de.mpg.mpdl.auth.model;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
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
@Table(name="groups")
public class UserGroup extends AbstractEntity {
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	
	@Column(name="name", unique=true, nullable=false)
    private String name;
    
    @ManyToMany(fetch = FetchType.EAGER, cascade = {CascadeType.MERGE})
    @JoinTable(name = "groups_grants", 
             joinColumns = { @JoinColumn(name = "group_id") }, 
             inverseJoinColumns = { @JoinColumn(name = "grant_id") })
    private Set<Grant> grants = new HashSet<Grant>();;
    
    @ManyToMany(fetch = FetchType.EAGER, cascade = {CascadeType.MERGE})
    @JoinTable(name = "groups_users",
    		joinColumns = { @JoinColumn(name = "group_id") },
    		inverseJoinColumns = { @JoinColumn(name = "user_id") })
    private Set<UserAccount> userMembers = new HashSet<UserAccount>();
    
    @ElementCollection
    private Set<String> ouMembers = new HashSet<>();
    
    @Version
    private long version;
    
    UserGroup() {}
    
    public UserGroup(String name) {
    	this.name = name;
    }

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Set<Grant> getGrants() {
		return grants;
	}

	public void setGrants(Set<Grant> grants) {
		this.grants = grants;
	}

	public Set<UserAccount> getUserMembers() {
		return userMembers;
	}

	public void setUserMembers(Set<UserAccount> userMembers) {
		this.userMembers = userMembers;
	}

	public Set<String> getOuMembers() {
		return ouMembers;
	}

	public void setOuMembers(Set<String> ouMembers) {
		this.ouMembers = ouMembers;
	}

	public long getVersion() {
		return version;
	}

	public void setVersion(long version) {
		this.version = version;
	}

}
