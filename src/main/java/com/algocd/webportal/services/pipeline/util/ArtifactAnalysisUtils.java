package com.algocd.webportal.services.pipeline.util;

import com.algocd.webportal.exceptions.ArtifactPipelineException;
import com.algocd.webportal.exceptions.ErrorReason;
import com.algocd.webportal.mql.tree.*;
import com.algocd.webportal.services.pipeline.models.ArtifactConfig;
import com.algocd.webportal.services.pipeline.models.ArtifactMetadata;
import com.algocd.webportal.services.pipeline.models.ArtifactParameter;
import org.jspecify.annotations.NonNull;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

public class ArtifactAnalysisUtils {

    private ArtifactAnalysisUtils() {
        // Utility class
    }

    public static ArtifactConfig extractConfig(Statement[] statements, String originalFilename, UUID userId) {
        String kind = "expert";
        String version = "v1";
        String name = originalFilename.replaceAll("\\.[^.]*$", "");

        Map<String, ArtifactParameter> params = new LinkedHashMap<>();

        for (Statement stmt : statements) {
            if (stmt instanceof PropertyStatement(String propNameRaw, Expression value)) {
                String propName = propNameRaw.toLowerCase();
                if (propName.startsWith("indicator_")) {
                    kind = "indicator";
                } else if (propName.startsWith("script_") || propName.equals("library")) {
                    throw new ArtifactPipelineException(ErrorReason.INVALID_ARTIFACT_TYPE,
                            "Invalid artifact kind: script or library not supported", userId, originalFilename);
                } else if (propName.equals("version") && value instanceof Literal.StringLiteral(String value1)) {
                    version = value1;
                }
            } else if (stmt instanceof VariableDeclaration varDecl) {
                params.put(varDecl.name(), getArtifactParameter(varDecl));
            }
        }

        ArtifactMetadata metadata = new ArtifactMetadata();
        metadata.setId(UUID.randomUUID().toString());
        metadata.setName(name);

        ArtifactConfig config = new ArtifactConfig();
        config.setKind(kind);
        config.setVersion(version);
        config.setMetadata(metadata);
        config.setParameters(params);

        return config;
    }

    private static @NonNull ArtifactParameter getArtifactParameter(VariableDeclaration varDecl) {
        ArtifactParameter param = new ArtifactParameter();

        String typeStr = switch (varDecl.type()) {
            case STRING, IDENTIFIER -> "String";
            case BOOL -> "bool";
            case INT -> "int";
            case DOUBLE -> "double";
            default -> varDecl.type().label;
        };
        param.setType(typeStr);

        if (varDecl.value() != null) {
            switch (varDecl.value()) {
                case Literal.StringLiteral(String value) -> param.setValue(value);
                case Literal.NumberLiteral(String value) -> {
                    try {
                        if (value.contains(".")) {
                            param.setValue(Double.parseDouble(value));
                        } else {
                            param.setValue(Integer.parseInt(value));
                        }
                    } catch (NumberFormatException e) {
                        param.setValue(value);
                    }
                }
                case Literal.BooleanLiteral(boolean value) -> param.setValue(value);
                case Identifier(String name) -> {
                    param.setValue(name);
                    param.setType("String");
                }
                default -> {
                }
            }
        }
        return param;
    }
}
