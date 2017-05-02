package net.ossindex.version.impl;

import org.antlr.v4.runtime.ConsoleErrorListener;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Recognizer;

public class VersionErrorListener extends ConsoleErrorListener {
	public int errors = 0;

	public void syntaxError(Recognizer<?,?> recognizer,
			Object offendingSymbol,
			int line,
			int charPositionInLine,
			String msg,
			RecognitionException e) {
		super.syntaxError(recognizer, offendingSymbol, line, charPositionInLine, msg, e);
		errors++;
	}

	public int getErrorCount() {
		return errors;
	}

	public boolean hasErrors() {
		return errors > 0;
	}
}
