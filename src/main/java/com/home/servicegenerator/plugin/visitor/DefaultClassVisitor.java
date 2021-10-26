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
        return processingSchema.postProcessCompilationUnit()
                .apply(
                        (CompilationUnit)super.visit(
                                processingSchema.preProcessCompilationUnit().apply(n, arg),
                                arg),
                        arg);
    }

    @Override
    public Visitable visit(PackageDeclaration n, Object arg) {
        return processingSchema.postProcessPackageDeclaration()
                .apply(
                        (PackageDeclaration)super.visit(
                                processingSchema.preProcessPackageDeclaration().apply(n, arg),
                                arg),
                        arg);
    }

    @Override
    public Visitable visit(TypeParameter n, Object arg) {
        return processingSchema.postProcessTypeParameter()
                .apply(
                        (TypeParameter)super.visit(
                                processingSchema.preProcessTypeParameter().apply(n, arg),
                                arg),
                        arg);
    }

    @Override
    public Visitable visit(LineComment n, Object arg) {
        return processingSchema.postProcessLineComment()
                .apply(
                        (LineComment)super.visit(
                                processingSchema.preProcessLineComment().apply(n, arg),
                                arg),
                        arg);
    }

    @Override
    public Visitable visit(BlockComment n, Object arg) {
        return processingSchema.postProcessBlockComment()
                .apply(
                        (BlockComment)super.visit(
                                processingSchema.preProcessBlockComment().apply(n, arg),
                                arg),
                        arg);
    }

    @Override
    public Visitable visit(ClassOrInterfaceDeclaration n, Object arg) {
        return processingSchema.postProcessClassOrInterfaceDeclaration()
                .apply(
                        (ClassOrInterfaceDeclaration)super.visit(
                                processingSchema.preProcessClassOrInterfaceDeclaration().apply(n, arg),
                                arg),
                        arg);
    }

    @Override
    public Visitable visit(EnumDeclaration n, Object arg) {
        return processingSchema.postProcessEnumDeclaration()
                .apply(
                        (EnumDeclaration)super.visit(
                                processingSchema.preProcessEnumDeclaration().apply(n, arg),
                                arg),
                        arg);
    }

    @Override
    public Visitable visit(EnumConstantDeclaration n, Object arg) {
        return processingSchema.postProcessEnumConstantDeclaration()
                .apply(
                        (EnumConstantDeclaration)super.visit(
                                processingSchema.preProcessEnumConstantDeclaration().apply(n, arg),
                                arg),
                        arg);
    }

    @Override
    public Visitable visit(AnnotationDeclaration n, Object arg) {
        return processingSchema.postProcessAnnotationDeclaration()
                .apply(
                        (AnnotationDeclaration)super.visit(
                                processingSchema.preProcessAnnotationDeclaration().apply(n, arg),
                                arg),
                        arg);
    }

    @Override
    public Visitable visit(AnnotationMemberDeclaration n, Object arg) {
        return processingSchema.postProcessAnnotationMemberDeclaration()
                .apply(
                        (AnnotationMemberDeclaration)super.visit(
                                processingSchema.preProcessAnnotationMemberDeclaration().apply(n, arg),
                                arg),
                        arg);
    }

    @Override
    public Visitable visit(FieldDeclaration n, Object arg) {
        return processingSchema.postProcessFieldDeclaration()
                .apply(
                        (FieldDeclaration)super.visit(
                                processingSchema.preProcessFieldDeclaration().apply(n, arg),
                                arg),
                        arg);
    }

    @Override
    public Visitable visit(VariableDeclarator n, Object arg) {
        return processingSchema.postProcessVariableDeclarator()
                .apply(
                        (VariableDeclarator)super.visit(
                                processingSchema.preProcessVariableDeclarator().apply(n, arg),
                                arg),
                        arg);
    }

    @Override
    public Visitable visit(ConstructorDeclaration n, Object arg) {
        return processingSchema.postProcessConstructorDeclaration()
                .apply(
                        (ConstructorDeclaration)super.visit(
                                processingSchema.preProcessConstructorDeclaration().apply(n, arg),
                                arg),
                        arg);
    }

    @Override
    public Visitable visit(MethodDeclaration n, Object arg) {
        return processingSchema.postProcessMethodDeclaration()
                .apply(
                        (MethodDeclaration)super.visit(
                                processingSchema.preProcessMethodDeclaration().apply(n, arg),
                                arg),
                        arg);
    }

    @Override
    public Visitable visit(Parameter n, Object arg) {
        return processingSchema.postProcessParameter()
                .apply(
                        (Parameter)super.visit(
                                processingSchema.preProcessParameter().apply(n, arg),
                                arg),
                        arg);
    }

    @Override
    public Visitable visit(InitializerDeclaration n, Object arg) {
        return processingSchema.postProcessInitializerDeclaration()
                .apply(
                        (InitializerDeclaration)super.visit(
                                processingSchema.preProcessInitializerDeclaration().apply(n, arg),
                                arg),
                        arg);
    }

    @Override
    public Visitable visit(JavadocComment n, Object arg) {
        return processingSchema.postProcessJavadocComment()
                .apply(
                        (JavadocComment)super.visit(
                                processingSchema.preProcessJavadocComment().apply(n, arg),
                                arg),
                        arg);
    }

    @Override
    public Visitable visit(ClassOrInterfaceType n, Object arg) {
        return processingSchema.postProcessClassOrInterfaceType()
                .apply(
                        (ClassOrInterfaceType)super.visit(
                                processingSchema.preProcessClassOrInterfaceType().apply(n, arg),
                                arg),
                        arg);
    }

    @Override
    public Visitable visit(PrimitiveType n, Object arg) {
        return processingSchema.postProcessPrimitiveType()
                .apply(
                        (PrimitiveType)super.visit(
                                processingSchema.preProcessPrimitiveType().apply(n, arg),
                                arg),
                        arg);
    }

    @Override
    public Visitable visit(ArrayType n, Object arg) {
        return processingSchema.postProcessArrayType()
                .apply(
                        (ArrayType)super.visit(
                                processingSchema.preProcessArrayType().apply(n, arg),
                                arg),
                        arg);
    }

    @Override
    public Visitable visit(ArrayCreationLevel n, Object arg) {
        return processingSchema.postProcessArrayCreationLevel()
                .apply(
                        (ArrayCreationLevel)super.visit(
                                processingSchema.preProcessArrayCreationLevel().apply(n, arg),
                                arg),
                        arg);
    }

    @Override
    public Visitable visit(IntersectionType n, Object arg) {
        return processingSchema.postProcessIntersectionType()
                .apply(
                        (IntersectionType)super.visit(
                                processingSchema.preProcessIntersectionType().apply(n, arg),
                                arg),
                        arg);
    }

    @Override
    public Visitable visit(UnionType n, Object arg) {
        return processingSchema.postProcessUnionType()
                .apply(
                        (UnionType)super.visit(
                                processingSchema.preProcessUnionType().apply(n, arg),
                                arg),
                        arg);
    }

    @Override
    public Visitable visit(VoidType n, Object arg) {
        return processingSchema.postProcessVoidType()
                .apply(
                        (VoidType)super.visit(
                                processingSchema.preProcessVoidType().apply(n, arg),
                                arg),
                        arg);
    }

    @Override
    public Visitable visit(WildcardType n, Object arg) {
        return processingSchema.postProcessWildcardType()
                .apply(
                        (WildcardType)super.visit(
                                processingSchema.preProcessWildcardType().apply(n, arg),
                                arg),
                        arg);
    }

    @Override
    public Visitable visit(UnknownType n, Object arg) {
        return processingSchema.postProcessUnknownType()
                .apply(
                        (UnknownType)super.visit(
                                processingSchema.preProcessUnknownType().apply(n, arg),
                                arg),
                        arg);
    }

    @Override
    public Visitable visit(ArrayAccessExpr n, Object arg) {
        return processingSchema.postProcessArrayAccessExpr()
                .apply(
                        (ArrayAccessExpr)super.visit(
                                processingSchema.preProcessArrayAccessExpr().apply(n, arg),
                                arg),
                        arg);
    }

    @Override
    public Visitable visit(ArrayCreationExpr n, Object arg) {
        return processingSchema.postProcessArrayCreationExpr()
                .apply(
                        (ArrayCreationExpr)super.visit(
                                processingSchema.preProcessArrayCreationExpr().apply(n, arg),
                                arg),
                        arg);
    }

    @Override
    public Visitable visit(ArrayInitializerExpr n, Object arg) {
        return processingSchema.postProcessArrayInitializerExpr()
                .apply(
                        (ArrayInitializerExpr)super.visit(
                                processingSchema.preProcessArrayInitializerExpr().apply(n, arg),
                                arg),
                        arg);
    }

    @Override
    public Visitable visit(AssignExpr n, Object arg) {
        return processingSchema.postProcessAssignExpr()
                .apply(
                        (AssignExpr)super.visit(
                                processingSchema.preProcessAssignExpr().apply(n, arg),
                                arg),
                        arg);
    }

    @Override
    public Visitable visit(BinaryExpr n, Object arg) {
        return processingSchema.postProcessBinaryExpr()
                .apply(
                        (BinaryExpr)super.visit(
                                processingSchema.preProcessBinaryExpr().apply(n, arg),
                                arg),
                        arg);
    }

    @Override
    public Visitable visit(CastExpr n, Object arg) {
        return processingSchema.postProcessCastExpr()
                .apply(
                        (CastExpr)super.visit(
                                processingSchema.preProcessCastExpr().apply(n, arg),
                                arg),
                        arg);
    }

    @Override
    public Visitable visit(ClassExpr n, Object arg) {
        return processingSchema.postProcessClassExpr()
                .apply(
                        (ClassExpr)super.visit(
                                processingSchema.preProcessClassExpr().apply(n, arg),
                                arg),
                        arg);
    }

    @Override
    public Visitable visit(ConditionalExpr n, Object arg) {
        return processingSchema.postProcessConditionalExpr()
                .apply(
                        (ConditionalExpr)super.visit(
                                processingSchema.preProcessConditionalExpr().apply(n, arg),
                                arg),
                        arg);
    }

    @Override
    public Visitable visit(EnclosedExpr n, Object arg) {
        return processingSchema.postProcessEnclosedExpr()
                .apply(
                        (EnclosedExpr)super.visit(
                                processingSchema.preProcessEnclosedExpr().apply(n, arg),
                                arg),
                        arg);
    }

    @Override
    public Visitable visit(FieldAccessExpr n, Object arg) {
        return processingSchema.postProcessFieldAccessExpr()
                .apply(
                        (FieldAccessExpr)super.visit(
                                processingSchema.preProcessFieldAccessExpr().apply(n, arg),
                                arg),
                        arg);
    }

    @Override
    public Visitable visit(InstanceOfExpr n, Object arg) {
        return processingSchema.postProcessInstanceOfExpr()
                .apply(
                        (InstanceOfExpr)super.visit(
                                processingSchema.preProcessInstanceOfExpr().apply(n, arg),
                                arg),
                        arg);
    }

    @Override
    public Visitable visit(StringLiteralExpr n, Object arg) {
        return processingSchema.postProcessStringLiteralExpr()
                .apply(
                        (StringLiteralExpr)super.visit(
                                processingSchema.preProcessStringLiteralExpr().apply(n, arg),
                                arg),
                        arg);
    }

    @Override
    public Visitable visit(IntegerLiteralExpr n, Object arg) {
        return processingSchema.postProcessIntegerLiteralExpr()
                .apply(
                        (IntegerLiteralExpr)super.visit(
                                processingSchema.preProcessIntegerLiteralExpr().apply(n, arg),
                                arg),
                        arg);
    }

    @Override
    public Visitable visit(LongLiteralExpr n, Object arg) {
        return processingSchema.postProcessLongLiteralExpr()
                .apply(
                        (LongLiteralExpr)super.visit(
                                processingSchema.preProcessLongLiteralExpr().apply(n, arg),
                                arg),
                        arg);
    }

    @Override
    public Visitable visit(CharLiteralExpr n, Object arg) {
        return processingSchema.postProcessCharLiteralExpr()
                .apply(
                        (CharLiteralExpr)super.visit(
                                processingSchema.preProcessCharLiteralExpr().apply(n, arg),
                                arg),
                        arg);
    }

    @Override
    public Visitable visit(DoubleLiteralExpr n, Object arg) {
        return processingSchema.postProcessDoubleLiteralExpr()
                .apply(
                        (DoubleLiteralExpr)super.visit(
                                processingSchema.preProcessDoubleLiteralExpr().apply(n, arg),
                                arg),
                        arg);
    }

    @Override
    public Visitable visit(BooleanLiteralExpr n, Object arg) {
        return processingSchema.postProcessBooleanLiteralExpr()
                .apply(
                        (BooleanLiteralExpr)super.visit(
                                processingSchema.preProcessBooleanLiteralExpr().apply(n, arg),
                                arg),
                        arg);
    }

    @Override
    public Visitable visit(NullLiteralExpr n, Object arg) {
        return processingSchema.postProcessNullLiteralExpr()
                .apply(
                        (NullLiteralExpr)super.visit(
                                processingSchema.preProcessNullLiteralExpr().apply(n, arg),
                                arg),
                        arg);
    }

    @Override
    public Visitable visit(MethodCallExpr n, Object arg) {
        return processingSchema.postProcessMethodCallExpr()
                .apply(
                        (MethodCallExpr)super.visit(
                                processingSchema.preProcessMethodCallExpr().apply(n, arg),
                                arg),
                        arg);
    }

    @Override
    public Visitable visit(NameExpr n, Object arg) {
        return processingSchema.postProcessNameExpr()
                .apply(
                        (NameExpr)super.visit(
                                processingSchema.preProcessNameExpr().apply(n, arg),
                                arg),
                        arg);
    }

    @Override
    public Visitable visit(ObjectCreationExpr n, Object arg) {
        return processingSchema.postProcessObjectCreationExpr()
                .apply(
                        (ObjectCreationExpr)super.visit(
                                processingSchema.preProcessObjectCreationExpr().apply(n, arg),
                                arg),
                        arg);
    }

    @Override
    public Visitable visit(Name n, Object arg) {
        return processingSchema.postProcessName()
                .apply(
                        (Name)super.visit(
                                processingSchema.preProcessName().apply(n, arg),
                                arg),
                        arg);
    }

    @Override
    public Visitable visit(SimpleName n, Object arg) {
        return processingSchema.postProcessSimpleName()
                .apply(
                        (SimpleName)super.visit(
                                processingSchema.preProcessSimpleName().apply(n, arg),
                                arg),
                        arg);
    }

    @Override
    public Visitable visit(ThisExpr n, Object arg) {
        return processingSchema.postProcessThisExpr()
                .apply(
                        (ThisExpr)super.visit(
                                processingSchema.preProcessThisExpr().apply(n, arg),
                                arg),
                        arg);
    }

    @Override
    public Visitable visit(SuperExpr n, Object arg) {
        return processingSchema.postProcessSuperExpr()
                .apply(
                        (SuperExpr)super.visit(
                                processingSchema.preProcessSuperExpr().apply(n, arg),
                                arg),
                        arg);
    }

    @Override
    public Visitable visit(UnaryExpr n, Object arg) {
        return processingSchema.postProcessUnaryExpr()
                .apply(
                        (UnaryExpr)super.visit(
                                processingSchema.preProcessUnaryExpr().apply(n, arg),
                                arg),
                        arg);
    }

    @Override
    public Visitable visit(VariableDeclarationExpr n, Object arg) {
        return processingSchema.postProcessVariableDeclarationExpr()
                .apply(
                        (VariableDeclarationExpr)super.visit(
                                processingSchema.preProcessVariableDeclarationExpr().apply(n, arg),
                                arg),
                        arg);
    }

    @Override
    public Visitable visit(MarkerAnnotationExpr n, Object arg) {
        return processingSchema.postProcessMarkerAnnotationExpr()
                .apply(
                        (MarkerAnnotationExpr)super.visit(
                                processingSchema.preProcessMarkerAnnotationExpr().apply(n, arg),
                                arg),
                        arg);
    }

    @Override
    public Visitable visit(SingleMemberAnnotationExpr n, Object arg) {
        return processingSchema.postProcessSingleMemberAnnotationExpr()
                .apply(
                        (SingleMemberAnnotationExpr)super.visit(
                                processingSchema.preProcessSingleMemberAnnotationExpr().apply(n, arg),
                                arg),
                        arg);
    }

    @Override
    public Visitable visit(NormalAnnotationExpr n, Object arg) {
        return processingSchema.postProcessNormalAnnotationExpr()
                .apply(
                        (NormalAnnotationExpr)super.visit(
                                processingSchema.preProcessNormalAnnotationExpr().apply(n, arg),
                                arg),
                        arg);
    }

    @Override
    public Visitable visit(MemberValuePair n, Object arg) {
        return processingSchema.postProcessMemberValuePair()
                .apply(
                        (MemberValuePair)super.visit(
                                processingSchema.preProcessMemberValuePair().apply(n, arg),
                                arg),
                        arg);
    }

    @Override
    public Visitable visit(ExplicitConstructorInvocationStmt n, Object arg) {
        return processingSchema.postProcessExplicitConstructorInvocationStmt()
                .apply(
                        (ExplicitConstructorInvocationStmt)super.visit(
                                processingSchema.preProcessExplicitConstructorInvocationStmt().apply(n, arg),
                                arg),
                        arg);
    }

    @Override
    public Visitable visit(LocalClassDeclarationStmt n, Object arg) {
        return processingSchema.postProcessLocalClassDeclarationStmt()
                .apply(
                        (LocalClassDeclarationStmt)super.visit(
                                processingSchema.preProcessLocalClassDeclarationStmt().apply(n, arg),
                                arg),
                        arg);
    }

    @Override
    public Visitable visit(LocalRecordDeclarationStmt n, Object arg) {
        return processingSchema.postProcessLocalRecordDeclarationStmt()
                .apply(
                        (LocalRecordDeclarationStmt)super.visit(
                                processingSchema.preProcessLocalRecordDeclarationStmt().apply(n, arg),
                                arg),
                        arg);
    }

    @Override
    public Visitable visit(AssertStmt n, Object arg) {
        return processingSchema.postProcessAssertStmt()
                .apply(
                        (AssertStmt)super.visit(
                                processingSchema.preProcessAssertStmt().apply(n, arg),
                                arg),
                        arg);
    }

    @Override
    public Visitable visit(BlockStmt n, Object arg) {
        return processingSchema.postProcessBlockStmt()
                .apply(
                        (BlockStmt)super.visit(
                                processingSchema.preProcessBlockStmt().apply(n, arg),
                                arg),
                        arg);
    }

    @Override
    public Visitable visit(LabeledStmt n, Object arg) {
        return processingSchema.postProcessLabeledStmt()
                .apply(
                        (LabeledStmt)super.visit(
                                processingSchema.preProcessLabeledStmt().apply(n, arg),
                                arg),
                        arg);
    }

    @Override
    public Visitable visit(EmptyStmt n, Object arg) {
        return processingSchema.postProcessEmptyStmt()
                .apply(
                        (EmptyStmt)super.visit(
                                processingSchema.preProcessEmptyStmt().apply(n, arg),
                                arg),
                        arg);
    }

    @Override
    public Visitable visit(ExpressionStmt n, Object arg) {
        return processingSchema.postProcessExpressionStmt()
                .apply(
                        (ExpressionStmt)super.visit(
                                processingSchema.preProcessExpressionStmt().apply(n, arg),
                                arg),
                        arg);
    }

    @Override
    public Visitable visit(SwitchStmt n, Object arg) {
        return processingSchema.postProcessSwitchStmt()
                .apply(
                        (SwitchStmt)super.visit(
                                processingSchema.preProcessSwitchStmt().apply(n, arg),
                                arg),
                        arg);
    }

    @Override
    public Visitable visit(SwitchEntry n, Object arg) {
        return processingSchema.postProcessSwitchEntry()
                .apply(
                        (SwitchEntry)super.visit(
                                processingSchema.preProcessSwitchEntry().apply(n, arg),
                                arg),
                        arg);
    }

    @Override
    public Visitable visit(BreakStmt n, Object arg) {
        return processingSchema.postProcessBreakStmt()
                .apply(
                        (BreakStmt)super.visit(
                                processingSchema.preProcessBreakStmt().apply(n, arg),
                                arg),
                        arg);
    }

    @Override
    public Visitable visit(ReturnStmt n, Object arg) {
        return processingSchema.postProcessReturnStmt()
                .apply(
                        (ReturnStmt)super.visit(
                                processingSchema.preProcessReturnStmt().apply(n, arg),
                                arg),
                        arg);
    }

    @Override
    public Visitable visit(IfStmt n, Object arg) {
        return processingSchema.postProcessIfStmt()
                .apply(
                        (IfStmt)super.visit(
                                processingSchema.preProcessIfStmt().apply(n, arg),
                                arg),
                        arg);
    }

    @Override
    public Visitable visit(WhileStmt n, Object arg) {
        return processingSchema.postProcessWhileStmt()
                .apply(
                        (WhileStmt)super.visit(
                                processingSchema.preProcessWhileStmt().apply(n, arg),
                                arg),
                        arg);
    }

    @Override
    public Visitable visit(ContinueStmt n, Object arg) {
        return processingSchema.postProcessContinueStmt()
                .apply(
                        (ContinueStmt)super.visit(
                                processingSchema.preProcessContinueStmt().apply(n, arg),
                                arg),
                        arg);
    }

    @Override
    public Visitable visit(DoStmt n, Object arg) {
        return processingSchema.postProcessDoStmt()
                .apply(
                        (DoStmt)super.visit(
                                processingSchema.preProcessDoStmt().apply(n, arg),
                                arg),
                        arg);
    }

    @Override
    public Visitable visit(ForEachStmt n, Object arg) {
        return processingSchema.postProcessForEachStmt()
                .apply(
                        (ForEachStmt)super.visit(
                                processingSchema.preProcessForEachStmt().apply(n, arg),
                                arg),
                        arg);
    }

    @Override
    public Visitable visit(ForStmt n, Object arg) {
        return processingSchema.postProcessForStmt()
                .apply(
                        (ForStmt)super.visit(
                                processingSchema.preProcessForStmt().apply(n, arg),
                                arg),
                        arg);
    }

    @Override
    public Visitable visit(ThrowStmt n, Object arg) {
        return processingSchema.postProcessThrowStmt()
                .apply(
                        (ThrowStmt)super.visit(
                                processingSchema.preProcessThrowStmt().apply(n, arg),
                                arg),
                        arg);
    }

    @Override
    public Visitable visit(SynchronizedStmt n, Object arg) {
        return processingSchema.postProcessSynchronizedStmt()
                .apply(
                        (SynchronizedStmt)super.visit(
                                processingSchema.preProcessSynchronizedStmt().apply(n, arg),
                                arg),
                        arg);
    }

    @Override
    public Visitable visit(TryStmt n, Object arg) {
        return processingSchema.postProcessTryStmt()
                .apply(
                        (TryStmt)super.visit(
                                processingSchema.preProcessTryStmt().apply(n, arg),
                                arg),
                        arg);
    }

    @Override
    public Visitable visit(CatchClause n, Object arg) {
        return processingSchema.postProcessCatchClause()
                .apply(
                        (CatchClause)super.visit(
                                processingSchema.preProcessCatchClause().apply(n, arg),
                                arg),
                        arg);
    }

    @Override
    public Visitable visit(LambdaExpr n, Object arg) {
        return processingSchema.postProcessLambdaExpr()
                .apply(
                        (LambdaExpr)super.visit(
                                processingSchema.preProcessLambdaExpr().apply(n, arg),
                                arg),
                        arg);
    }

    @Override
    public Visitable visit(MethodReferenceExpr n, Object arg) {
        return processingSchema.postProcessMethodReferenceExpr()
                .apply(
                        (MethodReferenceExpr)super.visit(
                                processingSchema.preProcessMethodReferenceExpr().apply(n, arg),
                                arg),
                        arg);
    }

    @Override
    public Visitable visit(TypeExpr n, Object arg) {
        return processingSchema.postProcessTypeExpr()
                .apply(
                        (TypeExpr)super.visit(
                                processingSchema.preProcessTypeExpr().apply(n, arg),
                                arg),
                        arg);
    }

    @Override
    public Visitable visit(NodeList n, Object arg) {
        return processingSchema.postProcessNodeList()
                .apply(
                        (NodeList)super.visit(
                                processingSchema.preProcessNodeList().apply(n, arg),
                                arg),
                        arg);
    }

    @Override
    public Visitable visit(ModuleDeclaration n, Object arg) {
        return processingSchema.postProcessModuleDeclaration()
                .apply(
                        (ModuleDeclaration)super.visit(
                                processingSchema.preProcessModuleDeclaration().apply(n, arg),
                                arg),
                        arg);
    }

    @Override
    public Visitable visit(ModuleRequiresDirective n, Object arg) {
        return processingSchema.postProcessModuleRequiresDirective()
                .apply(
                        (ModuleRequiresDirective)super.visit(
                                processingSchema.preProcessModuleRequiresDirective().apply(n, arg),
                                arg),
                        arg);
    }

    @Override
    public Visitable visit(ModuleExportsDirective n, Object arg) {
        return processingSchema.postProcessModuleExportsDirective()
                .apply(
                        (ModuleExportsDirective)super.visit(
                                processingSchema.preProcessModuleExportsDirective().apply(n, arg),
                                arg),
                        arg);
    }

    @Override
    public Visitable visit(ModuleProvidesDirective n, Object arg) {
        return processingSchema.postProcessModuleProvidesDirective()
                .apply(
                        (ModuleProvidesDirective)super.visit(
                                processingSchema.preProcessModuleProvidesDirective().apply(n, arg),
                                arg),
                        arg);
    }

    @Override
    public Visitable visit(ModuleUsesDirective n, Object arg) {
        return processingSchema.postProcessModuleUsesDirective()
                .apply(
                        (ModuleUsesDirective)super.visit(
                                processingSchema.preProcessModuleUsesDirective().apply(n, arg),
                                arg),
                        arg);
    }

    @Override
    public Visitable visit(ModuleOpensDirective n, Object arg) {
        return processingSchema.postProcessModuleOpensDirective()
                .apply(
                        (ModuleOpensDirective)super.visit(
                                processingSchema.preProcessModuleOpensDirective().apply(n, arg),
                                arg),
                        arg);
    }

    @Override
    public Visitable visit(UnparsableStmt n, Object arg) {
        return processingSchema.postProcessUnparsableStmt()
                .apply(
                        (UnparsableStmt)super.visit(
                                processingSchema.preProcessUnparsableStmt().apply(n, arg),
                                arg),
                        arg);
    }

    @Override
    public Visitable visit(ReceiverParameter n, Object arg) {
        return processingSchema.postProcessReceiverParameter()
                .apply(
                        (ReceiverParameter)super.visit(
                                processingSchema.preProcessReceiverParameter().apply(n, arg),
                                arg),
                        arg);
    }

    @Override
    public Visitable visit(VarType n, Object arg) {
        return processingSchema.postProcessVarType()
                .apply(
                        (VarType)super.visit(
                                processingSchema.preProcessVarType().apply(n, arg),
                                arg),
                        arg);
    }

    @Override
    public Visitable visit(Modifier n, Object arg) {
        return processingSchema.postProcessModifier()
                .apply(
                        (Modifier)super.visit(
                                processingSchema.preProcessModifier().apply(n, arg),
                                arg),
                        arg);
    }

    @Override
    public Visitable visit(SwitchExpr n, Object arg) {
        return processingSchema.postProcessSwitchExpr()
                .apply(
                        (SwitchExpr)super.visit(
                                processingSchema.preProcessSwitchExpr().apply(n, arg),
                                arg),
                        arg);
    }

    @Override
    public Visitable visit(YieldStmt n, Object arg) {
        return processingSchema.postProcessYieldStmt()
                .apply(
                        (YieldStmt)super.visit(
                                processingSchema.preProcessYieldStmt().apply(n, arg),
                                arg),
                        arg);
    }

    @Override
    public Visitable visit(TextBlockLiteralExpr n, Object arg) {
        return processingSchema.postProcessTextBlockLiteralExpr()
                .apply(
                        (TextBlockLiteralExpr)super.visit(
                                processingSchema.preProcessTextBlockLiteralExpr().apply(n, arg),
                                arg),
                        arg);
    }

    @Override
    public Visitable visit(PatternExpr n, Object arg) {
        return processingSchema.postProcessPatternExpr()
                .apply(
                        (PatternExpr)super.visit(
                                processingSchema.preProcessPatternExpr().apply(n, arg),
                                arg),
                        arg);
    }

    @Override
    public Visitable visit(RecordDeclaration n, Object arg) {
        return processingSchema.postProcessRecordDeclaration()
                .apply(
                        (RecordDeclaration)super.visit(
                                processingSchema.preProcessRecordDeclaration().apply(n, arg),
                                arg),
                        arg);
    }

    @Override
    public Visitable visit(CompactConstructorDeclaration n, Object arg) {
        return processingSchema.postProcessCompactConstructorDeclaration()
                .apply(
                        (CompactConstructorDeclaration)super.visit(
                                processingSchema.preProcessCompactConstructorDeclaration().apply(n, arg),
                                arg),
                        arg);
    }

    /**
     * Default AST processing schema.
     */
    private static final class DefaultASTProcessingSchema implements ASTProcessingSchema {
    }
}
