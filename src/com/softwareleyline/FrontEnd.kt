package com.softwareleyline

import com.google.gson.GsonBuilder
import org.apache.commons.cli.*
import java.io.FileInputStream
import java.io.InputStreamReader

/**
 * Created by Geoff on 5/2/2016.
 */
fun main(args : Array<String>){

    val options = Options().apply {
        addOption("c", "className", true, "the (fully-qualified) name of the class to rewrite, defaults to 'com.softwareleyline.ExampleCode'")
        addOption("g", "methodGraph", true, "a path to the json-encoded method call graph to instrument, defaults to  'ExampleCodeGraph.json'")
        addOption("m", "targetMethod", true, "the name of a zero-arg method to call on <className> to test, defaults to 'runDAG'")
    }

    var parser = GnuParser();

    val cmd = try {
        parser.parse(options, args)
    }
    catch(e : ParseException){
        System.err.println(e.message)
        HelpFormatter().printHelp("heffe", options)
        System.exit(1)
        throw UnsupportedOperationException()
    }

    val targetClass = cmd.getOptionValue("className", "com.softwareleyline.ExampleCode")
    val graph = cmd.getOptionValue("methodGraph", "ExampleCodeGraph.json")
    val targetMethod = cmd.getOptionValue("targetMethod", "runDAG")

    println("heffe instrumenting $graph in $targetClass, running with $targetMethod...")

    val runner = FrontEnd(targetClass, graph, targetMethod);

    val result = runner.run()

    println("took path $result")
}

class FrontEnd(val targetClass : String, val serializedGraphPath : String, val targetMethod : String){

    private val gson = GsonBuilder().setPrettyPrinting().create();

    fun run() : Int{
        val driver = Assig3Driver();

        val resource = javaClass.classLoader.getResource(serializedGraphPath)

        val graphStream = if(resource != null){ resource.openStream(); }
                          else{ FileInputStream(serializedGraphPath) }

        val graphSurrogate = gson.fromJson(InputStreamReader(graphStream), GraphSurrogate::class.java)

        val graph = graphSurrogate.asGraph();

        val closure = {
            val clazz = Class.forName(targetClass);
            val method = clazz.getMethod(targetMethod);

            val instance = clazz.newInstance()

            method.invoke(instance)

            Unit;
        }

        val (result, it) = driver.determinePathFor(targetClass, graph, closure)

        return result;

    }
}