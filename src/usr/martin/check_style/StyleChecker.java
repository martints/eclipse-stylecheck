package usr.martin.check_style;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.compiler.BuildContext;
import org.eclipse.jdt.core.compiler.ReconcileContext;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.CompilationUnit;

/**
 * This baseclass defines structures to easily process different style checks
 */
public final class StyleChecker
        extends org.eclipse.jdt.core.compiler.CompilationParticipant {
    private List<AbstractStyleCheck> styleChecks;

    private final Set<IFile> allCompiledFiles = new HashSet<IFile>();

    private IJavaProject project;

    public StyleChecker() {
    }

    @Override
    public int aboutToBuild(IJavaProject project_) {
        project = project_;
        setup(project.getProject());
        allCompiledFiles.clear();

        return READY_FOR_BUILD;
    }

    private void setup(IProject project_) {
        styleChecks = Activator.findActiveStyleCheckers(project_);
    }

    @Override
    public boolean isActive(IJavaProject project_) {
        return true;
    }

    @Override
    public void buildStarting(BuildContext[] files, boolean isBatch) {
        super.buildStarting(files, isBatch);

        if (files.length == 0) {
            return;
        }

        if (styleChecks == null) {
            setup(files[0].getFile().getProject());
        }

        for (BuildContext c : files) {
            allCompiledFiles.add(c.getFile());
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
                    context.getWorkingCopy()
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

    @Override
    public boolean isAnnotationProcessor() {
        return true;
    }

    @Override
    public void processAnnotations(BuildContext[] files) {
        if (files.length == 0) {
            return;
        }

        if (styleChecks == null) {
            setup(files[0].getFile().getProject());
        }

        for (BuildContext file : files) {
            allCompiledFiles.remove(file.getFile());

            ASTParser parser = ASTParser.newParser(AST.JLS3);
            ICompilationUnit source = JavaCore.createCompilationUnitFrom(file.getFile());
            if (source == null) {
                continue;
            }
            parser.setSource(source);
            parser.setBindingsRecovery(true);
            CompilationUnit compilationUnit = (CompilationUnit) parser.createAST(null);
            // Don't check for style problems, if the file cannot be parsed

            ProblemFactory problems = checkFile(
                    file.getFile(), (ICompilationUnit) compilationUnit.getJavaElement()
                    );
            if (! problems.isEmpty()) {
                file.recordNewProblems(problems.getProblems());
            }
        }
    }

    @Override
    public void buildFinished(IJavaProject project_) {
        super.buildFinished(project);
        project = project_;

        for (IFile file : allCompiledFiles) {
            ASTParser parser = ASTParser.newParser(AST.JLS3);
            ICompilationUnit source = JavaCore.createCompilationUnitFrom(file);
            if (source == null) {
                continue;
            }
            parser.setSource(source);
            parser.setBindingsRecovery(true);
            CompilationUnit compilationUnit = (CompilationUnit) parser.createAST(null);
            // Don't check for style problems, if the file cannot be parsed

            ProblemFactory problems = checkFile(
                    file, (ICompilationUnit) compilationUnit.getJavaElement()
                    );
            if (! problems.isEmpty()) {
                problems.createProblemMarkers();
            }
        }
    }

    private ProblemFactory checkFile(IFile file, ICompilationUnit compilationUnit) {
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
