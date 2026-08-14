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

### Known limitations

- Real on-screen rendering (`ErrorLensInlayRenderer.paint`) has not had
  a live visual pass yet -- see README.md "Known limitations".

[Unreleased]: https://github.com/GapHunterLabs/error-lens-companion/compare/0.1.0...HEAD
[0.1.0]: https://github.com/GapHunterLabs/error-lens-companion/commits/0.1.0
