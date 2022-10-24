# the montoya api

PortSwigger shipped a new extension API, `burp.api.montoya`, in the
current Burp release. The legacy `IBurpExtender` interfaces still load
and are not going anywhere soon, but new work is expected to use
Montoya and the old API will stop getting attention.

What it changes for this extension:

- entry point is `BurpExtension#initialize(MontoyaApi)` instead of
  `IBurpExtender#registerExtenderCallbacks`
- `IExtensionHelpers` is gone; requests and responses are real objects
  (`HttpRequest`, `HttpResponse`) with `headers()`, `hasHeader()`,
  `cookies()` and so on, instead of parsing header strings by hand
- registration is namespaced: `api.http()`, `api.scanner()`,
  `api.userInterface()`, `api.persistence()`
- persistence replaces `saveExtensionSetting` with a typed preference
  store

Plan: port in place as a second entry point rather than rewriting. The
parts worth keeping -- `Jwt`, `Claims`, `Entropy`, `TokenDiff`,
`TokenStore` -- have no Burp types in them at all, which was an
accident but a useful one. Only the parsing and registration layers
need new code.

Not starting from scratch and not deleting anything that works.
