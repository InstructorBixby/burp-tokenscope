# install

1. `mvn clean package`
2. Burp -> Extender -> Extensions -> Add
3. Extension type: **Java**
4. Select file: `target/tokenscope-0.1.0.jar`

Notes to self:

- it has to be the shaded jar. maven also writes
  `original-tokenscope-0.1.0.jar`, and burp loads that one without
  complaining and then nothing happens.
- burp holds the jar open, so `mvn package` while the extension is
  loaded fails on windows and silently keeps the old classes on linux.
  unload the extension first, or just use the reload button after
  building.
- no jython, no python path, no 2.7. that was the whole point.
