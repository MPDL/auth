package de.mpg.mpdl.auth;

import org.keycloak.admin.client.Keycloak;
import org.keycloak.representations.idm.RealmRepresentation;

public class KCTestClient {
	
	public static void main(String...strings ) {
		cloak();
	}
	public static void cloak() { 
	Keycloak keycloak = Keycloak.getInstance(
		    "http://localhost:8180/auth",
		    "master",
		    "kcadmin",
		    "kloake",
		    "admin-cli");
		RealmRepresentation realm = keycloak.realm("demo").toRepresentation();
		System.out.println(realm.getId() + " is " + realm.isEnabled());
		System.out.println(realm.getId() + " has " + realm.getUsers().size() + " users.");
		realm.getUsers().forEach(u -> System.out.println(u.getClass().getCanonicalName()));
	}
}
