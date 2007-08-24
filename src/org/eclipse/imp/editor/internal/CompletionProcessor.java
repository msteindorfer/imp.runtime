/*
 * (C) Copyright IBM Corporation 2007
 * 
 * This file is part of the Eclipse IMP.
 */
package org.eclipse.imp.editor.internal;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.imp.core.ErrorHandler;
import org.eclipse.imp.language.ILanguageService;
import org.eclipse.imp.language.Language;
import org.eclipse.imp.parser.IModelListener;
import org.eclipse.imp.parser.IParseController;
import org.eclipse.imp.services.IContentProposer;
import org.eclipse.imp.utils.ExtensionPointFactory;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.contentassist.IContentAssistProcessor;
import org.eclipse.jface.text.contentassist.IContextInformation;
import org.eclipse.jface.text.contentassist.IContextInformationValidator;

public class CompletionProcessor implements IContentAssistProcessor, IModelListener {
	private final IContextInformation[] NO_CONTEXTS= new IContextInformation[0];

	private ICompletionProposal[] NO_COMPLETIONS= new ICompletionProposal[0];

	private IParseController parseController;

	private IContentProposer contentProposer;

//	private HippieProposalProcessor hippieProcessor= new HippieProposalProcessor();

	public CompletionProcessor() {}

	public void setLanguage(Language language) {
	    contentProposer= (IContentProposer) ExtensionPointFactory.createExtensionPoint(language, ILanguageService.CONTENT_PROPOSER_SERVICE);
	}

	public AnalysisRequired getAnalysisRequired() {
	    return AnalysisRequired.TYPE_ANALYSIS;
	}

	public ICompletionProposal[] computeCompletionProposals(ITextViewer viewer, int offset) {
	    try {
		if (parseController != null && contentProposer != null) {
		    return contentProposer.getContentProposals(parseController, offset, viewer);
		}
		// TODO Once we move to 3.2, delegate to the HippieProposalProcessor
//		return hippieProcessor.computeCompletionProposals(viewer, offset);
	    } catch (Throwable e) {
		ErrorHandler.reportError("Universal Editor Error", e);
	    }
	    return NO_COMPLETIONS;
	}

	public IContextInformation[] computeContextInformation(ITextViewer viewer, int offset) {
	    return NO_CONTEXTS;
	}

	public char[] getCompletionProposalAutoActivationCharacters() {
	    return null;
	}

	public char[] getContextInformationAutoActivationCharacters() {
	    return null;
	}

	public IContextInformationValidator getContextInformationValidator() {
	    return null;
	}

	public String getErrorMessage() {
	    return null;
	}

	public void update(IParseController parseResult, IProgressMonitor monitor) {
	    this.parseController= parseResult;
	}
    }