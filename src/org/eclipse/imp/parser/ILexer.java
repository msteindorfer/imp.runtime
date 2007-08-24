/*
 * (C) Copyright IBM Corporation 2007
 * 
 * This file is part of the Eclipse IMP.
 */
package org.eclipse.imp.parser;

import lpg.runtime.IPrsStream;
import lpg.runtime.LexStream;
import lpg.runtime.Monitor;

public interface ILexer {
    public int[] getKeywordKinds();

    public LexStream getLexStream();

    public void initialize(char[] contents, String filename);

    public void lexer(Monitor monitor, IPrsStream prsStream);
}
