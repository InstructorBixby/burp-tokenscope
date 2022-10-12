# tokenscope

Burp extension that watches in-scope traffic for session tokens, keeps
what it sees in one table, and tells you the boring things about them
that are easy to miss by eye.

## what it does

- Collects tokens from `Set-Cookie`, `Authorization: Bearer` and a few
  common custom headers, from Proxy, Repeater and Target only.
- Table of host, name, length, repeat count, JWT `alg`, and a Shannon
  entropy estimate over the decoded bytes. See `docs/ui.md` for how to
  read the columns.
- Decodes JWT header and payload claims and shows them.
- Diffs the claims of two selected tokens side by side, which is the
  fastest way to see what actually changed between two users' sessions.
- Passive scanner check: session cookie without HttpOnly / Secure /
  SameSite, `alg: none`, a JWT with no `exp`, a JWT whose `exp` is more
  than a year out.
- A **Tokens** tab in the request/response editor.
- CSV export, because the notes have to go somewhere.

## what it does not do

- **It does not verify signatures.** It splits the token, base64url
  decodes two segments and shows you what they say. A token that says
  `alg: none` is reported as issued that way, not as forged.
- It does not modify, forge or replay anything, and there is no active
  scan check.
- It only looks at traffic in the Burp target scope.
- The entropy number is an estimate on a very small sample. Low on a
  long token is interesting; the absolute value is not.

## build

    mvn clean package

Extender -> Extensions -> Add -> Java ->
`target/tokenscope-0.4.0.jar`. It has to be the shaded jar; Maven also
leaves `original-tokenscope-*.jar`, which loads fine and does nothing.

Tests: `mvn test`. CI runs them on Java 8, 11 and 17.

## history

This was the first Java I ever wrote. The early commits are a single
enormous `BurpExtender` with a hand-rolled base64 decoder in it, and I
am leaving them alone.
