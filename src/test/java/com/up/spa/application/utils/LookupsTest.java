package com.up.spa.application.utils;

import static org.junit.jupiter.api.Assertions.*;

import org.bson.Document;
import org.junit.jupiter.api.Test;

public class LookupsTest {

  /**
   * Test para verificar que el método lookup retorna un documento con los campos correctos.
   */
  @Test
  void lookup_CreatesCorrectLookupDocument() {
    // Arrange
    String from = "collectionB";
    String localField = "fieldA";
    String foreignField = "fieldB";
    String as = "joinedData";

    // Act
    Document result = Lookups.lookup(from, localField, foreignField, as);

    // Assert
    assertNotNull(result, "El documento retornado no debería ser null.");
    assertTrue(result.containsKey("$lookup"), "El documento debería contener la clave '$lookup'.");

    Document lookupStage = result.get("$lookup", Document.class);
    assertNotNull(lookupStage, "El valor de '$lookup' no debería ser null.");
    assertEquals(from, lookupStage.getString("from"), "El campo 'from' no coincide.");
    assertEquals(localField, lookupStage.getString("localField"), "El campo 'localField' no coincide.");
    assertEquals(foreignField, lookupStage.getString("foreignField"), "El campo 'foreignField' no coincide.");
    assertEquals(as, lookupStage.getString("as"), "El campo 'as' no coincide.");
  }

  /**
   * Test para verificar que el método lookup maneja correctamente valores vacíos.
   */
  @Test
  void lookup_WithEmptyStrings_CreatesLookupDocumentWithEmptyFields() {
    // Arrange
    String from = "";
    String localField = "";
    String foreignField = "";
    String as = "";

    // Act
    Document result = Lookups.lookup(from, localField, foreignField, as);

    // Assert
    assertNotNull(result, "El documento retornado no debería ser null.");
    assertTrue(result.containsKey("$lookup"), "El documento debería contener la clave '$lookup'.");

    Document lookupStage = result.get("$lookup", Document.class);
    assertNotNull(lookupStage, "El valor de '$lookup' no debería ser null.");
    assertEquals(from, lookupStage.getString("from"), "El campo 'from' debería ser una cadena vacía.");
    assertEquals(localField, lookupStage.getString("localField"), "El campo 'localField' debería ser una cadena vacía.");
    assertEquals(foreignField, lookupStage.getString("foreignField"), "El campo 'foreignField' debería ser una cadena vacía.");
    assertEquals(as, lookupStage.getString("as"), "El campo 'as' debería ser una cadena vacía.");
  }

  /**
   * Test para verificar que el método lookup maneja valores null.
   * Nota: Este test puede fallar si el método no está diseñado para manejar nulls.
   * Dependiendo de la intención del método, puede ser necesario agregar validaciones.
   */
  @Test
  void lookup_WithNullValues_CreatesLookupDocumentWithNullFields() {
    // Arrange
    String from = null;
    String localField = null;
    String foreignField = null;
    String as = null;

    // Act
    Document result = Lookups.lookup(from, localField, foreignField, as);

    // Assert
    assertNotNull(result, "El documento retornado no debería ser null.");
    assertTrue(result.containsKey("$lookup"), "El documento debería contener la clave '$lookup'.");

    Document lookupStage = result.get("$lookup", Document.class);
    assertNotNull(lookupStage, "El valor de '$lookup' no debería ser null.");
    assertNull(lookupStage.get("from"), "El campo 'from' debería ser null.");
    assertNull(lookupStage.get("localField"), "El campo 'localField' debería ser null.");
    assertNull(lookupStage.get("foreignField"), "El campo 'foreignField' debería ser null.");
    assertNull(lookupStage.get("as"), "El campo 'as' debería ser null.");
  }

  /**
   * Test para verificar que múltiples llamadas al método lookup crean documentos independientes.
   */
  @Test
  void lookup_MultipleCalls_ReturnsIndependentDocuments() {
    // Arrange
    String from1 = "collectionB";
    String localField1 = "fieldA";
    String foreignField1 = "fieldB";
    String as1 = "joinedData1";

    String from2 = "collectionC";
    String localField2 = "fieldC";
    String foreignField2 = "fieldD";
    String as2 = "joinedData2";

    // Act
    Document result1 = Lookups.lookup(from1, localField1, foreignField1, as1);
    Document result2 = Lookups.lookup(from2, localField2, foreignField2, as2);

    // Assert
    // Verificar el primer documento
    assertNotNull(result1, "El primer documento retornado no debería ser null.");
    assertTrue(result1.containsKey("$lookup"), "El primer documento debería contener la clave '$lookup'.");
    Document lookupStage1 = result1.get("$lookup", Document.class);
    assertNotNull(lookupStage1, "El valor de '$lookup' en el primer documento no debería ser null.");
    assertEquals(from1, lookupStage1.getString("from"), "El campo 'from' del primer documento no coincide.");
    assertEquals(localField1, lookupStage1.getString("localField"), "El campo 'localField' del primer documento no coincide.");
    assertEquals(foreignField1, lookupStage1.getString("foreignField"), "El campo 'foreignField' del primer documento no coincide.");
    assertEquals(as1, lookupStage1.getString("as"), "El campo 'as' del primer documento no coincide.");

    // Verificar el segundo documento
    assertNotNull(result2, "El segundo documento retornado no debería ser null.");
    assertTrue(result2.containsKey("$lookup"), "El segundo documento debería contener la clave '$lookup'.");
    Document lookupStage2 = result2.get("$lookup", Document.class);
    assertNotNull(lookupStage2, "El valor de '$lookup' en el segundo documento no debería ser null.");
    assertEquals(from2, lookupStage2.getString("from"), "El campo 'from' del segundo documento no coincide.");
    assertEquals(localField2, lookupStage2.getString("localField"), "El campo 'localField' del segundo documento no coincide.");
    assertEquals(foreignField2, lookupStage2.getString("foreignField"), "El campo 'foreignField' del segundo documento no coincide.");
    assertEquals(as2, lookupStage2.getString("as"), "El campo 'as' del segundo documento no coincide.");
  }
}
