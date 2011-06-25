package usr.martin.check_style;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.compiler.BuildContext;
import org.eclipse.jdt.core.compiler.ReconcileContext;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.CompilationUnit;

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
		setup(project.getProject()); 
		
		return READY_FOR_BUILD;
	}

	private void setup(IProject project) {
		// get the state of all checkers:
		CheckStyleSettings settings = new CheckStyleSettings(project);
			
		List<AbstractStyleCheck> styleChecks_ = new ArrayList<AbstractStyleCheck>();
		for (AbstractStyleCheck check : getAllStyleChecks(settings)) {
			if (check.isEnabled()) {
				styleChecks_.add(check);
			}
		}
			
		styleChecks = Collections.unmodifiableList(styleChecks_);
	}

	@Override
	public boolean isActive(IJavaProject project) {
		return true;
	}

	
	@Override
	public boolean isAnnotationProcessor() {
		return true;
	}

	@Override
	public void processAnnotations(BuildContext[] files) {
		super.processAnnotations(files);
		
		if (files.length == 0) {
			return;
		}
		
		if (styleChecks == null) {
			setup(files[0].getFile().getProject());
		}

		for (BuildContext file : files) {
			ASTParser parser = ASTParser.newParser(AST.JLS3);
			parser.setSource(file.getContents());
			CompilationUnit compilationUnit = (CompilationUnit) parser.createAST(null);
			// Don't check for style problems, if the file cannot be parsed
	
			ProblemFactory problems = checkFile(file.getFile(), compilationUnit);
			if (! problems.isEmpty()) {
				file.recordNewProblems(problems.getProblems());
			}
		}
	}
	
	

	@Override
	public void reconcile(ReconcileContext context) {
		super.reconcile(context);
		
		if (styleChecks == null) {
			setup(context.getWorkingCopy().getJavaProject().getProject());
		}
		
		ProblemFactory problems;
		try {
			problems = checkFile(
					(IFile) context.getWorkingCopy().getCorrespondingResource(),
					context.getAST3()
					);
		} catch (JavaModelException e) {
			return;
		}
		
		if (problems.isEmpty()) {
			context.putProblems(CheckStyleProblem.PROBLEM_MARKER, null);
		} else {
			context.putProblems(CheckStyleProblem.PROBLEM_MARKER, problems.getProblems());
		}
	}

	private ProblemFactory checkFile(IFile file, CompilationUnit compilationUnit) {
		ProblemFactory problemFactory = new ProblemFactory(file, compilationUnit);

		for (AbstractStyleCheck check : styleChecks) {
			try {
				check.process(compilationUnit, problemFactory);
			} catch (JavaModelException e) {
				// Ignore the check, as there has been some syntax error somewhere
			}
		}

		return problemFactory;
	}

}
