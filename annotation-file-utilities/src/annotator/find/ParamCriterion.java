package annotator.find;

import java.util.ArrayList;
import java.util.List;

import annotations.el.InnerTypeLocation;

import com.sun.source.tree.ArrayTypeTree;
import com.sun.source.tree.IdentifierTree;
import com.sun.source.tree.MethodTree;
import com.sun.source.tree.ParameterizedTypeTree;
import com.sun.source.tree.Tree;
import com.sun.source.tree.VariableTree;
import com.sun.source.util.TreePath;

public class ParamCriterion implements Criterion {

  private String methodName;
  private Criterion parentCriterion;
  private Integer paramPos;

  public ParamCriterion(String methodName, Integer pos) {
    this.methodName = methodName.substring(0, methodName.indexOf(")") + 1);
    this.paramPos = pos;
  }

  /** {@inheritDoc} */
  @Override
  public boolean isSatisfiedBy(TreePath path, Tree leaf) {
    assert path == null || path.getLeaf() == leaf;
    return isSatisfiedBy(path);
  }

  /** {@inheritDoc} */
  @Override
  public boolean isSatisfiedBy(TreePath path) {

    if (path == null) {
      return false;
    }

    // no inner type location, want to annotate outermost type
    // i.e.   @Readonly List list;
    //        @Readonly List<String> list;
    Tree leaf = path.getLeaf();
    if (leaf instanceof VariableTree) {
      Tree parent = path.getParentPath().getLeaf();
      if (parent.getKind() == Tree.Kind.METHOD) {
        MethodTree mt = (MethodTree) parent;
        if (mt.getParameters().size() > paramPos) {
          if (mt.getParameters().get(paramPos).equals(leaf)) {
            return true;
          }
        }
      }
    } else {
      return this.isSatisfiedBy(path.getParentPath());
    }
    return false;
  }

  public Kind getKind() {
    return Kind.PARAM;
  }

  public String toString() {
    return "ParamCriterion for method: " + methodName + " at position: " +
    paramPos;
  }
}
