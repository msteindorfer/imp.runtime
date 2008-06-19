package org.eclipse.imp.editor;

import org.eclipse.imp.editor.internal.CompletionProcessor;
import org.eclipse.imp.editor.internal.FormattingController;
import org.eclipse.imp.editor.internal.HoverHelpController;
import org.eclipse.imp.editor.internal.OutlineController;
import org.eclipse.imp.editor.internal.PresentationController;
import org.eclipse.imp.editor.internal.SourceHyperlinkController;
import org.eclipse.imp.parser.IModelListener;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.ui.texteditor.ITextEditor;

public class ServiceControllerManager {
    private LanguageServiceManager fLanguageServiceManager;

    private SourceHyperlinkController fHyperLinkController;

    private HoverHelpController fHoverHelpController;

    private FormattingController fFormattingController;

    private IModelListener fOutlineController;

    private PresentationController fPresentationController;

    private CompletionProcessor fCompletionProcessor;

    private final ITextEditor fTextEditor;

    private ISourceViewer fSourceViewer;

    public ServiceControllerManager(ITextEditor textEditor, LanguageServiceManager serviceMgr) {
        fTextEditor= textEditor;
        fLanguageServiceManager= serviceMgr;
    }

    public void initialize() {
        IRegionSelectionService regionSelector= (IRegionSelectionService) fTextEditor.getAdapter(IRegionSelectionService.class);

        if (fHyperLinkController == null && fLanguageServiceManager.getHyperLinkDetector() != null)
            fHyperLinkController= new SourceHyperlinkController(fLanguageServiceManager.getHyperLinkDetector(), fTextEditor);

        fHoverHelpController= new HoverHelpController(fLanguageServiceManager.getLanguage());
        fFormattingController= new FormattingController(fLanguageServiceManager.getFormattingStrategy());
        fFormattingController.setParseController(fLanguageServiceManager.getParseController());

        if (fLanguageServiceManager.getModelBuilder() != null) {
            fOutlineController= new IMPOutlinePage(fLanguageServiceManager.getParseController(), fLanguageServiceManager.getModelBuilder(), fLanguageServiceManager.getLabelProvider(),
                    fLanguageServiceManager.getImageProvider(), regionSelector);
        } else {
            fOutlineController= new OutlineController(fTextEditor, fLanguageServiceManager.getLanguage());
        }
        fCompletionProcessor= new CompletionProcessor(fLanguageServiceManager.getLanguage());
    }

    public void setSourceViewer(ISourceViewer sourceViewer) {
        fSourceViewer= sourceViewer;
        fPresentationController= new PresentationController(fSourceViewer, fLanguageServiceManager.getLanguage());
    }

    public void setupModelListeners(ParserScheduler parserScheduler) {
        if (fOutlineController != null)
            parserScheduler.addModelListener(fOutlineController);
        if (fPresentationController != null)
            parserScheduler.addModelListener(fPresentationController);
        if (fHoverHelpController != null)
            parserScheduler.addModelListener(fHoverHelpController);
        if (fHyperLinkController != null)
            parserScheduler.addModelListener(fHyperLinkController);
        if (fCompletionProcessor != null)
            parserScheduler.addModelListener(fCompletionProcessor);
    }

    public SourceHyperlinkController getHyperLinkController() {
        return fHyperLinkController;
    }

    public HoverHelpController getHoverHelpController() {
        return fHoverHelpController;
    }

    public FormattingController getFormattingController() {
        return fFormattingController;
    }

    public IModelListener getOutlineController() {
        return fOutlineController;
    }

    public PresentationController getPresentationController() {
        return fPresentationController;
    }

    public ITextEditor getTextEditor() {
        return fTextEditor;
    }

    public ISourceViewer getSourceViewer() {
        return fSourceViewer;
    }

    public CompletionProcessor getCompletionProcessor() {
        return fCompletionProcessor;
    }
}
