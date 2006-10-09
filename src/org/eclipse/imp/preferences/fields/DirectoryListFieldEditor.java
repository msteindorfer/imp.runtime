package org.eclipse.uide.preferences.fields;

import java.io.File;
import java.util.Stack;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Text;
import org.eclipse.uide.preferences.ISafariPreferencesService;
import org.eclipse.uide.preferences.PreferenceDialogConstants;
import org.eclipse.uide.preferences.SafariPreferencesTab;
import org.eclipse.jface.preference.PreferencePage;

public class SafariDirectoryListFieldEditor extends SafariStringButtonFieldEditor {

	
	protected PreferencePage prefPage = null;
	protected SafariPreferencesTab prefTab = null;
	
	public SafariDirectoryListFieldEditor(
			PreferencePage page, SafariPreferencesTab tab,
    		ISafariPreferencesService service, String level, String name, String labelText,
    		int width, int strategy, Composite parent)
    {
    	super(page, tab, service, level, name, labelText, width, strategy, parent);
    	prefPage = page;
    	prefTab = tab;
    	this.getChangeControl(parent).setText(PreferenceDialogConstants.BROWSE_LABEL);
    }
	
	
    /**
     * Creates a SAFARI directory-list field editor.
     * Use the method <code>setTextLimit</code> to limit the text.
     * 
     * @param name the name of the preference this field editor works on
     * @param labelText the label text of the field editor
     * @param width the width of the text input field in characters,
     *  or <code>UNLIMITED</code> for no limit
     * @param parent the parent of the field editor's control
     */
    public SafariDirectoryListFieldEditor(
    		PreferencePage page, SafariPreferencesTab tab,
    		ISafariPreferencesService service, String level, String name, String labelText,
    		int width, Composite parent)
    {
        super(page, tab, service, level, name, labelText, width, VALIDATE_ON_KEY_STROKE, parent);
    	prefPage = page;
    	prefTab = tab;
    	this.getChangeControl(parent).setText(PreferenceDialogConstants.BROWSE_LABEL);
    }
    
    
    /**
     * Creates a SAFARI directory-list field editor.
     * Use the method <code>setTextLimit</code> to limit the text.
     * 
     * @param name the name of the preference this field editor works on
     * @param labelText the label text of the field editor
     * @param parent the parent of the field editor's control
     */
    public SafariDirectoryListFieldEditor(
    		PreferencePage page, SafariPreferencesTab tab,
    		ISafariPreferencesService service, String level, String name, String labelText, Composite parent)
    {
        super(page, tab, service, level, name, labelText, parent);
    	prefPage = page;
    	prefTab = tab;
    	this.getChangeControl(parent).setText(PreferenceDialogConstants.BROWSE_LABEL);
    }
    
    
    
    /*
     * Below copied from DirectoryListFieldEditor
     */
    
    
    protected String changePressed() {
        Text text= getTextControl();
        String curText= text.getText();
        Point sel= text.getSelection();
        boolean replace= (text.getSelectionCount() > 0); // any non-empty selection?
        boolean replaceAll= text.getSelectionCount() == text.getCharCount(); // all selected?
        File f = new File(getTextControl().getText());

        if (!f.exists())
    	f = null;
        File d = getDirectory(f);
        if (d == null)
    	return null;

        final String dirPath= d.getAbsolutePath();

        if (replaceAll)
    	return dirPath;

        int prevSep= curText.lastIndexOf(';', sel.x);

        if (prevSep < 0)
    	prevSep= 0;

        String leadingPart= (prevSep > 0) ? curText.substring(0, prevSep) + ';' : "";

        if (replace) {
    	// Replace the selected entries with the browse result
    	int followSep= curText.indexOf(';', sel.y);

    	if (followSep < 0)
    	    followSep= curText.length();
    	return leadingPart + dirPath + curText.substring(followSep);
        }

        if (sel.x == text.getCharCount()) // caret at end of field? => append new entry
    	return curText + ';' + dirPath;

        // insertion point in middle of text; insert new entry before this entry
        return leadingPart + dirPath + (prevSep > 0 ? "" : ";") + curText.substring(prevSep);
    }
    
    
    protected boolean doCheckState() {
    	
        //String path= getTextControl().getText();
        //if (path.length() == 0 && isEmptyStringAllowed()) {
        //	return true;
        //}
        
        String msg = null;

        // Here we check for empty or null strings, although
        // this may very well be checked at a higher level
        // (so we might not ever get here with this problem)
        String path = getTextControl().getText();
        if (path != null)
            path = path.trim();
        else
            path = "";//$NON-NLS-1$
        if (path.length() == 0 && !isEmptyStringAllowed()) {
                msg = "Path length is zero when empty string is not allowed";
                setErrorMessage(msg);
                return false;
        }
        
        // Check for balanced quotes
        final String singleQuote = "'";
        final String doubleQuote = "\"";
        Stack stack = new Stack();
        for (int i = 0; i < path.length(); i++) {
        	if (path.charAt(i) == '\'') {
        		if (!stack.empty() && singleQuote.equals(stack.peek()))
        			stack.pop();
        		else
        			stack.push(singleQuote);
        	}
        	if (path.charAt(i) == '"') {
        		if (!stack.empty() && doubleQuote.equals(stack.peek()))
        			stack.pop();
        		else
        			stack.push(doubleQuote);
        	}
        }
        if (stack.size() != 0)
        	return false;

        // Now validate list segments between quotes
        path = path.replace("\"", "'");
        String[] splits = path.split("'");       
        boolean splitsVerified = true;
        for (int i = 0; i < splits.length; i++) {
        	splitsVerified = splitsVerified && doCheckState(splits[i]);
        	if (!splitsVerified) return false;
        }
        return true;
    }
    
    
    protected boolean doCheckState(String path)
    {	// This is the real work of the original doCheckState()
        if (path.length() == 0) {
        	return true;
        }
        
        String[] pathElems= path.split(";");

        for(int i= 0; i < pathElems.length; i++) {
	    	String pathElem= pathElems[i].trim();
	    	File dir= new File(pathElem);
	
	    	if (!dir.isDirectory()) {
	    		setErrorMessage("Path list contains a name that is not a directory name");
	    		return false;
	    	}
        }
        
        return true;
    }


    /**
     * Helper that opens the directory chooser dialog.
     * @param startingDirectory The directory the dialog will open in.
     * @return File File or <code>null</code>.
     * 
     */
    private File getDirectory(File startingDirectory) {
        DirectoryDialog fileDialog = new DirectoryDialog(getShell(), SWT.OPEN);
        if (startingDirectory != null)
    	fileDialog.setFilterPath(startingDirectory.getPath());
        String dir = fileDialog.open();
        if (dir != null) {
    	dir = dir.trim();
    	if (dir.length() > 0)
    	    return new File(dir);
        }
        return null;
    }

}