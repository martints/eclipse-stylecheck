package usr.martin.check_style;

import org.eclipse.core.resources.IResource;
import org.eclipse.jdt.core.compiler.CategorizedProblem;

public class CheckStyleProblem 
		extends CategorizedProblem {
	public final static String PROBLEM_MARKER = Activator.PLUGIN_NAME + ".problem";

	private final IResource resource;
	
	private final Problem problem;
	
	private final String description;

	private int sourceStart;

	private int lineNumber;

	private int sourceEnd;

	public CheckStyleProblem(
			IResource resource_, Problem problem_,
			String description_, int start, int len, int line
			) {
		this.resource = resource_;
		this.problem = problem_;
		this.description = description_;
		sourceStart = start;
		sourceEnd = start + len;
		lineNumber = line;
	}

	@Override
	public String[] getArguments() {
		return new String[0];
	}

	@Override
	public int getID() {
		switch (problem) {
		case IMPORT: return ImportRelated + ExternalProblemNotFixable;
		default: return ExternalProblemNotFixable;
		}
	}

	@Override
	public String getMessage() {
		return description;
	}

	@Override
	public char[] getOriginatingFileName() {
		return resource.getFullPath().toPortableString().toCharArray();
	}

	@Override
	public int getSourceEnd() {
		return sourceEnd;
	}

	@Override
	public int getSourceLineNumber() {
		return lineNumber;
	}

	@Override
	public int getSourceStart() {
		return sourceStart;
	}

	@Override
	public boolean isError() {
		return false;
	}

	@Override
	public boolean isWarning() {
		return true;
	}

	@Override
	public void setSourceEnd(int sourceEnd_) {
		sourceEnd = sourceEnd_;
	}

	@Override
	public void setSourceLineNumber(int lineNumber_) {
		lineNumber = lineNumber_;
	}

	@Override
	public void setSourceStart(int sourceStart_) {
		sourceStart = sourceStart_;
	}

	@Override
	public int getCategoryID() {
		return CAT_CODE_STYLE;
	}

	@Override
	public String getMarkerType() {
		return PROBLEM_MARKER;
	}

	public static enum Problem {
		IMPORT,
	}
}
