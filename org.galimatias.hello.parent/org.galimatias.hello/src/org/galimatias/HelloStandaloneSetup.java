/*
 * generated by Xtext 2.18.0
 */
package org.galimatias;


/**
 * Initialization support for running Xtext languages without Equinox extension registry.
 */
public class HelloStandaloneSetup extends HelloStandaloneSetupGenerated {

	public static void doSetup() {
		new HelloStandaloneSetup().createInjectorAndDoEMFRegistration();
	}
}
