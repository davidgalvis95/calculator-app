package com.calculator.calculatorapi.serialization;

import com.calculator.calculatorapi.config.serialization.OperationRequestDeserializer;
import com.calculator.calculatorapi.dto.operations.NumberOperationRequest;
import com.calculator.calculatorapi.dto.operations.OperationRequest;
import com.calculator.calculatorapi.dto.operations.StringOperationRequest;
import com.calculator.calculatorapi.models.OperationType;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.DoubleNode;
import com.fasterxml.jackson.databind.node.IntNode;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
public class OperationRequestDeserializerTest {

    @Mock
    private JsonParser jsonParser;
    @Mock
    private DeserializationContext deserializationContext;
    @Mock
    private ObjectMapper objectMapper;
    @Mock
    private JsonNode jsonNode;
    @Mock
    private JsonNode operandsNode;

    @Test
    public void testDeserializeStringOperationRequest() throws IOException {
        //Given
        OperationRequestDeserializer deserializer = new OperationRequestDeserializer();
        Mockito.when(jsonParser.getCodec()).thenReturn(objectMapper);
        Mockito.when(jsonParser.readValueAsTree()).thenReturn(jsonNode);
        Mockito.when(jsonNode.get("operands")).thenReturn(operandsNode);
        Mockito.when(objectMapper.treeToValue(jsonNode.get("operationType"), OperationType.class))
                .thenReturn(OperationType.RANDOM_STRING);
        Mockito.when(operandsNode.has("seed")).thenReturn(true);
        Mockito.when(operandsNode.has("size")).thenReturn(true);
        Mockito.when(operandsNode.hasNonNull("seed")).thenReturn(true);
        Mockito.when(operandsNode.get("seed")).thenReturn(new DoubleNode(5.0));
        Mockito.when(operandsNode.get("size")).thenReturn(new IntNode(10));
        //When
        OperationRequest<?> operationRequest = deserializer.deserialize(jsonParser, deserializationContext);
        //Then
        assertEquals(StringOperationRequest.class, operationRequest.getClass());
        StringOperationRequest stringOperationRequest = (StringOperationRequest) operationRequest;
        assertEquals(5.0, stringOperationRequest.getOperands().getSeed());
        assertEquals(10, stringOperationRequest.getOperands().getSize());
        assertEquals(OperationType.RANDOM_STRING, stringOperationRequest.getOperationType());
    }

    @Test
    public void testDeserializeNumberOperationRequest() throws IOException {
        //Given
        OperationRequestDeserializer deserializer = new OperationRequestDeserializer();
        Mockito.when(jsonParser.getCodec()).thenReturn(objectMapper);
        Mockito.when(jsonParser.readValueAsTree()).thenReturn(jsonNode);
        Mockito.when(jsonNode.get("operands")).thenReturn(operandsNode);
        Mockito.when(objectMapper.treeToValue(jsonNode.get("operationType"), OperationType.class))
                .thenReturn(OperationType.ADDITION);
        Mockito.when(operandsNode.has("seed")).thenReturn(false);
        Mockito.when(operandsNode.get("a")).thenReturn(new DoubleNode(5));
        Mockito.when(operandsNode.get("b")).thenReturn(new IntNode(10));
        //When
        OperationRequest<?> operationRequest = deserializer.deserialize(jsonParser, deserializationContext);
        //Then
        assertEquals(NumberOperationRequest.class, operationRequest.getClass());
        NumberOperationRequest numberOperationRequest = (NumberOperationRequest) operationRequest;
        assertEquals(5, numberOperationRequest.getOperands().getA());
        assertEquals(10, numberOperationRequest.getOperands().getB());
        assertEquals(OperationType.ADDITION, numberOperationRequest.getOperationType());
    }
}
