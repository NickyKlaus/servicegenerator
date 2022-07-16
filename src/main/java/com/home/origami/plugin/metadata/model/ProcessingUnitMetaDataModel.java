package com.home.origami.plugin.metadata.model;

import com.github.javaparser.ast.expr.Name;
import com.home.origami.plugin.db.model.Model;
import com.home.origami.plugin.utils.ASTNodeConverter;

import org.dizitart.no2.collection.Document;
import org.dizitart.no2.common.mapper.Mappable;
import org.dizitart.no2.common.mapper.NitriteMapper;
import org.dizitart.no2.repository.annotations.Id;

public class ProcessingUnitMetaDataModel implements Mappable, Model {
    private static final ASTNodeConverter nodeConverter = new ASTNodeConverter();

    @Id
    private String path; // unique, not null, indexed
    private String type;
    private String pkg;
    private Name name; // AST name expr as String

    public ProcessingUnitMetaDataModel() {
    }

    public ProcessingUnitMetaDataModel(String path) {
        this.path = path;
    }

    @Override
    public Document write(NitriteMapper mapper) {
        var document = Document.createDocument();
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
