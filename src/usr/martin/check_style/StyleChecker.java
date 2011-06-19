package usr.martin.check_style;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.compiler.BuildContext;
import org.eclipse.jdt.core.compiler.ReconcileContext;
import org.eclipse.jdt.core.dom.CompilationUnit;

import usr.martin.check_style.CheckStyleProblem;
import usr.martin.check_style.CheckStyleSettings;

import usr.martin.check_style.import_check.StaticImportChecker;
import usr.martin.check_style.import_check.WildcardImportChecker;

/**
 * This baseclass defines structures to easily process different style checks
 */
public final class StyleChecker 
		extends org.eclipse.jdt.core.compiler.CompilationParticipant {
	private List<AbstractStyleCheck> styleChecks;
	
	public StyleChecker() {
	}

	private final AbstractStyleCheck[] getAllStyleChecks(CheckStyleSettings settings) {
		return new AbstractStyleCheck[] {
				new StaticImportChecker(settings),
				new WildcardImportChecker(settings),
				};
	}
	
	@Override
	public int aboutToBuild(IJavaProject project) {
		// get the state of all checkers:
		CheckStyleSettings settings = new CheckStyleSettings(project.getProject());
			
		List<AbstractStyleCheck> styleChecks_ = new ArrayList<AbstractStyleCheck>();
		for (AbstractStyleCheck check : getAllStyleChecks(settings)) {
			if (check.isEnabled()) {
				styleChecks_.add(check);
			}
		}
			
		styleChecks = Collections.unmodifiableList(styleChecks_); 
		
		return READY_FOR_BUILD;
	}

	@Override
	public boolean isActive(IJavaProject project) {
		return true;
	}

	
	@Override
	public void processAnnotations(BuildContext[] files) {
		super.processAnnotations(files);
	}

	@Override
	public void reconcile(ReconcileContext context) {
		super.reconcile(context);
		
		if (styleChecks == null) {
			aboutToBuild(context.getWorkingCopy().getJavaProject());
		}
		
		CompilationUnit compilationUnit;
		try {
			compilationUnit = context.getAST3();
		} catch (JavaModelException e) {
			return;
		}
		// Don't check for style problems, if the file cannot be parsed
		if (compilationUnit == null) {
			return;
		}

		ProblemFactory problemFactory = new ProblemFactory(context, compilationUnit);

		for (AbstractStyleCheck check : styleChecks) {
			try {
				check.process(context, compilationUnit, problemFactory);
			} catch (JavaModelException e) {
				// Ignore the check, as there has been some syntax error somewhere
			}
		}

		if (problemFactory.isEmpty()) {
			context.putProblems(CheckStyleProblem.PROBLEM_MARKER, null);
		} else {
			context.putProblems(CheckStyleProblem.PROBLEM_MARKER, problemFactory.getProblems());
			problemFactory.createProblemMarkers();
		}
	}
}
