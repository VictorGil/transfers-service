package net.devaction.transfersservice.core.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import net.devaction.transfersservice.api.entity.ResponseData;

/**
 * @author Víctor Gil
 *
 * since February 2020
 */
public class Response {
    private static final ObjectMapper MAPPER = new ObjectMapper();
    private static final JsonNode EMPTY_JSON_NODE = MAPPER.createObjectNode();

    @JsonProperty("status")
    private final Status status;

    @JsonProperty("error_message")
    private final String errorMessage;

    @JsonProperty("data")
    private final JsonNode data;

    public Response(Status status, String errorMessage, ResponseData responseData) {
        this.status = status;
        this.errorMessage = errorMessage;
        this.data = MAPPER.valueToTree(responseData);
    }

    public Response(Status status, String errorMessage) {
        this.status = status;
        this.errorMessage = errorMessage;
        data = EMPTY_JSON_NODE;
    }

    public Response() {
        this.status = Status.SUCCESS;
        this.errorMessage = "N/A";
        data = EMPTY_JSON_NODE;
    }
}
