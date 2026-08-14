# Demo — Error Lens Companion

Este es el plugin más riesgoso de los 7 -- el único que engancha el
pipeline REAL de highlighting del IDE (`TextEditorHighlightingPass` +
`InlayModel.addAfterLineEndElement`). Todo lo demás ya se probó en
tests automatizados (creación/reemplazo de inlays contra un
`Editor` real), pero el `paint()` real -- cómo se ve el texto en
pantalla -- nunca se vio en vivo hasta ahora.

## Paso 1 (garantizado, no necesita nada configurado)

1. Abrí `broken.json` -- tiene una coma sobrante en `"list"` y le
   falta la llave de cierre final, a propósito.
2. El IDE ya debería marcarlo con errores reales (el propio parser de
   JSON, sin necesitar SDK ni configuración de proyecto).
3. **Esto es lo importante:** ¿aparece un hint inline al final de la
   línea con el error, además del subrayado/ícono normal del gutter?
   Anotá cómo se ve -- alineación vertical, si el texto se lee bien,
   si el color contrasta con el fondo.

## Paso 2 (mejor esfuerzo -- puede necesitar un JDK configurado)

1. Abrí `Sample.java`. Tiene 4 métodos, cada uno con un problema
   distinto:
   - `brokenSyntax()`: falta un `;` -- error de sintaxis real,
     tampoco necesita SDK.
   - `unresolvedSymbol()`: llama a un método que no existe --
     necesita el proyecto con un JDK configurado para que el IDE
     pueda intentar resolverlo y marcarlo.
   - `unusedLocal()`: variable declarada y nunca leída -- warning,
     también puede necesitar inspecciones activas.
   - `twoProblemsOneLine()`: DOS símbolos sin resolver en la misma
     línea -- para ver si el plugin muestra un solo hint por línea
     (el más severo), no dos superpuestos.
2. Si el archivo se ve "gris"/sin ningún error marcado más allá del
   de sintaxis, es probable que el proyecto no tenga un JDK
   configurado -- normal en un sandbox vacío, no es un bug. Si querés
   forzarlo: `File → Project Structure → SDK` y elegí cualquier JDK
   que la máquina tenga instalado. Opcional, no es necesario para
   validar lo más importante (que es el Paso 1).

## Qué reportar

- ¿Aparece el hint inline al final de línea para los errores del
  JSON, sí o no?
- ¿Cómo se ve? (alineación, legibilidad, color) -- esto es lo que
  nunca se vio antes.
- Si probaste el Java también: ¿un solo hint por línea en
  `twoProblemsOneLine()`, no dos pisándose?
- ¿Algo se ve roto, cortado, o el IDE tira una excepción visible?
