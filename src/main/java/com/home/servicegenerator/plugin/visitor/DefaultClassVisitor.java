package com.home.servicegenerator.plugin.visitor;

import com.github.javaparser.ast.*;
import com.github.javaparser.ast.body.*;
import com.github.javaparser.ast.comments.BlockComment;
import com.github.javaparser.ast.comments.JavadocComment;
import com.github.javaparser.ast.comments.LineComment;
import com.github.javaparser.ast.expr.*;
import com.github.javaparser.ast.modules.*;
import com.github.javaparser.ast.stmt.*;
import com.github.javaparser.ast.type.*;
import com.github.javaparser.ast.visitor.CloneVisitor;
import com.github.javaparser.ast.visitor.Visitable;
import com.home.servicegenerator.api.ASTProcessingSchema;

/**
 * Creates implementation class using Abstract Syntax Tree of the base class as a template.
 *
 * This visitor goes around AST-nodes of the base class, processes them (applies a processing action) recursively and
 * copies resulted node into the AST of generating class.
 *
 * Note: according to AST Processing Schema API visitor clones each processing base class node by default
 *       (in case no explicit visitor's action provided).
 *
 * @see ASTProcessingSchema
 */
public class DefaultClassVisitor extends CloneVisitor {
    private ASTProcessingSchema processingSchema;

    public DefaultClassVisitor() {
        super();
        this.processingSchema = new DefaultASTProcessingSchema();
    }

    /**
     * Registers AST processing schema.
     * @param processingSchema - AST processing schema
     */
    public void registerProcessingSchema(final ASTProcessingSchema processingSchema) {
        if (processingSchema == null) {
            throw new IllegalArgumentException("Processing schema cannot be null!");
        }
        this.processingSchema = processingSchema;
    }

    @Override
    public Visitable visit(CompilationUnit n, Object arg) {
        return super.visit(
                processingSchema.processCompilationUnit().apply(n, arg),
                arg
        );
    }

    @Override
    public Visitable visit(PackageDeclaration n, Object arg) {
        return super.visit(
                processingSchema.processPackageDeclaration().apply(n, arg),
                arg
        );
    }

    @Override
    public Visitable visit(TypeParameter n, Object arg) {
        return super.visit(
                processingSchema.processTypeParameter().apply(n, arg),
                arg
        );
    }

    @Override
    public Visitable visit(LineComment n, Object arg) {
        return super.visit(
                processingSchema.processLineComment().apply(n, arg),
                arg
        );
    }

    @Override
    public Visitable visit(BlockComment n, Object arg) {
        return super.visit(
                processingSchema.processBlockComment().apply(n, arg),
                arg
        );
    }

    @Override
    public Visitable visit(ClassOrInterfaceDeclaration n, Object arg) {
        return super.visit(
                processingSchema.processClassOrInterfaceDeclaration().apply(n, arg),
                arg
        );
    }

    @Override
    public Visitable visit(EnumDeclaration n, Object arg) {
        return super.visit(
                processingSchema.processEnumDeclaration().apply(n, arg),
                arg
        );
    }

    @Override
    public Visitable visit(EnumConstantDeclaration n, Object arg) {
        return super.visit(
                processingSchema.processEnumConstantDeclaration().apply(n, arg),
                arg
        );
    }

    @Override
    public Visitable visit(AnnotationDeclaration n, Object arg) {
        return super.visit(
                processingSchema.processAnnotationDeclaration().apply(n, arg),
                arg
        );
    }

    @Override
    public Visitable visit(AnnotationMemberDeclaration n, Object arg) {
        return super.visit(
                processingSchema.processAnnotationMemberDeclaration().apply(n, arg),
                arg
        );
    }

    @Override
    public Visitable visit(FieldDeclaration n, Object arg) {
        return super.visit(
                processingSchema.processFieldDeclaration().apply(n, arg),
                arg
        );
    }

    @Override
    public Visitable visit(VariableDeclarator n, Object arg) {
        return super.visit(
                processingSchema.processVariableDeclarator().apply(n, arg),
                arg
        );
    }

    @Override
    public Visitable visit(ConstructorDeclaration n, Object arg) {
        return super.visit(
                processingSchema.processConstructorDeclaration().apply(n, arg),
                arg
        );
    }

    @Override
    public Visitable visit(MethodDeclaration n, Object arg) {
        return super.visit(
                processingSchema.processMethodDeclaration().apply(n, arg),
                arg
        );
    }

    @Override
    public Visitable visit(Parameter n, Object arg) {
        return super.visit(
                processingSchema.processParameter().apply(n, arg),
                arg
        );
    }

    @Override
    public Visitable visit(InitializerDeclaration n, Object arg) {
        return super.visit(
                processingSchema.processInitializerDeclaration().apply(n, arg),
                arg
        );
    }

    @Override
    public Visitable visit(JavadocComment n, Object arg) {
        return super.visit(
                processingSchema.processJavadocComment().apply(n, arg),
                arg
        );
    }

    @Override
    public Visitable visit(ClassOrInterfaceType n, Object arg) {
        return super.visit(
                processingSchema.processClassOrInterfaceType().apply(n, arg),
                arg
        );
    }

    @Override
    public Visitable visit(PrimitiveType n, Object arg) {
        return super.visit(
                processingSchema.processPrimitiveType().apply(n, arg),
                arg
        );
    }

    @Override
    public Visitable visit(ArrayType n, Object arg) {
        return super.visit(
                processingSchema.processArrayType().apply(n, arg),
                arg
        );
    }

    @Override
    public Visitable visit(ArrayCreationLevel n, Object arg) {
        return super.visit(
                processingSchema.processArrayCreationLevel().apply(n, arg),
                arg
        );
    }

    @Override
    public Visitable visit(IntersectionType n, Object arg) {
        return super.visit(
                processingSchema.processIntersectionType().apply(n, arg),
                arg
        );
    }

    @Override
    public Visitable visit(UnionType n, Object arg) {
        return super.visit(
                processingSchema.processUnionType().apply(n, arg),
                arg
        );
    }

    @Override
    public Visitable visit(VoidType n, Object arg) {
        return super.visit(
                processingSchema.processVoidType().apply(n, arg),
                arg
        );
    }

    @Override
    public Visitable visit(WildcardType n, Object arg) {
        return super.visit(
                processingSchema.processWildcardType().apply(n, arg),
                arg
        );
    }

    @Override
    public Visitable visit(UnknownType n, Object arg) {
        return super.visit(
                processingSchema.processUnknownType().apply(n, arg),
                arg
        );
    }

    @Override
    public Visitable visit(ArrayAccessExpr n, Object arg) {
        return super.visit(
                processingSchema.processArrayAccessExpr().apply(n, arg),
                arg
        );
    }

    @Override
    public Visitable visit(ArrayCreationExpr n, Object arg) {
        return super.visit(
                processingSchema.processArrayCreationExpr().apply(n, arg),
                arg
        );
    }

    @Override
    public Visitable visit(ArrayInitializerExpr n, Object arg) {
        return super.visit(
                processingSchema.processArrayInitializerExpr().apply(n, arg),
                arg
        );
    }

    @Override
    public Visitable visit(AssignExpr n, Object arg) {
        return super.visit(
                processingSchema.processAssignExpr().apply(n, arg),
                arg
        );
    }

    @Override
    public Visitable visit(BinaryExpr n, Object arg) {
        return super.visit(
                processingSchema.processBinaryExpr().apply(n, arg),
                arg
        );
    }

    @Override
    public Visitable visit(CastExpr n, Object arg) {
        return super.visit(
                processingSchema.processCastExpr().apply(n, arg),
                arg
        );
    }

    @Override
    public Visitable visit(ClassExpr n, Object arg) {
        return super.visit(
                processingSchema.processClassExpr().apply(n, arg),
                arg
        );
    }

    @Override
    public Visitable visit(ConditionalExpr n, Object arg) {
        return super.visit(
                processingSchema.processConditionalExpr().apply(n, arg),
                arg
        );
    }

    @Override
    public Visitable visit(EnclosedExpr n, Object arg) {
        return super.visit(
                processingSchema.processEnclosedExpr().apply(n, arg),
                arg
        );
    }

    @Override
    public Visitable visit(FieldAccessExpr n, Object arg) {
        return super.visit(
                processingSchema.processFieldAccessExpr().apply(n, arg),
                arg
        );
    }

    @Override
    public Visitable visit(InstanceOfExpr n, Object arg) {
        return super.visit(
                processingSchema.processInstanceOfExpr().apply(n, arg),
                arg
        );
    }

    @Override
    public Visitable visit(StringLiteralExpr n, Object arg) {
        return super.visit(
                processingSchema.processStringLiteralExpr().apply(n, arg),
                arg
        );
    }

    @Override
    public Visitable visit(IntegerLiteralExpr n, Object arg) {
        return super.visit(
                processingSchema.processIntegerLiteralExpr().apply(n, arg),
                arg
        );
    }

    @Override
    public Visitable visit(LongLiteralExpr n, Object arg) {
        return super.visit(
                processingSchema.processLongLiteralExpr().apply(n, arg),
                arg
        );
    }

    @Override
    public Visitable visit(CharLiteralExpr n, Object arg) {
        return super.visit(
                processingSchema.processCharLiteralExpr().apply(n, arg),
                arg
        );
    }

    @Override
    public Visitable visit(DoubleLiteralExpr n, Object arg) {
        return super.visit(
                processingSchema.processDoubleLiteralExpr().apply(n, arg),
                arg
        );
    }

    @Override
    public Visitable visit(BooleanLiteralExpr n, Object arg) {
        return super.visit(
                processingSchema.processBooleanLiteralExpr().apply(n, arg),
                arg
        );
    }

    @Override
    public Visitable visit(NullLiteralExpr n, Object arg) {
        return super.visit(
                processingSchema.processNullLiteralExpr().apply(n, arg),
                arg
        );
    }

    @Override
    public Visitable visit(MethodCallExpr n, Object arg) {
        return super.visit(
                processingSchema.processMethodCallExpr().apply(n, arg),
                arg
        );
    }

    @Override
    public Visitable visit(NameExpr n, Object arg) {
        return super.visit(
                processingSchema.processNameExpr().apply(n, arg),
                arg
        );
    }

    @Override
    public Visitable visit(ObjectCreationExpr n, Object arg) {
        return super.visit(
                processingSchema.processObjectCreationExpr().apply(n, arg),
                arg
        );
    }

    @Override
    public Visitable visit(Name n, Object arg) {
        return super.visit(
                processingSchema.processName().apply(n, arg),
                arg
        );
    }

    @Override
    public Visitable visit(SimpleName n, Object arg) {
        return super.visit(
                processingSchema.processSimpleName().apply(n, arg),
                arg
        );
    }

    @Override
    public Visitable visit(ThisExpr n, Object arg) {
        return super.visit(
                processingSchema.processThisExpr().apply(n, arg),
                arg
        );
    }

    @Override
    public Visitable visit(SuperExpr n, Object arg) {
        return super.visit(
                processingSchema.processSuperExpr().apply(n, arg),
                arg
        );
    }

    @Override
    public Visitable visit(UnaryExpr n, Object arg) {
        return super.visit(
                processingSchema.processUnaryExpr().apply(n, arg),
                arg
        );
    }

    @Override
    public Visitable visit(VariableDeclarationExpr n, Object arg) {
        return super.visit(
                processingSchema.processVariableDeclarationExpr().apply(n, arg),
                arg
        );
    }

    @Override
    public Visitable visit(MarkerAnnotationExpr n, Object arg) {
        return super.visit(
                processingSchema.processMarkerAnnotationExpr().apply(n, arg),
                arg
        );
    }

    @Override
    public Visitable visit(SingleMemberAnnotationExpr n, Object arg) {
        return super.visit(
                processingSchema.processSingleMemberAnnotationExpr().apply(n, arg),
                arg
        );
    }

    @Override
    public Visitable visit(NormalAnnotationExpr n, Object arg) {
        return super.visit(
                processingSchema.processNormalAnnotationExpr().apply(n, arg),
                arg
        );
    }

    @Override
    public Visitable visit(MemberValuePair n, Object arg) {
        return super.visit(
                processingSchema.processMemberValuePair().apply(n, arg),
                arg
        );
    }

    @Override
    public Visitable visit(ExplicitConstructorInvocationStmt n, Object arg) {
        return super.visit(
                processingSchema.processExplicitConstructorInvocationStmt().apply(n, arg),
                arg
        );
    }

    @Override
    public Visitable visit(LocalClassDeclarationStmt n, Object arg) {
        return super.visit(
                processingSchema.processLocalClassDeclarationStmt().apply(n, arg),
                arg
        );
    }

    @Override
    public Visitable visit(LocalRecordDeclarationStmt n, Object arg) {
        return super.visit(
                processingSchema.processLocalRecordDeclarationStmt().apply(n, arg),
                arg
        );
    }

    @Override
    public Visitable visit(AssertStmt n, Object arg) {
        return super.visit(
                processingSchema.processAssertStmt().apply(n, arg),
                arg
        );
    }

    @Override
    public Visitable visit(BlockStmt n, Object arg) {
        return super.visit(
                processingSchema.processBlockStmt().apply(n, arg),
                arg
        );
    }

    @Override
    public Visitable visit(LabeledStmt n, Object arg) {
        return super.visit(
                processingSchema.processLabeledStmt().apply(n, arg),
                arg
        );
    }

    @Override
    public Visitable visit(EmptyStmt n, Object arg) {
        return super.visit(
                processingSchema.processEmptyStmt().apply(n, arg),
                arg
        );
    }

    @Override
    public Visitable visit(ExpressionStmt n, Object arg) {
        return super.visit(
                processingSchema.processExpressionStmt().apply(n, arg),
                arg
        );
    }

    @Override
    public Visitable visit(SwitchStmt n, Object arg) {
        return super.visit(
                processingSchema.processSwitchStmt().apply(n, arg),
                arg
        );
    }

    @Override
    public Visitable visit(SwitchEntry n, Object arg) {
        return super.visit(
                processingSchema.processSwitchEntry().apply(n, arg),
                arg
        );
    }

    @Override
    public Visitable visit(BreakStmt n, Object arg) {
        return super.visit(
                processingSchema.processBreakStmt().apply(n, arg),
                arg
        );
    }

    @Override
    public Visitable visit(ReturnStmt n, Object arg) {
        return super.visit(
                processingSchema.processReturnStmt().apply(n, arg),
                arg
        );
    }

    @Override
    public Visitable visit(IfStmt n, Object arg) {
        return super.visit(
                processingSchema.processIfStmt().apply(n, arg),
                arg
        );
    }

    @Override
    public Visitable visit(WhileStmt n, Object arg) {
        return super.visit(
                processingSchema.processWhileStmt().apply(n, arg),
                arg
        );
    }

    @Override
    public Visitable visit(ContinueStmt n, Object arg) {
        return super.visit(
                processingSchema.processContinueStmt().apply(n, arg),
                arg
        );
    }

    @Override
    public Visitable visit(DoStmt n, Object arg) {
        return super.visit(
                processingSchema.processDoStmt().apply(n, arg),
                arg
        );
    }

    @Override
    public Visitable visit(ForEachStmt n, Object arg) {
        return super.visit(
                processingSchema.processForEachStmt().apply(n, arg),
                arg
        );
    }

    @Override
    public Visitable visit(ForStmt n, Object arg) {
        return super.visit(
                processingSchema.processForStmt().apply(n, arg),
                arg
        );
    }

    @Override
    public Visitable visit(ThrowStmt n, Object arg) {
        return super.visit(
                processingSchema.processThrowStmt().apply(n, arg),
                arg
        );
    }

    @Override
    public Visitable visit(SynchronizedStmt n, Object arg) {
        return super.visit(
                processingSchema.processSynchronizedStmt().apply(n, arg),
                arg
        );
    }

    @Override
    public Visitable visit(TryStmt n, Object arg) {
        return super.visit(
                processingSchema.processTryStmt().apply(n, arg),
                arg
        );
    }

    @Override
    public Visitable visit(CatchClause n, Object arg) {
        return super.visit(
                processingSchema.processCatchClause().apply(n, arg),
                arg
        );
    }

    @Override
    public Visitable visit(LambdaExpr n, Object arg) {
        return super.visit(
                processingSchema.processLambdaExpr().apply(n, arg),
                arg
        );
    }

    @Override
    public Visitable visit(MethodReferenceExpr n, Object arg) {
        return super.visit(
                processingSchema.processMethodReferenceExpr().apply(n, arg),
                arg
        );
    }

    @Override
    public Visitable visit(TypeExpr n, Object arg) {
        return super.visit(
                processingSchema.processTypeExpr().apply(n, arg),
                arg
        );
    }

    @Override
    public Visitable visit(NodeList n, Object arg) {
        return super.visit(
                processingSchema.processNodeList().apply(n, arg),
                arg
        );
    }



    @Override
    public Node visit(ImportDeclaration n, Object arg) {
        return super.visit(
                (ImportDeclaration) processingSchema.processImportDeclaration().apply(n, arg),
                arg
        );
    }

    @Override
    public Visitable visit(ModuleDeclaration n, Object arg) {
        return super.visit(
                processingSchema.processModuleDeclaration().apply(n, arg),
                arg
        );
    }

    @Override
    public Visitable visit(ModuleRequiresDirective n, Object arg) {
        return super.visit(
                processingSchema.processModuleRequiresDirective().apply(n, arg),
                arg
        );
    }

    @Override
    public Visitable visit(ModuleExportsDirective n, Object arg) {
        return super.visit(
                processingSchema.processModuleExportsDirective().apply(n, arg),
                arg
        );
    }

    @Override
    public Visitable visit(ModuleProvidesDirective n, Object arg) {
        return super.visit(
                processingSchema.processModuleProvidesDirective().apply(n, arg),
                arg
        );
    }

    @Override
    public Visitable visit(ModuleUsesDirective n, Object arg) {
        return super.visit(
                processingSchema.processModuleUsesDirective().apply(n, arg),
                arg
        );
    }

    @Override
    public Visitable visit(ModuleOpensDirective n, Object arg) {
        return super.visit(
                processingSchema.processModuleOpensDirective().apply(n, arg),
                arg
        );
    }

    @Override
    public Visitable visit(UnparsableStmt n, Object arg) {
        return super.visit(
                processingSchema.processUnparsableStmt().apply(n, arg),
                arg
        );
    }

    @Override
    public Visitable visit(ReceiverParameter n, Object arg) {
        return super.visit(
                processingSchema.processReceiverParameter().apply(n, arg),
                arg
        );
    }

    @Override
    public Visitable visit(VarType n, Object arg) {
        return super.visit(
                processingSchema.processVarType().apply(n, arg),
                arg
        );
    }

    @Override
    public Visitable visit(Modifier n, Object arg) {
        return super.visit(
                processingSchema.processModifier().apply(n, arg),
                arg
        );
    }

    @Override
    public Visitable visit(SwitchExpr n, Object arg) {
        return super.visit(
                processingSchema.processSwitchExpr().apply(n, arg),
                arg
        );
    }

    @Override
    public Visitable visit(YieldStmt n, Object arg) {
        return super.visit(
                processingSchema.processYieldStmt().apply(n, arg),
                arg
        );
    }

    @Override
    public Visitable visit(TextBlockLiteralExpr n, Object arg) {
        return super.visit(
                processingSchema.processTextBlockLiteralExpr().apply(n, arg),
                arg
        );
    }

    @Override
    public Visitable visit(PatternExpr n, Object arg) {
        return super.visit(
                processingSchema.processPatternExpr().apply(n, arg),
                arg
        );
    }

    @Override
    public Visitable visit(RecordDeclaration n, Object arg) {
        return super.visit(
                processingSchema.processRecordDeclaration().apply(n, arg),
                arg
        );
    }

    @Override
    public Visitable visit(CompactConstructorDeclaration n, Object arg) {
        return super.visit(
                processingSchema.processCompactConstructorDeclaration().apply(n, arg),
                arg
        );
    }

    /**
     * Default AST processing schema.
     */
    private static final class DefaultASTProcessingSchema implements ASTProcessingSchema {
    }
}
