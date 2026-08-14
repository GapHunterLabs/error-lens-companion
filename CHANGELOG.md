<!-- Keep a Changelog guide -> https://keepachangelog.com -->

# Error Lens Companion Changelog

## [Unreleased]

## [0.1.0]

### Added

- **Inline error/warning hints**: errors and warnings from the IDE's own
  highlighting now also show as a short icon+message at the end of the
  line that has them, not just as a gutter icon/squiggle.
- One hint per line -- the most severe diagnostic wins when a line has
  several problems.
- Long diagnostic messages are truncated with an ellipsis instead of
  running off-screen.

### Verified

- Live-tested 2026-08-14 in a real `runIde` sandbox: real inline hints
  confirmed rendering correctly for JSON syntax errors and real Java
  diagnostics (unresolved symbol, unused import/method/variable, syntax
  error) -- readable, correctly positioned. Confirmed one-hint-per-line
  holds against real `HighlightInfo` data, not just unit-test DTOs.
  0 bugs found. See README.md "Live verification".

[Unreleased]: https://github.com/GapHunterLabs/error-lens-companion/compare/0.1.0...HEAD
[0.1.0]: https://github.com/GapHunterLabs/error-lens-companion/commits/0.1.0
