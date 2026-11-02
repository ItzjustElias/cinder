# Contributing to Cinder

Bug reports and pull requests are welcome. The full developer guide is in
[docs/DEVELOPING.md](docs/DEVELOPING.md). The short version:

1. Open an issue first for anything bigger than a small fix, so nobody builds something that will not be merged.
2. Fork, then branch from `master`. Name the branch after the change: `fix-occlusion-cache`, `feat-per-type-ui`.
3. Run `./gradlew spotlessApply` before you commit and `./gradlew buildAll` before you push.
4. Open a pull request. Keep it to one change. The CI must be green.

Code that does not touch a loader belongs in `common/`. If you need a loader API there, add a method to
`Platform` and implement it in both loader modules instead of importing the loader.
