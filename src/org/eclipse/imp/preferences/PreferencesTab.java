/*
 * (C) Copyright IBM Corporation 2007
 * 
 * This file is part of the Eclipse IMP.
 */
package org.eclipse.imp.preferences;

import java.util.HashMap;
import java.util.Iterator;

import org.eclipse.imp.preferences.fields.FieldEditor;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.TabItem;

public abstract class PreferencesTab
{
	// The following fields are shared among specific
	// subtypes of preferences tab, which generally represent
	// the various levels on which preferences are set.
	// However, most of the fields have language-dependent
	// impementations, so they are not set here.
	
	// The page on which this tab occurs
	protected TabbedPreferencesPage prefPage = null;
	
	// Utilities for creating and managing preference fields
	protected PreferencesUtilities prefUtils;
	
	// The service for storing and accessing preference values
	protected IPreferencesService prefService;
	
	// The fields that occur on this tab
	protected FieldEditor[] fields = null;
	
	// Whether this tab is valid, that is, whether
	// all of its fields are valid
	protected boolean isValid = true;
	
	// The buttons on this tab
	protected Control[] buttons = null;
	
	
	
	// SMS 17 Nov 2006
	protected TabItem tabItem = null;
	
	public TabItem getTabItem() {
		return tabItem;
	}
	
	
	
	/**
	 * To create specific preferences fields.  At this level does nothing.
	 * To be overridden.
	 *
	 */
	protected FieldEditor[] createFields(Composite composite) {
		// TODO:  create specific preferences fields here
		System.out.println("PreferencesTab.createFields():  unimplemented");
		return null;
	}
	
	
	/**
	 * To create specific preferences fields.  At this level does nothing.
	 * To be overridden.
	 * 
	 * This version is adapted for use in automatically generating the tabs.
	 */
	protected FieldEditor[] createFields(
			TabbedPreferencesPage page,
			PreferencesTab tab,
			String level,
			Composite parent,
			IPreferencesService prefsService)
    {
		// TODO:  create specific preferences fields here
		System.out.println("PreferencesTab.createFields():  unimplemented");
		return null;
	}
	
	
	/*
	 * Methods to set or clear an "error mark" on tab labels
	 */


	public void setErrorMarkOnTab() {
		if (errorMessages.isEmpty()) {
			return;
		}
	   	String label = tabItem.getText();
	   	if (!label.startsWith(Markings.TAB_ERROR_MARK)) {
	   		label = Markings.TAB_ERROR_MARK + label;
	   	}
	   	if (!label.endsWith(Markings.TAB_ERROR_MARK)) {
	   		label = label + Markings.TAB_ERROR_MARK;
	   	}
	   	tabItem.setText(label);
	}

	public void clearErrorMarkOnTab() {
		if (!errorMessages.isEmpty()) {
			return;
		}
	   	String label = tabItem.getText();
	   	if (label.startsWith(Markings.TAB_ERROR_MARK)) {
	   		label = label.substring(2);
	   	}
	   	if (label.endsWith(Markings.TAB_ERROR_MARK)) {
	   		label = label.substring(0, label.length()-2);
	   	}
	   	tabItem.setText(label);
	}
	
	
	/*
	 * Methods and a field related to setting and clearing error messages
	 * for the tab	
	 */


	public HashMap errorMessages = new HashMap();
	

	public void clearErrorMessages(Object key) {
		String currentMessage = (String) errorMessages.get(key);
//		System.out.println("Tab = " + tabItem.getText() + ", field = " + ((FieldEditor)key).getPreferenceName() + ":  clearErrorMessages(), clearing message = " + currentMessage);
		errorMessages.remove(key);
		if (prefPage == null) return;
		if (errorMessages.size() == 0) {
//			System.out.println("Tab = " + tabItem.getText() + ", field = " + ((FieldEditor)key).getPreferenceName() + ":  clearErrorMessages(), setting message to null");
			prefPage.setErrorMessage(null);	// to clear
			return;
		}
		Iterator it = errorMessages.keySet().iterator();
		Object nextKey = it.next();
		String nextMessage = (String) errorMessages.get(nextKey);
//		System.out.println("Tab = " + tabItem.getText() + ", field = " + ((FieldEditor)key).getPreferenceName() + "clearErrorMessages(), setting message to:  " + nextMessage);
		prefPage.setErrorMessage(nextMessage);
	}
	
	
	public void setErrorMessage(Object key, String msg) {
		if (key != null && msg != null) {
//			System.out.println("Tab = " + tabItem.getText() + ", field = " + ((FieldEditor)key).getPreferenceName() + ":  setErrorMessage(), setting message to:  " + msg);
			errorMessages.remove(key);
			errorMessages.put(key, msg);
			prefPage.setErrorMessage(msg);
		}
	}
	
	
	
	/*
	 * 	A method to clear the modified mark on field labels
	 */
	
	public void clearModifiedMarksOnLabels()
	{	
		for (int i = 0; i < fields.length; i++) {
			fields[i].clearModifiedMarkOnLabel();
		}
	}
	
	
	
	/*
	 * A listener class for responding to selection of the tab
	 */

	
	/**
	 * A listener for responding to selection of the tab, mainly by
	 * clearing or setting the error message on the page according to
	 * whether there are error messages associated with the tab.
	 */
	public class TabSelectionListener implements SelectionListener
	{
		private PreferencePage page = null;
		private TabItem item = null;
		
		public TabSelectionListener(PreferencePage page, TabItem item) {
			this.page = page;
			this.item = item;
		}
		
		public void widgetSelected(SelectionEvent e) {
			if (prefPage == null) return;
			if (e.item != item) return;
			if (errorMessages.size() == 0) {
				prefPage.setErrorMessage(null);
				return;
			}
			
			Iterator it = errorMessages.keySet().iterator();
			Object nextKey = it.next();
			String nextMessage = (String) errorMessages.get(nextKey);
			prefPage.setErrorMessage(nextMessage);
		}
		
		public void widgetDefaultSelected(SelectionEvent e) {}
	}

	
	/*
	 * Methods for responding to buttons, with very simple implementations.
	 * 
	 * SMS 4 Dec 2006:  These could be made abstract, but why not provide
	 * a minimal useful representation?
	 */


	/**
	 * Apply the currently set preferences to the preferences model.
	 * More specifically store each field and clear its "modified" mark
	 * (not all fields will be modified, but that's not a problem).
	 * 
	 * This implementation of performApply is adequate for the provided
	 * implementations of the Default, Configuration, and Instance tabs
	 * but needs to be overridden in the Project tab to address the
	 * situation in which the project is not set.
	 */
	public void performApply()
	{
		for (int i = 0; i < fields.length; i++) {
			fields[i].store();
			fields[i].clearModifiedMarkOnLabel();
		}
	}	
	
	
	
	public boolean performCancel() {
		return true;
	}
	
	
	public void performDefaults() {
		
	}
	
	
	/**
	 * Apply the currently set preferences to the preferences model.
	 * More specifically store each field and clear its "modified" mark
	 * (not all fields will be modified, but that's not a problem).
	 * 
	 * This implementation of performOk is adequate for the provided
	 * implementations of the Default, Configuration, and Instance tabs
	 * but needs to be overridden in the Project tab to address the
	 * situation in which the project is not set and to unset the
	 * project when it is set.
	 * 
	 */
	public boolean performOk() {
		for (int i = 0; i < fields.length; i++) {
			fields[i].store();
			fields[i].clearModifiedMarkOnLabel();
		}
		return true;
	}

		
	
	
	/*
	 * Methods to set and get the "valid" state of the tab
	 * and to update the enabled state of buttons on the
	 * tab according to the valid state.
	 */
		
	
	/**
	 * Set the "valid" state of the tab according to the given
	 * value in conjunction with the occurrence of error messages.
	 * 
	 * Note:  This method will be called (indirectly) whenever a
	 * preference field is validated.  However, the state of the
	 * tab overall depends on the conjunction of the states of
	 * all of its fields.  The value provided here represents the
	 * state of one field; 
	 */
	public void setValid(boolean state) {
		// SMS 8 Dec 2006:  added second conjunct
        isValid = /*state &&*/ errorMessages.size() == 0;
        if (!isValid) {
        	setErrorMarkOnTab();
        } else {
        	clearErrorMarkOnTab();
        }
        prefPage.notifyState(isValid);
        updateButtons();
	}
	

	public boolean isValid() {
		//int numMessages = errorMessages.size();
		return isValid;
	}
	
	
	public void updateButtons() {
		if (buttons != null) {
        	//System.out.println("SPT.updateButtons():  buttons not null");
			for (int i = 0; i < buttons.length; i++) {
				Button button = (Button) buttons[i];
				if (button != null)
					// TODO:  define string constants for button texts
					if (button.getText().startsWith("Restore"))
						continue;
					button.setEnabled(isValid() /*	&& errorMessages.size() == 0*/);
			}
		} else {
        	//System.out.println("SPT.updateButtons():  buttons null");
		}
		
	}
	
}
