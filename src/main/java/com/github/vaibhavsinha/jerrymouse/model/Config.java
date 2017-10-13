package com.github.vaibhavsinha.jerrymouse.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * Created by vaibhav on 13/10/17.
 */
@Data
public class Config {
    private String host;
    private Integer port;
    @JsonProperty("resource_root")
    private String resourceRoot;
}
