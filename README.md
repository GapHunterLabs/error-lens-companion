# Error Lens Companion

IntelliJ-family plugin. Shows errors and warnings inline, at the end of
the line that has them, instead of only as a gutter icon or a squiggly
underline you have to hover to read. One hint per line (most severe
diagnostic wins) so it never turns into unreadable clutter.

## Why it exists

Ports the "Error Lens" concept -- VS Code's Error Lens extension has
10M+ installs and is one of that ecosystem's most-used extensions --
into the IDE's own highlighting pipeline. Confirmed before building this
that no equivalent already exists in JetBrains Marketplace. Same
"port a proven concept, no equivalent exists yet" bet as the other 6
plugins built this same session -- see `CONSTITUTION.md` §1 for the
documented-exception discipline this follows.

## Why built this way

- **Reads, never re-analyzes.** `ErrorLensHighlightingPass` is
  registered (via `TextEditorHighlightingPassFactoryRegistrar`) to run
  *after* the IDE's own general highlighting pass (`Pass.UPDATE_ALL`)
  finishes, and only reads the `HighlightInfo` results that pass already
  computed, through `DaemonCodeAnalyzerEx.processHighlights`. It never
  triggers, duplicates, or slows down any actual code analysis --
  structurally, it cannot be the reason your inspections get slower.
- **Off-EDT where the platform requires it.** `doCollectInformation`
  (reading `HighlightInfo`s) runs on a background thread, exactly like
  the platform's own `TextEditorHighlightingPass` contract requires --
  confirmed for real while writing `ErrorLensPassFactoryTest`, where an
  early version of the *test* (not the implementation) tripped the
  platform's own `assertBackgroundThread()` check. Only
  `doApplyInformationToEditor` (creating/disposing inlays) touches the
  editor, and only on the EDT.
- **One hint per line, most severe wins.** `LineDiagnosticSelector`
  collapses several diagnostics on one line down to a single inline
  hint -- avoids the "wall of text" failure mode a naive one-inlay-per-
  diagnostic implementation would have on a messy line.
- **Own severity scale, not the platform's.** `DiagnosticSeverity` maps
  down to exactly three levels (error / warning / weak warning) from a
  plain, constructible DTO -- keeps the selection and formatting logic
  unit-testable with plain JUnit, no IDE boot required, independent of
  the platform's own `HighlightSeverity` (which carries dozens of
  language-specific and internal levels this plugin deliberately never
  surfaces inline).
- **100% local** -- no network call, no account, no telemetry.

## Known limitations

This is the plugin in this session's batch with the least live
verification. Everything up through "an inlay gets created at the right
offset with the right text" is covered by real, passing tests against a
real `Editor`/`InlayModel` (see `ErrorLensInlayManagerTest`) -- but the
actual on-screen pixels (`ErrorLensInlayRenderer.paint`: exact vertical
alignment, color against every theme, font fallback for the icon
glyphs ✖ ⚠ ℹ) have not been looked at yet. If those glyphs don't render
cleanly in every font, that's the first thing to check by hand before
calling this Marketplace-ready.

## Usage

Automatic -- open any file the IDE already highlights errors/warnings
in, and matching lines get an inline hint at the end of the line. No
action to trigger, no configuration yet.

## Enterprise / Team Licensing

Need enterprise features, custom rules, or team licensing? Contact us at
**gaphunterlabs@gmail.com**.

## Development

```
./gradlew test           # unit tests
./gradlew buildPlugin    # generates build/distributions/*.zip
./gradlew verifyPlugin   # checks compatibility against real IDEs
```

## License

Apache-2.0. See `LICENSE`.
