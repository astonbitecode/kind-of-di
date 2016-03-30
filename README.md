# kind-of-di [![Build Status](https://travis-ci.org/astonbitecode/kind-of-di.svg?branch=master)](https://travis-ci.org/astonbitecode/kind-of-di)

This is a library that provides the means for simple Dependency Injection (DI) in Scala.

Its purpose is to help keeping the code clean and to make it easer to test; it is not to provide a full DI Framework.

## Quick Start

The _kind-of-di_ is actually creating instances of Types and injects them in other instances that need them.

In order for this to happen, the user code needs to provide information on _how_ an instance can be created.

### How to define Objects creation

If you want a `HelpfulClass` of yours to be created once and get injected in other classes (like in [Spring](https://projects.spring.io/spring-framework/#quick-start)'s Singleton scope) you can do it with the following piece of code:

```
import com.github.astonbitecode.di._

diDefine(() => new HelpfulClass())

```

The `HelpfulClass` has nothing special. It can be __any__ Class.

The call to `diDefine` has to be done in an early phase _before_ any instantiation of Classes that need the `HelpfulClass` to be injected.

### How to use Injections in your Classes

If you want the `HelpfulClass` from above to be injected into a Class of yours, you can do the following:

```
import com.github.astonbitecode.di._

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

## Scopes and Lifecycle

When calling the `diDefine`, you provide the information of __how__ instances will be created and injected.

The _kind-of-di_ uses these definitions when needed in order to deliver/inject the right object. Following the paradigm of other DI frameworks, scopes define the lifecycle of injected instances. Currently, the supported scopes are __Singleton__ and __Prototype__.

### Singleton Scope

A Singleton is an instance that is created __only once__, no matter how many Objects it will be injected in.

When defining Classes that should be injected as Singleton objects, there is one more thing to take into consideration: _When the instance will be created_.

A Singleton instance can be created __Eagerly__, at the time of its _definition_, or __Lazily__, at the time of its _injection_.

* Eager Singletons are defined like:

	`diDefine(() => new MyInjectableClass(), DIScope.SINGLETON_EAGER)`
* Lazy Singletons are defined like:

	`diDefine(() => new MyInjectableClass(), DIScope.SINGLETON_LAZY)`

### Prototype Scope

Unlike Singleton scope, in the Prototype scope __a new instance__ is created for each Object that needs to have it injected in.

Prototypes are defined like:

`diDefine(() => new MyInjectableClass(), DIScope.PROTOTYPE)`

Because of the Prototype nature, the laziness cannot be controlled by the _kind-of-di_. The library will create instances whenever the injection should happen.

However, laziness can be controlled by Scala anyway; defining the injected field as a `lazy val`:

```
class MyClassWithPrototype {
  lazy val mic = inject[MyInjectableClass]
}
```


## Short TODO List

* Provide a Maven artifact
* Enrich this README and add wiki pages
* Implement DSL
* Implement `init` hooks