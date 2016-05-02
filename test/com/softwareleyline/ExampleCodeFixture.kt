package com.softwareleyline

import org.assertj.core.api.Assertions
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test

/**
 * Created by Geoff on 5/1/2016.
 */
class ExampleCodeFixture {

    @Test fun when_branching_left_should_get_zero(){
        //setup
        val driver = Assig3Driver()
        val graph = SampleGraph.buildGraph()

        //act
        val (pathID, pathProvider) = driver.determinePathFor("com.softwareleyline.ExampleCode", graph, {
            val instance = ExampleCode(true, true, true)
            instance.runDAG();
        })

        //assert
        assertThat(pathID).isEqualTo(2)
        assertThat(pathProvider()).isEqualTo(Path("A", "B", "C", "D", "E", "F"))
    }

}

