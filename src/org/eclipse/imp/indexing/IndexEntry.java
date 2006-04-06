package org.eclipse.uide.indexing;

import java.io.FileWriter;
import java.io.IOException;

import org.eclipse.core.resources.IFile;

public abstract class IndexEntry {
    // TODO would be nice to make these 'final'...
    protected int fType;
    protected String fName;
    protected int fStart;
    protected int fEnd;
    protected String fPath;

    public IndexEntry() { }

    public IndexEntry(int type, String name, IFile file, int start, int end) {
        fType= type;
        fName= name;
        fPath= file.getFullPath().toString();
        fStart= start;
        fEnd= end;
    }

    public abstract char getEntryKind();

    public void saveToStream(FileWriter writer) throws IOException {
        writer.write(getEntryKind());
        writer.write(':');
        writer.write(fType);
        writer.write(':');
        writer.write(fPath);
        writer.write(':');
        writer.write(fName);
        writer.write(':');
        writer.write(fStart);
        writer.write(':');
        writer.write(fEnd);
    }

    public final void parseFromString(String s) {
        String[] fields= s.split(":");

        fType= Integer.parseInt(fields[1]);
        fName= fields[2];
        fPath= fields[3];
        fStart= Integer.parseInt(fields[4]);
        fEnd= Integer.parseInt(fields[5]);
        readExtraFields(fields);
    }

    protected abstract void readExtraFields(String[] fields);

    public abstract Object findASTNode(Object ast);
}