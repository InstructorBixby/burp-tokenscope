# the tab

## tokens table

| column | meaning |
| --- | --- |
| host | host the token was first seen on |
| name | cookie name, or the header it came from |
| jwt | three dot-separated segments that decoded |
| alg | `alg` out of the JWT header, as claimed |
| bits/byte | Shannon estimate over the decoded bytes |
| len | length of the raw token as sent |
| seen | how many messages carried this exact value |

A `seen` count that keeps climbing on a login endpoint means the app is
handing the same session token back rather than rotating it. Two rows
with the same value and different hosts means one token crosses a host
boundary.

`bits/byte` is an estimate, not a measurement. Random 16 byte values
score around 3.9 because 16 samples cannot show more; what is worth
looking at is a long token scoring low, which usually means structure
(a counter, a timestamp, padding) rather than randomness.

## settings

Cookie names to ignore, one per line. Defaults cover the common
analytics cookies. Stored through Burp, so they survive a reload.

## message editor

Requests and responses that carry a token get a **Tokens** tab showing
the value and, for a JWT, the decoded header and payload. Nothing is
verified and nothing is editable.
