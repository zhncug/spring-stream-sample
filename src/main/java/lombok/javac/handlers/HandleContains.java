package lombok.javac.handlers;

import com.sun.tools.javac.code.Flags;
import com.sun.tools.javac.code.Symbol;
import com.sun.tools.javac.code.Type;
import com.sun.tools.javac.code.Types;
import com.sun.tools.javac.model.JavacTypes;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.util.ListBuffer;
import com.sun.tools.javac.util.Name;
import lombok.Contains;
import lombok.core.AST;
import lombok.core.AnnotationValues;
import lombok.core.HandlerPriority;
import lombok.javac.*;

import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.ExecutableType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;

import java.util.List;

import static lombok.javac.handlers.JavacHandlerUtil.genJavaLangTypeRef;
import static lombok.javac.handlers.JavacHandlerUtil.injectMethod;
import static lombok.javac.handlers.JavacHandlerUtil.recursiveSetGeneratedBy;

/**
 * Created by nan.zhang on 18-2-10.
 */
@HandlerPriority(65536)
@ResolutionResetNeeded
public class HandleContains extends JavacAnnotationHandler<Contains> {

    @Override
    public void handle(AnnotationValues<Contains> annotation, JCTree.JCAnnotation ast, JavacNode annotationNode) {

        try {
            JavacNode node = annotationNode.up();
            if (node.getKind() != AST.Kind.FIELD) {
                annotationNode.addError("@Contains is allowed only on fields");
                return;
            }
            Name delegateName = annotationNode.toName(node.getName());
            JavacResolution reso = new JavacResolution(annotationNode.getContext());
            JCTree member = node.get();
            if (member.type == null) {
                reso.resolveClassMember(node);
            }
            Type delegateType = member.type;
            if (delegateType instanceof Type.ClassType) {
                Type.ClassType ct = (Type.ClassType) delegateType;
                //TODO validate that this field is a collection type
                // if(!Collection)
                //   annotationNode.addError("@Contains can only be used on collections");
                final String methodName = "contains";
                MethodSig methodSig = getMethodBinding(methodName, ct, annotationNode.getTypesUtil());
                if (methodSig == null) throw new Exception("no method " + methodName + " in " + ct.tsym.name);
                JCTree.JCMethodDecl methodDecl = createDelegateMethod(methodSig, annotationNode, delegateName);
                injectMethod(node.up(), methodDecl);
            } else {
                annotationNode.addError("@Contains can only use concrete class types");
                return;
            }
        } catch (Exception ex) {
            //ex.printStackTrace();
            annotationNode.addError("@Contains unexpected error: " + ex.getMessage());
        }

    }

    public JCTree.JCMethodDecl createDelegateMethod(MethodSig sig, JavacNode annotation, Name delegateName) throws JavacResolution.TypeNotConvertibleException {

        JavacTreeMaker maker = annotation.getTreeMaker();

        com.sun.tools.javac.util.List<JCTree.JCAnnotation> annotations;
        if (sig.isDeprecated) {
            annotations = com.sun.tools.javac.util.List.of(maker.Annotation(genJavaLangTypeRef(annotation, "Deprecated"), com.sun.tools.javac.util.List.<JCTree.JCExpression>nil()));
        } else {
            annotations = com.sun.tools.javac.util.List.nil();
        }

        JCTree.JCModifiers mods = maker.Modifiers(Flags.PUBLIC, annotations);
        JCTree.JCExpression returnType = JavacResolution.typeToJCTree((Type) sig.type.getReturnType(), annotation.getAst(), true);
        boolean useReturn = sig.type.getReturnType().getKind() != TypeKind.VOID;
        ListBuffer<JCTree.JCVariableDecl> params = sig.type.getParameterTypes().isEmpty() ? null : new ListBuffer<>();
        ListBuffer<JCTree.JCExpression> args = sig.type.getParameterTypes().isEmpty() ? null : new ListBuffer<>();
        ListBuffer<JCTree.JCExpression> thrown = sig.type.getThrownTypes().isEmpty() ? null : new ListBuffer<>();
        ListBuffer<JCTree.JCTypeParameter> typeParams = sig.type.getTypeVariables().isEmpty() ? null : new ListBuffer<>();
        ListBuffer<JCTree.JCExpression> typeArgs = sig.type.getTypeVariables().isEmpty() ? null : new ListBuffer<>();
        Types types = Types.instance(annotation.getContext());

        for (TypeMirror param : sig.type.getTypeVariables()) {
            Name name = ((Type.TypeVar) param).tsym.name;

            ListBuffer<JCTree.JCExpression> bounds = new ListBuffer<JCTree.JCExpression>();
            for (Type type : types.getBounds((Type.TypeVar) param)) {
                bounds.append(JavacResolution.typeToJCTree(type, annotation.getAst(), true));
            }

            typeParams.append(maker.TypeParameter(name, bounds.toList()));
            typeArgs.append(maker.Ident(name));
        }

        for (TypeMirror ex : sig.type.getThrownTypes()) {
            thrown.append(JavacResolution.typeToJCTree((Type) ex, annotation.getAst(), true));
        }

        int idx = 0;
        String[] paramNames = sig.getParameterNames();
        boolean varargs = sig.elem.isVarArgs();
        for (TypeMirror param : sig.type.getParameterTypes()) {
            long flags = JavacHandlerUtil.addFinalIfNeeded(Flags.PARAMETER, annotation.getContext());
            JCTree.JCModifiers paramMods = maker.Modifiers(flags);
            Name name = annotation.toName(paramNames[idx++]);
            if (varargs && idx == paramNames.length) {
                paramMods.flags |= Flags.VARARGS;
            }
            params.append(maker.VarDef(paramMods, name, JavacResolution.typeToJCTree((Type) param, annotation.getAst(), true), null));
            args.append(maker.Ident(name));
        }

        JCTree.JCExpression accessor = maker.Select(maker.Ident(annotation.toName("this")), delegateName);

        JCTree.JCExpression delegateCall = maker.Apply(toList(typeArgs), maker.Select(accessor, sig.name), toList(args));
        JCTree.JCStatement body = useReturn ? maker.Return(delegateCall) : maker.Exec(delegateCall);
        JCTree.JCBlock bodyBlock = maker.Block(0, com.sun.tools.javac.util.List.of(body));
        StringBuilder generatedMethodName = new StringBuilder(delegateName);
        generatedMethodName.append(sig.name.toString());
        generatedMethodName.setCharAt(delegateName.length(), Character.toUpperCase(generatedMethodName.charAt(delegateName.length())));
        return recursiveSetGeneratedBy(maker.MethodDef(mods, annotation.toName(generatedMethodName.toString()), returnType, toList(typeParams), toList(params), toList(thrown), bodyBlock, null), annotation.get(), annotation.getContext());
    }

    public static <T> com.sun.tools.javac.util.List<T> toList(ListBuffer<T> collection) {
        return collection == null ? com.sun.tools.javac.util.List.<T>nil() : collection.toList();
    }

    public static class MethodSig {
        final Name name;
        final ExecutableType type;
        final boolean isDeprecated;
        final ExecutableElement elem;

        MethodSig(Name name, ExecutableType type, boolean isDeprecated, ExecutableElement elem) {
            this.name = name;
            this.type = type;
            this.isDeprecated = isDeprecated;
            this.elem = elem;
        }

        String[] getParameterNames() {
            List<? extends VariableElement> paramList = elem.getParameters();
            String[] paramNames = new String[paramList.size()];
            for (int i = 0; i < paramNames.length; i++) {
                paramNames[i] = paramList.get(i).getSimpleName().toString();
            }
            return paramNames;
        }

        @Override
        public String toString() {
            return (isDeprecated ? "@Deprecated " : "") + name + " " + type;
        }
    }

    public MethodSig getMethodBinding(String name, Type.ClassType ct, JavacTypes types) {
        MethodSig result = null;
        Symbol.TypeSymbol tsym = ct.asElement();
        if (tsym == null) throw new IllegalArgumentException("no class");

        for (Symbol member : tsym.getEnclosedElements()) {
            if (member.getKind() != ElementKind.METHOD || !name.equals(member.name.toString())) {
                continue;
            }
            if (member.isStatic()) continue;
            if (member.isConstructor()) continue;
            ExecutableElement exElem = (ExecutableElement) member;
            if (!exElem.getModifiers().contains(Modifier.PUBLIC)) continue;
            ExecutableType methodType = (ExecutableType) types.asMemberOf(ct, member);
            boolean isDeprecated = (member.flags() & Flags.DEPRECATED) != 0;
            result = new MethodSig(member.name, methodType, isDeprecated, exElem);
        }
        if (result == null) {
            if (ct.supertype_field instanceof Type.ClassType) {
                result = getMethodBinding(name, (Type.ClassType) ct.supertype_field, types);
            }
            if (result == null) {
                if (ct.interfaces_field != null) {
                    for (Type iface : ct.interfaces_field) {
                        if (iface instanceof Type.ClassType) {
                            result = getMethodBinding(name, (Type.ClassType) iface, types);
                            if (result != null) {
                                break;
                            }
                        }
                    }
                }
            }
        }
        return result;
    }
}
