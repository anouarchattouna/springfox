package com.mangofactory.schema.property.field
import com.mangofactory.swagger.mixins.ModelPropertyLookupSupport
import com.mangofactory.swagger.mixins.ModelProviderSupport
import com.mangofactory.swagger.mixins.TypesForTestingSupport
import com.mangofactory.schema.plugins.ModelContext
import com.mangofactory.schema.alternates.AlternateTypeProvider
import com.mangofactory.service.model.AllowableListValues
import spock.lang.Specification

import static com.google.common.collect.Lists.newArrayList

@Mixin([TypesForTestingSupport, ModelPropertyLookupSupport, ModelProviderSupport])
class FieldModelPropertySpec extends Specification {
  def "Extracting information from resolved fields" () {
    given:
      def typeToTest = typeForTestingGettersAndSetters()
      def modelContext = ModelContext.inputParam(typeToTest, documentationType())
      def field = field(typeToTest, fieldName)
      def sut = new FieldModelProperty(fieldName, field, new AlternateTypeProvider())

    expect:
      sut.propertyDescription() == null //documentationType(): Added test
      !sut.required
      sut.typeName(modelContext) == typeName
      sut.qualifiedTypeName() == qualifiedTypeName
      if (allowableValues != null) {
        def values = newArrayList(allowableValues)
        sut.allowableValues() == new AllowableListValues(values, "string")
      } else {
        sut.allowableValues() == null
      }
      sut.getName() == fieldName
      sut.getType() == field.getType()

    where:
    fieldName       || description          | isRequired | typeName             | qualifiedTypeName                                               | allowableValues
    "intProp"       || "int Property Field" | true       | "int"                | "int"                                                           | null
    "boolProp"      || null                 | false      | "boolean"            | "boolean"                                                       | null
    "enumProp"      || null                 | false      | "string"             | "com.mangofactory.schema.ExampleEnum"                   | ["ONE", "TWO"]
    "genericProp"   || null                 | false      | "GenericType«string»"| "com.mangofactory.schema.GenericType<java.lang.String>" | null
  }

  def "Extracting information from generic fields with array type binding" () {
    given:
      def typeToTest = typeForTestingGettersAndSetters()
      def modelContext = ModelContext.inputParam(typeToTest, documentationType())
      def field = field(typeToTest, fieldName)
      def sut = new FieldModelProperty(fieldName, field, new AlternateTypeProvider())

    expect:
      sut.typeName(modelContext) == typeName
      sut.qualifiedTypeName() == qualifiedTypeName
      sut.getName() == fieldName
      sut.getType() == field.getType()


    where:
      fieldName             || typeName                       | qualifiedTypeName
      "genericByteArray"    || "GenericType«Array«byte»»"     | "com.mangofactory.schema.GenericType<byte[]>"
      "genericCategoryArray"|| "GenericType«Array«Category»»" | "com.mangofactory.schema.GenericType<com.mangofactory.schema.Category[]>"
  }
}