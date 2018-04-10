package lombok.javac.handlers;

import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.util.List;
import com.sun.tools.javac.util.Name;
import lombok.ConfigurationKeys;
import lombok.HelloWorld;
import lombok.core.AnnotationValues;
import lombok.javac.Javac;
import lombok.javac.JavacAnnotationHandler;
import lombok.javac.JavacNode;
import lombok.javac.JavacTreeMaker;

import java.lang.reflect.Modifier;

import static lombok.core.handlers.HandlerUtil.handleFlagUsage;

public class HandleHelloWorld extends JavacAnnotationHandler<HelloWorld> {
    @Override
    public void handle(AnnotationValues<HelloWorld> annotation, JCTree.JCAnnotation ast, JavacNode annotationNode) {
        handleFlagUsage(annotationNode, ConfigurationKeys.GETTER_FLAG_USAGE, "@HelloWorld");
        JavacNode typeNode = annotationNode.up();

        JCTree.JCMethodDecl helloWorldMethod = createHelloWorld(typeNode);

        JavacHandlerUtil.injectMethod(typeNode, helloWorldMethod);
    }

    private JCTree.JCMethodDecl createHelloWorld(JavacNode type) {
        JavacTreeMaker treeMaker = type.getTreeMaker();

        JCTree.JCModifiers modifiers = treeMaker.Modifiers(Modifier.PUBLIC);
        List<JCTree.JCTypeParameter> methodGenericTypes = List.<JCTree.JCTypeParameter>nil();
        JCTree.JCExpression methodType = treeMaker.TypeIdent(Javac.CTC_VOID);
        Name methodName = type.toName("helloWorld");
        List<JCTree.JCVariableDecl> methodParameters = List.<JCTree.JCVariableDecl>nil();
        List<JCTree.JCExpression> methodThrows = List.<JCTree.JCExpression>nil();

        JCTree.JCExpression printlnMethod =
                JavacHandlerUtil.chainDots(type, "System", "out", "println");
        List<JCTree.JCExpression> printlnArgs = List.<JCTree.JCExpression>of(treeMaker.Literal("hello world"));
        JCTree.JCMethodInvocation printlnInvocation =
                treeMaker.Apply(List.<JCTree.JCExpression>nil(), printlnMethod, printlnArgs);
        JCTree.JCBlock methodBody =
                treeMaker.Block(0, List.<JCTree.JCStatement>of(treeMaker.Exec(printlnInvocation)));

        JCTree.JCExpression defaultValue = null;

        return treeMaker.MethodDef(
                modifiers,
                methodName,
                methodType,
                methodGenericTypes,
                methodParameters,
                methodThrows,
                methodBody,
                defaultValue
        );
    }
}


