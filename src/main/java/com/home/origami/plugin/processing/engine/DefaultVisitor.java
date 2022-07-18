package com.home.origami.plugin.processing.engine;

import com.github.javaparser.ast.ArrayCreationLevel;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.ImportDeclaration;
import com.github.javaparser.ast.Modifier;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.PackageDeclaration;
import com.github.javaparser.ast.body.AnnotationDeclaration;
import com.github.javaparser.ast.body.AnnotationMemberDeclaration;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.CompactConstructorDeclaration;
import com.github.javaparser.ast.body.ConstructorDeclaration;
import com.github.javaparser.ast.body.EnumConstantDeclaration;
import com.github.javaparser.ast.body.EnumDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.InitializerDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.body.ReceiverParameter;
import com.github.javaparser.ast.body.RecordDeclaration;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.comments.BlockComment;
import com.github.javaparser.ast.comments.JavadocComment;
import com.github.javaparser.ast.comments.LineComment;
import com.github.javaparser.ast.expr.ArrayAccessExpr;
import com.github.javaparser.ast.expr.ArrayCreationExpr;
import com.github.javaparser.ast.expr.ArrayInitializerExpr;
import com.github.javaparser.ast.expr.AssignExpr;
import com.github.javaparser.ast.expr.BinaryExpr;
import com.github.javaparser.ast.expr.BooleanLiteralExpr;
import com.github.javaparser.ast.expr.CastExpr;
import com.github.javaparser.ast.expr.CharLiteralExpr;
import com.github.javaparser.ast.expr.ClassExpr;
import com.github.javaparser.ast.expr.ConditionalExpr;
import com.github.javaparser.ast.expr.DoubleLiteralExpr;
import com.github.javaparser.ast.expr.EnclosedExpr;
import com.github.javaparser.ast.expr.FieldAccessExpr;
import com.github.javaparser.ast.expr.InstanceOfExpr;
import com.github.javaparser.ast.expr.IntegerLiteralExpr;
import com.github.javaparser.ast.expr.LambdaExpr;
import com.github.javaparser.ast.expr.LongLiteralExpr;
import com.github.javaparser.ast.expr.MarkerAnnotationExpr;
import com.github.javaparser.ast.expr.MemberValuePair;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.MethodReferenceExpr;
import com.github.javaparser.ast.expr.Name;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.expr.NormalAnnotationExpr;
import com.github.javaparser.ast.expr.NullLiteralExpr;
import com.github.javaparser.ast.expr.ObjectCreationExpr;
import com.github.javaparser.ast.expr.PatternExpr;
import com.github.javaparser.ast.expr.SimpleName;
import com.github.javaparser.ast.expr.SingleMemberAnnotationExpr;
import com.github.javaparser.ast.expr.StringLiteralExpr;
import com.github.javaparser.ast.expr.SuperExpr;
import com.github.javaparser.ast.expr.SwitchExpr;
import com.github.javaparser.ast.expr.TextBlockLiteralExpr;
import com.github.javaparser.ast.expr.ThisExpr;
import com.github.javaparser.ast.expr.TypeExpr;
import com.github.javaparser.ast.expr.UnaryExpr;
import com.github.javaparser.ast.expr.VariableDeclarationExpr;
import com.github.javaparser.ast.modules.ModuleDeclaration;
import com.github.javaparser.ast.modules.ModuleExportsDirective;
import com.github.javaparser.ast.modules.ModuleOpensDirective;
import com.github.javaparser.ast.modules.ModuleProvidesDirective;
import com.github.javaparser.ast.modules.ModuleRequiresDirective;
import com.github.javaparser.ast.modules.ModuleUsesDirective;
import com.github.javaparser.ast.stmt.AssertStmt;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.BreakStmt;
import com.github.javaparser.ast.stmt.CatchClause;
import com.github.javaparser.ast.stmt.ContinueStmt;
import com.github.javaparser.ast.stmt.DoStmt;
import com.github.javaparser.ast.stmt.EmptyStmt;
import com.github.javaparser.ast.stmt.ExplicitConstructorInvocationStmt;
import com.github.javaparser.ast.stmt.ExpressionStmt;
import com.github.javaparser.ast.stmt.ForEachStmt;
import com.github.javaparser.ast.stmt.ForStmt;
import com.github.javaparser.ast.stmt.IfStmt;
import com.github.javaparser.ast.stmt.LabeledStmt;
import com.github.javaparser.ast.stmt.LocalClassDeclarationStmt;
import com.github.javaparser.ast.stmt.LocalRecordDeclarationStmt;
import com.github.javaparser.ast.stmt.ReturnStmt;
import com.github.javaparser.ast.stmt.SwitchEntry;
import com.github.javaparser.ast.stmt.SwitchStmt;
import com.github.javaparser.ast.stmt.SynchronizedStmt;
import com.github.javaparser.ast.stmt.ThrowStmt;
import com.github.javaparser.ast.stmt.TryStmt;
import com.github.javaparser.ast.stmt.UnparsableStmt;
import com.github.javaparser.ast.stmt.WhileStmt;
import com.github.javaparser.ast.stmt.YieldStmt;
import com.github.javaparser.ast.type.ArrayType;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import com.github.javaparser.ast.type.IntersectionType;
import com.github.javaparser.ast.type.PrimitiveType;
import com.github.javaparser.ast.type.TypeParameter;
import com.github.javaparser.ast.type.UnionType;
import com.github.javaparser.ast.type.UnknownType;
import com.github.javaparser.ast.type.VarType;
import com.github.javaparser.ast.type.VoidType;
import com.github.javaparser.ast.type.WildcardType;
import com.github.javaparser.ast.visitor.*;
import com.home.origami.api.ASTProcessingSchema;
import com.home.origami.api.context.Context;

/**
 * Visitor goes around Abstract Syntax Tree nodes of the base class, processes them (applies a processing action)
 * recursively and modifies base nodes with generated ones.
 *
 * Note: according to AST Processing Schema API visitor modifies each processing base class node by default
 *       (in case no explicit visitor's action provided).
 *
 * @see ASTProcessingSchema
 */
class DefaultVisitor implements GenericVisitor<Visitable, Context> {
    private final ModifierVisitor<Context> visitor;
    private ASTProcessingSchema processingSchema;

    public DefaultVisitor() {
        this.visitor = new ModifierVisitor<>();
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
    public Visitable visit(CompilationUnit n, Context arg) {
        return processingSchema.postProcessCompilationUnit()
                .apply(
                        (CompilationUnit)visitor.visit(
                                processingSchema.preProcessCompilationUnit().apply(n, arg),
                                arg),
                        arg);
    }

    @Override
    public Visitable visit(PackageDeclaration n, Context arg) {
        return processingSchema.postProcessPackageDeclaration()
                .apply(
                        (PackageDeclaration)visitor.visit(
                                processingSchema.preProcessPackageDeclaration().apply(n, arg),
                                arg),
                        arg);
    }

    @Override
    public Node visit(ImportDeclaration n, Context arg) {
        return processingSchema.postProcessImportDeclaration()
                .apply(
                        (ImportDeclaration)visitor.visit(
                                (ImportDeclaration)processingSchema.preProcessImportDeclaration().apply(n, arg),
                                arg),
                        arg);
    }

    @Override
    public Visitable visit(TypeParameter n, Context arg) {
        return processingSchema.postProcessTypeParameter()
                .apply(
                        (TypeParameter)visitor.visit(
                                processingSchema.preProcessTypeParameter().apply(n, arg),
                                arg),
                        arg);
    }

    @Override
    public Visitable visit(LineComment n, Context arg) {
        return processingSchema.postProcessLineComment()
                .apply(
                        (LineComment)visitor.visit(
                                processingSchema.preProcessLineComment().apply(n, arg),
                                arg),
                        arg);
    }

    @Override
    public Visitable visit(BlockComment n, Context arg) {
        return processingSchema.postProcessBlockComment()
                .apply(
                        (BlockComment)visitor.visit(
                                processingSchema.preProcessBlockComment().apply(n, arg),
                                arg),
                        arg);
    }

    @Override
    public Visitable visit(ClassOrInterfaceDeclaration n, Context arg) {
        return processingSchema.postProcessClassOrInterfaceDeclaration()
                .apply(
                        (ClassOrInterfaceDeclaration)visitor.visit(
                                processingSchema.preProcessClassOrInterfaceDeclaration().apply(n, arg),
                                arg),
                        arg);
    }

    @Override
    public Visitable visit(EnumDeclaration n, Context arg) {
        return processingSchema.postProcessEnumDeclaration()
                .apply(
                        (EnumDeclaration)visitor.visit(
                                processingSchema.preProcessEnumDeclaration().apply(n, arg),
                                arg),
                        arg);
    }

    @Override
    public Visitable visit(EnumConstantDeclaration n, Context arg) {
        return processingSchema.postProcessEnumConstantDeclaration()
                .apply(
                        (EnumConstantDeclaration)visitor.visit(
                                processingSchema.preProcessEnumConstantDeclaration().apply(n, arg),
                                arg),
                        arg);
    }

    @Override
    public Visitable visit(AnnotationDeclaration n, Context arg) {
        return processingSchema.postProcessAnnotationDeclaration()
                .apply(
                        (AnnotationDeclaration)visitor.visit(
                                processingSchema.preProcessAnnotationDeclaration().apply(n, arg),
                                arg),
                        arg);
    }

    @Override
    public Visitable visit(AnnotationMemberDeclaration n, Context arg) {
        return processingSchema.postProcessAnnotationMemberDeclaration()
                .apply(
                        (AnnotationMemberDeclaration)visitor.visit(
                                processingSchema.preProcessAnnotationMemberDeclaration().apply(n, arg),
                                arg),
                        arg);
    }

    @Override
    public Visitable visit(FieldDeclaration n, Context arg) {
        return processingSchema.postProcessFieldDeclaration()
                .apply(
                        (FieldDeclaration)visitor.visit(
                                processingSchema.preProcessFieldDeclaration().apply(n, arg),
                                arg),
                        arg);
    }

    @Override
    public Visitable visit(VariableDeclarator n, Context arg) {
        return processingSchema.postProcessVariableDeclarator()
                .apply(
                        (VariableDeclarator)visitor.visit(
                                processingSchema.preProcessVariableDeclarator().apply(n, arg),
                                arg),
                        arg);
    }

    @Override
    public Visitable visit(ConstructorDeclaration n, Context arg) {
        return processingSchema.postProcessConstructorDeclaration()
                .apply(
                        (ConstructorDeclaration)visitor.visit(
                                processingSchema.preProcessConstructorDeclaration().apply(n, arg),
                                arg),
                        arg);
    }

    @Override
    public Visitable visit(MethodDeclaration n, Context arg) {
        return processingSchema.postProcessMethodDeclaration()
                .apply(
                        (MethodDeclaration)visitor.visit(
                                processingSchema.preProcessMethodDeclaration().apply(n, arg),
                                arg),
                        arg);
    }

    @Override
    public Visitable visit(Parameter n, Context arg) {
        return processingSchema.postProcessParameter()
                .apply(
                        (Parameter)visitor.visit(
                                processingSchema.preProcessParameter().apply(n, arg),
                                arg),
                        arg);
    }

    @Override
    public Visitable visit(InitializerDeclaration n, Context arg) {
        return processingSchema.postProcessInitializerDeclaration()
                .apply(
                        (InitializerDeclaration)visitor.visit(
                                processingSchema.preProcessInitializerDeclaration().apply(n, arg),
                                arg),
                        arg);
    }

    @Override
    public Visitable visit(JavadocComment n, Context arg) {
        return processingSchema.postProcessJavadocComment()
                .apply(
                        (JavadocComment)visitor.visit(
                                processingSchema.preProcessJavadocComment().apply(n, arg),
                                arg),
                        arg);
    }

    @Override
    public Visitable visit(ClassOrInterfaceType n, Context arg) {
        return processingSchema.postProcessClassOrInterfaceType()
                .apply(
                        (ClassOrInterfaceType)visitor.visit(
                                processingSchema.preProcessClassOrInterfaceType().apply(n, arg),
                                arg),
                        arg);
    }

    @Override
    public Visitable visit(PrimitiveType n, Context arg) {
        return processingSchema.postProcessPrimitiveType()
                .apply(
                        (PrimitiveType)visitor.visit(
                                processingSchema.preProcessPrimitiveType().apply(n, arg),
                                arg),
                        arg);
    }

    @Override
    public Visitable visit(ArrayType n, Context arg) {
        return processingSchema.postProcessArrayType()
                .apply(
                        (ArrayType)visitor.visit(
                                processingSchema.preProcessArrayType().apply(n, arg),
                                arg),
                        arg);
    }

    @Override
    public Visitable visit(ArrayCreationLevel n, Context arg) {
        return processingSchema.postProcessArrayCreationLevel()
                .apply(
                        (ArrayCreationLevel)visitor.visit(
                                processingSchema.preProcessArrayCreationLevel().apply(n, arg),
                                arg),
                        arg);
    }

    @Override
    public Visitable visit(IntersectionType n, Context arg) {
        return processingSchema.postProcessIntersectionType()
                .apply(
                        (IntersectionType)visitor.visit(
                                processingSchema.preProcessIntersectionType().apply(n, arg),
                                arg),
                        arg);
    }

    @Override
    public Visitable visit(UnionType n, Context arg) {
        return processingSchema.postProcessUnionType()
                .apply(
                        (UnionType)visitor.visit(
                                processingSchema.preProcessUnionType().apply(n, arg),
                                arg),
                        arg);
    }

    @Override
    public Visitable visit(VoidType n, Context arg) {
        return processingSchema.postProcessVoidType()
                .apply(
                        (VoidType)visitor.visit(
                                processingSchema.preProcessVoidType().apply(n, arg),
                                arg),
                        arg);
    }

    @Override
    public Visitable visit(WildcardType n, Context arg) {
        return processingSchema.postProcessWildcardType()
                .apply(
                        (WildcardType)visitor.visit(
                                processingSchema.preProcessWildcardType().apply(n, arg),
                                arg),
                        arg);
    }

    @Override
    public Visitable visit(UnknownType n, Context arg) {
        return processingSchema.postProcessUnknownType()
                .apply(
                        (UnknownType)visitor.visit(
                                processingSchema.preProcessUnknownType().apply(n, arg),
                                arg),
                        arg);
    }

    @Override
    public Visitable visit(ArrayAccessExpr n, Context arg) {
        return processingSchema.postProcessArrayAccessExpr()
                .apply(
                        (ArrayAccessExpr)visitor.visit(
                                processingSchema.preProcessArrayAccessExpr().apply(n, arg),
                                arg),
                        arg);
    }

    @Override
    public Visitable visit(ArrayCreationExpr n, Context arg) {
        return processingSchema.postProcessArrayCreationExpr()
                .apply(
                        (ArrayCreationExpr)visitor.visit(
                                processingSchema.preProcessArrayCreationExpr().apply(n, arg),
                                arg),
                        arg);
    }

    @Override
    public Visitable visit(ArrayInitializerExpr n, Context arg) {
        return processingSchema.postProcessArrayInitializerExpr()
                .apply(
                        (ArrayInitializerExpr)visitor.visit(
                                processingSchema.preProcessArrayInitializerExpr().apply(n, arg),
                                arg),
                        arg);
    }

    @Override
    public Visitable visit(AssignExpr n, Context arg) {
        return processingSchema.postProcessAssignExpr()
                .apply(
                        (AssignExpr)visitor.visit(
                                processingSchema.preProcessAssignExpr().apply(n, arg),
                                arg),
                        arg);
    }

    @Override
    public Visitable visit(BinaryExpr n, Context arg) {
        return processingSchema.postProcessBinaryExpr()
                .apply(
                        (BinaryExpr)visitor.visit(
                                processingSchema.preProcessBinaryExpr().apply(n, arg),
                                arg),
                        arg);
    }

    @Override
    public Visitable visit(CastExpr n, Context arg) {
        return processingSchema.postProcessCastExpr()
                .apply(
                        (CastExpr)visitor.visit(
                                processingSchema.preProcessCastExpr().apply(n, arg),
                                arg),
                        arg);
    }

    @Override
    public Visitable visit(ClassExpr n, Context arg) {
        return processingSchema.postProcessClassExpr()
                .apply(
                        (ClassExpr)visitor.visit(
                                processingSchema.preProcessClassExpr().apply(n, arg),
                                arg),
                        arg);
    }

    @Override
    public Visitable visit(ConditionalExpr n, Context arg) {
        return processingSchema.postProcessConditionalExpr()
                .apply(
                        (ConditionalExpr)visitor.visit(
                                processingSchema.preProcessConditionalExpr().apply(n, arg),
                                arg),
                        arg);
    }

    @Override
    public Visitable visit(EnclosedExpr n, Context arg) {
        return processingSchema.postProcessEnclosedExpr()
                .apply(
                        (EnclosedExpr)visitor.visit(
                                processingSchema.preProcessEnclosedExpr().apply(n, arg),
                                arg),
                        arg);
    }

    @Override
    public Visitable visit(FieldAccessExpr n, Context arg) {
        return processingSchema.postProcessFieldAccessExpr()
                .apply(
                        (FieldAccessExpr)visitor.visit(
                                processingSchema.preProcessFieldAccessExpr().apply(n, arg),
                                arg),
                        arg);
    }

    @Override
    public Visitable visit(InstanceOfExpr n, Context arg) {
        return processingSchema.postProcessInstanceOfExpr()
                .apply(
                        (InstanceOfExpr)visitor.visit(
                                processingSchema.preProcessInstanceOfExpr().apply(n, arg),
                                arg),
                        arg);
    }

    @Override
    public Visitable visit(StringLiteralExpr n, Context arg) {
        return processingSchema.postProcessStringLiteralExpr()
                .apply(
                        (StringLiteralExpr)visitor.visit(
                                processingSchema.preProcessStringLiteralExpr().apply(n, arg),
                                arg),
                        arg);
    }

    @Override
    public Visitable visit(IntegerLiteralExpr n, Context arg) {
        return processingSchema.postProcessIntegerLiteralExpr()
                .apply(
                        (IntegerLiteralExpr)visitor.visit(
                                processingSchema.preProcessIntegerLiteralExpr().apply(n, arg),
                                arg),
                        arg);
    }

    @Override
    public Visitable visit(LongLiteralExpr n, Context arg) {
        return processingSchema.postProcessLongLiteralExpr()
                .apply(
                        (LongLiteralExpr)visitor.visit(
                                processingSchema.preProcessLongLiteralExpr().apply(n, arg),
                                arg),
                        arg);
    }

    @Override
    public Visitable visit(CharLiteralExpr n, Context arg) {
        return processingSchema.postProcessCharLiteralExpr()
                .apply(
                        (CharLiteralExpr)visitor.visit(
                                processingSchema.preProcessCharLiteralExpr().apply(n, arg),
                                arg),
                        arg);
    }

    @Override
    public Visitable visit(DoubleLiteralExpr n, Context arg) {
        return processingSchema.postProcessDoubleLiteralExpr()
                .apply(
                        (DoubleLiteralExpr)visitor.visit(
                                processingSchema.preProcessDoubleLiteralExpr().apply(n, arg),
                                arg),
                        arg);
    }

    @Override
    public Visitable visit(BooleanLiteralExpr n, Context arg) {
        return processingSchema.postProcessBooleanLiteralExpr()
                .apply(
                        (BooleanLiteralExpr)visitor.visit(
                                processingSchema.preProcessBooleanLiteralExpr().apply(n, arg),
                                arg),
                        arg);
    }

    @Override
    public Visitable visit(NullLiteralExpr n, Context arg) {
        return processingSchema.postProcessNullLiteralExpr()
                .apply(
                        (NullLiteralExpr)visitor.visit(
                                processingSchema.preProcessNullLiteralExpr().apply(n, arg),
                                arg),
                        arg);
    }

    @Override
    public Visitable visit(MethodCallExpr n, Context arg) {
        return processingSchema.postProcessMethodCallExpr()
                .apply(
                        (MethodCallExpr)visitor.visit(
                                processingSchema.preProcessMethodCallExpr().apply(n, arg),
                                arg),
                        arg);
    }

    @Override
    public Visitable visit(NameExpr n, Context arg) {
        return processingSchema.postProcessNameExpr()
                .apply(
                        (NameExpr)visitor.visit(
                                processingSchema.preProcessNameExpr().apply(n, arg),
                                arg),
                        arg);
    }

    @Override
    public Visitable visit(ObjectCreationExpr n, Context arg) {
        return processingSchema.postProcessObjectCreationExpr()
                .apply(
                        (ObjectCreationExpr)visitor.visit(
                                processingSchema.preProcessObjectCreationExpr().apply(n, arg),
                                arg),
                        arg);
    }

    @Override
    public Visitable visit(Name n, Context arg) {
        return processingSchema.postProcessName()
                .apply(
                        (Name)visitor.visit(
                                processingSchema.preProcessName().apply(n, arg),
                                arg),
                        arg);
    }

    @Override
    public Visitable visit(SimpleName n, Context arg) {
        return processingSchema.postProcessSimpleName()
                .apply(
                        (SimpleName)visitor.visit(
                                processingSchema.preProcessSimpleName().apply(n, arg),
                                arg),
                        arg);
    }

    @Override
    public Visitable visit(ThisExpr n, Context arg) {
        return processingSchema.postProcessThisExpr()
                .apply(
                        (ThisExpr)visitor.visit(
                                processingSchema.preProcessThisExpr().apply(n, arg),
                                arg),
                        arg);
    }

    @Override
    public Visitable visit(SuperExpr n, Context arg) {
        return processingSchema.postProcessSuperExpr()
                .apply(
                        (SuperExpr)visitor.visit(
                                processingSchema.preProcessSuperExpr().apply(n, arg),
                                arg),
                        arg);
    }

    @Override
    public Visitable visit(UnaryExpr n, Context arg) {
        return processingSchema.postProcessUnaryExpr()
                .apply(
                        (UnaryExpr)visitor.visit(
                                processingSchema.preProcessUnaryExpr().apply(n, arg),
                                arg),
                        arg);
    }

    @Override
    public Visitable visit(VariableDeclarationExpr n, Context arg) {
        return processingSchema.postProcessVariableDeclarationExpr()
                .apply(
                        (VariableDeclarationExpr)visitor.visit(
                                processingSchema.preProcessVariableDeclarationExpr().apply(n, arg),
                                arg),
                        arg);
    }

    @Override
    public Visitable visit(MarkerAnnotationExpr n, Context arg) {
        return processingSchema.postProcessMarkerAnnotationExpr()
                .apply(
                        (MarkerAnnotationExpr)visitor.visit(
                                processingSchema.preProcessMarkerAnnotationExpr().apply(n, arg),
                                arg),
                        arg);
    }

    @Override
    public Visitable visit(SingleMemberAnnotationExpr n, Context arg) {
        return processingSchema.postProcessSingleMemberAnnotationExpr()
                .apply(
                        (SingleMemberAnnotationExpr)visitor.visit(
                                processingSchema.preProcessSingleMemberAnnotationExpr().apply(n, arg),
                                arg),
                        arg);
    }

    @Override
    public Visitable visit(NormalAnnotationExpr n, Context arg) {
        return processingSchema.postProcessNormalAnnotationExpr()
                .apply(
                        (NormalAnnotationExpr)visitor.visit(
                                processingSchema.preProcessNormalAnnotationExpr().apply(n, arg),
                                arg),
                        arg);
    }

    @Override
    public Visitable visit(MemberValuePair n, Context arg) {
        return processingSchema.postProcessMemberValuePair()
                .apply(
                        (MemberValuePair)visitor.visit(
                                processingSchema.preProcessMemberValuePair().apply(n, arg),
                                arg),
                        arg);
    }

    @Override
    public Visitable visit(ExplicitConstructorInvocationStmt n, Context arg) {
        return processingSchema.postProcessExplicitConstructorInvocationStmt()
                .apply(
                        (ExplicitConstructorInvocationStmt)visitor.visit(
                                processingSchema.preProcessExplicitConstructorInvocationStmt().apply(n, arg),
                                arg),
                        arg);
    }

    @Override
    public Visitable visit(LocalClassDeclarationStmt n, Context arg) {
        return processingSchema.postProcessLocalClassDeclarationStmt()
                .apply(
                        (LocalClassDeclarationStmt)visitor.visit(
                                processingSchema.preProcessLocalClassDeclarationStmt().apply(n, arg),
                                arg),
                        arg);
    }

    @Override
    public Visitable visit(LocalRecordDeclarationStmt n, Context arg) {
        return processingSchema.postProcessLocalRecordDeclarationStmt()
                .apply(
                        (LocalRecordDeclarationStmt)visitor.visit(
                                processingSchema.preProcessLocalRecordDeclarationStmt().apply(n, arg),
                                arg),
                        arg);
    }

    @Override
    public Visitable visit(AssertStmt n, Context arg) {
        return processingSchema.postProcessAssertStmt()
                .apply(
                        (AssertStmt)visitor.visit(
                                processingSchema.preProcessAssertStmt().apply(n, arg),
                                arg),
                        arg);
    }

    @Override
    public Visitable visit(BlockStmt n, Context arg) {
        return processingSchema.postProcessBlockStmt()
                .apply(
                        (BlockStmt)visitor.visit(
                                processingSchema.preProcessBlockStmt().apply(n, arg),
                                arg),
                        arg);
    }

    @Override
    public Visitable visit(LabeledStmt n, Context arg) {
        return processingSchema.postProcessLabeledStmt()
                .apply(
                        (LabeledStmt)visitor.visit(
                                processingSchema.preProcessLabeledStmt().apply(n, arg),
                                arg),
                        arg);
    }

    @Override
    public Visitable visit(EmptyStmt n, Context arg) {
        return processingSchema.postProcessEmptyStmt()
                .apply(
                        (EmptyStmt)visitor.visit(
                                processingSchema.preProcessEmptyStmt().apply(n, arg),
                                arg),
                        arg);
    }

    @Override
    public Visitable visit(ExpressionStmt n, Context arg) {
        return processingSchema.postProcessExpressionStmt()
                .apply(
                        (ExpressionStmt)visitor.visit(
                                processingSchema.preProcessExpressionStmt().apply(n, arg),
                                arg),
                        arg);
    }

    @Override
    public Visitable visit(SwitchStmt n, Context arg) {
        return processingSchema.postProcessSwitchStmt()
                .apply(
                        (SwitchStmt)visitor.visit(
                                processingSchema.preProcessSwitchStmt().apply(n, arg),
                                arg),
                        arg);
    }

    @Override
    public Visitable visit(SwitchEntry n, Context arg) {
        return processingSchema.postProcessSwitchEntry()
                .apply(
                        (SwitchEntry)visitor.visit(
                                processingSchema.preProcessSwitchEntry().apply(n, arg),
                                arg),
                        arg);
    }

    @Override
    public Visitable visit(BreakStmt n, Context arg) {
        return processingSchema.postProcessBreakStmt()
                .apply(
                        (BreakStmt)visitor.visit(
                                processingSchema.preProcessBreakStmt().apply(n, arg),
                                arg),
                        arg);
    }

    @Override
    public Visitable visit(ReturnStmt n, Context arg) {
        return processingSchema.postProcessReturnStmt()
                .apply(
                        (ReturnStmt)visitor.visit(
                                processingSchema.preProcessReturnStmt().apply(n, arg),
                                arg),
                        arg);
    }

    @Override
    public Visitable visit(IfStmt n, Context arg) {
        return processingSchema.postProcessIfStmt()
                .apply(
                        (IfStmt)visitor.visit(
                                processingSchema.preProcessIfStmt().apply(n, arg),
                                arg),
                        arg);
    }

    @Override
    public Visitable visit(WhileStmt n, Context arg) {
        return processingSchema.postProcessWhileStmt()
                .apply(
                        (WhileStmt)visitor.visit(
                                processingSchema.preProcessWhileStmt().apply(n, arg),
                                arg),
                        arg);
    }

    @Override
    public Visitable visit(ContinueStmt n, Context arg) {
        return processingSchema.postProcessContinueStmt()
                .apply(
                        (ContinueStmt)visitor.visit(
                                processingSchema.preProcessContinueStmt().apply(n, arg),
                                arg),
                        arg);
    }

    @Override
    public Visitable visit(DoStmt n, Context arg) {
        return processingSchema.postProcessDoStmt()
                .apply(
                        (DoStmt)visitor.visit(
                                processingSchema.preProcessDoStmt().apply(n, arg),
                                arg),
                        arg);
    }

    @Override
    public Visitable visit(ForEachStmt n, Context arg) {
        return processingSchema.postProcessForEachStmt()
                .apply(
                        (ForEachStmt)visitor.visit(
                                processingSchema.preProcessForEachStmt().apply(n, arg),
                                arg),
                        arg);
    }

    @Override
    public Visitable visit(ForStmt n, Context arg) {
        return processingSchema.postProcessForStmt()
                .apply(
                        (ForStmt)visitor.visit(
                                processingSchema.preProcessForStmt().apply(n, arg),
                                arg),
                        arg);
    }

    @Override
    public Visitable visit(ThrowStmt n, Context arg) {
        return processingSchema.postProcessThrowStmt()
                .apply(
                        (ThrowStmt)visitor.visit(
                                processingSchema.preProcessThrowStmt().apply(n, arg),
                                arg),
                        arg);
    }

    @Override
    public Visitable visit(SynchronizedStmt n, Context arg) {
        return processingSchema.postProcessSynchronizedStmt()
                .apply(
                        (SynchronizedStmt)visitor.visit(
                                processingSchema.preProcessSynchronizedStmt().apply(n, arg),
                                arg),
                        arg);
    }

    @Override
    public Visitable visit(TryStmt n, Context arg) {
        return processingSchema.postProcessTryStmt()
                .apply(
                        (TryStmt)visitor.visit(
                                processingSchema.preProcessTryStmt().apply(n, arg),
                                arg),
                        arg);
    }

    @Override
    public Visitable visit(CatchClause n, Context arg) {
        return processingSchema.postProcessCatchClause()
                .apply(
                        (CatchClause)visitor.visit(
                                processingSchema.preProcessCatchClause().apply(n, arg),
                                arg),
                        arg);
    }

    @Override
    public Visitable visit(LambdaExpr n, Context arg) {
        return processingSchema.postProcessLambdaExpr()
                .apply(
                        (LambdaExpr)visitor.visit(
                                processingSchema.preProcessLambdaExpr().apply(n, arg),
                                arg),
                        arg);
    }

    @Override
    public Visitable visit(MethodReferenceExpr n, Context arg) {
        return processingSchema.postProcessMethodReferenceExpr()
                .apply(
                        (MethodReferenceExpr)visitor.visit(
                                processingSchema.preProcessMethodReferenceExpr().apply(n, arg),
                                arg),
                        arg);
    }

    @Override
    public Visitable visit(TypeExpr n, Context arg) {
        return processingSchema.postProcessTypeExpr()
                .apply(
                        (TypeExpr)visitor.visit(
                                processingSchema.preProcessTypeExpr().apply(n, arg),
                                arg),
                        arg);
    }

    @Override
    public Visitable visit(NodeList n, Context arg) {
        return processingSchema.postProcessNodeList()
                .apply(
                        (NodeList<?>)visitor.visit(
                                processingSchema.preProcessNodeList().apply(n, arg),
                                arg),
                        arg);
    }

    @Override
    public Visitable visit(ModuleDeclaration n, Context arg) {
        return processingSchema.postProcessModuleDeclaration()
                .apply(
                        (ModuleDeclaration)visitor.visit(
                                processingSchema.preProcessModuleDeclaration().apply(n, arg),
                                arg),
                        arg);
    }

    @Override
    public Visitable visit(ModuleRequiresDirective n, Context arg) {
        return processingSchema.postProcessModuleRequiresDirective()
                .apply(
                        (ModuleRequiresDirective)visitor.visit(
                                processingSchema.preProcessModuleRequiresDirective().apply(n, arg),
                                arg),
                        arg);
    }

    @Override
    public Visitable visit(ModuleExportsDirective n, Context arg) {
        return processingSchema.postProcessModuleExportsDirective()
                .apply(
                        (ModuleExportsDirective)visitor.visit(
                                processingSchema.preProcessModuleExportsDirective().apply(n, arg),
                                arg),
                        arg);
    }

    @Override
    public Visitable visit(ModuleProvidesDirective n, Context arg) {
        return processingSchema.postProcessModuleProvidesDirective()
                .apply(
                        (ModuleProvidesDirective)visitor.visit(
                                processingSchema.preProcessModuleProvidesDirective().apply(n, arg),
                                arg),
                        arg);
    }

    @Override
    public Visitable visit(ModuleUsesDirective n, Context arg) {
        return processingSchema.postProcessModuleUsesDirective()
                .apply(
                        (ModuleUsesDirective)visitor.visit(
                                processingSchema.preProcessModuleUsesDirective().apply(n, arg),
                                arg),
                        arg);
    }

    @Override
    public Visitable visit(ModuleOpensDirective n, Context arg) {
        return processingSchema.postProcessModuleOpensDirective()
                .apply(
                        (ModuleOpensDirective)visitor.visit(
                                processingSchema.preProcessModuleOpensDirective().apply(n, arg),
                                arg),
                        arg);
    }

    @Override
    public Visitable visit(UnparsableStmt n, Context arg) {
        return processingSchema.postProcessUnparsableStmt()
                .apply(
                        (UnparsableStmt)visitor.visit(
                                processingSchema.preProcessUnparsableStmt().apply(n, arg),
                                arg),
                        arg);
    }

    @Override
    public Visitable visit(ReceiverParameter n, Context arg) {
        return processingSchema.postProcessReceiverParameter()
                .apply(
                        (ReceiverParameter)visitor.visit(
                                processingSchema.preProcessReceiverParameter().apply(n, arg),
                                arg),
                        arg);
    }

    @Override
    public Visitable visit(VarType n, Context arg) {
        return processingSchema.postProcessVarType()
                .apply(
                        (VarType)visitor.visit(
                                processingSchema.preProcessVarType().apply(n, arg),
                                arg),
                        arg);
    }

    @Override
    public Visitable visit(Modifier n, Context arg) {
        return processingSchema.postProcessModifier()
                .apply(
                        (Modifier)visitor.visit(
                                processingSchema.preProcessModifier().apply(n, arg),
                                arg),
                        arg);
    }

    @Override
    public Visitable visit(SwitchExpr n, Context arg) {
        return processingSchema.postProcessSwitchExpr()
                .apply(
                        (SwitchExpr)visitor.visit(
                                processingSchema.preProcessSwitchExpr().apply(n, arg),
                                arg),
                        arg);
    }

    @Override
    public Visitable visit(YieldStmt n, Context arg) {
        return processingSchema.postProcessYieldStmt()
                .apply(
                        (YieldStmt)visitor.visit(
                                processingSchema.preProcessYieldStmt().apply(n, arg),
                                arg),
                        arg);
    }

    @Override
    public Visitable visit(TextBlockLiteralExpr n, Context arg) {
        return processingSchema.postProcessTextBlockLiteralExpr()
                .apply(
                        (TextBlockLiteralExpr)visitor.visit(
                                processingSchema.preProcessTextBlockLiteralExpr().apply(n, arg),
                                arg),
                        arg);
    }

    @Override
    public Visitable visit(PatternExpr n, Context arg) {
        return processingSchema.postProcessPatternExpr()
                .apply(
                        (PatternExpr)visitor.visit(
                                processingSchema.preProcessPatternExpr().apply(n, arg),
                                arg),
                        arg);
    }

    @Override
    public Visitable visit(RecordDeclaration n, Context arg) {
        return processingSchema.postProcessRecordDeclaration()
                .apply(
                        (RecordDeclaration)visitor.visit(
                                processingSchema.preProcessRecordDeclaration().apply(n, arg),
                                arg),
                        arg);
    }

    @Override
    public Visitable visit(CompactConstructorDeclaration n, Context arg) {
        return processingSchema.postProcessCompactConstructorDeclaration()
                .apply(
                        (CompactConstructorDeclaration)visitor.visit(
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
