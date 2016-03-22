package com.softwareleyline

import javassist.ClassPool
import org.assertj.core.api.Assertions.*
import org.junit.Test
import java.lang.reflect.Proxy

/**
 * Created by Geoff on 3/22/2016.
 */

var staticCallCount = 0;

fun incrementCallCount() {
    staticCallCount += 1
}

class JavassistFixture {

    @Test fun hello_again_kotlin(){
        assertThat(1).isEqualTo(1);
    }

    @Test fun when_attempting_to_instrument_method_should_get_called_back_properly(){

        //setup
        val pool = ClassPool.getDefault();
        var testingClazz = pool.get("com.softwareleyline.Testing")

        //act
        testingClazz.getMethod("foo", "()V").insertBefore(
                "com.softwareleyline.JavassistFixtureKt.incrementCallCount();"
        );
        testingClazz.freeze();
        var instance = testingClazz.toClass().newInstance() as Testing
        instance.foo();
        instance.foo();

        //assert
        assertThat(instance).isNotInstanceOf(Proxy::class.java)
        assertThat(staticCallCount).isEqualTo(2)
        assertThat(instance.callCount).isEqualTo(2)
    }
}

class Testing{

    var callCount : Int = 0

    fun foo() : Unit {
        callCount += 1
    }
}