# changelog

## 0.4.0

- message editor "Tokens" tab
- ignore list for analytics cookies, persisted through Burp
- entropy is measured over the decoded bytes, not the encoding
- claim diff for two selected tokens
- csv export
- store is bounded at 5000 tokens, oldest dropped first
- tests moved to JUnit 5, cookie cases are table driven

## 0.3.0

- passive scanner check: cookie flags, alg none, missing exp, very long
  exp
- flat JSON claim reader replacing the regex that read one claim
- split into Token / TokenStore / TokenParser / Jwt / Claims / Entropy
- table model only touched on the EDT

## 0.2.0

- suite tab with a token table instead of printing to stdout
- base64url decoding, which is what a JWT actually uses
- bearer and custom header tokens, not just cookies
- scope and tool filtering

## 0.1.0

- printed Set-Cookie values to the extender output pane
