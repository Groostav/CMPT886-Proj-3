package com.softwareleyline

import gr.gousiosg.javacg.stat.ClassVisitor
import gr.gousiosg.javacg.stat.MethodVisitor
import javassist.ClassPool
import javassist.bytecode.analysis.ControlFlow
import javassist.bytecode.stackmap.BasicBlock
import org.apache.bcel.Repository
import org.apache.bcel.classfile.ClassParser
import org.apache.bcel.classfile.JavaClass
import org.apache.bcel.classfile.Method
import org.apache.bcel.generic.*
import org.assertj.core.api.Assertions.*
import org.junit.Test
import java.lang.reflect.Proxy
import java.util.*

/**
 * Created by Geoff on 3/22/2016.
 */

var staticCallCount = 0;

//todo how i can suppress warnings
fun incrementCallCount() {
    staticCallCount += 1
}

// ok, so if you wanted to add context it looks like you're limited to modifying the jbc to include field decls.
// is this so bad?
class TestingSimpleRewrite {

    var callCount : Int = 0

    fun foo() : Unit {
        callCount += 1
    }
}

class TestingIntrospectionOfInterestingMethod{

    var thingy = 0;

    fun bar() : Unit {

        var x = 3.0 + 4.0 + 5.0;

        while(thingy < 5){
            x = x + 3.0;
        }

        thingy = x.toInt();
    }
}

class JavassistFixture {

    @Test fun hello_again_kotlin(){
        assertThat(1).isEqualTo(1);
    }

    @Test fun when_attempting_to_instrument_method_should_get_called_back_properly(){

        //setup
        val pool = ClassPool.getDefault();
        var testingClazz = pool.get("com.softwareleyline.TestingSimpleRewrite")

        //act
        testingClazz.getMethod("foo", "()V").insertBefore(
                // note that I found this name through google + trial and error,
                // http://stackoverflow.com/questions/33907095/kotlin-how-to-do-reflection-on-packages
                "com.softwareleyline.JavassistFixtureKt.incrementCallCount();"
        );
        testingClazz.freeze();
        var instance = testingClazz.toClass().newInstance() as TestingSimpleRewrite
        instance.foo();
        instance.foo();

        //assert
        assertThat(instance).isNotInstanceOf(Proxy::class.java)
        assertThat(staticCallCount).isEqualTo(2)
        assertThat(instance.callCount).isEqualTo(2)
    }

    @Test fun when_inspecting_method_should_get_reasonable_introspection(){

        //setup
        val pool = ClassPool.getDefault();
        var introspectableClass = pool.get("com.softwareleyline.TestingIntrospectionOfInterestingMethod")

        //act
        val method = introspectableClass.getMethod("bar", "()V")
        val blocks = ControlFlow(method).basicBlocks()
        // ok, so each block has 'entrances' and 'exits',
        // except for the first one in the method and the last one in the method.
        // interestingly, the entrance to the first node what would-be callsites) is Block[]

        //assert
        assertThat(blocks).hasSize(4);
        assertThat(blocks.first().incomings()).isEqualTo(0) //first block in the method
        assertThat(blocks.first().exits()).isEqualTo(1)
        assertThat(blocks.last().incomings()).isEqualTo(1) //last block,
        assertThat(blocks.last().exits()).isEqualTo(0); //return statement has no 'to' block, _statically_ at least.

        //now how the bugger do i get source code.
    }





    @Test fun when_using_static_analysis_to_build_identifiers_should_properly_identify(){

        val methodVisitorFactory = { m : MethodGen, c : JavaClass -> object : MethodVisitor(m, c) {
            override fun visitINVOKEINTERFACE(i: INVOKEINTERFACE?) {
                super.visitINVOKEINTERFACE(i)
            }

            override fun visitINVOKESTATIC(i: INVOKESTATIC?) {
                super.visitINVOKESTATIC(i)
            }

            override fun visitINVOKEVIRTUAL(i: INVOKEVIRTUAL?) {
                super.visitINVOKEVIRTUAL(i)
            }

            override fun visitINVOKESPECIAL(i: INVOKESPECIAL?) {
                super.visitINVOKESPECIAL(i)
            }
        }}

        val clazz = Repository.lookupClass(ExampleDag::class.java)

        val classVisitor = object : ClassVisitor(clazz){
            override fun visitMethod(method: Method?) {
                val mg = MethodGen(method, this.clazz.className, this.constants)
                val visitor = methodVisitorFactory(mg, clazz);
                visitor.start()
            }
        }

        classVisitor.start();
    }
}

//from http://www2.cs.sfu.ca/~wsumner/teaching/886/dynamic.pdf
class ExampleDag(){

    val random = Random();

    fun exampleDag(){

        var goLeft = A();

        if(goLeft){

            var goAcross = B();

            if(goAcross){
                C();
            }
        }
        else{
            C();
        }

        val goLeftAgain = D()

        if(goLeftAgain){
            E();
        }

        F();
    }


    fun A() : Boolean { return random.nextBoolean(); }
    fun B() : Boolean { return random.nextBoolean(); }
    fun C(){}
    fun D() : Boolean { return random.nextBoolean(); }
    fun E(){}
    fun F(){}
}