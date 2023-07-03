package com.calculator.calculatorapi.config.serialization;

import com.calculator.calculatorapi.dto.operations.*;
import com.calculator.calculatorapi.models.OperationType;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.io.IOException;

public class OperationRequestDeserializer extends JsonDeserializer<OperationRequest<?>> {

    @Override
    public OperationRequest<?> deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {
        ObjectMapper objectMapper = (ObjectMapper) jsonParser.getCodec();
        JsonNode node = jsonParser.readValueAsTree();
        JsonNode operandsNode = node.get("operands");
        OperationType operationType = objectMapper.treeToValue(node.get("operationType"), OperationType.class);
//        ObjectNode objectNode = (ObjectNode) operandsNode;
        if (operandsNode.has("seed") && operandsNode.has("size")) {
            return new StringOperationRequest(
                    new StringOperationValues(
                            operandsNode.hasNonNull("seed") ? operandsNode.get("seed").asDouble() : null,
                            operandsNode.get("size").asInt()),
                    operationType
            );
        } else {
            return new NumberOperationRequest(
                    new NumberOperationValues(
                            operandsNode.get("a").asInt(),
                            operandsNode.get("b").asInt()),
                    operationType
            );
        }
    }
}
