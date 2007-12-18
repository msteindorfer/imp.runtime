/*
 * (C) Copyright IBM Corporation 2007
 * 
 * This file is part of the Eclipse IMP.
 */
package org.eclipse.imp.runtime;

import org.eclipse.imp.language.LanguageRegistry;
import org.eclipse.imp.preferences.PreferenceCache;
import org.eclipse.imp.preferences.PreferenceConstants;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IStartup;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

/**
 * The main plugin class to be used in the desktop.
 */
public class RuntimePlugin extends PluginBase implements IStartup {
    public static final String IMP_RUNTIME= "org.eclipse.imp.runtime"; // must match plugin ID in MANIFEST.MF

    /**
     * The (unqualified) ID of the language descriptor extension point.
     */
    public static String LANGUAGE_DESCRIPTOR= "languageDescription";

    // The singleton instance.
    private static RuntimePlugin sPlugin;

    public RuntimePlugin() {
	sPlugin= this;
    }

    /**
     * Returns the singleton instance.
     */
    public static RuntimePlugin getInstance() {
	return sPlugin;
    }

    public String getID() {
	return IMP_RUNTIME;
    }

    /**
     * This method is called upon plug-in activation
     */
    public void start(BundleContext context) throws Exception {
	super.start(context);

	// Initialize the Preferences fields with the preference store data.
        IPreferenceStore prefStore= getPreferenceStore();

        PreferenceCache.emitMessages= prefStore.getBoolean(PreferenceConstants.P_EMIT_MESSAGES);
    }

    /**
     * This method is called when the plug-in is stopped
     */
    public void stop(BundleContext context) throws Exception {
	super.stop(context);
	sPlugin= null;
    }

    /**
     * Returns an image descriptor for the image file at the given plug-in relative path.
     * 
     * @param path
     *                the path
     * @return the image descriptor
     */
    public static ImageDescriptor getImageDescriptor(String path) {
	return AbstractUIPlugin.imageDescriptorFromPlugin(IMP_RUNTIME, path);
    }

    public void earlyStartup() {
	LanguageRegistry.registerLanguages();
    }
}
