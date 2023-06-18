package com.merkletree.regular.mapper;

import org.mapstruct.Mapper;
import java.util.Map;

@Mapper(componentModel = "spring")
public interface NodesMapper
{
    Map<String, String> nodeDboToDto(Map<Integer, String> map);
}
