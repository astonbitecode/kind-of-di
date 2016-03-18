# kind-of-di

A library that provides the means for simple Dependency Injection (DI) in Scala.

The purpose of the library is to help keeping the code clean and to make it easer to test; it is not to provide a full DI Framework.

## Quick Start

The _kind-of-di_ is actually creating instances of Types and injects them in other instances that need them.

In order for this to happen, the user code needs to provide information on _how_ an instance can be created.

### How to define Objects creation

If you want a `HelpfulClass` of yours to be created once and get injected in other classes (like in [Spring](https://projects.spring.io/spring-framework/#quick-start)'s Singleton scope) you can do it with the following piece of code:

```
import go.libre.abc.kindof.di._

diDefine(() => new HelpfulClass())

```

The `HelpfulClass` has nothing special. It can be __any__ Class.

The call to `diDefine` has to be done in an early phase _before_ any instantiation of Classes that need the `HelpfulClass` to be injected.

### How to use Injections in your Classes

If you want the `HelpfulClass` from above to be injected into a Class of yours, you can do the following:

```
import go.libre.abc.kindof.di._

class MyClass {
  private val helpfulClass = inject[HelpfulClass]

	/*
	 * Rest of the code here
	 */
}

```

Now the `MyClass` can be instantiated as usual...

`val myClass = new MyClass()`

...and the `helpfulClass` field will get populated by the library.

<Going further>
<Please, refer to the [wiki](https://github.com/astonbitecode/kind-of-di/wiki) for more information>

## Short TODO List

* Provide a Maven artifact
* Provide a SBT plugin
* Enrich this README and add pages
* Add to Travis CI
* Implement DSL
* Implement `init` hooks