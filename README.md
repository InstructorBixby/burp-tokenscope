# tokenscope

burp extension that watches traffic for session tokens (cookies,
bearer headers), pulls them out and shows them so i can see whether
they rotate, repeat, or carry claims i can read.

in java, because my jython extension is stuck on python 2.7 and i am
tired of it. first java i have written, so expect rough edges.

## build

    mvn clean package

load `target/tokenscope-0.1.0.jar` in Extender -> Extensions -> Add ->
Java. Note it is the shaded jar you want; `original-tokenscope-*.jar`
is the one maven leaves behind before shading and burp will load it
happily and then do nothing.

## what it does not do

it does not verify signatures. it decodes the header and payload of a
jwt and shows them, that is all. nothing here forges or modifies a
token.
