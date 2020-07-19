package com.codefathers.cfkclient.dtos.resources;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.core.io.ByteArrayResource;

import java.util.ArrayList;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class ResourceListDto {
    private ArrayList<ByteArrayResource> resources;
}
