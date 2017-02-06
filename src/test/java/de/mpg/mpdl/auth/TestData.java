package de.mpg.mpdl.auth;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import de.mpg.mpdl.auth.model.Grant;
import de.mpg.mpdl.auth.model.UserAccount;
import de.mpg.mpdl.auth.model.UserRole;

public class TestData {
	
	private final String testContext = "http://some.org/ctxs/ctx/testctx";
	private final String testOuId = "http://some.org/ous/ou/testou";
	
	private List<UserAccount> testUsersList;
	private Set<Grant> testGrantsSetDepositor;
	private Set<Grant> testGrantsSetModerator;
	private Grant testGrantDepositor;
	private Grant testGrantModerator;
	private UserRole testRoleDepositor;
	private UserRole testRoleModerator;
	private UserRole testRoleAdministrator;
	private UserAccount testUserModerator;
	private UserAccount testUserDepositor;
	private UserAccount testUserAdministrator;
	
	public List<UserAccount> getTestUsersList() {
		testUsersList = new ArrayList<>();
		testUsersList.add(getTestUserDepositor());
		testUsersList.add(getTestUserModerator());
		return testUsersList;
	}
	public void setTestUsersList(List<UserAccount> testUsersList) {
		this.testUsersList = testUsersList;
	}
	public Set<Grant> getTestGrantsSetDepositor() {
		testGrantsSetDepositor = new HashSet<>();
		testGrantsSetDepositor.add(getTestGrantDepositor());
		return testGrantsSetDepositor;
	}
	public void setTestGrantsSetDepositor(Set<Grant> testGrantsSetDepositor) {
		this.testGrantsSetDepositor = testGrantsSetDepositor;
	}
	public Set<Grant> getTestGrantsSetModerator() {
		testGrantsSetModerator = new HashSet<>();
		testGrantsSetModerator.add(getTestGrantModerator());
		return testGrantsSetModerator;
	}
	public void setTestGrantsSetModerator(Set<Grant> testGrantsSetModerator) {
		this.testGrantsSetModerator = testGrantsSetModerator;
	}
	public Grant getTestGrantDepositor() {
		testGrantDepositor = new Grant(getTestRoleDepositor(), Grant.TargetType.CONTEXT, "vm44.mpdl.mpg.de/inge/pure_contexts/context/pure_28054");
		testGrantDepositor.setId(8L);
		return testGrantDepositor;
	}
	public void setTestGrantDepositor(Grant testGrantDepositor) {
		this.testGrantDepositor = testGrantDepositor;
	}
	public Grant getTestGrantModerator() {
		testGrantModerator = new Grant(getTestRoleModerator(), Grant.TargetType.CONTEXT, testContext);
		return testGrantModerator;
	}
	public void setTestGrantModerator(Grant testGrantModerator) {
		this.testGrantModerator = testGrantModerator;
	}
	public UserRole getTestRoleDepositor() {
		testRoleDepositor = new UserRole("DEPOSITOR");
		testRoleDepositor.setId(1L);
		return testRoleDepositor;
	}
	public void setTestRoleDepositor(UserRole testRoleDepositor) {
		this.testRoleDepositor = testRoleDepositor;
	}
	public UserRole getTestRoleModerator() {
		testRoleModerator = new UserRole("TEST_MODERATOR");
		return testRoleModerator;
	}
	public void setTestRoleModerator(UserRole testRoleModerator) {
		this.testRoleModerator = testRoleModerator;
	}
	public UserRole getTestRoleAdministrator() {
		testRoleAdministrator = new UserRole("TEST_ADMINISTRATOR");
		return testRoleAdministrator;
	}
	public void setTestRoleAdministrator(UserRole testRoleAdministrator) {
		this.testRoleAdministrator = testRoleAdministrator;
	}
	public UserAccount getTestUserModerator() {
		testUserModerator = UserAccount.getBuilder()
				.active(true)
				.email("testModerator@some.org")
				.firstName("Modera")
				.lastName("Tor")
				.ouid(testOuId)
				.password("temopwd123")
				.userid("testModerator")
				.grants(getTestGrantsSetModerator())
				.build();
		testGrantModerator.setId(2L);
						
		return testUserModerator;
	}
	public void setTestUserModerator(UserAccount testUserModerator) {
		this.testUserModerator = testUserModerator;
	}
	public UserAccount getTestUserDepositor() {
		testUserDepositor = UserAccount.getBuilder()
				.active(true)
				.email("testDepositor@some.org")
				.firstName("Deposi")
				.lastName("Tor")
				.ouid(testOuId)
				.password("tedepwd456")
				.userid("testDepositor")
				.grants(getTestGrantsSetDepositor())
				.build();
		testUserDepositor.setId(null);
		
		return testUserDepositor;
	}
	public void setTestUserDepositor(UserAccount testUserDepositor) {
		this.testUserDepositor = testUserDepositor;
	}
	public UserAccount getTestUserAdministrator() {
		return testUserAdministrator;
	}
	public void setTestUserAdministrator(UserAccount testUserAdministrator) {
		this.testUserAdministrator = testUserAdministrator;
	}
	
	

}
