package com.mast.readup.entities;

import java.util.List;

public interface JsonStringListConverter {

    String convertToDatabaseColumn(List<String> attribute);

    List<String> convertToEntityAttribute(String dbData);
}
