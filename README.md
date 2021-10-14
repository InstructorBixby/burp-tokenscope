# tokenscope

Burp extension that watches in-scope traffic for session tokens
(Set-Cookie, `Authorization: Bearer`, a few common custom headers),
keeps them in a table with length, repeat count and a Shannon entropy
estimate, and decodes the claims of anything that is a JWT.

It also registers a passive scanner check for the boring-but-real
stuff: session cookies missing HttpOnly / Secure / SameSite, `alg:none`,
a JWT with no `exp`, a JWT whoose `exp` is a year or more out.

## build

    mvn clean package

Load `target/tokenscope-0.2.0.jar` via Extender -> Extensions -> Add ->
Java. It has to be the shaded jar; maven also leaves
`original-tokenscope-*.jar` and Burp will load that one and do nothing.

## what it does not do

- **It does not verify signatures.** It splits a JWT, base64url-decodes
  the header and payload, and shows you what they say.
- It does not modify, forge or replay anything. There is no active scan
  check and there is not going to be one.
- It only looks at traffic that is in the Burp target scope.

## notes

First Java I have written, so the early history is not pretty.
