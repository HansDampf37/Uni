plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.5.0"
}
rootProject.name = "Uni"
include("graph")
include("graph:propa")
findProject(":graph:propa")?.name = "propa"
include("propa")
include("propa:analysis")
findProject(":propa:analysis")?.name = "analysis"
include("analysis")
include("algebra")
include("numeric")
include("or")
include("utils")
include("api")
