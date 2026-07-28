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

tasks.register("installGitHooks", Copy::class.java) {
    group = "git hooks"
    description = "Installs Git hooks from the 'git-hooks' directory to .git/hooks"

    from(rootProject.projectDir.resolve("git-hooks"))
    into(rootProject.projectDir.resolve(".git/hooks"))

    // Ensure the copied files are executable
    doLast {
        rootProject.projectDir.resolve(".git/hooks").listFiles()?.forEach { file ->
            if (file.isFile) {
                file.setExecutable(true)
            }
        }
    }
}