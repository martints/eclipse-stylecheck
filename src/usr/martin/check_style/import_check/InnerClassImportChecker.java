package usr.martin.check_style.import_check;

import org.eclipse.jdt.core.Flags;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IImportDeclaration;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;

import usr.martin.check_style.AbstractStyleCheck;
import usr.martin.check_style.CheckStyleSettings;
import usr.martin.check_style.CheckStyleProblem;
import usr.martin.check_style.ProblemFactory;

/**
 * Eventually disallow static imports
 */
public class InnerClassImportChecker 
        extends AbstractStyleCheck {

    private boolean enabled;

    public InnerClassImportChecker(CheckStyleSettings settings) {
        enabled = ! settings.mayImportInnerClasses();
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    @Override
    public void process(ICompilationUnit compilationUnit, ProblemFactory problemFactory) 
            throws JavaModelException {
        for (IImportDeclaration d : compilationUnit.getImports()) {
            if (d.isOnDemand() || ((d.getFlags() & Flags.AccStatic) != 0)) {
                continue;
            }
            String className = d.getElementName();
            try {
                IType type = d.getJavaProject().findType(className);
                if (type == null) {
                    continue;
                }
                
                if (type.isMember()) {
                    problemFactory.createProblem(CheckStyleProblem.Problem.IMPORT, "Inner classes should not be imported", d);
                }
            } catch (JavaModelException e_) {
                // Nothing to do 
            }
        }
    }

}
