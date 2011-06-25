package usr.martin.check_style;

import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.JavaModelException;

/**
 * This class describes a basic style check
 */
public abstract class AbstractStyleCheck {

    public abstract boolean isEnabled();

    public abstract void process(
            ICompilationUnit compilationUnit, 
            ProblemFactory problemFactory
            ) throws JavaModelException;

}
