package com.softwareleyline


import com.google.gson.Gson
import com.google.gson.GsonBuilder
import org.assertj.core.api.Assertions
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test

/**
 * Created by Geoff on 5/2/2016.
 */
class GraphSurrogateFixture{

    @Test fun when_going_to_and_from_surrogates_should_preserve_info(){

        val initialGraph = SampleGraph.buildGraph();

        val surrogate = initialGraph.toSurrogate();

        val reconstructedGraph = surrogate.asGraph();

        assertThat(reconstructedGraph).isEqualTo(initialGraph);
    }

    @Test fun when_serializing_and_derserializing_should_keep_information(){

        val gson = GsonBuilder().setPrettyPrinting().create();

        val initialGraph = SampleGraph.buildGraph();

        val surrogate = initialGraph.toSurrogate();

        val serialized = gson.toJson(surrogate);

        val deserialized = gson.fromJson(serialized, GraphSurrogate::class.java);

        val resultingGraph = deserialized.asGraph();

        assertThat(resultingGraph).isEqualTo(initialGraph);
    }
}