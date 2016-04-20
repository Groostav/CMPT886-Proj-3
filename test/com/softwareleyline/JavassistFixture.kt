
//nice. take that file.
@file:Suppress("unused")

package com.softwareleyline

import com.sun.org.apache.xpath.internal.operations.Bool
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
            }

            override fun visitINVOKESTATIC(i: INVOKESTATIC?) {
            }

            override fun visitINVOKEVIRTUAL(i: INVOKEVIRTUAL?) {
            }

            override fun visitINVOKESPECIAL(i: INVOKESPECIAL?) {
            }
        }}

        val clazz = Repository.lookupClass(ExampleCode::class.java)

        val classVisitor = object : ClassVisitor(clazz){
            override fun visitMethod(method: Method?) {
                val mg = MethodGen(method, this.clazz.className, this.constants)
                val visitor = methodVisitorFactory(mg, clazz);
                visitor.start()
            }
        }

        classVisitor.start();

        //so this gets me call sites but it doesnt get me basic blocks.
        //but the empty visitor does get you a visit method on all of the things inside a basic block.
        //ah ok, but its not traversing in order of the basic block graph,
        // its traversing in order of the static (source-code-ish) hierarchy,
        // that might be fine for my purposes.

        // so, maybe we specialize to just a function call graph.... er...
        // hmm... no this doesnt get me the graph I want, i need a basic-block graph...
        //
    }

    @Test fun do_thing(){
        //setup
        val pool = ClassPool.getDefault();
        var introspectableClass = pool.get("com.softwareleyline.ExampleCode")

        //act
        val method = introspectableClass.getMethod("exampleDag", "()V")
        val blocks = ControlFlow(method).basicBlocks()
        // ok, so each block has 'entrances' and 'exits',
        // except for the first one in the method and the last one in the method.
        // interestingly, the entrance to the first node what would-be callsites) is Block[]

        //so, fuck, these block objects are surrogates. I suspect there the block.position()
        // and block.length() methods give me an index and offset value into the file, so if I
        // knew the counter-part binaryFile.getInstructionAt(block.position()) i'd be in good shape.
        // but I don't, and after 10 minutes of googling I'm not suere where it is.
        // this documentation is aweful. Also I just noticed the really wierd licensing.
        // JBoss is weird.

        //going to look at soot now --as if i have the time :depressed: :reallyJustWantToSleep:

        introspectableClass.freeze();
        var instance = introspectableClass.toClass().newInstance() as ExampleCode

        instance.exampleDag();

    }


    //ok, so soot:
    // cant find it on maven except from robovm.org
    //   => robovm is JVM on IOS? nifty? How did I not know about this thing?
    //        => why does microsoft have the power to shut it down? wat!?
    // back on track: their most recent github release is 2013.
    // alright looking at walla and spoon.


    //walla:
    // The T. J. Watson Libraries for Analysis (WALA) provide static analysis capabilities for
    // Java bytecode and related languages and for JavaScript
    // static analysis. damn.

    //Spoon:
    // so spoon is nifty but its not a library its a framework. I like things in jars that I put in my /lib folder.
    // I'll tolerate a little hanky-panky with classloaders if thats what the library does (eg javassist),
    // but I really dont want to have to dick around with build systems (again) to get this assignment off the ground

    //so, lets spend another 10 minutes with javassist.


    private fun getExits(block : ControlFlow.Block) : Sequence<BasicBlock> {
        //nifty syntax for closures consistently impresses me.
        return DelegateSequence({block.incomings()}, {block.incoming(it)})
    }

    private fun getEnters(block : ControlFlow.Block) : Sequence<BasicBlock> {
        return DelegateSequence({block.exits()}, {block.exit(it)})
    }

    class DelegateSequence(val size: () -> Int, val get : (Int) -> ControlFlow.Block) : Sequence<ControlFlow.Block> {

        //still have these nasty things though, I suspect theres some clever kotlin trick I'm missing.
        override fun iterator(): Iterator<ControlFlow.Block> {
            return object : Iterator<ControlFlow.Block>{

                var currentIdx = 0;

                override fun next(): ControlFlow.Block {
                    return get(currentIdx)
                }

                override fun hasNext(): Boolean {
                    return currentIdx < size()
                }

            }
        }
    }
}