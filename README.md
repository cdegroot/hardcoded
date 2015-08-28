[![Build Status](https://travis-ci.org/cdegroot/hardcoded.svg)](https://travis-ci.org/cdegroot/hardcoded)
[![JitPack](https://img.shields.io/github/release/cdegroot/hardcoded.svg?label=JitPack)](https://jitpack.io/#cdegroot/hardcoded/)

# hardcoded
Away with configuration frameworks!

(another Rantware™ production by Cees de Groot)

## Rationale

Configuration files are big buckets of crap in any project that's less trivial than "hello, world". If you would look at them as code, they would not pass muster:

* Unrelated concerns in a single file
* No, or "meh" type safety
* IDEs don't understand them

In all shops where I worked the last decade or so, configuration files were checked in with the code anyway. So you need a release cycle to change them. Might as well then solve these issues and hardcode everything. 

## Interface

The library lets you say:

```scala
class DatabasePool(env: Environment) {
  // Configure client pool size: prod, lt have 50, all the rest have 2
  val clientPoolSize = EnvLocal("CLIENT_POOLSIZE", (PROD, LT) -> 50, DEFAULT -> 2)
  ...
  def setup = {
    ...
    configurePool(clientPoolSize.get(env))
    ...
  }
  ...
}
```

You hardcode the run-time values with the code, it's type safe, your IDE understands it, and you understand it. I threw in the first string (optional) so you can specify an environment variable to override the hardcoded values (see also http://12factor.net/config). This is the whole of the library API.

## How to integrate

Use [JitPack](https://jitpack.io/#cdegroot/hardcoded)

## License

This code is released under the Apache License, Version 2.0. See the included LICENSE file for details.

