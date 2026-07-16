tasks.register("copy", Copy::class.java) {
    group = "MyGroup"
    description = "This is a copy task"

    from(fileTree(projectDir) {
        include("**/*.md")
        exclude("build/**")
    })
    into(layout.buildDirectory.dir("docs"))
    includeEmptyDirs = false
}