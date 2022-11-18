# tokenscope

Burp extension that watches in-scope traffic for session tokens, keeps
what it sees in one table, and tells you the boring things about them
that are easy to miss by eye.

> **Which build to load:** the legacy one,
> `net.c01n31.tokenscope.BurpExtender`. There is a Montoya port under
> `src/main/java/net/c01n31/tokenscope/montoya/` and it is **not
> finished** -- it collects tokens, logs a line, and has no table, no
> diff and no scanner checks. See `docs/montoya.md`.

## what it does

- Collects tokens from `Set-Cookie`, `Authorization: Bearer` and a few
  common custom headers, from Proxy, Repeater and Target only.
- Table of host, name, length, repeat count, JWT `alg`, and a Shannon
  entropy estimate over the decoded bytes (`docs/ui.md`).
- Decodes JWT header and payload claims and shows them.
- Diffs the claims of two selected tokens side by side.
- Passive scanner check: session cookie without HttpOnly / Secure /
  SameSite, `alg: none`, a JWT with no `exp`, a JWT whose `exp` is more
  than a year out.
- A **Tokens** tab in the request/response editor.
- CSV export.

## what it does not do

- **It does not verify signatures.** It splits the token, base64url
  decodes two segments and shows you what they say.
- It does not modify, forge or replay anything, and there is no active
  scan check.
- It only looks at traffic in the Burp target scope.
- The entropy number is an estimate on a very small sample.

## build

    mvn clean package

Extender -> Extensions -> Add -> Java -> the shaded
`target/tokenscope-*.jar`. Maven also leaves
`original-tokenscope-*.jar`, which loads fine and does nothing.

Tests: `mvn test`.

## history

First Java I ever wrote. The early commits are one enormous
`BurpExtender` with a hand-rolled base64 decoder in it, and I am
leaving them alone.
