
rootProject.name = "uni"
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
