package com.home.servicegenerator.plugin.processing.registry.meta.model;

import com.github.javaparser.ast.expr.Name;
import com.home.servicegenerator.plugin.utils.ASTNodeConverter;

import org.dizitart.no2.Document;
import org.dizitart.no2.IndexType;
import org.dizitart.no2.mapper.Mappable;
import org.dizitart.no2.mapper.NitriteMapper;
import org.dizitart.no2.objects.Id;
import org.dizitart.no2.objects.Index;
import org.dizitart.no2.objects.Indices;

@Indices({
        @Index(value = "path", type = IndexType.Unique)
})
public class ProcessingUnitMetaModel implements Mappable, MetaModel {
    private static final ASTNodeConverter nodeConverter = new ASTNodeConverter();

    @Id
    private String path; // unique, not null, indexed
    private String type;
    private String pkg;
    private Name name; // AST name expr as String

    public ProcessingUnitMetaModel() {
    }

    public ProcessingUnitMetaModel(String path) {
        this.path = path;
    }

    @Override
    public Document write(NitriteMapper mapper) {
        var document = new Document();
        document.put("path", getPath());
        document.put("type", getType());
        document.put("pkg", getPkg());
        if (getName() != null) {
            document.put("name", nodeConverter.convertToJson(getName()));
        }
        return document;
    }

    @Override
    public void read(NitriteMapper mapper, Document document) {
        if (document != null) {
            setPath(document.get("path", String.class));
            setType(document.get("type", String.class));
            setType(document.get("pkg", String.class));
            setName(nodeConverter.convertToASTNode(document.get("name", String.class), Name.class));
        }
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getPkg() {
        return pkg;
    }

    public void setPkg(String pkg) {
        this.pkg = pkg;
    }

    public Name getName() {
        return name;
    }

    public void setName(Name name) {
        this.name = name;
    }
}
